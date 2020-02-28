package com.example.thunderbolt_108.serverapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thunderbolt_108.serverapp.Common.Common;
import com.example.thunderbolt_108.serverapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    FButton btnsignin;
    TextView txtslogan;

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
        setContentView(R.layout.activity_main);

        btnsignin=(FButton) findViewById(R.id.signin);

        txtslogan=(TextView)findViewById(R.id.txtSlogan);
        Typeface typeface=Typeface.createFromAsset(getAssets(),"fonts/Vahika.ttf");
        txtslogan.setTypeface(typeface);

        Paper.init(this);

        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SignIn=new Intent(MainActivity.this,SignInAct.class);
                startActivity(SignIn);
            }
        });

        //Check Remember
        String user=Paper.book().read(Common.USER_KEY);
        String pwd=Paper.book().read(Common.PWD_KEY);
        if (user!=null && pwd!=null)
        {
            if (!user.isEmpty() && !pwd.isEmpty())
                login(user,pwd);
        }
    }

    private void login(String phone, String pwd) {
        //Save user & Password
        //Init firebase
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference users =database.getReference("User");
        final AlertDialog dialog=new SpotsDialog.Builder().setContext(MainActivity.this).build();
        dialog.show();
        dialog.setMessage("Please Wait . . .");
        dialog.setCancelable(false);
        final String localPhone=phone;
        final String localPassword=pwd;
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(localPhone).exists()) {
                    dialog.dismiss();

                    //Get user info
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone); //set phone
                    if (Boolean.parseBoolean(user.getIsStaff()))
                    {
                        if (user.getPassword().equals(localPassword)) {
                            Toast.makeText(MainActivity.this, "Sign In Successfully", Toast.LENGTH_SHORT).show();
                            Intent login=new Intent(MainActivity.this,Home.class);
                            startActivity(login);
                            Common.currentUser=user;
                            finish();

                        } else {
                            Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                        Toast.makeText(MainActivity.this, "Please Sign In With Staff Account", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "User Not Exist In Database", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
