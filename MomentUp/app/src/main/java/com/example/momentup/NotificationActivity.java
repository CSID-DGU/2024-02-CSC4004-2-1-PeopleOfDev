package com.example.momentup;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private RecyclerView recyclerNotifications;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Initialize views
        initViews();

        // Set up back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 액티비티 종료
            }
        });

        // Set up RecyclerView
        setupRecyclerView();

        // Load notifications
        loadNotifications();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        recyclerNotifications = findViewById(R.id.recycler_notifications);
    }

    private void setupRecyclerView() {
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotificationAdapter(new NotificationAdapter.OnNotificationClickListener() {
            @Override
            public void onNotificationClick(NotificationItem notification, int position) {
                // 알림 클릭 시 처리
                // 예: 상세 페이지로 이동하거나 처리하는 코드
            }
        });

        recyclerNotifications.setAdapter(adapter);
    }

    private void loadNotifications() {
        // 테스트용 더미 데이터 생성
        List<NotificationItem> notifications = new ArrayList<>();

        notifications.add(new NotificationItem(
                "새로운 챌린지",
                "새로운 챌린지가 등록되었습니다.",
                "방금 전",
                R.drawable.ic_notification_mono
        ));

        notifications.add(new NotificationItem(
                "목표 달성",
                "오늘의 목표를 달성했습니다! 축하합니다.",
                "1시간 전",
                R.drawable.ic_notification_mono
        ));

        notifications.add(new NotificationItem(
                "친구 소식",
                "김민수님이 회원님의 게시물을 좋아합니다.",
                "2시간 전",
                R.drawable.ic_notification_mono
        ));

        notifications.add(new NotificationItem(
                "챌린지 알림",
                "진행 중인 챌린지가 곧 종료됩니다.",
                "어제",
                R.drawable.ic_notification_mono
        ));

        // 어댑터에 데이터 설정
        adapter.setNotifications(notifications);
    }
}