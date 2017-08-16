package com.zero.phonebookdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zero.phonebookdemo.Fragment.CallListFragment;
import com.zero.phonebookdemo.Fragment.CallLogFragment;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return CallLogFragment.newInstance();
                    case 1:
                        return CallListFragment.newInstance();
                }
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
    }

    public void callLog(View view) {
        viewPager.setCurrentItem(0);
    }

    public void callList(View view) {
        viewPager.setCurrentItem(1);
    }
}
