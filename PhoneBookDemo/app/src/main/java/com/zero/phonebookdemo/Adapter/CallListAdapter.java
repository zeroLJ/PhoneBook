package com.zero.phonebookdemo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zero.phonebookdemo.ContactActivity;
import com.zero.phonebookdemo.InfoClass.ContactInfo;
import com.zero.phonebookdemo.R;

import java.util.ArrayList;

/**
 * Created by zero on 2017/8/10.
 */

public class CallListAdapter extends RecyclerView.Adapter<CallListAdapter.MyViewHolder>{
    private ArrayList<ContactInfo> contactInfoList;
    private Context context;
    public static String CONTACTID = "contactId";

    public CallListAdapter(Context context, ArrayList<ContactInfo> contactInfoList){
        this.context = context;
        this.contactInfoList = contactInfoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_call_list, parent,
                false));
        return holder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ContactInfo contactInfo = contactInfoList.get(position);
        if (contactInfo.letter!=null && !contactInfo.letter.equals("")){
            holder.name_tv.setText(contactInfo.letter);
            holder.name_tv.setOnClickListener(null);
        }else {
            holder.name_tv.setText(contactInfo.name);
            final Long contactId = contactInfo.contactId;
            holder.name_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ContactActivity.class);
                    //传递此id用于查询与刷新该联系人的信息
                    intent.putExtra(CONTACTID, contactId);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return contactInfoList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name_tv;
        public MyViewHolder(View view) {
            super(view);
            name_tv = (TextView) view.findViewById(R.id.name_item_call_list);
        }
    }

}

