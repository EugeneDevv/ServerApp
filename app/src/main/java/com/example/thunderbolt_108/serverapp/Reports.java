package com.example.thunderbolt_108.serverapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.thunderbolt_108.serverapp.Common.Common;
import com.example.thunderbolt_108.serverapp.Model.Request;
import com.example.thunderbolt_108.serverapp.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.core.Context;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Reports extends AppCompatActivity {
    private TextView textViewFilters;
    private RecyclerView recyclerViewFilters;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fbExport;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private boolean mDevicePermissionGranted;
    FirebaseDatabase db;
    private String username;
    private boolean date;
    private boolean user;
    private Query query;
    private Calendar mFromDate,mToDate;
    FirebaseRecyclerAdapter<Request,ReportsAdapter.MyViewHolder> adapter;
    private SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        textViewFilters= findViewById(R.id.textViewFilters);
        recyclerViewFilters = findViewById(R.id.recyclerViewFilters);
        db=FirebaseDatabase.getInstance();
        query=db.getReference("Requests");
        recyclerViewFilters.setHasFixedSize(true);
        String format = "dd-MM-yyyy HH:mm";
        sdf = new SimpleDateFormat(format);
        fbExport = findViewById(R.id.fb_export_pdf);
        layoutManager=new LinearLayoutManager(this);
        recyclerViewFilters.setLayoutManager(layoutManager);
        loadOrders();
        textViewFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Reports.this);
                builder.setTitle("Choose filters to apply.");
                date = false;
                user = false;
// Add a checkbox list
                String[] filters = {"Date", "User"};
                boolean[] checkedItems = {false, false};
                builder.setMultiChoiceItems(filters, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // The user checked or unchecked a box
                        if (which==0){
                            date = isChecked;
                        }
                        else if(which==1){
                            user = isChecked;
                        }

                    }
                });

// Add OK and Cancel buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The user clicked OK
                        if (date && user){
                            dateAndUserFilters();
                        }
                        else if (date && !user){
                            dateFilters();
                        }
                        else if (!date && user){
                            userFilters();
                        }
                        else{

                        }

                    }
                });
                builder.setNegativeButton("Cancel", null);

// Create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        DevicePermission();
        fbExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePDF(recyclerViewFilters);
            }
        });

    }

    private void dateAndUserFilters() {
       selectDates();
       getUsers();
    }
    private void loadOrders() {
        FirebaseRecyclerOptions<Request> options=new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(query,Request.class)
                .build();

        adapter=new FirebaseRecyclerAdapter<Request, ReportsAdapter.MyViewHolder>(options) {
            @NonNull
            @Override
            public ReportsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView= LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.list_orders_report,viewGroup,false);
                return new ReportsAdapter.MyViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull ReportsAdapter.MyViewHolder holder, final int position, @NonNull final Request model) {

                // initialize listview
                //    myViewHolder.lvReports.setAdpater(new CustomAdapterArrayAdapter(mContext,ingredientsList));
                holder.tvOrderNo.setText(adapter.getRef(position).getKey());
                //myViewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                holder.tvAddress.setText(model.getAddress());
                holder.tvPhone.setText(model.getPhone());
                holder.tvDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));
                holder.tvPaymentMode.setText(model.getPaymentMethod());
                holder.tvPaymentState.setText(model.getPaymentState());
                holder.tvName.setText(model.getName());
                holder.tvAmount.setText(model.getTotal());

                //New event button

            }

        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerViewFilters.setAdapter(adapter);
    }

    private void selectDates() {
        AlertDialog.Builder dialogDates = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        mFromDate = Calendar.getInstance();
        mToDate = Calendar.getInstance();
        final TextView fromDate = new TextView(this);
        fromDate.setText("From date");
        fromDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener fDate = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        mFromDate.set(Calendar.YEAR,year);
                        mFromDate.set(Calendar.MONTH,month);
                        mFromDate.set(Calendar.DAY_OF_MONTH,day);

                        fromDate.setText(sdf.format(mFromDate.getTime()));
                        query = FirebaseDatabase.getInstance()
                                .getReference()
                                .child("Requests")
                                .orderByChild("date")
                                .startAt(sdf.format(mFromDate.getTime()))
                                .endAt(sdf.format(mToDate.getTime()));
                        loadOrders();
                    }
                };
                new DatePickerDialog(Reports.this,fDate, mFromDate.get(Calendar.YEAR), mFromDate.get(Calendar.MONTH), mFromDate.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        final TextView toDate = new TextView(this);
        toDate.setText("To date");
        toDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener tDate = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        mToDate.set(Calendar.YEAR,year);
                        mToDate.set(Calendar.MONTH,month);
                        mToDate.set(Calendar.DAY_OF_MONTH,day);
                        String format = "dd-MM-yyyy HH:mm";
                        SimpleDateFormat sdf = new SimpleDateFormat(format);toDate.setText(sdf.format(mToDate.getTime()));
                    }
                };
                new DatePickerDialog(Reports.this,tDate, mToDate.get(Calendar.YEAR), mToDate.get(Calendar.MONTH), mToDate.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        layout.addView(fromDate);
        layout.addView(toDate);
        dialogDates.setTitle("Select Dates")
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancel",null);
        dialogDates.setView(layout);
        dialogDates.show();
    }

    private void userFilters() {
        getUsers();
        query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Requests")
                .orderByChild("name")
                .equalTo(username);
    }

    private void getUsers() {
        List<String> spinnerUsers =  new ArrayList<>();
        spinnerUsers.add("Alan");
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final Spinner usernameBox = new Spinner(this);
        ArrayAdapter<String> userAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,spinnerUsers);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usernameBox.setAdapter(userAdapter);
        layout.addView(usernameBox);
        dialog.setTitle("Username")
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancel", null);
        dialog.setView(layout);
        dialog.show();
    }

    private void dateFilters() {
       selectDates();
    }

    public void generatePDF(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }

                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            bigCanvas.drawColor(Color.WHITE);

            Document document = new Document(PageSize.A4);
            final File file = new File(Environment.getExternalStorageDirectory().toString() + "/Download");
            file.mkdirs();
            final File fileSave = new File(file, "OrdersReport.pdf");
            try {
                PdfWriter.getInstance(document, new FileOutputStream(fileSave));
            } catch (DocumentException | FileNotFoundException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < size; i++) {

                try {
                    //Adding the content to the document
                    Bitmap bmp = bitmaCache.get(String.valueOf(i));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image image = Image.getInstance(stream.toByteArray());
                    float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                            - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                    image.scalePercent(scaler);
                    image.setAlignment(com.itextpdf.text.Image.ALIGN_CENTER | com.itextpdf.text.Image.ALIGN_TOP);
                    if (!document.isOpen()) {
                        document.open();
                    }
                    document.add(image);

                } catch (Exception ex) {
                    Log.e("TAG-ORDER PRINT ERROR", ex.getMessage());
                }
            }

            if (document.isOpen()) {
                document.close();
            }
            // Set on UI Thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AlertDialog.Builder builder = new AlertDialog.Builder(Reports.this);
                    builder.setTitle("Success")
                            .setMessage("PDF File Generated Successfully.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(fileSave), "application/pdf");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    startActivity(intent);
                                }

                            }).show();
                }
            });

        }

    }
    private void DevicePermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            mDevicePermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mDevicePermissionGranted = false;
        switch (requestCode) {

            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mDevicePermissionGranted = true;
                }
            }
            break;
        }
    }
}
