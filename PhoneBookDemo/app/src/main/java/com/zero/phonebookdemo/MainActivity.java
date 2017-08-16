package com.zero.phonebookdemo;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
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
                switch (position) {
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

    public void addCallLog(View view) {
        insertCallLog("13711330352","10","3","1");
    }

    public void addNewContact(View view) {
        addContact();// 直接跳转到系统的新建联系人页面
//        Util.addContact(this,"刘佳佳","13711330352");// 代码直接插入
    }

    /**
     * 插入一条通话记录
     * @param number 通话号码
     * @param duration 通话时长（响铃时长）以秒为单位 1分30秒则输入90
     * @param type  通话类型  1呼入 2呼出 3未接
     * @param isNew 是否已查看    0已看1未看
     */
    private void insertCallLog(String number, String duration, String type, String isNew) {
        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.NUMBER, number);
        values.put(CallLog.Calls.DATE, System.currentTimeMillis() );
        values.put(CallLog.Calls.DURATION, duration);
        values.put(CallLog.Calls.TYPE, type);
        values.put(CallLog.Calls.NEW, isNew);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALL_LOG}, 1000);
        }
        getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
    }


    // 直接跳转到系统的新建联系人页面
    public void addContact() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.dir/person");
        intent.setType("vnd.android.cursor.dir/contact");
        intent.setType("vnd.android.cursor.dir/raw_contact");
        startActivity(intent);
    }
}




