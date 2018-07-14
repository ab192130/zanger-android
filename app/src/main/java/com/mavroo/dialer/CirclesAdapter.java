package com.mavroo.dialer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

    private String getDefaultKey(CircleItem item) {
        String defaultKey = null;

        switch (item.defaultType) {
            case DEFAULT_TYPE_HOME:
                defaultKey = "default_number_home";
                break;

            case DEFAULT_TYPE_TAXI:
                defaultKey = "default_number_taxi";
                break;

            case DEFAULT_TYPE_RESTAURANT:
                defaultKey = "default_number_restaurant";
                break;

            case DEFAULT_TYPE_HOTEL:
                defaultKey = "default_number_hotel";
                break;

            default:
                defaultKey = "default_number_home";
                break;
        }

        return defaultKey;
    }

    private String getSettingsNumber(CircleItem item) {
        return settingsManager.readSetting(getDefaultKey(item), "");
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
                            InputPhoneNumberDialogTwo dialogInput = new InputPhoneNumberDialogTwo();
                            dialogInput.setRequestCode(CirclesAdapter.CODE_REQUEST_DEFAULT_NUMBER);
                            dialogInput.show(mFragmentManager, "none_tag");
                        }
                    }

                    if(number != null && !number.isEmpty())
                        DialpadManager.makeCall(mContext, number);
                }
            });

            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if(circleItem.number != null) {
                MenuItem miCall   = menu.add(Menu.NONE, 1, 1, "Call");
                MenuItem miChange = menu.add(Menu.NONE, 1, 1, "Change number");
                MenuItem miDelete = menu.add(Menu.NONE, 1, 1, "Clear number");

                // User selected "Call"
                miCall.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        DialpadManager.makeCall(mContext, circleItem.number);

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
