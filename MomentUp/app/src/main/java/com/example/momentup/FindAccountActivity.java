package com.example.momentup;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class FindAccountActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TextView tabFindId;
    private TextView tabFindPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_account);

        // Initialize views
        viewPager = findViewById(R.id.viewPager);
        tabFindId = findViewById(R.id.tab_find_id);
        tabFindPassword = findViewById(R.id.tab_find_password);
        ImageButton btnBack = findViewById(R.id.btn_back);

        // Set up ViewPager
        FindAccountPagerAdapter pagerAdapter = new FindAccountPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Disable swipe
        viewPager.setUserInputEnabled(false);

        // Set initial state
        tabFindId.setSelected(true);
        tabFindPassword.setSelected(false);

        // Set click listeners
        btnBack.setOnClickListener(v -> finish());

        tabFindId.setOnClickListener(v -> {
            viewPager.setCurrentItem(0, true);
            updateTabs(0);
        });

        tabFindPassword.setOnClickListener(v -> {
            viewPager.setCurrentItem(1, true);
            updateTabs(1);
        });

        // ViewPager page change callback
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateTabs(position);
            }
        });
    }

    private void updateTabs(int position) {
        tabFindId.setSelected(position == 0);
        tabFindPassword.setSelected(position == 1);
    }
}