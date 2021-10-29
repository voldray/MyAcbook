package com.example.maccountbook;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.maccountbook.adapter.AccountAdapter;
import com.example.maccountbook.db.AccountBean;
import com.example.maccountbook.db.DBManager;
import com.example.maccountbook.utils.BudgetDialog;
import com.example.maccountbook.utils.RecordActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ListView todayLv;    //展示今日收支情况的ListView
    Button editBtn;
    Button anlBtn;
    Button menuBtn;
    private boolean mIsOpen = true;
    //声明数据源
    List<AccountBean> mDatas;
    AccountAdapter adapter;
    int year,month,day;
    //头布局相关控件
    View headerView;
    TextView topOutTv,topInTv,topbudgetTv,topConTv;
    ImageView topShowIv;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTime();
        initView();
        //读取预算
        preferences = getSharedPreferences("budget", Context.MODE_PRIVATE);
        //添加ListView的头布局
        addLVHeaderView();
        mDatas = new ArrayList<>();
        //设置适配器：加载每一行数据到列表当中
        adapter = new AccountAdapter(this, mDatas);
        todayLv.setAdapter(adapter);

    }
    /** 初始化自带的View的方法*/
    private void initView() {
        todayLv = findViewById(R.id.main_lv);
        editBtn = findViewById(R.id.main_btn_edit);
        anlBtn = findViewById(R.id.main_iv_search);
        menuBtn = findViewById(R.id.main_btn_menu);
        editBtn.setOnClickListener(this);
        setLVLongClickListener();
        //主页+按钮点击事件
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsOpen) {
                    showIcon();
                    mIsOpen = false;
                } else {
                    closeIcon();
                    mIsOpen = true;
                }
            }
        });

    }

    /*设置ListView的长按事件*/
    private void setLVLongClickListener() {
        todayLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {  //点击了头布局
                    return false;
                }
                int pos = position-1;
                AccountBean clickBean = mDatas.get(pos);  //获取正在被点击的这条信息

                //弹出提示用户是否删除的对话框
                showDeleteItemDialog(clickBean);
                return false;
            }

        });
    }

    /* 弹出是否删除某一条记录的对话框*/
    private void showDeleteItemDialog(final AccountBean clickBean) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("您确定要删除这条记录么？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int click_id = clickBean.getId();
                        //执行删除的操作
                        DBManager.deleteItemFromAccounttbById(click_id);
                        mDatas.remove(clickBean);   //实时刷新，移除集合当中的对象
                        adapter.notifyDataSetChanged();   //提示适配器更新数据
                        setTopTvShow();   //改变头布局TextView显示的内容
                    }
                });
        builder.create().show();   //显示对话框
    }

    /** 给ListView添加头布局的方法*/
    private void addLVHeaderView() {
        //将布局转换成View对象
        headerView = getLayoutInflater().inflate(R.layout.item_mainlv_top, null);
        todayLv.addHeaderView(headerView);
        //查找头布局可用控件
        topOutTv = headerView.findViewById(R.id.item_mainlv_top_tv_out);
        topInTv = headerView.findViewById(R.id.item_mainlv_top_tv_in);
        topbudgetTv = headerView.findViewById(R.id.item_mainlv_top_tv_budget);
        topConTv = headerView.findViewById(R.id.item_mainlv_top_tv_day);
//        topShowIv = headerView.findViewById(R.id.item_mainlv_top_iv_hide);
        topbudgetTv.setOnClickListener(this);
        headerView.setOnClickListener(this);
//        topShowIv.setOnClickListener(this);
    }

    /* 获取今日的具体时间*/
    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    // 当activity获取焦点时，会调用的方法
    @Override
    protected void onResume() {
        super.onResume();
        loadDBData();
        setTopTvShow();

    }

    private void setTopTvShow() {
        //获取今日支出和收入总金额，显示在view当中
        float incomeOneDay = DBManager.getSumMoneyOneDay(year, month, day, 1);
        float outcomeOneDay = DBManager.getSumMoneyOneDay(year, month, day, 0);
        String infoOneDay = "今日支出 ￥"+outcomeOneDay+"  收入 ￥"+incomeOneDay;
        topConTv.setText(infoOneDay);
        //获取本月收入和支出总金额
        float incomeOneMonth = DBManager.getSumMoneyOneMonth(year, month, 1);
        float outcomeOneMonth = DBManager.getSumMoneyOneMonth(year, month, 0);
        topInTv.setText("￥"+incomeOneMonth);
        topOutTv.setText("￥"+outcomeOneMonth);
        //设置显示预算剩余
        float bmoney = preferences.getFloat("bmoney",0);  //获取预算
        if (bmoney==0){
            topbudgetTv.setText("￥ 0");
        }else{
            float syMoney = bmoney-outcomeOneMonth;
            topbudgetTv.setText("￥"+syMoney);
        }

    }

    private void loadDBData() {
        List<AccountBean> list = DBManager.getAccountListOneDayFromAccounttb(year, month, day);
        mDatas.clear();
        mDatas.addAll(list);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_iv_search:  //搜索
                Intent it = new Intent(this, SearchActivity.class);  //跳转界面
                startActivity(it);
                break;
            case R.id.main_btn_edit:   //添加记录
                Intent it1 = new Intent(this, RecordActivity.class);  //跳转界面
                startActivity(it1);
                break;
            case R.id.item_mainlv_top_tv3:  //设置预算
                showBudgetDialog();
                break;

        }
    }

    /*显示预算设置对话框*/
    private void showBudgetDialog() {
        BudgetDialog dialog = new BudgetDialog(this);
        dialog.show();
        dialog.setDialogSize();
        dialog.setOnEnsureListener(new BudgetDialog.OnEnsureListener() {
            @Override
            public void onEnsure(float money) {
                //将预算金额写入到共享参数当中，进行存储
                SharedPreferences.Editor editor = preferences.edit();
                editor.putFloat("bmoney",money);
                editor.commit();

                //计算剩余金额
                float outcomeOneMonth = DBManager.getSumMoneyOneMonth(year,month,0);
                float syMoney = money-outcomeOneMonth;  //预算剩余 = 预算-支出
                topbudgetTv.setText("￥"+syMoney);
            }
        });
    }


    /**
     * 动画实现，因为都有角度，所有，要有三角函数计算
     * 使图标位移距离相等，实现扇形效果
     */
    //收起按钮
    private void closeIcon() {

        //设置动画时间
        int duration = 600;
        //动画距离,屏幕宽度的60%
        float distance = getScreenWidth()*0.3f;
        //相邻ImageView运动角度式22.5度
        float angle2 = (float)(135f*Math.PI/180);
        float angle3 = (float)(45.5f*Math.PI/180);

        Interpolator interpolator = getInterpolator(0.2f, 1f, 0.2f, 1f);
        menuBtn.animate().rotation(0f).scaleX(1f).scaleY(1f).setDuration(duration).setInterpolator(interpolator);
        editBtn.animate().alpha(0).translationX(0).translationY(0).setDuration(duration).setInterpolator(interpolator);
        anlBtn.animate().alpha(0).translationX(0).translationY(0).setDuration(duration).setInterpolator(interpolator);


    }

    //显示更多按钮
    private void showIcon() {
        //设置动画时间
        int duration = 600;
        //动画距离,屏幕宽度的30%
        float distance = getScreenWidth()*0.3f;
        //相邻ImageView运动角度式22.5度
        float angle2 = (float)(135f*Math.PI/180);
        float angle3 = (float)(45.5f*Math.PI/180);

        Interpolator interpolator = getInterpolator(0.2f, 1f, 0.2f, 1f);
        float translationX_icon1 = (float) (distance * Math.cos(angle2));
        float translationY_icon1 = -(float) (distance * Math.sin(angle2));
        float translationX_icon2 = (float) (distance * Math.cos(angle3));
        float translationY_icon2 = -(float) (distance * Math.sin(angle3));
        menuBtn.animate().rotation(45f).scaleX(0.4f).scaleY(0.4f).setDuration(duration).setInterpolator(interpolator);
        editBtn.animate().alpha(1).translationX(translationX_icon1).translationY(translationY_icon1)
                .setDuration(duration).setInterpolator(interpolator);
        anlBtn.animate().alpha(1).translationX(translationX_icon2).translationY(translationY_icon2)
                .setDuration(duration).setInterpolator(interpolator);
    }

    //竖屏时获取屏幕宽度，横屏时，获取高度
    private int getScreenWidth() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int x = outMetrics.widthPixels;
        int y = outMetrics.heightPixels;
        return x>y?y:x;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Interpolator getInterpolator(float x1, float x2, float y1, float y2) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 21) {
            return new PathInterpolator(x1, x2, y1, y2);
        } else {
            return new LinearInterpolator();
        }
    }


}
