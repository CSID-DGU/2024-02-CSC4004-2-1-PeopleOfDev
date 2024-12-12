package com.example.momentup;

import static com.example.momentup.RetrofitClient.BASE_URL;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.momentup.api.ApiResult;
import com.example.momentup.dto.GroupRecentPostsDto;
import com.example.momentup.model.Group;
import com.example.momentup.request.CreateGroupRequest;
import com.example.momentup.service.GroupApiService;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RequiredArgsConstructor
public class GroupRepository {
    private final GroupApiService apiService;
    private final Context context;

    @Builder
    public static GroupRepository create(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return new GroupRepository(
                retrofit.create(GroupApiService.class),
                context
        );
    }

    public interface GroupCallback {
        void onSuccess(Group group);
        void onFailure(Throwable throwable);
    }

    public void getGroup(Long groupNumber, GroupCallback callback) {
        apiService.getGroup(groupNumber).enqueue(new Callback<ApiResult<Group>>() {
            @Override
            public void onResponse(Call<ApiResult<Group>> call, Response<ApiResult<Group>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccessful()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("GroupRepository", "Get group failed. Response: " + response.code()
                                + ", Error: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    callback.onFailure(new Exception("Failed to load group: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResult<Group>> call, Throwable t) {
                Log.e("GroupRepository", "Network error during get group", t);
                callback.onFailure(t);
            }
        });
    }

    public void createGroup(String groupName, String hashTag, String groupIntro,
                            Long userNumber, Uri profileImageUri,
                            GroupCreationCallback callback) {

        // CreateGroupRequest 객체 생성
        CreateGroupRequest request = CreateGroupRequest.builder()
                .groupName(groupName)
                .hashTag(hashTag)
                .groupIntro(groupIntro)
                .userNumber(userNumber)
                .build();

        // request를 JSON으로 변환
        String requestJson = new Gson().toJson(request);
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                requestJson
        );

        // 프로필 이미지 처리
        MultipartBody.Part profilePart = null;
        if (profileImageUri != null) {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(profileImageUri);
                ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];

                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
                }

                RequestBody requestFile = RequestBody.create(
                        MediaType.parse("image/*"),
                        byteBuffer.toByteArray()
                );
                profilePart = MultipartBody.Part.createFormData("profile", "profile.jpg", requestFile);
            } catch (IOException e) {
                callback.onFailure(e);
                return;
            }
        }

        // API 호출
        apiService.createGroup(request, profilePart).enqueue(new Callback<ApiResult<Long>>() {
            @Override
            public void onResponse(Call<ApiResult<Long>> call, Response<ApiResult<Long>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccessful()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("GroupRepository", "Create group failed. Response: " + response.code()
                                + ", Error: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    callback.onFailure(new Exception("Group creation failed: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResult<Long>> call, Throwable t) {
                Log.e("GroupRepository", "Network error during group creation", t);
                callback.onFailure(t);
            }
        });
    }

    public interface GroupCreationCallback {
        void onSuccess(Long groupNumber);
        void onFailure(Throwable throwable);
    }

    public void getGroupRecentPosts(Long groupNumber, GroupPostsCallback callback) {
        apiService.getGroupRecentPosts(groupNumber).enqueue(new Callback<ApiResult<List<GroupRecentPostsDto>>>() {
            @Override
            public void onResponse(Call<ApiResult<List<GroupRecentPostsDto>>> call,
                                   Response<ApiResult<List<GroupRecentPostsDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccessful()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onFailure(new Exception("Failed to load recent posts"));
                }
            }

            @Override
            public void onFailure(Call<ApiResult<List<GroupRecentPostsDto>>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public interface GroupPostsCallback {
        void onSuccess(List<GroupRecentPostsDto> posts);
        void onFailure(Throwable throwable);
    }
}