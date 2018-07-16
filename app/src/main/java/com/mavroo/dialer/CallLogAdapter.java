package com.mavroo.dialer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.GenericViewHolder> {

    private final List<com.mavroo.dialer.CallLog> mList;
//    private Cursor mCursor;
    private Context mContext;
    private CallLogHelper callLogHelper;
    private OnBottomReachedListener onBottomReachedListener;
    private PhoneNumberManager phoneNumberManager;

    private static final int VIEWTYPE_INCOMING = 1;
    private static final int VIEWTYPE_OUTGOING = 2;

    CallLogAdapter(Context context, List<com.mavroo.dialer.CallLog> list){
        callLogHelper = CallLogHelper.getInstance();

        mContext = context;
//        mCursor =  cursor;
        mList = list;
        phoneNumberManager = new PhoneNumberManager();
    }

    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        com.mavroo.dialer.CallLog callLog = mList.get(position);

        return callLog.direction;
    }

    @NonNull
    @Override
    public GenericViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        switch (viewType) {
            case VIEWTYPE_OUTGOING: // outgoing, sent
                itemView = layoutInflater
                        .inflate(R.layout.item_call_log_out, parent, false);

                return new RightViewHolder(itemView);
            default: // incoming, received
                itemView = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_call_log_in, parent, false);

                return new LeftViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder holder, int position) {
        com.mavroo.dialer.CallLog callLog = mList.get(position);

        if(callLog == null)
            return;

        String number   = callLog.number;
        String duration = callLog.duration;
        String date     = callLog.date;
        int    type     = callLog.type;

        final Cursor contactCursor = ContactHelper.getByPhoneNumber(mContext, number);

        //number = phoneNumberManager.format(number);

        holder.setDataOnView(position, number, duration, date, type);

        if(!VariableManager.hasSize(mList)) {

            holder.hasContact   = false;
            holder.contactKey   = null;
            holder.contactName  = null;
            holder.contactPhoto = null;
            holder.photoImageView.setOnClickListener(null);

            return;
        }

        // from contact data
        try {
            if(!contactCursor.moveToFirst()) {
                return;
            }

            final String contactKey = contactCursor.getString(contactCursor
                    .getColumnIndex(ContactsContract.PhoneLookup.LOOKUP_KEY));
            final String contactName = contactCursor.getString(contactCursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            String contactPhoto = contactCursor.getString(contactCursor
                    .getColumnIndex(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI));

            holder.hasContact = true;
            holder.contactKey = contactKey;
            holder.contactName = contactName;
            holder.contactPhoto = contactPhoto;
            holder.setContactData();
        } finally {

            // if(position == mCursor.getCount() - 1) {
            //     onBottomReachedListener.onBottomReached(position);
            // } else
            //     onBottomReachedListener.onBottomNotReached(position);

            if (contactCursor != null) {
                contactCursor.close();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    abstract class GenericViewHolder extends RecyclerView.ViewHolder
    {
        ImageView typeImageView;
        TextView  actorTextView;
        TextView  targetTextView;
        TextView  dateTextView;
        ImageView photoImageView;

        String    number;

        boolean   hasContact;
        String    contactKey;
        String    contactName;
        String    contactPhoto;

        GenericViewHolder(View view) {
            super(view);

            actorTextView  = view.findViewById(R.id.item_call_log_actor);
            targetTextView = view.findViewById(R.id.item_call_log_target_text);
            dateTextView   = view.findViewById(R.id.item_call_log_date);
            photoImageView = view.findViewById(R.id.item_call_log_photo);
            typeImageView  = view.findViewById(R.id.item_call_log_type_image);
        }

        public void setDataOnView(int position, String number, String duration, String date, int type) {

            if(photoImageView != null)
                photoImageView.setImageResource(R.drawable.image_placeholder_face);

            dateTextView.setText(date);

            this.number = phoneNumberManager.format(number);

            if(!hasContact)
                photoImageView.setOnClickListener(null);

        }

        public void setContactData() {
            // @todo: fix bug with contact data persisting in recycled list.
            if(!hasContact)
                return;

            if(photoImageView != null) {
                if(contactPhoto != null && !contactPhoto.equals(""))
                    photoImageView.setImageURI(Uri.parse(this.contactPhoto));

                photoImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CallLogHelper.showContact(mContext, contactKey);
                    }
                });
            }
        }
    }

    class LeftViewHolder extends GenericViewHolder{

        LeftViewHolder(View view) {
            super(view);
        }

        @Override
        public void setDataOnView(int position, String number, String duration, String date, int type) {
            super.setDataOnView(position, number, duration, date, type);

            actorTextView.setText(this.number);

            switch (type){
                case CallLog.Calls.MISSED_TYPE:
                    typeImageView.setImageResource(R.drawable.image_call_log_missed);

                    targetTextView.setText(R.string.call_log_action_missed);
                    targetTextView.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
                    break;

                case CallLog.Calls.REJECTED_TYPE:
                    typeImageView.setImageResource(R.drawable.image_call_log_rejected);

                    targetTextView.setText(R.string.call_log_action_rejected);
                    targetTextView.setTextColor(mContext.getResources().getColor(android.R.color.holo_orange_dark));
                    break;

                default:
                    targetTextView.setText(R.string.call_log_action_incoming);
                    targetTextView.setTextColor(mContext.getResources().getColor(R.color.colorTextDefault));
                    typeImageView.setImageResource(R.drawable.image_call_log_in);
                    break;

            }
        }

        @Override
        public void setContactData() {
            super.setContactData();

            actorTextView.setText(this.contactName);
        }
    }

    class RightViewHolder extends GenericViewHolder {

        RightViewHolder(View view) {
            super(view);
        }

        @Override
        public void setDataOnView(int position, String number, String duration, String date, int type) {
            super.setDataOnView(position, number, duration, date, type);

            targetTextView.setText(number);
            targetTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            typeImageView.setImageResource(R.drawable.image_call_log_out);
        }

        @Override
        public void setContactData() {
            super.setContactData();

            targetTextView.setText(this.contactName);
        }
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener;
    }
}
