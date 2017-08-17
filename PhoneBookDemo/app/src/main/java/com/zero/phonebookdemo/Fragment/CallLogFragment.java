package com.zero.phonebookdemo.Fragment;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zero.phonebookdemo.Adapter.CallLogAdapter;
import com.zero.phonebookdemo.InfoClass.CallLogInfo;
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
 */
public class CallLogFragment extends Fragment {
    ArrayList<CallLogInfo> callLogInfoList = new ArrayList<>();
    RecyclerView recyclerView;
    CallLogAdapter adapter;
    private PtrClassicFrameLayout mPtrFrame;

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
        View view = inflater.inflate(R.layout.fragment_call_log, container, false);
        //获取通话记录
        callLogInfoList = getCallHistoryList(getActivity(), 50);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_callLog);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置adapter
        adapter = new CallLogAdapter(getContext(), callLogInfoList);
        recyclerView.setAdapter(adapter);

        mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.rotate_header_list_view);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPtrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //在这写刷新要完成的代码
                        callLogInfoList.clear();
                        callLogInfoList = getCallHistoryList(getActivity(), 50);
                        adapter = new CallLogAdapter(getContext(), callLogInfoList);
                        recyclerView.setAdapter(adapter);
                        mPtrFrame.refreshComplete();//停止刷新效果
                    }
                }, 0);
            }
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        // default is false
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        return view;
    }

    public static CallLogFragment newInstance() {
        CallLogFragment fragment = new CallLogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 利用系统CallLog获取通话历史记录
     * @param activity
     * @param num      要读取记录的数量
     * @return
     */
    public ArrayList<CallLogInfo> getCallHistoryList(Activity activity, int num) {
        ArrayList<CallLogInfo> callLogInfoList = new ArrayList<>();
        Cursor cs;
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
        if (cs != null && cs.getCount() > 0) {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date_today = simpleDateFormat.format(date);
            for (cs.moveToFirst(); (!cs.isAfterLast()) && i < num; cs.moveToNext(), i++) {
                String callName = cs.getString(0);
                String callNumber = cs.getString(1);
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
                        callTypeStr = CallLogInfo.CALLOUT;
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
        }
        return callLogInfoList;
    }


}
