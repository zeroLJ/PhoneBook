package com.zero.phonebookdemo.InfoClass;

import java.util.ArrayList;

/**
 * Created by WatchVoice04 on 2017/8/11.
 */

public class ContactInfo {
    public String name;//联系人名称
    public ArrayList<String> phoneNum = new ArrayList<>();//联系人手机号列表
    public Long photoId;//联系人头像ID
    public Long contactId;//联系人ID
    public String letter;//联系人名称首字母
    ContactInfo(String name, String phoneNum, Long photoId, Long contactId){
        this.name = name;
        this.phoneNum.add(phoneNum);
        this.photoId = photoId;
        this.contactId = contactId;
    }

    public ContactInfo(String name, Long contactId) {
        this.name = name;
        this.contactId = contactId;
    }

    public ContactInfo(String letter){
        this.letter = letter;
    }
}
