package com.zero.phonebookdemo;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ContactsAdapter adapter;
    ArrayList<Contacts> contactsList = new ArrayList<>();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.notifyItemChanged(contactsList.size()-1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_callList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置adapter
        adapter = new ContactsAdapter(this, contactsList);
        recyclerView.setAdapter(adapter);
        getPhoneBook();
    }

    private void getPhoneBook() {

        //新开线程，防止通讯录太多联系人，卡顿
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.PHOTO_ID,ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
                Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        cols, null, null, null);
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                int numberFieldColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int photoFieldColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);
                int contactIdFieldColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                String name_previous = "";
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String name = cursor.getString(nameFieldColumnIndex);
                    String phoneNum = cursor.getString(numberFieldColumnIndex);
                    Long photoId = cursor.getLong(photoFieldColumnIndex);
                    Long contactId = cursor.getLong(contactIdFieldColumnIndex);
//            Toast.makeText(getContext(), name + " " + phoneNum, Toast.LENGTH_SHORT).show();
                    if(name.equals(name_previous)){
                        contactsList.get(contactsList.size()-1).phoneNum.add(phoneNum);
                    }else {
                        Contacts contacts = new Contacts(name,phoneNum,photoId,contactId);
                        contactsList.add(contacts);
                        handler.sendEmptyMessage(0);
                        name_previous = name;
                    }
                }
            }
        }).start();
    }

}
