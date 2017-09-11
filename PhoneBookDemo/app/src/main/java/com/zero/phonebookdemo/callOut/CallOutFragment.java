package com.zero.phonebookdemo.callOut;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zero.phonebookdemo.R;
import com.zero.phonebookdemo.util.PhonenumberUtil;

/**
 *create by zero on 2017/8/10.
 *拨号页面
 */
public class CallOutFragment extends Fragment implements View.OnClickListener, View.OnTouchListener{
    private StringBuilder numBuilder = new StringBuilder();
    private TextView number_tv;
    private ImageView delete_iv;
    private boolean isFirstInit = true;
    private View view;
    public CallOutFragment() {
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
        if (isFirstInit){
            isFirstInit = false;
            view = inflater.inflate(R.layout.fragment_call_out, container, false);
            number_tv = (TextView) view.findViewById(R.id.number_tv_callOut);
            delete_iv = (ImageView) view.findViewById(R.id.delete_iv_callOut);
            delete_iv.setOnClickListener(this);
            view.findViewById(R.id.layout_callOut_0).setOnClickListener(this);
            view.findViewById(R.id.layout_callOut_1).setOnClickListener(this);
            view.findViewById(R.id.layout_callOut_2).setOnClickListener(this);
            view.findViewById(R.id.layout_callOut_3).setOnClickListener(this);
            view.findViewById(R.id.layout_callOut_4).setOnClickListener(this);
            view.findViewById(R.id.layout_callOut_5).setOnClickListener(this);
            view.findViewById(R.id.layout_callOut_6).setOnClickListener(this);
            view.findViewById(R.id.layout_callOut_7).setOnClickListener(this);
            view.findViewById(R.id.layout_callOut_8).setOnClickListener(this);
            view.findViewById(R.id.layout_callOut_9).setOnClickListener(this);
            view.findViewById(R.id.layout_callOut_star).setOnClickListener(this);
            view.findViewById(R.id.layout_callOut_well).setOnClickListener(this);
            view.findViewById(R.id.layout_callOut_call).setOnClickListener(this);
            view.findViewById(R.id.layout_callOut_0).setOnTouchListener(this);
            view.findViewById(R.id.layout_callOut_1).setOnTouchListener(this);
            view.findViewById(R.id.layout_callOut_2).setOnTouchListener(this);
            view.findViewById(R.id.layout_callOut_3).setOnTouchListener(this);
            view.findViewById(R.id.layout_callOut_4).setOnTouchListener(this);
            view.findViewById(R.id.layout_callOut_5).setOnTouchListener(this);
            view.findViewById(R.id.layout_callOut_6).setOnTouchListener(this);
            view.findViewById(R.id.layout_callOut_7).setOnTouchListener(this);
            view.findViewById(R.id.layout_callOut_8).setOnTouchListener(this);
            view.findViewById(R.id.layout_callOut_9).setOnTouchListener(this);
            view.findViewById(R.id.layout_callOut_star).setOnTouchListener(this);
            view.findViewById(R.id.layout_callOut_well).setOnTouchListener(this);
            delete_iv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    numBuilder.delete(0,numBuilder.length());
                    number_tv.setText(numBuilder);
                    return false;
                }
            });
        }
        return view;
    }

    public static CallOutFragment newInstance() {
        CallOutFragment fragment = new CallOutFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_callOut_0:
                numBuilder.append(0);
                number_tv.setText(numBuilder);
                break;
            case R.id.layout_callOut_1:
                numBuilder.append(1);
                number_tv.setText(numBuilder);
                break;
            case R.id.layout_callOut_2:
                numBuilder.append(2);
                number_tv.setText(numBuilder);
                break;
            case R.id.layout_callOut_3:
                numBuilder.append(3);
                number_tv.setText(numBuilder);
                break;
            case R.id.layout_callOut_4:
                numBuilder.append(4);
                number_tv.setText(numBuilder);
                break;
            case R.id.layout_callOut_5:
                numBuilder.append(5);
                number_tv.setText(numBuilder);
                break;
            case R.id.layout_callOut_6:
                numBuilder.append(6);
                number_tv.setText(numBuilder);
                break;
            case R.id.layout_callOut_7:
                numBuilder.append(7);
                number_tv.setText(numBuilder);
                break;
            case R.id.layout_callOut_8:
                numBuilder.append(8);
                number_tv.setText(numBuilder);
                break;
            case R.id.layout_callOut_9:
                numBuilder.append(9);
                number_tv.setText(numBuilder);
                break;
            case R.id.layout_callOut_star:
                numBuilder.append("*");
                number_tv.setText(numBuilder);
                break;
            case R.id.layout_callOut_well:
                numBuilder.append("#");
                number_tv.setText(numBuilder);
                break;
            case R.id.delete_iv_callOut:
                if (numBuilder.length()>0){
                    if (numBuilder.length()>20) numBuilder.delete(19,numBuilder.length());
                    numBuilder.deleteCharAt(numBuilder.length()-1);
                    number_tv.setText(numBuilder);
                }
                break;
            case R.id.layout_callOut_call:
                callOut();
                break;
        }
    }

    private void callOut(){
        String num = numBuilder.toString();
        if (PhonenumberUtil.isCallAbleNum(num)){
//            Intent intent = new Intent(getContext(), CallOnActivity.class);
//            intent.putExtra("num", num);
//            startActivity(intent);
        }else {
            Toast.makeText(getContext(), "请输入正确的手机号码，若拨打固话请加上区号！", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        TextView textView = null;
        ImageView imageView = null;
        switch (v.getId()){
            case R.id.layout_callOut_0:
                textView = (TextView) v.findViewById(R.id.tv_callOut_0);
                imageView = (ImageView) v.findViewById(R.id.iv_callOut_0);
                break;
            case R.id.layout_callOut_1:
                textView = (TextView) v.findViewById(R.id.tv_callOut_1);
                imageView = (ImageView) v.findViewById(R.id.iv_callOut_1);
                break;
            case R.id.layout_callOut_2:
                textView = (TextView) v.findViewById(R.id.tv_callOut_2);
                imageView = (ImageView) v.findViewById(R.id.iv_callOut_2);
                break;
            case R.id.layout_callOut_3:
                textView = (TextView) v.findViewById(R.id.tv_callOut_3);
                imageView = (ImageView) v.findViewById(R.id.iv_callOut_3);
                break;
            case R.id.layout_callOut_4:
                textView = (TextView) v.findViewById(R.id.tv_callOut_4);
                imageView = (ImageView) v.findViewById(R.id.iv_callOut_4);
                break;
            case R.id.layout_callOut_5:
                textView = (TextView) v.findViewById(R.id.tv_callOut_5);
                imageView = (ImageView) v.findViewById(R.id.iv_callOut_5);
                break;
            case R.id.layout_callOut_6:
                textView = (TextView) v.findViewById(R.id.tv_callOut_6);
                imageView = (ImageView) v.findViewById(R.id.iv_callOut_6);
                break;
            case R.id.layout_callOut_7:
                textView = (TextView) v.findViewById(R.id.tv_callOut_7);
                imageView = (ImageView) v.findViewById(R.id.iv_callOut_7);
                break;
            case R.id.layout_callOut_8:
                textView = (TextView) v.findViewById(R.id.tv_callOut_8);
                imageView = (ImageView) v.findViewById(R.id.iv_callOut_8);
                break;
            case R.id.layout_callOut_9:
                textView = (TextView) v.findViewById(R.id.tv_callOut_9);
                imageView = (ImageView) v.findViewById(R.id.iv_callOut_9);
                break;
            case R.id.layout_callOut_star:
                textView = (TextView) v.findViewById(R.id.tv_callOut_star);
                imageView = (ImageView) v.findViewById(R.id.iv_callOut_star);
                break;
            case R.id.layout_callOut_well:
                textView = (TextView) v.findViewById(R.id.tv_callOut_well);
                imageView = (ImageView) v.findViewById(R.id.iv_callOut_well);
                break;
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                imageView.setImageResource(R.drawable.call_num_background_down);
                textView.setTextColor(getResources().getColor(R.color.white));
                break;
            case MotionEvent.ACTION_UP:
                imageView.setImageResource(R.drawable.call_num_background_up);
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            default:

                break;
        }
        return false;
    }
}
