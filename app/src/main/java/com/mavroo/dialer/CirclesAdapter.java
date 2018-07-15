package com.mavroo.dialer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CirclesAdapter extends RecyclerView.Adapter<CirclesAdapter.CircleViewHolder>{
    public final static int CODE_REQUEST_DEFAULT_NUMBER = 999;
    public final static int DEFAULT_TYPE_HOME       = 1;
    public final static int DEFAULT_TYPE_TAXI       = 2;
    public final static int DEFAULT_TYPE_RESTAURANT = 3;
    public final static int DEFAULT_TYPE_HOTEL      = 4;
    public final static int DEFAULT_TYPE_CONTACT    = 5;

    private Context mContext;
    private Activity mActivity;
    private FragmentManager mFragmentManager;
    private List<CircleItem> listItems = new ArrayList<>();
    private SettingsManager settingsManager;

    CirclesAdapter(Context context, FragmentManager fragmentManager, List<CircleItem> listItems) {
        mContext  = context;
        mActivity = (Activity) context;
        mFragmentManager = fragmentManager;
        settingsManager = ((MainActivity) mActivity).settingsManager;
        this.listItems = listItems;
    }

    @NonNull
    @Override
    public CircleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.item_circle, parent, false);

        return new CircleViewHolder(view);
    }

    private String getSettingsNumber(CircleItem item) {
        return settingsManager.read(item.getTypeName(), "");
    }

    @Override
    public void onBindViewHolder(@NonNull final CircleViewHolder holder, int position) {
        final CircleItem item = listItems.get(position);
        final View itemView = holder.itemView;

        final String settingNumber = getSettingsNumber(item);
        if (settingNumber != null && !settingNumber.isEmpty())
            item.number = settingNumber;

        holder.circleTitle.setText(item.name);
        holder.circleImage.setImageResource(item.imageId);
        holder.circleItem = item;
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class CircleViewHolder extends RecyclerView.ViewHolder
    implements View.OnCreateContextMenuListener, View.OnLongClickListener{

        TextView circleTitle;
        ImageView circleImage;

        CircleItem circleItem;

        CircleViewHolder(final View itemView) {
            super(itemView);

            circleImage = itemView.findViewById(R.id.item_circle_image);
            circleTitle = itemView.findViewById(R.id.item_circle_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String number = null;

//                Dialog dialogMy = new InputPhoneNumberDialog(holder.itemView.getContext());
                    if(circleItem.isDefault()) {
                        if (circleItem.number != null && !circleItem.number.isEmpty()) {
                            number = circleItem.number;
                        } else {
                            // @todo: set defaultKey to dialog
                            showDialogChangeNumber(circleItem);
                        }
                    }

                    if(number != null && !number.isEmpty())
                        DialpadManager.makeCall(mContext, number);
                }
            });

            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        private void showDialogChangeNumber(CircleItem item) {
            InputPhoneNumberDialogTwo dialogInput = new InputPhoneNumberDialogTwo();
            dialogInput.setRequestCode(CirclesAdapter.CODE_REQUEST_DEFAULT_NUMBER);

            dialogInput.setOnPhoneInputListener(new InputPhoneNumberDialogTwo.OnPhoneNumberInputListener() {
                @Override
                public void onApplyInputPhoneNumber(@Nullable Bundle data, int requestCode) {
                    if(requestCode == CirclesAdapter.CODE_REQUEST_DEFAULT_NUMBER)  {

                        // data - phone number
                        String key = circleItem.getTypeName();

                        String phoneNumber = data.getString("phone_number");

                        settingsManager.save(key, phoneNumber);
                        circleItem.number = phoneNumber;

                        notifyDataSetChanged();
                    }
                }
            });

            dialogInput.show(mFragmentManager, "none_tag");
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if(circleItem.number != null) {
                MenuItem miCall   = menu.add(Menu.NONE, 1, 1, "Call");
                MenuItem miChange = menu.add(Menu.NONE, 1, 1, "Change number");
                MenuItem miRemove = menu.add(Menu.NONE, 1, 1, "Remove number");

                // User selected "Call"
                miCall.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        DialpadManager.makeCall(mContext, circleItem.number);

                        return true;
                    }
                });

                miChange.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        showDialogChangeNumber(circleItem);
                        return true;
                    }
                });

                miRemove.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        settingsManager.remove(circleItem.getTypeName());
                        circleItem.number = null;
                        notifyDataSetChanged();
                        return true;
                    }
                });
            } else {
                MenuItem miSet = menu.add(Menu.NONE, 1, 1, "Set");

                miSet.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        showDialogChangeNumber(circleItem);
                        return true;
                    }
                });
            }

            MenuItem miHide = menu.add(Menu.NONE, 1, 1, "Unhighlight");
        }

        @Override
        public boolean onLongClick(View v) {

            return false;
        }
    }
}
