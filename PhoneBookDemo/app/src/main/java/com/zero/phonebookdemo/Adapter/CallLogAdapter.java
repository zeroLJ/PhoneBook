package com.zero.phonebookdemo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zero.phonebookdemo.InfoClass.CallLogInfo;
import com.zero.phonebookdemo.R;

import java.util.ArrayList;

/**
 * Created by WatchVoice04 on 2017/8/11.
 */

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder> {
    ArrayList<CallLogInfo> callLogInfoList;
    Context context;
    public CallLogAdapter(Context context, ArrayList<CallLogInfo> callLogInfoList){
        this.context = context;
        this.callLogInfoList = callLogInfoList;
    }

    @Override
    public CallLogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CallLogViewHolder holder = new CallLogViewHolder(LayoutInflater.from(context).inflate(R.layout.item_call_log, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(CallLogViewHolder holder, int position) {
        CallLogInfo callLogInfo = callLogInfoList.get(position);
        if(callLogInfo.callName != null){
            holder.name_tv.setText(callLogInfo.callName);
            holder.number_tv.setText(callLogInfo.callNumber);
        }else {
            holder.name_tv.setText(callLogInfo.callNumber);
            holder.number_tv.setText("");
        }
        holder.date_tv.setText(callLogInfo.callDate);
        holder.duration_tv.setText(callLogInfo.callDuration);
        if (callLogInfo.callType.equals(CallLogInfo.CALLIN)){
            holder.type_iv.setImageResource(R.drawable.incoming_call);
        }else if(callLogInfo.callType.equals(CallLogInfo.CALLOUT)){
            holder.type_iv.setImageResource(R.drawable.outgoing_call);
        }else if(callLogInfo.callType.equals(CallLogInfo.CAllMISS)){
            holder.type_iv.setImageResource(R.drawable.missed_call);
        }
    }

    @Override
    public int getItemCount() {
        return callLogInfoList.size();
    }

    class CallLogViewHolder extends RecyclerView.ViewHolder {
        TextView name_tv, number_tv, date_tv, duration_tv;
        ImageView type_iv;
        public CallLogViewHolder(View view) {
            super(view);
            name_tv = (TextView) view.findViewById(R.id.name_item_call_log);
            number_tv = (TextView) view.findViewById(R.id.number_item_call_log);
            date_tv = (TextView) view.findViewById(R.id.date_item_call_log);
            duration_tv = (TextView) view.findViewById(R.id.duration_item_call_log);
            type_iv = (ImageView) view.findViewById(R.id.type_item_call_log);
        }
    }
}
