package com.example.momentup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FindAccountPagerAdapter extends FragmentStateAdapter {

    public FindAccountPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new FindIdFragment() : new FindPasswordFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
