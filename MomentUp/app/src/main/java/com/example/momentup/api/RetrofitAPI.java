package com.example.momentup.api;

import com.example.momentup.dto.UserDto;
import com.example.momentup.request.LoginRequest;
import com.example.momentup.response.SignUpResponse;
import com.example.momentup.UserTokenRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitAPI {
    @POST("/api/v1/users/login")
    Call<ApiResult<UserDto>> login(@Body LoginRequest loginRequest);

    @POST("/api/v1/users/user-token")
    Call<ApiResult<Void>> updateUserToken(@Body UserTokenRequest tokenRequest);

    Call<SignUpResponse> signup(RequestBody nameBody, RequestBody usernameBody, RequestBody passwordBody, RequestBody emailBody, MultipartBody.Part imagePart, String fcmToken);

    @Multipart
    @POST("/api/v1/users")
    Call<ApiResult<Long>> saveUser(
            @Part("user") RequestBody user,
            @Part MultipartBody.Part profile
    );
}
