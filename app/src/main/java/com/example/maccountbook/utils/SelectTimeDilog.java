package com.example.maccountbook.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.maccountbook.R;

/*
* 在记录页面弹出事件对话框
* */
public class SelectTimeDilog extends Dialog implements View.OnClickListener {
    EditText hourEt,minuteEt;
    DatePicker datePicker;
    Button ensureBtn,cancelBtn;
    private Object OnEnsureListener;

    public interface OnEnsureListener{
        public void onEnsure(String time,int year,int month,int day);
    }
    OnEnsureListener onEnsureLinstener;

    public void setOnEnsureLinstener(SelectTimeDilog.OnEnsureListener onEnsureLinstener) {
        this.onEnsureLinstener = onEnsureLinstener;
    }

    public SelectTimeDilog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_time);
        hourEt = findViewById(R.id.dialog_time_et_hour);
        minuteEt = findViewById(R.id.dialog_time_et_minute);
        datePicker = findViewById(R.id.dialog_time_dp);
        ensureBtn = findViewById(R.id.dialog_time_btn_ensure);
        cancelBtn = findViewById(R.id.dialog_time_btn_cancel);
        ensureBtn.setOnClickListener(this);  //添加点击监听事件
        cancelBtn.setOnClickListener(this);
        hideDatePickerHeader();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_time_btn_cancel:
                cancel();
                break;
            case R.id.dialog_time_btn_ensure:
                int year = datePicker.getYear();  //选择年份
                int month = datePicker.getMonth()+1;
                int dayOfMonth = datePicker.getDayOfMonth();
                String monthstr = String.valueOf(month);
                if(month<10){
                    monthstr = "0"+month;
                }
                String dayStr = String.valueOf(dayOfMonth);
                if(dayOfMonth<10){
                    dayStr="0"+dayOfMonth;
                }
                //获取输入的小时和分钟
                String hourStr = hourEt.getText().toString();
                String minuteStr = minuteEt.getText().toString();
                int hour = 0;
                if (!TextUtils.isEmpty(hourStr)){
                    hour = Integer.parseInt(hourStr);
                    hour = hour%24;
                }
                int minute = 0;
                if (!TextUtils.isEmpty(minuteStr)){
                    minute = Integer.parseInt(minuteStr);
                    minute = minute%60;
                }

                hourStr=String.valueOf(hour);
                minuteStr=String.valueOf(minute);
                if(hour<10){
                    hourStr="0"+hour;
                }
                if(minute<10){
                    minuteStr="0"+minute;
                }
                String timeFormat = year+"年"+monthstr+"月"+dayStr+"日"+hourStr+":"+minuteStr;
                if (onEnsureLinstener!=null){
                    onEnsureLinstener.onEnsure(timeFormat,year,month,dayOfMonth);
                }
                cancel();
                break;
        }
    }

    //隐藏DatePicker头布局
    private void  hideDatePickerHeader(){
        ViewGroup rootView = (ViewGroup) datePicker.getChildAt(0);
        if(rootView == null){
            return;
        }
        View headerView = rootView.getChildAt(0);
        if (headerView == null){
            return;
        }

        //5.0+
        int headerId = getContext().getResources().getIdentifier("day_picker_selector_layout","id","android");
        if (headerId ==headerView.getId()){
            headerView.setVisibility(View.GONE);
            ViewGroup.LayoutParams layoutParamsRoot = rootView.getLayoutParams();
            layoutParamsRoot.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            rootView.setLayoutParams(layoutParamsRoot);

            ViewGroup animator = (ViewGroup) rootView.getChildAt(1);
            ViewGroup.LayoutParams layoutParamsAnimator = rootView.getLayoutParams();
            layoutParamsAnimator.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            animator.setLayoutParams(layoutParamsAnimator);

            View child = animator.getChildAt(0);
            ViewGroup.LayoutParams layoutParamsChild = child.getLayoutParams();
            layoutParamsChild.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            child.setLayoutParams(layoutParamsChild);
            return;
        }

        //6.0+
        headerId = getContext().getResources().getIdentifier("date_picker_header","id","android");
        if (headerId ==headerView.getId()){
            headerView.setVisibility(View.GONE);

        }
    }
}
