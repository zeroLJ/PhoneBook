package com.zero.phonebookdemo.callList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zero.phonebookdemo.R;

import java.util.ArrayList;

/**
 * Created by zero on 2017/8/10.
 */

public class CallListAdapter extends RecyclerView.Adapter {
    private ArrayList<ContactInfo> contactInfoList;
    private Context context;
    public static String CONTACTID = "contactId";
    public static final int TYPE_CONTACT = 1001;
    public static final int TYPE_LETTER = 1002;

    CallListAdapter(Context context, ArrayList<ContactInfo> contactInfoList) {
        this.context = context;
        this.contactInfoList = contactInfoList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CONTACT) {
            return new ContactViewHolder(LayoutInflater.from(context).inflate(R.layout.item_call_list, parent,
                    false));
        } else if (viewType == TYPE_LETTER) {
            return new LetterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_call_list_letter, parent,
                    false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ContactInfo contactInfo = contactInfoList.get(position);
        if (holder.getItemViewType() == TYPE_CONTACT) {
            ((ContactViewHolder) holder).name_tv.setText(contactInfo.name);
            final Long contactId = contactInfo.contactId;
            ((ContactViewHolder) holder).name_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(context, ContactActivity.class);
//                    //传递此id用于查询与刷新该联系人的信息
//                    intent.putExtra(CONTACTID, contactId);
//                    context.startActivity(intent);

                    showContactDialog(contactId);
                }
            });
        } else if (holder.getItemViewType() == TYPE_LETTER) {
            ((LetterViewHolder) holder).letter_tv.setText(contactInfo.letter);
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

    class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView name_tv;

        public ContactViewHolder(View view) {
            super(view);
            name_tv = (TextView) view.findViewById(R.id.name_item_call_list);
        }

    }

    class LetterViewHolder extends RecyclerView.ViewHolder {
        TextView letter_tv;

        public LetterViewHolder(View view) {
            super(view);
            letter_tv = (TextView) view.findViewById(R.id.letter_item_call_list);
        }
    }

    private void showContactDialog(final Long contactId){
        String name = null;//联系人名称
        ArrayList<String> phoneNum = new ArrayList<>();//联系人手机号列表

        String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        //设置查询条件
        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "='"+contactId+"'";
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                cols, selection, null, null);
        int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
        int numberFieldColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            name = cursor.getString(nameFieldColumnIndex);
            String num = cursor.getString(numberFieldColumnIndex);
            if (num.contains("-")){
                num = num.replace("-","");
            }
            phoneNum.add(num);
//            Toast.makeText(getContext(), name + " " + phoneNum, Toast.LENGTH_SHORT).show();
        }

        final Dialog dialog = new Dialog(context, R.style.dialog);
        View view = View.inflate(context, R.layout.dialog_contact, null);
        view.findViewById(R.id.edit_bt_dialog_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开联系人的编辑页面
                Intent editIntent = new Intent(Intent.ACTION_EDIT, Uri.parse("content://com.android.contacts/contacts/"+contactId));
                context.startActivity(editIntent);
            }
        });
        view.findViewById(R.id.cancel_iv_dialog_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_dialog_contact);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new ContactDialogAdapter(context, phoneNum));
        ((TextView)view.findViewById(R.id.name_tv_dialog_contact)).setText(name);

        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


    }

}

