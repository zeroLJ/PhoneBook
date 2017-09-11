package com.zero.phonebookdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zero.phonebookdemo.callList.CallListFragment;
import com.zero.phonebookdemo.callLog.CallLogFragment;
import com.zero.phonebookdemo.callOut.CallOutFragment;
import com.zero.phonebookdemo.view.CustomViewPager;

/**
 * create by zero on 2017/8/10.
 * 打电话主页面
 */

public class CallActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout callLog_layout, callOut_layout, callList_layout;
    private LinearLayout addContact_layout;
    private CustomViewPager viewPager;
    private final String TAG = CallActivity.class.getCanonicalName();
    private final int addContactRequestCode = 100;
    private ImageView callOut_iv, callLog_iv, callList_iv;
    private TextView callOut_tv, callLog_tv, callList_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        initView();
        setListener();
    }


    private void initView() {
        callLog_layout = (LinearLayout) findViewById(R.id.layout_callLog_activity_call);
        callOut_layout = (LinearLayout) findViewById(R.id.layout_callOut_activity_call);
        callList_layout = (LinearLayout) findViewById(R.id.layout_callList_activity_call);
        addContact_layout = (LinearLayout) findViewById(R.id.layout_add_contact_activity_call);
        callLog_iv = (ImageView) findViewById(R.id.callLog_iv_activity_call);
        callOut_iv = (ImageView) findViewById(R.id.callOut_iv_activity_call);
        callList_iv = (ImageView) findViewById(R.id.callList_iv_activity_call);
        callLog_tv = (TextView) findViewById(R.id.callLog_tv_activity_call);
        callOut_tv = (TextView) findViewById(R.id.callOut_tv_activity_call);
        callList_tv = (TextView) findViewById(R.id.callList_tv_activity_call);

        viewPager = (CustomViewPager) findViewById(R.id.viewpager_activity_call);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return CallLogFragment.newInstance();
                    case 1:
                        return CallOutFragment.newInstance();
                    case 2:
                        return CallListFragment.newInstance();
                }
                return null;
            }
            @Override
            public int getCount() {
                return 3;
            }
        });
        setCurrentPage(0);
    }

    private void setListener() {
        callLog_layout.setOnClickListener(this);
        callOut_layout.setOnClickListener(this);
        callList_layout.setOnClickListener(this);
        addContact_layout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_callLog_activity_call:
                setCurrentPage(0);
                break;
            case R.id.layout_callOut_activity_call:
                setCurrentPage(1);
                break;
            case R.id.layout_callList_activity_call:
                setCurrentPage(2);
                break;
            case R.id.layout_add_contact_activity_call:
                //打开系统的新建联系人页面
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType("vnd.android.cursor.dir/person");
                intent.setType("vnd.android.cursor.dir/contact");
                intent.setType("vnd.android.cursor.dir/raw_contact");
//                startActivity(intent);
                startActivityForResult(intent,addContactRequestCode);
                break;
        }
    }

    private void setCurrentPage(int i){
        switch (i){
            case 0:
                addContact_layout.setVisibility(View.GONE);
                callLog_iv.setImageResource(R.drawable.call_log_on);
                callOut_iv.setImageResource(R.drawable.call_out_off);
                callList_iv.setImageResource(R.drawable.call_list_off);
                callLog_tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                callOut_tv.setTextColor(getResources().getColor(R.color.colorNavMenuTextColor));
                callList_tv.setTextColor(getResources().getColor(R.color.colorNavMenuTextColor));
                break;
            case 1:
                addContact_layout.setVisibility(View.GONE);
                callLog_iv.setImageResource(R.drawable.call_log_off);
                callOut_iv.setImageResource(R.drawable.call_out_on);
                callList_iv.setImageResource(R.drawable.call_list_off);
                callLog_tv.setTextColor(getResources().getColor(R.color.colorNavMenuTextColor));
                callOut_tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                callList_tv.setTextColor(getResources().getColor(R.color.colorNavMenuTextColor));
                break;
            case 2:
                addContact_layout.setVisibility(View.VISIBLE);
                callLog_iv.setImageResource(R.drawable.call_log_off);
                callOut_iv.setImageResource(R.drawable.call_out_off);
                callList_iv.setImageResource(R.drawable.call_list_on);
                callLog_tv.setTextColor(getResources().getColor(R.color.colorNavMenuTextColor));
                callOut_tv.setTextColor(getResources().getColor(R.color.colorNavMenuTextColor));
                callList_tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
        }
        viewPager.setCurrentItem(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("ssss","requestCode:"+ requestCode);
        Log.i("ssss","resultCode:"+ resultCode);
        Log.i("ssss","data:"+ data);
        if (requestCode == addContactRequestCode){
            CallListFragment.isRefresh = true;
        }
    }
}



