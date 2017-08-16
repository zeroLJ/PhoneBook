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

public class CallListAdapter extends RecyclerView.Adapter{
    private ArrayList<ContactInfo> contactInfoList;
    private Context context;
    public static String CONTACTID = "contactId";
    public static int TYPE_CONTACT = 1001;
    public static int TYPE_LETTER = 1002;

    public CallListAdapter(Context context, ArrayList<ContactInfo> contactInfoList){
        this.context = context;
        this.contactInfoList = contactInfoList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_CONTACT){
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_call_list, parent,
                false));
        }
        if (viewType == TYPE_LETTER){
            return new LetterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_call_list_letter, parent,
                    false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ContactInfo contactInfo = contactInfoList.get(position);
        if(holder.getItemViewType() == TYPE_CONTACT){
            ((MyViewHolder)holder).name_tv.setText(contactInfo.name);
            final Long contactId = contactInfo.contactId;
            ((MyViewHolder)holder).name_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ContactActivity.class);
                    //传递此id用于查询与刷新该联系人的信息
                    intent.putExtra(CONTACTID, contactId);
                    context.startActivity(intent);
                }
            });
        }

        if (holder.getItemViewType() == TYPE_LETTER){
            ((LetterViewHolder)holder).letter_tv.setText(contactInfo.letter);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = contactInfoList.get(position).getType();
        return type;
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

    class LetterViewHolder extends RecyclerView.ViewHolder{
        TextView letter_tv;
        public LetterViewHolder(View view) {
            super(view);
            letter_tv = (TextView) view.findViewById(R.id.letter_item_call_list);
        }
    }

}

