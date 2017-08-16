package com.zero.phonebookdemo;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.zero.phonebookdemo.Adapter.CallListAdapter;

import java.io.InputStream;
import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {
    private String name;//联系人名称
    private ArrayList<String> phoneNum = new ArrayList<>();//联系人手机号列表
    private Long photoId;//联系人头像ID
    private Long contactId;//联系人ID
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Intent intent = getIntent();
        contactId = intent.getLongExtra(CallListAdapter.CONTACTID,0);
    }

    //获取联系人头像
    private Bitmap getBitmap(Long photoId, Long contactId) {
        Bitmap contactPhoto;//photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
        if(photoId > 0 ) {
            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactId);
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), uri);
            contactPhoto = BitmapFactory.decodeStream(input);
        }else {
            contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        }
        return contactPhoto;
    }

    public void edit(View view) {
        //打开联系人的编辑页面
        Intent editIntent = new Intent(Intent.ACTION_EDIT,Uri.parse("content://com.android.contacts/contacts/"+contactId));
        startActivity(editIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshContactInfo();
        setTitle(name);
        String s = "";
        for (int i=0;i<phoneNum.size();i++){
            String num = phoneNum.get(i);
            if (num.contains("-")){
                num = num.replace("-","");
            }
            s = s + num +"\n";
        }
        ((TextView)findViewById(R.id.num)).setText(s);

        Bitmap contactPhoto = null;
        contactPhoto = getBitmap(photoId, contactId);
        ((MLRoundedImageView)findViewById(R.id.image)).setImageBitmap(contactPhoto);

    }

    //查询联系人信息
    private void refreshContactInfo() {
        String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.PHOTO_ID,ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
        //设置查询条件
        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "='"+contactId+"'";
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                cols, selection, null, null);
        int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
        int numberFieldColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        int photoFieldColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);
        int contactIdFieldColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
        phoneNum.clear();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            name = cursor.getString(nameFieldColumnIndex);
            photoId = cursor.getLong(photoFieldColumnIndex);
            contactId = cursor.getLong(contactIdFieldColumnIndex);
            String num = cursor.getString(numberFieldColumnIndex);
            phoneNum.add(num);
//            Toast.makeText(getContext(), name + " " + phoneNum, Toast.LENGTH_SHORT).show();
        }
    }
}
