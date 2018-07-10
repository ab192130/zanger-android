package com.mavroo.dialer;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CirclesAdapter extends RecyclerView.Adapter<CirclesAdapter.CircleViewHolder> {
    private List<CircleItem> listItems = new ArrayList<>();

    CirclesAdapter(List<CircleItem> listItems) {
        this.listItems = listItems;
    }

    @NonNull
    @Override
    public CircleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.item_circle, parent, false);

        return new CircleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CircleViewHolder holder, int position) {
        CircleItem item = listItems.get(position);

        holder.circleTitle.setText(item.name);
        holder.circleImage.setImageResource(item.imageId);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialogMy = new NumberInputDialog(holder.itemView.getContext());

                dialogMy.show();

                /*AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("What is your home number?");
                builder.setCancelable(true);

                builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ...
                    }
                });

                builder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...
                    }
                });

                builder.show();*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class CircleViewHolder extends RecyclerView.ViewHolder {

        TextView circleTitle;
        ImageView circleImage;

        CircleViewHolder(View itemView) {
            super(itemView);

            circleImage = itemView.findViewById(R.id.item_circle_image);
            circleTitle = itemView.findViewById(R.id.item_circle_name);
        }
    }
}
