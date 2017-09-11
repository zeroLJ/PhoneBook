package com.zero.phonebookdemo.callLog;

/**
 * Created by zero on 2017/8/11.
 */

public class CallLogInfo {
    public static String CALLIN = "呼入";
    public static String CALLOUT = "呼出";
    public static String CAllMISS = "未接";
    String callName;    //通话名字
    String callNumber;  //通话号码
    String callType;    //通话类型
    String callDate;    //拨打时间
    String callDuration;//通话时长
    CallLogInfo(String callName, String callNumber, String callType, String callDate, String callDuration){
        this.callName = callName;
        this.callNumber = callNumber;
        this.callType = callType;
        this.callDate = callDate;
        this.callDuration = callDuration;
    }
}
