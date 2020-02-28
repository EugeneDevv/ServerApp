package com.example.thunderbolt_108.serverapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.thunderbolt_108.serverapp.Common.Common;
import com.example.thunderbolt_108.serverapp.ViewHolder.OrderDetailAdapter;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderDetail extends AppCompatActivity {

    TextView order_id,order_phone,order_address,order_total,order_comment,user_name,payMode,payState;
    String order_id_value="";
    RecyclerView lstfoods;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Vahika.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_order_detail);

        order_id=(TextView)findViewById(R.id.order_id);
        order_phone=(TextView)findViewById(R.id.order_phone);
        order_address=(TextView)findViewById(R.id.order_address);
        order_total=(TextView)findViewById(R.id.totalAmount);
        order_comment=(TextView)findViewById(R.id.order_comment);
        user_name=(TextView)findViewById(R.id.userName);
        payMode=(TextView)findViewById(R.id.paymentMethod);
        payState=(TextView)findViewById(R.id.paymentState);

        lstfoods=(RecyclerView)findViewById(R.id.lstFoods);
        lstfoods.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        lstfoods.setLayoutManager(layoutManager);

        if (getIntent()!=null)
            order_id_value=getIntent().getStringExtra("OrderId");

        //Set Value
        order_id.setText(order_id_value);
        user_name.setText(Common.currentRequest.getName());
        order_phone.setText(Common.currentRequest.getPhone());
        order_address.setText(Common.currentRequest.getAddress());
        order_total.setText(Common.currentRequest.getTotal());
        order_comment.setText(Common.currentRequest.getComment());
        payMode.setText(Common.currentRequest.getPaymentMethod());
        payState.setText(Common.currentRequest.getPaymentState());

        OrderDetailAdapter adapter=new OrderDetailAdapter(Common.currentRequest.getFoods());
        adapter.notifyDataSetChanged();
        lstfoods.setAdapter(adapter);
    }
}
