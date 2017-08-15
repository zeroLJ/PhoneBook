package com.zero.phonebookdemo;

import opensource.jpinyin.PinyinHelper;

/**
 * Created by zero on 2017/4/5.
 */
public class HanZiToPinYinUtil {
    /**
     * 返回一个字符串的拼音
     * @param hanZi
     * @return
     */
    public static String stringToPinYin(String hanZi){
        String pinYin = PinyinHelper.convertToPinyinString(hanZi," ");
        //将汉字读音返回
        return pinYin;
    }

    /**
     * 返回一个字符串的首字母
     * @param string
     * @return
     */
    public static String getFirstLetter(String string){
        String firstLetter = PinyinHelper.getShortPinyin(String.valueOf(string));
        firstLetter = String.valueOf(firstLetter.charAt(0)).toUpperCase();
        //将汉字读音返回
        return firstLetter;
    }
}
