package com.example.thunderbolt_108.serverapp.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.thunderbolt_108.serverapp.Interface.ItemClickListener;
import com.example.thunderbolt_108.serverapp.R;

import info.hoang8f.widget.FButton;

public class ShipperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView shipper_name,shipper_phone;
    public FButton btn_edit,btn_remove;
    private ItemClickListener itemClickListener;

    public ShipperViewHolder(@NonNull View itemView) {
        super(itemView);

        shipper_name=(TextView)itemView.findViewById(R.id.shipper_name);
        shipper_phone=(TextView)itemView.findViewById(R.id.shipper_phone);
        btn_edit=(FButton)itemView.findViewById(R.id.btn_edit);
        btn_remove=(FButton)itemView.findViewById(R.id.btn_remove);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
