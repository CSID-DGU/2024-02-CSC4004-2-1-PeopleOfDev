package com.example.momentup;

import static android.content.ContentValues.TAG;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerGroups;
    private GroupAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FCM 토큰 가져오기
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "토큰 가져오기 실패", task.getException());
                            return;
                        }

                        String token = task.getResult();
                        Log.d(TAG, "FCM 토큰: " + token);
                        // TODO: 서버에 토큰 전송
                    }
                });

        // 알림 권한 요청 (Android 13 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission();
        }

        // Initialize RecyclerView
        recyclerGroups = findViewById(R.id.recycler_groups);
        recyclerGroups.setLayoutManager(new LinearLayoutManager(this));

        // Create and set adapter
        groupAdapter = new GroupAdapter(createSampleGroups());
        recyclerGroups.setAdapter(groupAdapter);

        // Setup bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Already on home
                return true;
            } else if (itemId == R.id.nav_calendar) {
                // Navigate to calendar
                return true;
            } else if (itemId == R.id.nav_camera) {
                // Handle camera
                return true;
            } else if (itemId == R.id.nav_challenges) {
                // Handle bookmark
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Handle profile
                return true;
            }
            return false;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    1
            );
        }
    }

    private List<GroupItem> createSampleGroups() {
        List<GroupItem> groups = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            groups.add(new GroupItem(
                    "그룹명 " + i,
                    "30분 전 업데이트"
            ));
        }
        return groups;
    }
}

// Group Item Data Class
class GroupItem {
    String name;
    String description;

    GroupItem(String name, String description) {
        this.name = name;
        this.description = description;
    }
}

// Group Adapter
class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private List<GroupItem> groups;

    GroupAdapter(List<GroupItem> groups) {
        this.groups = groups;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        GroupItem group = groups.get(position);
        holder.nameText.setText(group.name);
        holder.descText.setText(group.description);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView descText;

        GroupViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_group_name);
            descText = itemView.findViewById(R.id.text_group_desc);
        }
    }
}
