package com.zero.phonebookdemo;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Intent intent = getIntent();
        String name = intent.getStringExtra(ContactsAdapter.NAME);
        ArrayList<String> phoneNum = intent.getStringArrayListExtra(ContactsAdapter.PHONENUM);
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

        Long photoId = intent.getLongExtra(ContactsAdapter.PHOTOID,0);
        Long contactId = intent.getLongExtra(ContactsAdapter.CONTACTID,0);
        Bitmap contactPhoto = null;
        contactPhoto = getBitmap(photoId, contactId);
        ((ImageView)findViewById(R.id.image)).setImageBitmap(contactPhoto);
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
}
