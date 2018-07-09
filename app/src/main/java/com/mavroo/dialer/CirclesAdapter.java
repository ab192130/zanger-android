package com.mavroo.dialer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CirclesAdapter extends RecyclerView.Adapter<CirclesAdapter.CircleViewHolder> {
    List<CircleItem> listItems = new ArrayList<>();

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
    public void onBindViewHolder(@NonNull CircleViewHolder holder, int position) {
        CircleItem item = listItems.get(position);

        holder.circleTitle.setText(item.name);
        holder.circleImage.setImageResource(item.imageId);
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
