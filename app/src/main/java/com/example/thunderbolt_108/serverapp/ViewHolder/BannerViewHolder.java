package com.example.thunderbolt_108.serverapp.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thunderbolt_108.serverapp.Common.Common;
import com.example.thunderbolt_108.serverapp.Interface.ItemClickListener;
import com.example.thunderbolt_108.serverapp.R;

public class  BannerViewHolder extends RecyclerView.ViewHolder implements
        View.OnCreateContextMenuListener{

    public TextView banner_name;
    public ImageView banner_image;


    public BannerViewHolder(@NonNull View itemView) {
        super(itemView);
        banner_name=(TextView)itemView.findViewById(R.id.banner_name);
        banner_image=(ImageView)itemView.findViewById(R.id.banner_image);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select The Action");
        menu.add(0,0,getAdapterPosition(),Common.UPDATE);
        menu.add(0,1,getAdapterPosition(),Common.DELETE);

    }
}


