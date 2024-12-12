package com.example.momentup;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.momentup.databinding.ActivityGroupDetailBinding;
import com.example.momentup.dto.GroupRecentPostsDto;
import com.example.momentup.model.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailActivity extends AppCompatActivity {
    private ActivityGroupDetailBinding binding;
    private GroupRepository groupRepository;
    private RecentUploadAdapter recentUploadAdapter;
    private Long groupNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        groupNumber = getIntent().getLongExtra("groupNumber", -1L);
        if (groupNumber == -1L) {
            Toast.makeText(this, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        loadGroupDetails();
        loadRecentPosts();
    }

    private void initializeViews() {
        recentUploadAdapter = new RecentUploadAdapter();
        binding.recyclerViewRecentUploads.setAdapter(recentUploadAdapter);
        binding.recyclerViewRecentUploads.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
    }

    private void loadGroupDetails() {
        groupRepository = GroupRepository.create(this);

        groupRepository.getGroup(groupNumber, new GroupRepository.GroupCallback() {
            @Override
            public void onSuccess(Group group) {
                runOnUiThread(() -> {
                    binding.tvGroupName.setText(group.getGroupName());
                    if (!TextUtils.isEmpty(group.getGroupPicturePath())) {
                        Glide.with(GroupDetailActivity.this)
                                .load(group.getGroupPicturePath())
                                .into(binding.bannerImage);
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                runOnUiThread(() -> {
                    Toast.makeText(
                            GroupDetailActivity.this,
                            "그룹 정보를 불러오는데 실패했습니다: " + throwable.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }
        });
    }

    private void loadRecentPosts() {
        groupRepository.getGroupRecentPosts(groupNumber, new GroupRepository.GroupPostsCallback() {
            @Override
            public void onSuccess(List<GroupRecentPostsDto> posts) {
                runOnUiThread(() -> {
                    recentUploadAdapter.setPosts(posts);
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                runOnUiThread(() -> {
                    Toast.makeText(GroupDetailActivity.this,
                            "최근 게시물을 불러오는데 실패했습니다: " + throwable.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private static class RecentUploadAdapter extends RecyclerView.Adapter<RecentUploadAdapter.ViewHolder> {
        private List<GroupRecentPostsDto> posts = new ArrayList<>();

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recent_upload, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            GroupRecentPostsDto post = posts.get(position);
            if (!TextUtils.isEmpty(post.getPicturePath())) {
                Glide.with(holder.itemView.getContext())
                        .load(post.getPicturePath())
                        .centerCrop()
                        .into(holder.imageView);
            }
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        public void setPosts(List<GroupRecentPostsDto> posts) {
            this.posts = posts;
            notifyDataSetChanged();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            ViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.imageView);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}