package com.example.thunderbolt_108.serverapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.thunderbolt_108.serverapp.Common.Common;
import com.example.thunderbolt_108.serverapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;
import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignInAct extends AppCompatActivity {
    MaterialEditText edtPhone,edtPassword;
    FButton btnSignIn;

    CheckBox ckbRemember;

    FirebaseDatabase db;
    DatabaseReference users;

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
        setContentView(R.layout.activity_sign_in2);

        edtPhone=(MaterialEditText) findViewById(R.id.edtPhone);
        edtPassword=(MaterialEditText) findViewById(R.id.edtPassword);
        btnSignIn=(FButton)findViewById(R.id.btnSignIn);
        ckbRemember=(CheckBox)findViewById(R.id.ckbRemember);

        //Init Paper
        Paper.init(this);

        //Init firebase
        db=FirebaseDatabase.getInstance();
        users=db.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SignInUser(edtPhone.getText().toString(), edtPassword.getText().toString());

            }
        });
    }

    private void SignInUser(final String phone, String password) {
        //Save user & Password
        if (ckbRemember.isChecked())
        {
            Paper.book().write(Common.USER_KEY,edtPhone.getText().toString());
            Paper.book().write(Common.PWD_KEY,edtPassword.getText().toString());

        }
        final android.app.AlertDialog dialog=new SpotsDialog.Builder().setContext(SignInAct.this).build();
        dialog.show();
        dialog.setMessage("Please Wait . . .");
        dialog.setCancelable(false);
        final String localPhone=phone;
        final String localPassword=password;
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
                            Toast.makeText(SignInAct.this, "Sign In Successfully", Toast.LENGTH_SHORT).show();
                            Intent login=new Intent(SignInAct.this,Home.class);
                            startActivity(login);
                            Common.currentUser=user;
                            finish();

                        } else {
                            Toast.makeText(SignInAct.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                        Toast.makeText(SignInAct.this, "Please Sign In With Staff Account", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    dialog.dismiss();
                    Toast.makeText(SignInAct.this, "User Not Exist In Database", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
