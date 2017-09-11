package com.zero.phonebookdemo.callList;

/**
 * Created by zero on 2017/8/11.
 * 联系人信息
 */

public class ContactInfo {
    public String name;//联系人名称
    public Long contactId;//联系人ID
    public String letter;//联系人名称首字母
    private int type;

    public ContactInfo(String name, Long contactId) {
        this.name = name;
        this.contactId = contactId;
        type = CallListAdapter.TYPE_CONTACT;
    }

    public ContactInfo(String letter) {
        this.letter = letter;
        type = CallListAdapter.TYPE_LETTER;
    }

    public int getType() {
        return type;
    }
}
