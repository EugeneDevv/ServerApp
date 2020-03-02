package com.example.thunderbolt_108.serverapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.thunderbolt_108.serverapp.Common.Common;
import com.example.thunderbolt_108.serverapp.Model.Request;

import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.MyViewHolder> {
private List<Request> mListRequests;
private Context mContext;
    public ReportsAdapter(List<Request> mListRequests, Context mContext){
    this.mListRequests = mListRequests;
    this.mContext = mContext;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_orders_report,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvOrderNo,tvName,tvPhone,tvAddress,tvPaymentMode,tvPaymentState, tvDate,tvAmount;
        ListView lvReports;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNo = itemView.findViewById(R.id.order_id_report);
            tvName = itemView.findViewById(R.id.userName_report);
            tvPhone = itemView.findViewById(R.id.order_phone_report);
            tvAddress = itemView.findViewById(R.id.order_address_report);
            tvPaymentMode = itemView.findViewById(R.id.paymentMethod_report);
            tvPaymentState = itemView.findViewById(R.id.paymentState_report);
            tvDate = itemView.findViewById(R.id.order_date_report);
            tvAmount = itemView.findViewById(R.id.totalAmount_report);
            lvReports = itemView.findViewById(R.id.list_foods_report);
        }
    }

}
