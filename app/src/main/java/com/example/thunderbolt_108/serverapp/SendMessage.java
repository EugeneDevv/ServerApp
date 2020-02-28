package com.example.thunderbolt_108.serverapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.thunderbolt_108.serverapp.Common.Common;
import com.example.thunderbolt_108.serverapp.Model.DataMessage;
import com.example.thunderbolt_108.serverapp.Model.MyResponse;
import com.example.thunderbolt_108.serverapp.Remote.APIService;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SendMessage extends AppCompatActivity {

    MaterialEditText edtMessage,edtTitle;
    FButton btnSend;
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
        setContentView(R.layout.activity_send_message);

        mService=Common.getFCMClient();

        edtTitle=(MaterialEditText)findViewById(R.id.edtTitle);
        edtMessage=(MaterialEditText)findViewById(R.id.edtMessage);

        btnSend=(FButton)findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTitle.getText().toString().trim().length()==0 || edtMessage.getText().toString().trim().length()==0)
                {
                    Toast.makeText(SendMessage.this, "Please Enter Title & Message !!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    String abc = new StringBuilder("/topics/").append(Common.topicName).toString();
                    Map<String, String> dataSend = new HashMap<>();
                    dataSend.put("title", edtTitle.getText().toString());
                    dataSend.put("message", edtMessage.getText().toString());
                    DataMessage dataMessage = new DataMessage(abc, dataSend);

                    mService.sendNotification(dataMessage)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.isSuccessful())
                                        Toast.makeText(SendMessage.this, "Message Sent Successfully !!!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Toast.makeText(SendMessage.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                    edtTitle.setText(" ");
                    edtMessage.setText(" ");
                }
            }
        });
    }
}
