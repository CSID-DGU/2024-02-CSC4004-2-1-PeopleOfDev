package com.example.momentup;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            // 복귀 화살표 표시
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle("咎引");
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<NotificationBean> dataList = new ArrayList<>();
        dataList.add(new NotificationBean("관심을 신청하세요", "Park Happy 님 팔로우 요청드립니다.", true));
        dataList.add(new NotificationBean("관심을 신청하세요", "Park Happy 님 팔로우 요청드립니다.", true));
        dataList.add(new NotificationBean("김동구", "지금 당신의 순간을 업로드하세요!", false));
        dataList.add(new NotificationBean("관심을 신청하세요", "Park Happy 님 팔로우 요청드립니다.", true));
        NotificationRecyclerAdapter notificationRecyclerAdapter = new NotificationRecyclerAdapter(dataList, this);
        recyclerView.setAdapter(notificationRecyclerAdapter);
    }

    static class NotificationRecyclerAdapter extends RecyclerView.Adapter<NotificationRecyclerAdapter.NotificationViewHolder> {

        private final List<NotificationBean> dataList;
        private final Context context;

        public NotificationRecyclerAdapter(List<NotificationBean> dataList, Context context) {
            this.dataList = dataList;
            this.context = context;
        }

        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(context).inflate(R.layout.notification_recycler_item, parent, false);
            return new NotificationViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
            NotificationBean notificationBean = dataList.get(position);
            holder.bind(notificationBean);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class NotificationViewHolder extends RecyclerView.ViewHolder {
            private TextView titleTv;
            private TextView contentTv;
            private LinearLayout optBtnLl;
            private ImageView headImg;
            private Button acceptBtn;
            private Button refuseBtn;

            public NotificationViewHolder(@NonNull View itemView) {
                super(itemView);
                titleTv = itemView.findViewById(R.id.title);
                headImg = itemView.findViewById(R.id.head_img);
                contentTv = itemView.findViewById(R.id.content);
                optBtnLl = itemView.findViewById(R.id.opt_btn_ll);
                acceptBtn = itemView.findViewById(R.id.accept);
                refuseBtn = itemView.findViewById(R.id.refuse);
            }

            public void bind(NotificationBean item) {
                titleTv.setText(item.getTitle());
                contentTv.setText(item.getContent());
                if (item.applyMessage) {
                    headImg.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.notification_gray_bg));
                    optBtnLl.setVisibility(View.VISIBLE);
                    acceptBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(itemView.getContext(), "accept", Toast.LENGTH_SHORT).show();
                        }
                    });
                    refuseBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(itemView.getContext(), "refuse", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    headImg.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.camera));
                    optBtnLl.setVisibility(View.GONE);
                }
            }
        }
    }

    private static class NotificationBean {
        String title;
        String content;
        private boolean applyMessage;

        public NotificationBean(String title, String content, boolean applyMessage) {
            this.title = title;
            this.content = content;
            this.applyMessage = applyMessage;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
