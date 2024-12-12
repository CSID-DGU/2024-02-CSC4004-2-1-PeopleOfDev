package com.example.momentup.service;

import com.example.momentup.api.ApiResult;
import com.example.momentup.dto.GroupRecentPostsDto;
import com.example.momentup.model.Group;
import com.example.momentup.request.CreateGroupRequest;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface GroupApiService {
    // 그룹 정보 조회
    @GET("/api/v1/group/invite-code/{groupNumber}")
    Call<ApiResult<Group>> getGroup(@Path("groupNumber") Long groupNumber);

    // 그룹 생성 (이미지 있는 경우)
    @Multipart
    @POST("/api/v1/group")
    Call<ApiResult<Long>> createGroup(
            @Part("group") CreateGroupRequest request,
            @Part MultipartBody.Part profile
    );

    // 그룹 생성 (이미지 없는 경우)
    @POST("/api/v1/group")
    Call<ApiResult<Long>> createGroup(
            @Body CreateGroupRequest request
    );

    // 최근 게시물 조회
    @GET("/api/v1/group/posts/recent/{groupNumber}")
    Call<ApiResult<List<GroupRecentPostsDto>>> getGroupRecentPosts(
            @Path("groupNumber") Long groupNumber
    );
}