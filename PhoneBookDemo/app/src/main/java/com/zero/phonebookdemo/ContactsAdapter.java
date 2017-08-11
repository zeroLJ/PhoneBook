package com.zero.phonebookdemo;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by zero on 2017/8/10.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder>{
    private ArrayList<Contacts> contactsList;
    private Context context;
    public static String NAME = "name";
    public static String PHONENUM = "phoneNum";
    public static String PHOTOID = "photoId";
    public static String CONTACTID = "contactId";

    ContactsAdapter(Context context, ArrayList<Contacts> contactsList){
        this.context = context;
        this.contactsList = contactsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_call_list, parent,
                false));
        return holder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Contacts contacts = contactsList.get(position);
        holder.name_tv.setText(contacts.name);
        holder.phoneNum_tv.setText(contacts.phoneNum.get(0));
        //得到联系人头像Bitamp
        Bitmap contactPhoto = null;
        Long photoId = contacts.photoId;
        final Long contact_id = contacts.contactId;
        //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
        if(photoId > 0 ) {
            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contact_id);
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
            contactPhoto = BitmapFactory.decodeStream(input);
        }else {
            contactPhoto = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        }
        holder.icon_iv.setImageBitmap(contactPhoto);
        holder.name_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContactActivity.class);
                Contacts contacts = contactsList.get(position);
                intent.putExtra(NAME,contacts.name);
                intent.putExtra(PHONENUM,contacts.phoneNum);
                intent.putExtra(PHOTOID,contacts.photoId);
                intent.putExtra(CONTACTID,contacts.contactId);
                context.startActivity(intent);
            }
        });


//        holder.icon_iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //移除所选item
//                contactsList.remove(position);
//                ContactsAdapter.this.notifyDataSetChanged();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name_tv,phoneNum_tv;
        ImageView icon_iv;
        public MyViewHolder(View view)
        {
            super(view);
            name_tv = (TextView) view.findViewById(R.id.name);
            phoneNum_tv = (TextView) view.findViewById(R.id.phone_number);
            icon_iv = (ImageView) view.findViewById(R.id.icon);
        }
    }

}

class Contacts {
    String name;//联系人名称
    ArrayList<String> phoneNum = new ArrayList<>();//联系人手机号列表
    Long photoId;//联系人头像ID
    Long contactId;//联系人ID
    Contacts(String name, String phoneNum, Long photo_id, Long contact_id){
        this.name = name;
        this.phoneNum.add(phoneNum);
        this.photoId = photo_id;
        this.contactId = contact_id;
    }
}
