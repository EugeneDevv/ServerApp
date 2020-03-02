package com.example.thunderbolt_108.serverapp.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.thunderbolt_108.serverapp.R;

public class OrderViewHolder extends RecyclerView.ViewHolder{

    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress,txtOrderDate,txtPaymentState,txtPaymentMethod,txtName,txtTotal;
    public Button btnEdit,btnRemove,btnDetail,btnDirection;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        txtOrderAddress= itemView.findViewById(R.id.order_address_report);
        txtOrderId= itemView.findViewById(R.id.order_id_report);
        txtOrderStatus= itemView.findViewById(R.id.order_status);
        txtOrderPhone= itemView.findViewById(R.id.order_phone_report);
        txtOrderDate= itemView.findViewById(R.id.order_date);
        txtPaymentState=(TextView)itemView.findViewById(R.id.paymentState_report);
        txtPaymentMethod=(TextView)itemView.findViewById(R.id.paymentMethod_report);
        txtName=(TextView)itemView.findViewById(R.id.userName_report);
        txtTotal=(TextView)itemView.findViewById(R.id.totalAmount_report);

        btnEdit=(Button)itemView.findViewById(R.id.btnEdit);
        btnRemove=(Button)itemView.findViewById(R.id.btnRemove);
        btnDetail=(Button)itemView.findViewById(R.id.btnDetail);
        btnDirection=(Button)itemView.findViewById(R.id.btnDirection);

    }

}
