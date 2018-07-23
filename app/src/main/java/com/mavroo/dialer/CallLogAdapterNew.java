package com.mavroo.dialer;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class CallLogAdapterNew extends RecyclerView.Adapter<CallLogAdapterNew.GenericViewHolder> {

    private static final int VIEWTYPE_INCOMING = 1;
    private static final int VIEWTYPE_OUTGOING = 2;

    private Context mContext;
    private Activity mActivity;
    private List<CallLogNew> mList;
    private LayoutInflater mInflater;

    CallLogAdapterNew(Context context, List<com.mavroo.dialer.CallLogNew> list){
        mContext = context;
        mActivity = (Activity) mContext;
        mList = list;
    }

    @Override
    public int getItemViewType(int position) {
        com.mavroo.dialer.CallLogNew callLog = mList.get(position);
        return callLog.direction;
    }

    @NonNull
    @Override
    public GenericViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        mInflater = LayoutInflater.from(mContext);

        switch (viewType) {
            case VIEWTYPE_OUTGOING: // outgoing, sent
                itemView = mInflater
                        .inflate(R.layout.item_call_log_out_new, parent, false);

                return new RightViewHolder(itemView);
            default: // incoming, received
                itemView = mInflater
                        .inflate(R.layout.item_call_log_in_new, parent, false);

                return new LeftViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder holder, int position) {
        CallLogNew callLog = mList.get(position);

        holder.setDataOnView(position, callLog);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    abstract class GenericViewHolder extends RecyclerView.ViewHolder
    {

        TextView tvTarget;
        View mView;
        CallLogNew callLog;
        TextView tvActorName;
        ViewGroup layoutBubble;

        GenericViewHolder(View view) {
            super(view);

            //...
            mView = view;
            tvActorName = view.findViewById(R.id.item_call_log_actor);
            tvTarget = view.findViewById(R.id.item_call_log_target_text);
        }

        void setDataOnView(int position, CallLogNew callLog) {
            //...
            this.callLog = callLog;

            layoutBubble = mView.findViewById(R.id.item_call_log_bubble_container);
        }

        public void setContactData() {
            //...
        }
    }

    class LeftViewHolder extends GenericViewHolder{

        ImageView ivActorPhoto;

        LeftViewHolder(View view) {
            super(view);

            ivActorPhoto = view.findViewById(R.id.item_call_log_photo);
        }

        @Override
        public void setDataOnView(int position, final CallLogNew callLog) {
            super.setDataOnView(position, callLog);

            tvActorName.setText(callLog.getActorName());

            if(ivActorPhoto != null)
                ivActorPhoto.setOnClickListener(null);

            if(callLog.hasContact()) {
                if(callLog.contactName != null && tvActorName != null)
                    tvActorName.setText(callLog.contactName);

                if(ivActorPhoto != null) {
                    if(callLog.contactPhoto != null)
                        ivActorPhoto.setImageURI(Uri.parse(callLog.contactPhoto));

                    ivActorPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CallLogHelper.showContact(mContext, callLog.contactKey);
                        }
                    });
                }
            }

            //...
            for (int i = 0; i < callLog.getBubbles().size(); i++) {
                CallLogBubble bubble = callLog.getBubbles().get(i);

                LinearLayout layoutChild = new LinearLayout(mContext);
                layoutChild.setOrientation(LinearLayout.VERTICAL);
                layoutChild.setGravity(Gravity.FILL_VERTICAL);

                View vBubble = mInflater.inflate(R.layout.item_call_log_in_child, layoutChild, false);
                ImageView ivType1   = vBubble.findViewById(R.id.item_call_log_type_image);
                ImageView ivType   = vBubble.findViewById(R.id.item_call_log_type_image_sm);
                TextView tvTarget  = vBubble.findViewById(R.id.item_call_log_target_text);
                TextView tvDate    = vBubble.findViewById(R.id.item_call_log_date);
                TextView tvRepeats = vBubble.findViewById(R.id.item_call_log_repeats);

                tvDate.setText(bubble.date);

                switch (bubble.status){
                    case CallLog.Calls.MISSED_TYPE:
                        ivType.setImageResource(R.drawable.image_call_log_missed);
                        ivType1.setImageResource(R.drawable.image_call_log_missed);
                        tvTarget.setText(R.string.call_log_action_missed);
                        tvTarget.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
                        break;

                    case CallLog.Calls.REJECTED_TYPE:
                        ivType.setImageResource(R.drawable.image_call_log_rejected);
                        ivType1.setImageResource(R.drawable.image_call_log_rejected);
                        tvTarget.setText(R.string.call_log_action_rejected);
                        tvTarget.setTextColor(mContext.getResources().getColor(android.R.color.holo_orange_dark));
                        break;

                    default:
                        ivType.setImageResource(R.drawable.image_call_log_in);
                        ivType1.setImageResource(R.drawable.image_call_log_in);
                        break;
                }

                if(bubble.repeats > 1)
                    tvRepeats.setText("(" + bubble.repeats + ")");

                layoutBubble.addView(vBubble);
            }
        }

        @Override
        public void setContactData() {
            super.setContactData();

            //...
        }
    }

    class RightViewHolder extends GenericViewHolder {

        RightViewHolder(View view) {
            super(view);
        }

        @Override
        public void setDataOnView(int position, CallLogNew callLog) {
            super.setDataOnView(position, callLog);

            //...
            for (int i = 0; i < callLog.getBubbles().size(); i++) {
                final CallLogBubble bubble = callLog.getBubbles().get(i);
                bubble.setTargetContactData(mContext);

                LinearLayout layoutChild = new LinearLayout(mContext);
                layoutChild.setOrientation(LinearLayout.VERTICAL);
                layoutChild.setGravity(Gravity.FILL_VERTICAL);

                View vBubble = mInflater.inflate(R.layout.item_call_log_out_child, layoutChild, false);
                TextView tvTarget = vBubble.findViewById(R.id.item_call_log_target_text);
                TextView tvDate = vBubble.findViewById(R.id.item_call_log_date);
                TextView tvRepeats = vBubble.findViewById(R.id.item_call_log_repeats);
                ImageView ivTargetPhoto = vBubble.findViewById(R.id.item_call_log_photo);

                tvActorName.setText("You");
                tvTarget.setText(callLog.getActorName());
                tvDate.setText(bubble.date);

                if(ivTargetPhoto != null)
                    ivTargetPhoto.setOnClickListener(null);

                if (bubble.hasContact()) {
                    if(ivTargetPhoto != null) {
                        if(bubble.contactPhoto != null)
                            ivTargetPhoto.setImageURI(Uri.parse(bubble.contactPhoto));

                        ivTargetPhoto.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CallLogHelper.showContact(mContext, bubble.contactKey);
                            }
                        });
                    }

                    if(bubble.contactName != null)
                        tvTarget.setText(bubble.contactName);
                }

                if(bubble.repeats > 1)
                    tvRepeats.setText("(" + bubble.repeats + ")");

                layoutBubble.addView(vBubble);
            }
        }

        @Override
        public void setContactData() {
            super.setContactData();

            //...
        }
    }
}
