package com.zero.phonebookdemo.callList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zero.phonebookdemo.R;
import com.zero.phonebookdemo.util.PhonenumberUtil;

import java.util.List;

/**
 * Created by zero on 2017/8/29.
 */

public class ContactDialogAdapter extends RecyclerView.Adapter<ContactDialogAdapter.ContactViewHolder>{
    private List<String> phoneNumList;
    private Context context;

    public ContactDialogAdapter(Context context, List<String> phoneNumList){
        this.context = context;
        this.phoneNumList = phoneNumList;
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(context).inflate(R.layout.item_contact_num, parent,
                false));
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        final String num = phoneNumList.get(position);
        holder.count_tv.setText("号码"+(position+1));
        holder.number_vt.setText(num);
        holder.callOut_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhonenumberUtil.isCallAbleNum(num)){
//                    Intent intent = new Intent(context, CallOnActivity.class);
//                    intent.putExtra("num", num);
//                    context.startActivity(intent);
                }else {
                    Toast.makeText(context, "请检查是否为正确的手机号码，若拨打固话请加上区号!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return phoneNumList.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder{
        TextView count_tv, number_vt;
        ImageView callOut_iv;
        public ContactViewHolder(View itemView) {
            super(itemView);
            count_tv = (TextView) itemView.findViewById(R.id.count_item_contact_num);
            number_vt = (TextView) itemView.findViewById(R.id.number_item_contact_num);
            callOut_iv = (ImageView) itemView.findViewById(R.id.callOut_iv_item_contact_num);
        }
    }
}
