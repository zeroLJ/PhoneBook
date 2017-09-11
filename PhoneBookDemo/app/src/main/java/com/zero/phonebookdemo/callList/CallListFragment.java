package com.zero.phonebookdemo.callList;


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
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.zero.phonebookdemo.util.HanZiToPinYinUtil;
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
 * create by zero on 2017/8/10.
 * 通讯录页面
 */
public class CallListFragment extends Fragment {
    //判断是否新建联系人后返回的页面，若是则刷新通讯录
    public static boolean isRefresh = false;

    private static String TAG = CallListFragment.class.getCanonicalName();

    //侧边字母滑动栏要显示的文字
    private String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"
            , "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};

    //通讯录所有联系人信息相关
    private ArrayList<ContactInfo> contactInfoList = new ArrayList<>();
    private HashMap<String, Integer> hashMap = new HashMap<>();//存放字母所在位置
    private CallListAdapter adapter;

    //搜索结果的联系人信息相关
    private ArrayList<ContactInfo> contactInfoList_search = new ArrayList<>();
    private HashMap<String, Integer> hashMap_search = new HashMap<>();
    private CallListAdapter adapter_search;

    //联系人列表相关
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private QuickSideBarView quickSideBarView;
    private QuickSideBarTipsView quickSideBarTipsView;

    private EditText search_et;
    private RelativeLayout search_layout;
    private ProgressBar progressBar;
    private TextView tips_tv;


    private static final int SEARCHSTART = 1001;
    private static final int SEARCHFINISH = 1002;
    private static final int REFRESH = 1003;

    private InputMethodManager imm;

    //查询线程
    private Thread thread;

    //输入停止计时器
    private Timer timer;

    private boolean isSearching = false;

    private boolean isFirstInit = true;

    private View view;

    //下拉刷新控件
    private PtrClassicFrameLayout mPtrFrame;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEARCHSTART:
                    String str = msg.obj.toString();
                    getCallList(str);
                    break;
                case SEARCHFINISH:
                    manager.setStackFromEnd(true);
                    if (mPtrFrame.isRefreshing()) {
                        mPtrFrame.refreshComplete();//停止刷新效果
                    }
                    if (isSearching) {
                        adapter_search = new CallListAdapter(getContext(), contactInfoList_search);
                        recyclerView.setAdapter(adapter_search);
                        manager.scrollToPositionWithOffset(0, 0);
                    }
                    search_layout.setVisibility(View.VISIBLE);
                    break;
                case REFRESH:
                    if (mPtrFrame.isRefreshing()) {
                        mPtrFrame.refreshComplete();//停止刷新效果
                    }
                    if (!isSearching) {
                        adapter = new CallListAdapter(getContext(), contactInfoList);
                        recyclerView.setAdapter(adapter);
                        manager.scrollToPositionWithOffset(0, 0);
                        manager.setStackFromEnd(true);
                    }
                    progressBar.setVisibility(View.GONE);
                    if (contactInfoList.size() != 0){
                        search_layout.setVisibility(View.VISIBLE);
                    }else {
                        tips_tv.setVisibility(View.VISIBLE);
                    }
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
        Log.i(TAG, "init");
        //只在第一次初始化
        if (isFirstInit) {
            isFirstInit = false;
            imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            view = inflater.inflate(R.layout.fragment_call_list, container, false);
            initView(view);
            setListener();
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "start");
        search_et.addTextChangedListener(textWatcher);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "pause");
        //不移除监听的话，下次回到此页面会自动执行一次搜索
        search_et.removeTextChangedListener(textWatcher);
    }


    private void initView(View view) {
        quickSideBarView = (QuickSideBarView) view.findViewById(R.id.quickSideBarView);
        quickSideBarTipsView = (QuickSideBarTipsView) view.findViewById(R.id.quickSideBarTipsView);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_callList);
        search_et = (EditText) view.findViewById(R.id.search_et_callList);
        search_layout = (RelativeLayout) view.findViewById(R.id.search_layout_call_list);
        tips_tv = (TextView) view.findViewById(R.id.tips_tv_call_list);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String s1 = "没有联系人\n请打开";
        builder.append(s1);
        String s2 = "系统设置->应用管理->PhoneBookDemo->权限管理";
        builder.append(s2);
        builder.append("检查是否已经开启了读取联系人权限");
        builder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.colorPrimary));       //设置文件颜色
                ds.setUnderlineText(false);      //设置下划线
            }
        }, s1.length(), s1.length() + s2.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tips_tv.setText(builder);
        tips_tv.setMovementMethod(LinkMovementMethod.getInstance());
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_call_list);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        //设置adapter
//        adapter = new CallListAdapter(getContext(), contactInfoList);
//        recyclerView.setAdapter(adapter);
//

        search_layout.setVisibility(View.GONE);
        //获取所有联系人
        getCallList(null);

        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < letters.length; i++) {
            arrayList.add(letters[i]);
        }
        //不自定义则默认26个字母
        quickSideBarView.setLetters(arrayList);

        mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.rotate_header_list_view);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                search_layout.setVisibility(View.GONE);
                tips_tv.setVisibility(View.GONE);
                mPtrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //在这写刷新要完成的代码
                        if (isSearching) {
                            getCallList(search_et.getText().toString());
                        } else {
                            getCallList(null);
                        }
                    }
                }, 0);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
    }

    private void setListener() {
        //设置监听
        quickSideBarView.setOnQuickSideBarTouchListener(new OnQuickSideBarTouchListener() {
            @Override
            public void onLetterChanged(String letter, int position, float y) {
                quickSideBarTipsView.setText(letter, position, y);
                //有此key则获取位置并滚动到该位置
                if (isSearching) {
                    if (hashMap_search.containsKey(letter)) {
                        manager.scrollToPositionWithOffset(hashMap_search.get(letter), 0);
                        manager.setStackFromEnd(true);
                    }
                } else {
                    if (hashMap.containsKey(letter)) {
                        manager.scrollToPositionWithOffset(hashMap.get(letter), 0);
                        manager.setStackFromEnd(true);
                    }
                }

            }

            @Override
            public void onLetterTouching(boolean touching) {
                imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
                //可以自己加入动画效果渐显渐隐
                quickSideBarTipsView.setVisibility(touching ? View.VISIBLE : View.INVISIBLE);
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

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String s = search_et.getText().toString();
                    if (!s.equals("")) {
                        handler.obtainMessage(CallListFragment.SEARCHSTART, s).sendToTarget();
                        isSearching = true;
                    } else {
                        handler.obtainMessage(REFRESH).sendToTarget();
                        isSearching = false;
                    }

                }
            }, 500);
        }
    };


    /**
     * 获取通讯录列表
     * @param s 查询条件，null时为查询所有联系人。
     */
    private void getCallList(final String s) {
        //若上一次的查询任务仍在进行，则取消
        if (thread != null) {
            handler.removeCallbacks(thread);
        }
        //新开线程，防止通讯录太多联系人，卡顿
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<ContactInfo> contactInfoList = new ArrayList<>();
                HashMap<String, Integer> hashMap = new HashMap<>();
                ArrayList<ContactInfo>[] letterList = new ArrayList[letters.length];
                String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
                Cursor cursor;
                if (s!=null){
                    //设置查询条件
                    String selection = ContactsContract.PhoneLookup.DISPLAY_NAME + " like '%" + s
                            + "%' or " + ContactsContract.CommonDataKinds.Phone.NUMBER + " like '%" + s + "%'";
                    //ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY 这个参数是让查询结果按字母排序
                    cursor = getContext().getContentResolver()
                            .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    cols, selection, null, ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY);
                }else {
                    cursor = getContext().getContentResolver()
                            .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    cols, null, null, ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY);
                }
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                int contactIdFieldColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String name = cursor.getString(nameFieldColumnIndex);
                    Long contactId = cursor.getLong(contactIdFieldColumnIndex);
                    String letter = HanZiToPinYinUtil.getFirstLetter(name);
                    ContactInfo contactInfo = new ContactInfo(name, contactId);
                    boolean isLetter = false;
                    for (int j = 0; j < letters.length; j++) {
                        if (letter.equals(letters[j])) {
                            if (letterList[j] == null) {
                                letterList[j] = new ArrayList<ContactInfo>();
                            }
                            letterList[j].add(contactInfo);
                            isLetter = true;
                            break;
                        }
                    }
                    if (!isLetter) {
                        if (letterList[26] == null) {
                            letterList[26] = new ArrayList<ContactInfo>();
                        }
                        letterList[26].add(contactInfo);
                    }
                }
                cursor.close();
                for (int i = 0; i < letters.length; i++) {
                    hashMap.put(letters[i], contactInfoList.size());
                    if (letterList[i] != null) {
                        contactInfoList.add(new ContactInfo(letters[i]));
                        for (int j = 0; j < letterList[i].size(); j++) {
                            contactInfoList.add(letterList[i].get(j));
                        }
                    }
                }
                if (s != null){
                    CallListFragment.this.contactInfoList_search = contactInfoList;
                    CallListFragment.this.hashMap_search = hashMap;
                    handler.obtainMessage(SEARCHFINISH).sendToTarget();
                }else {
                    CallListFragment.this.contactInfoList = contactInfoList;
                    CallListFragment.this.hashMap = hashMap;
                    handler.obtainMessage(REFRESH).sendToTarget();
                }
            }
        });
        thread.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        //只有从新建联系人页面返回时才执行
        if (isRefresh){
            Log.i("ssss","resume");
            isRefresh = false;
            search_et.setText("");
//            getCallList(null);
            mPtrFrame.autoRefresh();
        }
    }
}

