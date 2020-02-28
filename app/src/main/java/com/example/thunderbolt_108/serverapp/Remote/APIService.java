package com.example.thunderbolt_108.serverapp.Remote;

import com.example.thunderbolt_108.serverapp.Model.DataMessage;
import com.example.thunderbolt_108.serverapp.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAYeh2XVI:APA91bGkbFq48ZlgqXCfxJgRyxMSFy5FjpGPE3MGI6sxEYG0yyQmBYbmdJA5FcLOzWeP9cPNK9xYUt_qcoup14tskwK-CpnGVKfH0N3XVi5picdbK6YlJDXvd_J7epswz2T6BTXYJCN_"
            }

    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);
}
