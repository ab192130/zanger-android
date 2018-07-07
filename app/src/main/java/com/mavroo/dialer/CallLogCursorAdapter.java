package com.mavroo.dialer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CallLogCursorAdapter extends RecyclerView.Adapter<CallLogCursorAdapter.GenericViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private CallLogHelper callLogHelper;
    private OnBottomReachedListener onBottomReachedListener;
    private PhoneNumberManager phoneNumberManager;

    private static final int VIEWTYPE_INCOMING = 1;
    private static final int VIEWTYPE_OUTGOING = 2;

    CallLogCursorAdapter(Context context, Cursor cursor){
        callLogHelper = CallLogHelper.getInstance();

        mContext = context;
        mCursor =  cursor;
        phoneNumberManager = new PhoneNumberManager();
    }

    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        mCursor.moveToPosition(position);

        int type = mCursor.getInt(mCursor.getColumnIndex(CallLog.Calls.TYPE));
        //...

        if(type == CallLog.Calls.OUTGOING_TYPE)
            return VIEWTYPE_OUTGOING;

        return VIEWTYPE_INCOMING;
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
        if(!mCursor.moveToPosition(position))
            return;

        String number   = mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.NUMBER));
        String duration = mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.DURATION));
        String date     = mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.DATE));
        int    type     = mCursor.getInt(mCursor.getColumnIndex(CallLog.Calls.TYPE));
        date            = DateHelper.getInstance().getDateString(Long.valueOf(date));

        final Cursor contactCursor = ContactHelper.getByPhoneNumber(mContext, number);

        number = phoneNumberManager.format(number);

        holder.setDataOnView(position, number, duration, date, type);

        if(CallLogHelper.hasLogs(contactCursor)) {
            // from contact data
            try {
                if(contactCursor.moveToFirst()) {
                    final String contactKey = contactCursor.getString(contactCursor
                            .getColumnIndex(ContactsContract.PhoneLookup.LOOKUP_KEY));
                    final String contactName = contactCursor.getString(contactCursor
                            .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                    String contactPhoto = contactCursor.getString(contactCursor
                            .getColumnIndex(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI));

                    holder.setContactData(contactKey, contactName, contactPhoto);

                    // if(holder.photoImageView != null && contactPhoto != null && !contactPhoto.equals("")) {
                    //     holder.photoImageView.setImageURI(Uri.parse(contactPhoto));
                    // }

                     /*if(holder.photoImageView != null)
                     holder.photoImageView.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             CallLogHelper.showContact(mContext, contactKey);
                         }
                     });*/
                }
            } finally {

                // if(position == mCursor.getCount() - 1) {
                //     onBottomReachedListener.onBottomReached(position);
                // } else
                //     onBottomReachedListener.onBottomNotReached(position);

                contactCursor.close();
            }
        }

        //holder.dateTextView.setText(date);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    abstract class GenericViewHolder extends RecyclerView.ViewHolder
    {
        ImageView typeImageView;
        TextView  actorTextView;
        TextView  targetTextView;
        TextView  dateTextView;
        ImageView photoImageView;

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
        }

//        public abstract void setContactData(String contactKey, String contactName, String contactPhoto);
        public void setContactData(final String contactKey, final String contactName, String contactPhoto) {
            // @todo: fix bug with contact data persisting in recycled list.

            this.contactKey   = contactKey;
            this.contactName  = contactName;
            this.contactPhoto = contactPhoto;

            if(photoImageView != null) {
                if(contactPhoto != null && !contactPhoto.equals(""))
                    photoImageView.setImageURI(Uri.parse(contactPhoto));

                Log.e("MYAPP:CONTACT_KEY", contactKey);

                if(!contactKey.equals(""))
                photoImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CallLogHelper.showContact(mContext, contactKey);
                        Log.e("MYAPP:CONTACT_KEY", contactKey + "-" + contactName);
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

            actorTextView.setText(number);

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
        public void setContactData(String contactKey, String contactName, String contactPhoto) {
            super.setContactData(contactKey, contactName, contactPhoto);

            actorTextView.setText(contactName);
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
            typeImageView.setImageResource(R.drawable.image_call_log_out);
            targetTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        }

        @Override
        public void setContactData(String contactKey, String contactName, String contactPhoto) {
            super.setContactData(contactKey, contactName, contactPhoto);

            targetTextView.setText(contactName);
        }
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener;
    }
}
