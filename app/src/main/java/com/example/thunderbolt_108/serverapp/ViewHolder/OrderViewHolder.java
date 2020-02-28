package com.example.thunderbolt_108.serverapp.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.thunderbolt_108.serverapp.Interface.ItemClickListener;
import com.example.thunderbolt_108.serverapp.R;

public class OrderViewHolder extends RecyclerView.ViewHolder{

    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress,txtOrderDate,txtPaymentState,txtPaymentMethod,txtName,txtTotal;
    public Button btnEdit,btnRemove,btnDetail,btnDirection;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        txtOrderAddress=(TextView)itemView.findViewById(R.id.order_address);
        txtOrderId=(TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus=(TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone=(TextView)itemView.findViewById(R.id.order_phone);
        txtOrderDate=(TextView)itemView.findViewById(R.id.order_date);
        txtPaymentState=(TextView)itemView.findViewById(R.id.paymentState);
        txtPaymentMethod=(TextView)itemView.findViewById(R.id.paymentMethod);
        txtName=(TextView)itemView.findViewById(R.id.userName);
        txtTotal=(TextView)itemView.findViewById(R.id.totalAmount);

        btnEdit=(Button)itemView.findViewById(R.id.btnEdit);
        btnRemove=(Button)itemView.findViewById(R.id.btnRemove);
        btnDetail=(Button)itemView.findViewById(R.id.btnDetail);
        btnDirection=(Button)itemView.findViewById(R.id.btnDirection);

    }

}
