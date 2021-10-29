package com.example.maccountbook.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.maccountbook.R;

public class DBOpenHelper extends SQLiteOpenHelper {
    public DBOpenHelper(@Nullable Context context) {
        super(context,"tally.db" , null, 1);
    }

    //    创建数据库的方法，只有项目第一次运行时，会被调用
    String beizhu;   //备注
    float money;  //价格
    String time ;  //保存时间字符串
    int year;
    int month;
    int day;
    int kind;   //类型
    @Override
    public void onCreate(SQLiteDatabase db) {
//        创建表示类型的表
        String sql = "create table typetb(id integer primary key autoincrement,typename varchar(10),imageId integer,sImageId integer,kind integer)";
        db.execSQL(sql);
        insertType(db);
        //创建记账表
        sql = "create table accounttb(id integer primary key autoincrement,typename varchar(10),sImageId integer,beizhu varchar(80),money float," +
                "time varchar(60),year integer,month integer,day integer,kind integer)";
        db.execSQL(sql);
    }

    private void insertType(SQLiteDatabase db) {
//      向typetb表当中插入元素
        String sql = "insert into typetb (typename,imageId,sImageId,kind) values (?,?,?,?)";
        db.execSQL(sql,new Object[]{"餐饮", R.drawable.ic_canyin,R.drawable.ic_canyin_fs,0});
        db.execSQL(sql,new Object[]{"交通", R.drawable.ic_jiaotong,R.drawable.ic_jiaotong_fs,0});
        db.execSQL(sql,new Object[]{"购物", R.drawable.ic_gouwu,R.drawable.ic_gouwu_fs,0});
        db.execSQL(sql,new Object[]{"服饰", R.drawable.ic_fushi,R.drawable.ic_fushi_fs,0});
        db.execSQL(sql,new Object[]{"日用品", R.drawable.ic_riyongpin,R.drawable.ic_riyongpin_fs,0});
        db.execSQL(sql,new Object[]{"娱乐", R.drawable.ic_yule,R.drawable.ic_yule_fs,0});
        db.execSQL(sql,new Object[]{"零食", R.drawable.ic_lingshi,R.drawable.ic_lingshi_fs,0});
        db.execSQL(sql,new Object[]{"烟酒茶", R.drawable.ic_yanjiu,R.drawable.ic_yanjiu_fs,0});
        db.execSQL(sql,new Object[]{"学习", R.drawable.ic_xuexi,R.drawable.ic_xuexi_fs,0});
        db.execSQL(sql,new Object[]{"医疗", R.drawable.ic_yiliao,R.drawable.ic_yiliao_fs,0});
        db.execSQL(sql,new Object[]{"住宅", R.drawable.ic_zhufang,R.drawable.ic_zhufang_fs,0});
        db.execSQL(sql,new Object[]{"水电煤", R.drawable.ic_shuidianfei,R.drawable.ic_shuidianfei_fs,0});
        db.execSQL(sql,new Object[]{"通讯", R.drawable.ic_tongxun,R.drawable.ic_tongxun_fs,0});
        db.execSQL(sql,new Object[]{"人情", R.drawable.ic_renqingwanglai,R.drawable.ic_renqingwanglai_fs,0});
        db.execSQL(sql,new Object[]{"其他", R.drawable.ic_qita,R.drawable.ic_qita_fs,0});

        db.execSQL(sql,new Object[]{"薪资", R.drawable.in_xinzi,R.drawable.in_xinzi_fs,1});
        db.execSQL(sql,new Object[]{"奖金", R.drawable.in_jiangjin,R.drawable.in_jiangjin_fs,1});
        db.execSQL(sql,new Object[]{"借入", R.drawable.in_jieru,R.drawable.in_jieru_fs,1});
        db.execSQL(sql,new Object[]{"收债", R.drawable.in_shouzhai,R.drawable.in_shouzhai_fs,1});
        db.execSQL(sql,new Object[]{"利息收入", R.drawable.in_lixifuji,R.drawable.in_lixifuji_fs,1});
        db.execSQL(sql,new Object[]{"投资回报", R.drawable.in_touzi,R.drawable.in_touzi_fs,1});
        db.execSQL(sql,new Object[]{"二手交易", R.drawable.in_ershoushebei,R.drawable.in_ershoushebei_fs,1});
        db.execSQL(sql,new Object[]{"意外所得", R.drawable.in_yiwai,R.drawable.in_yiwai_fs,1});
        db.execSQL(sql,new Object[]{"其他", R.drawable.in_qt,R.drawable.in_qt_fs,1});
    }

    // 数据库版本在更新时发生改变，会调用此方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}