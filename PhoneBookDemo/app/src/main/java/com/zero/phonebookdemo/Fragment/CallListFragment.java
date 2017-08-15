package com.zero.phonebookdemo.Fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.zero.phonebookdemo.Adapter.CallListAdapter;
import com.zero.phonebookdemo.InfoClass.ContactInfo;
import com.zero.phonebookdemo.HanZiToPinYinUtil;
import com.zero.phonebookdemo.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *create by zero on 2017/8/10.
 */
public class CallListFragment extends Fragment {
    private ArrayList<ContactInfo> contactInfoList = new ArrayList<>();
    private String[] letters = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","#"};
    private ArrayList<ContactInfo>[] letterList = new ArrayList[letters.length];
    HashMap<String,Integer> hashMap = new HashMap<>();//存储每个字母在列表中的位置
    private CallListAdapter adapter;
    private RecyclerView recyclerView;
    LinearLayoutManager manager;
    QuickSideBarView quickSideBarView;
    QuickSideBarTipsView quickSideBarTipsView;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.notifyItemChanged(contactInfoList.size()-1);
//            adapter.notifyDataSetChanged();
        }
    };

    public CallListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_call_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_callList);
        manager =new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置adapter
        adapter = new CallListAdapter(getContext(), contactInfoList);
        recyclerView.setAdapter(adapter);
        getCallList();

        quickSideBarView = (QuickSideBarView) view.findViewById(R.id.quickSideBarView);
        quickSideBarTipsView = (QuickSideBarTipsView) view.findViewById(R.id.quickSideBarTipsView);
        //设置监听
        quickSideBarView.setOnQuickSideBarTouchListener(new OnQuickSideBarTouchListener() {
            @Override
            public void onLetterChanged(String letter, int position, float y) {
                quickSideBarTipsView.setText(letter, position, y);
                //有此key则获取位置并滚动到该位置
//                if(letters.containsKey(letter)) {
//                    recyclerView.scrollToPosition(letters.get(letter));
//                }
                if(hashMap.containsKey(letter)){
                    //滑动到对应字母所在的位置，并在屏幕的顶部
                    manager.scrollToPositionWithOffset(hashMap.get(letter),0);
                    manager.setStackFromEnd(true);
                }
            }

            @Override
            public void onLetterTouching(boolean touching) {
                //可以自己加入动画效果渐显渐隐
                quickSideBarTipsView.setVisibility(touching? View.VISIBLE: View.INVISIBLE);
            }
        });

        ArrayList<String> arrayList =new ArrayList<>();
        for (int i=0;i<letters.length;i++){
            arrayList.add(letters[i]);
        }
        //不自定义则默认26个字母
        quickSideBarView.setLetters(arrayList);
        return view;
    }

    public static CallListFragment newInstance() {
        CallListFragment fragment = new CallListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void getCallList() {
        //新开线程，防止通讯录太多联系人，卡顿
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
                //ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY 这个参数是让查询结果按字母排序
                Cursor cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        cols, null, null, ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY);
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                int contactIdFieldColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String name = cursor.getString(nameFieldColumnIndex);
                    Long contactId = cursor.getLong(contactIdFieldColumnIndex);
                    String letter = HanZiToPinYinUtil.getFirstLetter(name);
                    ContactInfo contactInfo = new ContactInfo(name,contactId);
                    boolean isLetter = false;
                    for (int j = 0; j < letters.length; j++){
                        if(letter.equals(letters[j])){
                            if (letterList[j]==null){
                                letterList[j] = new ArrayList<ContactInfo>();
                            }
                            letterList[j].add(contactInfo);
                            isLetter = true;
                            break;
                        }
                    }
                    if(!isLetter){
                        if (letterList[26]==null){
                            letterList[26] = new ArrayList<ContactInfo>();
                        }
                        letterList[26].add(contactInfo);
                    }
                }
                for (int i = 0; i < letters.length; i++) {

                        hashMap.put(letters[i],contactInfoList.size());
                        Log.i("ssss","s"+contactInfoList.size());
                    if(letterList[i]!=null){
                        contactInfoList.add(new ContactInfo(letters[i]));
                        handler.sendEmptyMessage(0);
                        for (int j = 0 ;j < letterList[i].size();j++){
                            contactInfoList.add(letterList[i].get(j));
                            handler.sendEmptyMessage(0);
                        }
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

}


