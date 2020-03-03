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
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thunderbolt_108.serverapp.Common.Common;
import com.example.thunderbolt_108.serverapp.Model.Request;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Reports extends AppCompatActivity {
    private TextView textViewFilters;
    private RecyclerView recyclerViewFilters;
    private ScrollView clReport;
    private Bitmap bitmap;
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
    private Date fromTimeMillis;
    private Date toTimeMillis;
    private List<Request> spinnerUsersList;
    private List<String> spinnerUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        textViewFilters= findViewById(R.id.textViewFilters);
        clReport = findViewById(R.id.clRepor);
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
        fbExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewFilters.setVisibility(View.GONE);
                //fbExport.setVisibility(View.GONE);
              //  bitmap = loadBitmapFromView(llScroll, llScroll.getWidth(), llScroll.getHeight());
                Log.d("size"," "+clReport.getWidth() +"  "+clReport.getWidth());
                bitmap = loadBitmapFromView(recyclerViewFilters, recyclerViewFilters.getWidth(), recyclerViewFilters.getHeight());
               DevicePermission();
            }
        });

    }
    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
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
                        fromTimeMillis = mFromDate.getTime();
                        toTimeMillis = mToDate.getTime();
                        //This method returns the time in millis
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
                        if (fromDate.getText().toString().equals("From date") || toDate.getText().toString().equals("To date")){
                            Toast.makeText(Reports.this, "Please select a valid date", Toast.LENGTH_SHORT).show();
                        }
                        else {
                        long timeMilliFrom = fromTimeMillis.getTime();
                        long timeMilliTo = toTimeMillis.getTime();
                        query = FirebaseDatabase.getInstance()
                                .getReference()
                                .child("Requests")
                                .orderByKey()
                                .startAt(String.valueOf(timeMilliFrom))
                                .endAt(String.valueOf(timeMilliTo));
                        loadOrders();
                    }
                    }
                })
                .setNegativeButton("Cancel",null);
        dialogDates.setCancelable(false);
        dialogDates.setView(layout);
        dialogDates.show();
    }

    private void userFilters() {
        getUsers();

    }

    private void getUsers() {
        spinnerUsersList = new ArrayList<>();
        spinnerUsers = new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Requests");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot requestSnapshot: dataSnapshot.getChildren()) {
                    if (requestSnapshot.exists()){
                        Request request = requestSnapshot.getValue(Request.class);
                        //Request mp = spinnerUsersList.get(0);
                            spinnerUsersList.add(request);
                    }
                }
                int size = spinnerUsersList.size();
                if (size!=0){
                    int size1;
                    for (size1=0;size1<size;size1++){
                        Request request = spinnerUsersList.get(size1);
                        username = request.getName();
                        if (username!=null){
                            spinnerUsers.add(username);
                        }
                    }
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Reports.this);
                    LinearLayout layout = new LinearLayout(Reports.this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    final Spinner usernameBox = new Spinner(Reports.this);
                    ArrayAdapter<String> userAdapter = new ArrayAdapter<>(Reports.this,android.R.layout.simple_spinner_dropdown_item, spinnerUsers);
                    userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    usernameBox.setAdapter(userAdapter);
                    layout.addView(usernameBox);
                    dialog.setTitle("Username")
                            .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    query = FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("Requests")
                                            .orderByChild("name")
                                            .equalTo(usernameBox.getSelectedItem().toString());
                                    loadOrders();
                                }
                            })
                            .setNegativeButton("Cancel", null);
                    dialog.setView(layout);
                    dialog.show();
                }
                else {
                    Toast.makeText(Reports.this, "Database is empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });
    }

    private void dateFilters() {
       selectDates();
    }

    public void generatePDF() {
       // WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);

        // write the document content
        File sd = Environment.getExternalStorageDirectory();
        //String backupDBPath = "TasteServer/Bizwiz.pdf";
      //  File backupDB = new File(sd, backupDBPath);
        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/TasteServer");
        Random random = new Random();
        int randomNumber = random.nextInt(80-65);
        dir.mkdir();
        String targetPdf =  "Report";
        //File filePath;
       // filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(createFile(targetPdf)));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
        textViewFilters.setVisibility(View.VISIBLE);
        Toast.makeText(this, "PDF created successfully!!!", Toast.LENGTH_SHORT).show();

        openGeneratedPDF();

    }
    int num = 0;

    public File createFile(String prefix) {
        String filename = prefix + "(" + num + ").pdf";  //create the correct filename
        File myFile = new File(Environment.getExternalStorageDirectory() + "/TasteServer", filename);
        try {
            if (!myFile.exists()) {
                myFile.createNewFile();
            } else {
                ++num;               //increase the file index
                createFile(prefix);  //simply call this method again with the same prefix
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return myFile;
    }
    private void openGeneratedPDF(){
        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/TasteServer");
      //  File file = new File("/sdcard/pdffromScroll.pdf");
        if (dir.exists())
        {

                Toast.makeText(Reports.this, "File stored in " + dir, Toast.LENGTH_LONG).show();

        }
    }
    private void DevicePermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            mDevicePermissionGranted = true;
            generatePDF();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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
                    generatePDF();
                }
                else {
                    Toast.makeText(Reports.this,"Device permission denied",Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }
}
