package com.example.thunderbolt_108.serverapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.thunderbolt_108.serverapp.Common.Common;
import com.example.thunderbolt_108.serverapp.Model.DataMessage;
import com.example.thunderbolt_108.serverapp.Model.MyResponse;
import com.example.thunderbolt_108.serverapp.Model.Request;
import com.example.thunderbolt_108.serverapp.Model.Token;
import com.example.thunderbolt_108.serverapp.Remote.APIService;
import com.example.thunderbolt_108.serverapp.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderStatus extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;

    FirebaseDatabase db;
    DatabaseReference reuests;

    MaterialSpinner spinner,shipperSpinner;

    APIService mService;

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
        setContentView(R.layout.activity_order_status);

        //Init Service
        mService=Common.getFCMClient();

        //Firebase
        db=FirebaseDatabase.getInstance();
        reuests=db.getReference("Requests");

        //Init
        recyclerView=(RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders();
    }

    private void loadOrders() {
        FirebaseRecyclerOptions<Request> options=new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(reuests,Request.class)
                .build();

        adapter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView=LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.order_layout,viewGroup,false);
                return new OrderViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, final int position, @NonNull final Request model) {

                holder.txtOrderId.setText(adapter.getRef(position).getKey());
                holder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                holder.txtOrderAddress.setText(model.getAddress());
                holder.txtOrderPhone.setText(model.getPhone());
                holder.txtOrderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));
                holder.txtPaymentMethod.setText(model.getPaymentMethod());
                holder.txtPaymentState.setText(model.getPaymentState());
                holder.txtName.setText(model.getName());
                holder.txtTotal.setText(model.getTotal());

                //New event button
                holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showUpdateDialog(adapter.getRef(position).getKey(),adapter.getItem(position));
                    }
                });

                holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOrder(adapter.getRef(position).getKey());
                    }
                });

                holder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent orderDetail=new Intent(OrderStatus.this,OrderDetail.class);
                        Common.currentRequest=model;
                        orderDetail.putExtra("OrderId",adapter.getRef(position).getKey());
                        startActivity(orderDetail);

                    }
                });

                holder.btnDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent trackingOrder=new Intent(OrderStatus.this,TrackingOrder.class);
                        Common.currentRequest=model;
                        startActivity(trackingOrder);
                    }
                });


                }

        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void deleteOrder(final String key) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Hey, "+Common.currentUser.getName());
        alertDialog.setMessage("Do You Want To Delete Order?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                reuests.child(key).removeValue();
                adapter.notifyDataSetChanged();
                Toast.makeText(OrderStatus.this, "Order Deleted Successfully", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                Toast.makeText(OrderStatus.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setIcon(R.mipmap.picture);
        alertDialog.show();
    }

    private void showUpdateDialog(String key, final Request item) {


            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
            alertDialog.setTitle("Update Order");
        alertDialog.setCancelable(false);
            alertDialog.setMessage("Please Choose Status");

            LayoutInflater inflater = this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.update_order_layout, null);

            spinner = (MaterialSpinner)view.findViewById(R.id.statusSpinner);
            shipperSpinner = (MaterialSpinner)view.findViewById(R.id.shipperSpinner);

            spinner.setItems("Placed","On Process","Shipping");

            //Load all shipper phone to spinner
        final List<String>shipperList=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(Common.SHIPPERS_TABLE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot shipperSnapShot:dataSnapshot.getChildren())
                            shipperList.add(shipperSnapShot.getKey());
                        shipperSpinner.setItems(shipperList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            alertDialog.setView(view);

            final String localKey = key;
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                    if (item.getStatus().equals("2"))
                    {
                        FirebaseDatabase.getInstance().getReference(Common.ORDER_NEED_SHIP_TABLE)
                                .child(shipperSpinner.getItems().get(shipperSpinner.getSelectedIndex()).toString())
                                .child(localKey)
                                .setValue(item);

                        reuests.child(localKey).setValue(item);
                        adapter.notifyDataSetChanged();     //Add To update item size
                        sendOrderStatusToUser(localKey, item);
                        sendOrderShipRequestToShipper(shipperSpinner.getItems().get(shipperSpinner.getSelectedIndex()).toString(),item);
                    }
                    else {
                        reuests.child(localKey).setValue(item);
                        adapter.notifyDataSetChanged();     //Add To update item size
                        sendOrderStatusToUser(localKey, item);
                    }
                }
            });

            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Toast.makeText(OrderStatus.this, "Cancel", Toast.LENGTH_SHORT).show();
                }
            });

            alertDialog.show();
    }

    private void sendOrderShipRequestToShipper(String shipperPhone, Request item) {

        DatabaseReference tokens=db.getReference("Tokens");
        tokens.child(shipperPhone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            Token token = dataSnapshot.getValue(Token.class);

                            Map<String, String> dataSend = new HashMap<>();
                            dataSend.put("title", Common.currentUser.getName());
                            dataSend.put("message", "You Have New Order For Shipping !!!");
                            DataMessage dataMessage = new DataMessage(token.getToken(), dataSend);

                            mService.sendNotification(dataMessage)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success == 1) {
                                                Toast.makeText(OrderStatus.this, "Order Sent To Shipper Successfully !!!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(OrderStatus.this, "Failed To Send Notification !!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR", t.getMessage());

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void sendOrderStatusToUser(final String key,final Request item) {
        DatabaseReference tokens=db.getReference("Tokens");
        tokens.child(item.getPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists())
                        {

                            Token token=dataSnapshot.getValue(Token.class);

                            Map<String,String> dataSend=new HashMap<>();
                            dataSend.put("title",Common.currentUser.getName());
                            dataSend.put("message","Your Order"+" "+key+" "+"Was Updated !!!");
                            DataMessage dataMessage=new DataMessage(token.getToken(),dataSend);

                            mService.sendNotification(dataMessage)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success==1)
                                            {
                                                Toast.makeText(OrderStatus.this, "Order Was Updated", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                Toast.makeText(OrderStatus.this, "Order Was Updated But Failed To Send Notification", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR",t.getMessage());

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadOrders();
    }
}
