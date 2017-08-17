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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.zero.phonebookdemo.Adapter.CallListAdapter;
import com.zero.phonebookdemo.HanZiToPinYinUtil;
import com.zero.phonebookdemo.InfoClass.ContactInfo;
import com.zero.phonebookdemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 *create by zero on 2017/8/10.
 */
public class CallListFragment extends Fragment {
    //侧边字母滑动栏要显示的文字
    private String[] letters = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","#"};

    //通讯录所有联系人列表相关
    private ArrayList<ContactInfo> contactInfoList = new ArrayList<>();
    private ArrayList<ContactInfo>[] letterList = new ArrayList[letters.length];
    private HashMap<String,Integer> hashMap = new HashMap<>();
    private CallListAdapter adapter;

    //搜索结果的联系人列表相关
    private ArrayList<ContactInfo> contactInfoList_search = new ArrayList<>();
    private ArrayList<ContactInfo>[] letterList_search;
    private HashMap<String,Integer> hashMap_search = new HashMap<>();
    private CallListAdapter adapter_search;


    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private QuickSideBarView quickSideBarView;
    private QuickSideBarTipsView quickSideBarTipsView;
    private EditText search_et;


    private static final int SEARCH = 1001;
    private static final int SEARCHFINISH = 1002;
    private static final int REFRESH = 1003;
    private InputMethodManager imm;


    private Thread thread;
    private Timer timer;

    private boolean isSearching = false;

    private PtrClassicFrameLayout mPtrFrame;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEARCH:
                    Log.i("ssss",msg.obj.toString());
                    String str = msg.obj.toString();
                    getSearchCallList(str);
                    break;
                case SEARCHFINISH:
                    if(isSearching){
                        adapter_search = new CallListAdapter(getContext(), contactInfoList_search);
                        recyclerView.setAdapter(adapter_search);
                        manager.scrollToPositionWithOffset(0,0);
                    }
                    manager.setStackFromEnd(true);
                    if (mPtrFrame.isRefreshing()){
                        mPtrFrame.refreshComplete();//停止刷新效果
                    }
                    break;
                case REFRESH:
                    if(!isSearching){
                        recyclerView.setAdapter(adapter);
                        manager.scrollToPositionWithOffset(0,0);
                        manager.setStackFromEnd(true);
                    }
                    if (mPtrFrame.isRefreshing()){
                        mPtrFrame.refreshComplete();//停止刷新效果
                    }
                    search_et.setVisibility(View.VISIBLE);
                    break;
            }
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
        imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_call_list, container, false);
        initView(view);
        setListener();

        return view;
    }



    private void initView(View view) {
        quickSideBarView = (QuickSideBarView) view.findViewById(R.id.quickSideBarView);
        quickSideBarTipsView = (QuickSideBarTipsView) view.findViewById(R.id.quickSideBarTipsView);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_callList);
        search_et = (EditText) view.findViewById(R.id.search_et_callList);
        manager =new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置adapter
        adapter = new CallListAdapter(getContext(), contactInfoList);
        recyclerView.setAdapter(adapter);
//        getCallList();

        ArrayList<String> arrayList =new ArrayList<>();
        for (int i=0;i<letters.length;i++){
            arrayList.add(letters[i]);
        }
        //不自定义则默认26个字母
        quickSideBarView.setLetters(arrayList);

        mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.rotate_header_list_view);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPtrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //在这写刷新要完成的代码
                        if(isSearching){
                            getSearchCallList(search_et.getText().toString());
                        }else {
                            getCallList();
                        }
                    }
                }, 0);
            }
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        // default is false
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.postDelayed(new Runnable() {    //启动自动刷新一次
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);
    }

    private void setListener() {
        //设置监听
        quickSideBarView.setOnQuickSideBarTouchListener(new OnQuickSideBarTouchListener() {
            @Override
            public void onLetterChanged(String letter, int position, float y) {
                quickSideBarTipsView.setText(letter, position, y);
                //有此key则获取位置并滚动到该位置
//                if(letters.containsKey(letter)) {
//                    recyclerView.scrollToPosition(letters.get(letter));
//                }
                if(isSearching){
                    if(hashMap_search.containsKey(letter)){
                        manager.scrollToPositionWithOffset(hashMap_search.get(letter),0);
                        manager.setStackFromEnd(true);
                    }
                }else {
                    if(hashMap.containsKey(letter)){
                        manager.scrollToPositionWithOffset(hashMap.get(letter),0);
                        manager.setStackFromEnd(true);
                    }
                }

            }

            @Override
            public void onLetterTouching(boolean touching) {
                imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
                //可以自己加入动画效果渐显渐隐
                quickSideBarTipsView.setVisibility(touching? View.VISIBLE: View.INVISIBLE);
            }
        });

        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (timer!=null){
                    timer.cancel();
                }
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        String s =  search_et.getText().toString();
                        if (!s.equals("")){
                            handler.obtainMessage(CallListFragment.SEARCH, s).sendToTarget();
                            isSearching = true;
                        }else {
                            handler.obtainMessage(REFRESH).sendToTarget();
                            isSearching = false;
                        }

                    }
                },500);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
            }
        });
    }

    public static CallListFragment newInstance() {
        CallListFragment fragment = new CallListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void getCallList() {
        if (thread!=null){
            handler.removeCallbacks(thread);
        }
        contactInfoList.clear();
        hashMap.clear();
        letterList = new ArrayList[letters.length];
        //新开线程，防止通讯录太多联系人，卡顿
        thread = new Thread(new Runnable() {
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
                    if(letterList[i]!=null){
                        contactInfoList.add(new ContactInfo(letters[i]));
                        for (int j = 0 ;j < letterList[i].size();j++){
                            contactInfoList.add(letterList[i].get(j));
                        }
                    }
                }
                handler.obtainMessage(REFRESH).sendToTarget();
            }
        });
        thread.start();
    }

    private void getSearchCallList(final String s) {
        if (thread!=null){
            handler.removeCallbacks(thread);
        }
        letterList_search = new ArrayList[letters.length];
        hashMap_search.clear();
        contactInfoList_search.clear();
        //新开线程，防止通讯录太多联系人，卡顿
        thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
                //设置查询条件
                String selection = ContactsContract.PhoneLookup.DISPLAY_NAME + " like '%"+s+"%' or " + ContactsContract.CommonDataKinds.Phone.NUMBER + " like '%"+s+"%'" ;
                //ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY 这个参数是让查询结果按字母排序
                Cursor cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        cols, selection, null, ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY);
                if (cursor.getCount()>0){
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
                                if (letterList_search[j]==null){
                                    letterList_search[j] = new ArrayList<ContactInfo>();
                                }
                                letterList_search[j].add(contactInfo);
                                isLetter = true;
                                break;
                            }
                        }
                        if(!isLetter){
                            if (letterList_search[26]==null){
                                letterList_search[26] = new ArrayList<ContactInfo>();
                            }
                            letterList_search[26].add(contactInfo);
                        }
                    }

                    for (int i = 0; i < letters.length; i++) {
                        hashMap_search.put(letters[i],contactInfoList_search.size());
                        if(letterList_search[i]!=null){
                            contactInfoList_search.add(new ContactInfo(letters[i]));
                            for (int j = 0 ;j < letterList_search[i].size();j++){
                                contactInfoList_search.add(letterList_search[i].get(j));
                            }
                        }
                    }
                }
                handler.obtainMessage(SEARCHFINISH).sendToTarget();
            }
        });
        thread.start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}


