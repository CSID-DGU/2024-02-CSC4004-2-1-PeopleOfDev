package com.example.momentup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AlarmActivity extends AppCompatActivity {

    boolean editModel = false;
    private AlarmRecyclerAdapter alarmRecyclerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            // 복귀 화살표 표시
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(" 각 그룹에 대한 알림 시간 설정");
        }

        TextView textView = findViewById(R.id.edit_alarm);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editModel = !editModel;
                if (editModel) {
                    textView.setText("확인하다");
                } else {
                    textView.setText("개정하다");
                }
                alarmRecyclerAdapter.setEditModel(editModel);
            }
        });

        List<AlarmBean> dataList = new ArrayList<>();
        dataList.add(new AlarmBean("모두", "08:30", "12:30", true));
        dataList.add(new AlarmBean("홍야지의 스포츠 일기", "12:30", "17:30", true));
        dataList.add(new AlarmBean("김종국에 대한 연구", "08:30", "12:30", true));
        dataList.add(new AlarmBean("백종원의 쿠킹클래스", "08:30", "12:30", false));
        alarmRecyclerAdapter = new AlarmRecyclerAdapter(dataList, this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(alarmRecyclerAdapter);
    }

    static class AlarmRecyclerAdapter extends RecyclerView.Adapter<AlarmRecyclerAdapter.AlarmViewHolder> {

        private final List<AlarmBean> dataList;
        private final Context context;
        private boolean editModel;

        public void setEditModel(boolean edit) {
            editModel = edit;
            notifyDataSetChanged();
        }

        public AlarmRecyclerAdapter(List<AlarmBean> dataList, Context context) {
            this.dataList = dataList;
            this.context = context;
        }

        @NonNull
        @Override
        public AlarmRecyclerAdapter.AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(context).inflate(R.layout.alarm_recycler_item, parent, false);
            return new AlarmRecyclerAdapter.AlarmViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull AlarmRecyclerAdapter.AlarmViewHolder holder, @SuppressLint("RecyclerView") int position) {
            AlarmBean notificationBean = dataList.get(position);
            holder.bind(notificationBean, editModel);
            holder.startTimeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editModel) {
                        showTimePickerDialog(position, true);
                    }
                }
            });
            holder.aSwitch.setOnCheckedChangeListener(null);
            holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isPressed()) {
                        AlarmBean alarmBean = dataList.get(position);
                        alarmBean.active = isChecked;
                        notifyItemChanged(position);
                    }
                }
            });
            holder.endTimeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editModel) {
                        showTimePickerDialog(position, false);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        private void showTimePickerDialog(int position, boolean start) {
            TimePickerSelectDialog dialog = new TimePickerSelectDialog(context);
            dialog.show();

            // 확인 버튼 클릭 시 시간 설정
            dialog.setConfirmClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String timeText = String.format(Locale.getDefault(), "%02d:%02d",
                            dialog.getHour(),
                            dialog.getMinute());
                    AlarmBean alarmBean = dataList.get(position);
                    if (start) {
                        alarmBean.startTime = timeText;
                    } else {
                        alarmBean.endTime = timeText;
                    }
                    notifyItemChanged(position);
                    dialog.dismiss();
                }
            });
        }

        class AlarmViewHolder extends RecyclerView.ViewHolder {
            private TextView titleTv;
            private Switch aSwitch;
            private TextView startTimeTv;
            private TextView endTimeTv;
            private ImageView deleteImg;
            private LinearLayout root;

            public AlarmViewHolder(@NonNull View itemView) {
                super(itemView);
                root = itemView.findViewById(R.id.root);
                titleTv = itemView.findViewById(R.id.title);
                startTimeTv = itemView.findViewById(R.id.start_time);
                endTimeTv = itemView.findViewById(R.id.end_time);
                deleteImg = itemView.findViewById(R.id.delete);
                aSwitch = itemView.findViewById(R.id.switch_controller);
            }

            public void bind(AlarmBean item, boolean edit) {
                titleTv.setText(item.getTitle());
                startTimeTv.setText(item.getStartTime());
                endTimeTv.setText(item.getEndTime());

                if (edit) {
                    startTimeTv.setPaintFlags(startTimeTv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    endTimeTv.setPaintFlags(endTimeTv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                } else {
                    startTimeTv.setPaintFlags(startTimeTv.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                    endTimeTv.setPaintFlags(endTimeTv.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                }

                aSwitch.setChecked(item.active);

                if (item.active) {
                    root.setBackgroundColor(Color.parseColor("#16222F"));
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#FE8316"));
                    aSwitch.setTrackTintList(colorStateList);
                } else {
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#CCD3DB"));
                    aSwitch.setTrackTintList(colorStateList);
                    root.setBackgroundColor(Color.parseColor("#7D8896"));
                }
            }
        }
    }

    private static class AlarmBean {
        String title;
        String startTime;
        String endTime;
        boolean active;

        public AlarmBean(String title, String startTime, String endTime, boolean active) {
            this.title = title;
            this.startTime = startTime;
            this.endTime = endTime;
            this.active = active;
        }

        public String getTitle() {
            return title;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public boolean isActive() {
            return active;
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
