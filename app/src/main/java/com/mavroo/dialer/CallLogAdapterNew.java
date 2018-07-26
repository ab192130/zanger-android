package com.mavroo.dialer;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.BlockedNumberContract;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
    private ClipboardManager clipboardManager;

    CallLogAdapterNew(Context context, List<com.mavroo.dialer.CallLogNew> list){
        mContext = context;
        mActivity = (Activity) mContext;
        mList = list;

        clipboardManager = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
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
                final CallLogBubble bubble = callLog.getBubbles().get(i);

                LinearLayout layoutChild = new LinearLayout(mContext);
                layoutChild.setOrientation(LinearLayout.VERTICAL);
                layoutChild.setGravity(Gravity.FILL_VERTICAL);

                final View vBubble = mInflater.inflate(R.layout.item_call_log_in_child, layoutChild, false);
                ImageView ivType1  = vBubble.findViewById(R.id.item_call_log_type_image);
                ImageView ivType   = vBubble.findViewById(R.id.item_call_log_type_image_sm);
                ImageView ivDevice = vBubble.findViewById(R.id.item_call_log_bubble_device);
                TextView tvTarget  = vBubble.findViewById(R.id.item_call_log_target_text);
                TextView tvDate    = vBubble.findViewById(R.id.item_call_log_bubble_date);
                TextView tvRepeats = vBubble.findViewById(R.id.item_call_log_repeats);
                TextView tvDuration = vBubble.findViewById(R.id.item_call_log_bubble_duration);
                final LinearLayout layoutExtra = vBubble.findViewById(R.id.item_cal_log_bubble_extra);

                tvDate.setText(bubble.date);
                tvDuration.setText(bubble.getDurationText());

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

                if(bubble.hasContact()) {
                    if(bubble.contactDevice != -1) {
                        ivDevice.setImageResource(bubble.getDeviceIconRes());
                        ivDevice.setVisibility(View.VISIBLE);
                    }
                }

                if(bubble.repeats > 1)
                tvRepeats.setText(bubble.getRepeatsText());

                vBubble.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int visibility = layoutExtra.getVisibility();

                        if (visibility == View.VISIBLE)
                            layoutExtra.setVisibility(View.GONE);
                        else
                            layoutExtra.setVisibility(View.VISIBLE);
                    }
                });

                vBubble.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        vBubble.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                MenuItem menuCopy    = menu.add("Copy number");
                                menuCopy.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        ClipData clip = ClipData.newPlainText("label", bubble.number);
                                        clipboardManager.setPrimaryClip(clip);
                                        return false;
                                    }
                                });

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    MenuItem menuBlock = menu.add("Block number");
                                    menuBlock.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.N)
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            ContentValues values = new ContentValues();
                                            values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, bubble.number);
                                            mActivity.getContentResolver().insert(BlockedNumberContract.BlockedNumbers.CONTENT_URI, values);
                                            return false;
                                        }
                                    });
                                }

                                MenuItem menuDelete  = menu.add("Delete");
                                menuDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        for (int i = 0; i < bubble.keys.size(); i++) {
                                            CallLogHelper.deleteById(mActivity, bubble.keys.get(i));
                                        }

                                        if(callLog.getBubbles().size() == 1) {
                                            //...
                                            mList.remove(callLog);
                                        }

                                        callLog.getBubbles().remove(bubble);
                                        layoutBubble.removeView(vBubble);
                                        return false;
                                    }
                                });
                            }
                        });

                        return false;
                    }
                });

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
        public void setDataOnView(int position, final CallLogNew callLog) {
            super.setDataOnView(position, callLog);

            //...
            for (int i = 0; i < callLog.getBubbles().size(); i++) {
                final CallLogBubble bubble = callLog.getBubbles().get(i);

                LinearLayout layoutChild = new LinearLayout(mContext);
                layoutChild.setOrientation(LinearLayout.VERTICAL);
                layoutChild.setGravity(Gravity.FILL_VERTICAL);

                final View vBubble = mInflater.inflate(R.layout.item_call_log_out_child, layoutChild, false);
                TextView tvTarget = vBubble.findViewById(R.id.item_call_log_target_text);
                TextView tvDate = vBubble.findViewById(R.id.item_call_log_bubble_date);
                TextView tvRepeats = vBubble.findViewById(R.id.item_call_log_repeats);
                TextView tvDuration = vBubble.findViewById(R.id.item_call_log_bubble_duration);
                ImageView ivTargetPhoto = vBubble.findViewById(R.id.item_call_log_photo);
                ImageView ivDevice = vBubble.findViewById(R.id.item_call_log_bubble_device);
                final LinearLayout layoutExtra = vBubble.findViewById(R.id.item_cal_log_bubble_extra);

                tvActorName.setText("You");
                tvTarget.setText(bubble.number);
                tvDate.setText(bubble.date);
                tvDuration.setText(bubble.getDurationText());

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

                    if(bubble.contactDevice != -1) {
                        ivDevice.setImageResource(bubble.getDeviceIconRes());
                        ivDevice.setVisibility(View.VISIBLE);
                    }
                }

                if(bubble.repeats > 1)
                    tvRepeats.setText(bubble.getRepeatsText());

                vBubble.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int visibility = layoutExtra.getVisibility();

                        if (visibility == View.VISIBLE)
                            layoutExtra.setVisibility(View.GONE);
                        else
                            layoutExtra.setVisibility(View.VISIBLE);
                    }
                });

                vBubble.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        vBubble.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                MenuItem menuCopy    = menu.add("Copy number");
                                menuCopy.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        ClipData clip = ClipData.newPlainText("label", bubble.number);
                                        clipboardManager.setPrimaryClip(clip);
                                        return false;
                                    }
                                });

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    MenuItem menuBlock = menu.add("Block number");
                                    menuBlock.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.N)
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            ContentValues values = new ContentValues();
                                            values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, bubble.number);
                                            mActivity.getContentResolver().insert(BlockedNumberContract.BlockedNumbers.CONTENT_URI, values);
                                            return false;
                                        }
                                    });
                                }

                                MenuItem menuDelete  = menu.add("Delete");
                                menuDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        for (int i = 0; i < bubble.keys.size(); i++) {
                                            CallLogHelper.deleteById(mActivity, bubble.keys.get(i));
                                        }

                                        if(callLog.getBubbles().size() == 1) {
                                            //...
                                            mList.remove(callLog);
                                        }

                                        callLog.getBubbles().remove(bubble);
                                        layoutBubble.removeView(vBubble);
                                        return false;
                                    }
                                });
                            }
                        });

                        return false;
                    }
                });

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
