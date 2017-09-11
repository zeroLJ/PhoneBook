package com.zero.phonebookdemo.callLog;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zero.phonebookdemo.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * create by zero on 2017/8/10.
 * 通话记录页面
 */
public class CallLogFragment extends Fragment {
    //判断是否打完点换后返回的页面，若是则刷新通话记录
    public static boolean isRefresh = false;

    private ArrayList<CallLogInfo> callLogInfoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CallLogAdapter adapter;
    private PtrClassicFrameLayout mPtrFrame;
    private ProgressBar progressBar;
    private TextView tips_tv;

    private boolean isFirstInit = true;
    private View view;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter = new CallLogAdapter(getContext(), callLogInfoList);
            recyclerView.setAdapter(adapter);
            if (mPtrFrame.isRefreshing()){
                mPtrFrame.refreshComplete();//停止刷新效果
            }
            progressBar.setVisibility(View.GONE);
            if (callLogInfoList.size()==0){
                tips_tv.setVisibility(View.VISIBLE);
            }
        }
    };

    public CallLogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (isFirstInit){
            isFirstInit = false;
            view = inflater.inflate(R.layout.fragment_call_log, container, false);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar_call_log);
            tips_tv = (TextView) view.findViewById(R.id.tips_tv_call_log);
            setTips();
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_callLog);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            //设置Item增加、移除动画
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            //设置adapter
            adapter = new CallLogAdapter(getContext(), callLogInfoList);
            recyclerView.setAdapter(adapter);

            //获取通话记录
            getCallHistoryList(getActivity(),50);

            mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.rotate_header_list_view);
            mPtrFrame.setLastUpdateTimeRelateObject(this);
            mPtrFrame.setPtrHandler(new PtrHandler() {
                @Override
                public void onRefreshBegin(PtrFrameLayout frame) {
                    tips_tv.setVisibility(View.GONE);
                    mPtrFrame.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //在这写刷新要完成的代码
                            getCallHistoryList(getActivity(),50);
                        }
                    }, 0);
                }
                @Override
                public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                    return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                }
            });
        }
        return view;
    }

    public static CallLogFragment newInstance() {
        CallLogFragment fragment = new CallLogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void setTips(){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String s1 = "没有通话记录\n请打开";
        builder.append(s1);
        String s2 = "系统设置->应用管理->音书->权限管理";
        builder.append(s2);
        builder.append("检查是否已经开启了读取通话记录权限");
        builder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.colorPrimary));       //设置文件颜色
                ds.setUnderlineText(false);      //设置下划线
            }
        }, s1.length(), s1.length() + s2.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tips_tv.setText(builder);
        tips_tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 利用系统CallLog获取通话历史记录
     * @param activity
     * @param num      要读取记录的数量
     * @return
     */
    public void getCallHistoryList(final Activity activity, final int num) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<CallLogInfo> callLogInfoList = new ArrayList<>();
                Cursor cs = null;
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.READ_CALL_LOG}, 1000);

                }
                cs = activity.getContentResolver().query(CallLog.Calls.CONTENT_URI, //系统方式获取通讯录存储地址
                        new String[]{
                                CallLog.Calls.CACHED_NAME,  //姓名
                                CallLog.Calls.NUMBER,    //号码
                                CallLog.Calls.TYPE,  //呼入/呼出(2)/未接
                                CallLog.Calls.DATE,  //拨打时间
                                CallLog.Calls.DURATION,   //通话时长
                        }, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
                int i = 0;
                Log.i("ssss",""+cs.getCount());
                if (cs != null && cs.getCount() > 0) {
                    Date date = new Date(System.currentTimeMillis());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String date_today = simpleDateFormat.format(date);
                    for (cs.moveToFirst(); (!cs.isAfterLast()) && i < num; cs.moveToNext(), i++) {
                        String callName = cs.getString(0);
                        String callNumber = cs.getString(1);
                        //如果名字为空，在通讯录查询一次有没有对应联系人
                        if (callName == null || callName.equals("")){
                            String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME};
                            //设置查询条件
                            String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + "='"+callNumber+"'";
                            Cursor cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    cols, selection, null, null);
                            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                            if (cursor.getCount()>0){
                                cursor.moveToFirst();
                                callName = cursor.getString(nameFieldColumnIndex);
                            }
                            cursor.close();
                        }
                        //通话类型
                        int callType = Integer.parseInt(cs.getString(2));
                        String callTypeStr = "";
                        switch (callType) {
                            case CallLog.Calls.INCOMING_TYPE:
                                callTypeStr = CallLogInfo.CALLIN;
                                break;
                            case CallLog.Calls.OUTGOING_TYPE:
                                callTypeStr = CallLogInfo.CALLOUT;
                                break;
                            case CallLog.Calls.MISSED_TYPE:
                                callTypeStr = CallLogInfo.CAllMISS;
                                break;
                            default:
                                //其他类型的，例如新增号码等记录不算进通话记录里，直接跳过
//                                Log.i("ssss",""+callType);
                                i--;
                                continue;
                        }
                        //拨打时间
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date callDate = new Date(Long.parseLong(cs.getString(3)));
                        String callDateStr = sdf.format(callDate);
                        if (callDateStr.equals(date_today)) { //判断是否为今天
                            sdf = new SimpleDateFormat("HH:mm");
                            callDateStr = sdf.format(callDate);
                        } else if (date_today.contains(callDateStr.substring(0, 7))) { //判断是否为当月
                            sdf = new SimpleDateFormat("dd");
                            int callDay = Integer.valueOf(sdf.format(callDate));

                            int day = Integer.valueOf(sdf.format(date));
                            if (day - callDay == 1) {
                                callDateStr = "昨天";
                            } else {
                                sdf = new SimpleDateFormat("MM-dd");
                                callDateStr = sdf.format(callDate);
                            }
                        } else if (date_today.contains(callDateStr.substring(0, 4))) { //判断是否为当年
                            sdf = new SimpleDateFormat("MM-dd");
                            callDateStr = sdf.format(callDate);
                        }

                        //通话时长
                        int callDuration = Integer.parseInt(cs.getString(4));
                        int min = callDuration / 60;
                        int sec = callDuration % 60;
                        String callDurationStr = "";
                        if (sec > 0) {
                            if (min > 0) {
                                callDurationStr = min + "分" + sec + "秒";
                            } else {
                                callDurationStr = sec + "秒";
                            }
                        }

                        CallLogInfo callLogInfo = new CallLogInfo(callName, callNumber, callTypeStr, callDateStr, callDurationStr);
                        callLogInfoList.add(callLogInfo);
                    }
                    cs.close();
                    CallLogFragment.this.callLogInfoList = callLogInfoList;
                }
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        //只有从新建联系人页面返回时才执行
        if (isRefresh){
            Log.i("ssss","resume");
            isRefresh = false;
            mPtrFrame.autoRefresh();
        }
    }
}
