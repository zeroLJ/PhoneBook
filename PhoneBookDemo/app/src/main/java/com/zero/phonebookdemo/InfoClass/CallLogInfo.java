package com.zero.phonebookdemo.InfoClass;

/**
 * Created by zero on 2017/8/11.
 */

public class CallLogInfo {
    public static String CALLIN = "呼入";
    public static String CALLOUT = "呼出";
    public static String CAllMISS = "未接";
    public String callName;    //通话名字
    public String callNumber;  //通话号码
    public String callType;    //通话类型
    public String callDate;    //拨打时间
    public String callDuration;//通话时长
    public CallLogInfo(String callName, String callNumber, String callType, String callDate, String callDuration){
        this.callName = callName;
        this.callNumber = callNumber;
        this.callType = callType;
        this.callDate = callDate;
        this.callDuration = callDuration;
    }
}
