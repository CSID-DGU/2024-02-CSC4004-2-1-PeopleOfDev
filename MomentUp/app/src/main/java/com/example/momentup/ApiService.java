package com.example.momentup;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    @POST("/api/user/login")
    Call<LoginResponse> login(
            @Body LoginRequest loginRequest,
            @Header("FCM-Token") String fcmToken
    );

    @Multipart
    @POST("/api/user/signup")
    Call<SignUpResponse> signup(
            @Part("name") RequestBody name,
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part("email") RequestBody email,
            @Part MultipartBody.Part image,
            @Header("FCM-Token") String fcmToken
    );
}