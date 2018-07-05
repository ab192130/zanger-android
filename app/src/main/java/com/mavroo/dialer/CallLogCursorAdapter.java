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

public class CallLogCursorAdapter extends RecyclerView.Adapter<CallLogCursorAdapter.CallLogViewHolder> {
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
        return type;
    }

    @NonNull
    @Override
    public CallLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        switch (viewType) {
            case 2: // outgoing, sent
                itemView = layoutInflater
                        .inflate(com.mavroo.dialer.R.layout.item_call_log_out, parent, false);
                break;
            default: // incoming, received
                itemView = LayoutInflater.from(mContext)
                        .inflate(com.mavroo.dialer.R.layout.item_call_log_in, parent, false);
                break;
        }

        return new CallLogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogViewHolder holder, int position) {
        if(!mCursor.moveToPosition(position))
            return;

        String number   = mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.NUMBER));
        String duration = mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.DURATION));
        String date     = mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.DATE));
        int    type     = mCursor.getInt(mCursor.getColumnIndex(CallLog.Calls.TYPE));
        date            = DateHelper.getInstance().getDateString(Long.valueOf(date));

        final Cursor contactCursor = ContactHelper.getByPhoneNumber(mContext, number);

        // defaults
        //holder.nameTextView.setText(R.string.item_call_log_caller_unknown);

        number = phoneNumberManager.format(number);

        if(holder.photoImageView != null)
        holder.photoImageView.setImageResource(R.drawable.image_placeholder_face);

        switch (type) {
            case 2: // outgoing, sent
                holder.targetTextView.setText(number);
                break;

            default: // incoming, received
                holder.actorTextView.setText(number);
                break;
        }

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

                    //holder.numberTextView.setText(contactName);
                    // holder.numberTextView.setText(phoneNumberManager.format(number));

                    switch (type) {
                        case 2: // outgoing, sent
                            holder.targetTextView.setText(contactName);
                            break;

                        default: // incoming, received
                            holder.actorTextView.setText(contactName);
                            break;
                    }

                    if(holder.photoImageView != null && contactPhoto != null && !contactPhoto.equals("")) {
                        holder.photoImageView.setImageURI(Uri.parse(contactPhoto));
                    }

                    if(holder.photoImageView != null)
                    holder.photoImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CallLogHelper.showContact(mContext, contactKey);
                        }
                    });
                }
            } finally {

                /*if(position == mCursor.getCount() - 1) {
                    onBottomReachedListener.onBottomReached(position);
                } else
                    onBottomReachedListener.onBottomNotReached(position);*/

                contactCursor.close();
            }
        }

        holder.dateTextView.setText(date);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    class CallLogViewHolder extends RecyclerView.ViewHolder{
        TextView actorTextView;
        TextView targetTextView;
        TextView dateTextView;
        ImageView photoImageView;

        CallLogViewHolder(View view) {
            super(view);

            actorTextView  = view.findViewById(R.id.item_call_log_actor);
            targetTextView = view.findViewById(R.id.item_call_log_target_text);
            dateTextView   = view.findViewById(R.id.item_call_log_date);
            photoImageView = view.findViewById(R.id.item_call_log_photo);
        }
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener;
    }
}
