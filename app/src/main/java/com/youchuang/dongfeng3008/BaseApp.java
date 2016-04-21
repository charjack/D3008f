package com.youchuang.dongfeng3008;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NANA on 2016/4/21.
 */
public class BaseApp extends Application{
    public static Context context;
    public static int ifopenliebiao = 0;
    public static boolean ifdebug = false;
    public static int current_media = 0; //0:music 1: video 2:picture
    public static int music_play_mode = 0;
    public static List<String> muscilists = new ArrayList<>();
    public static int current_music_play_num = -1;


    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        SharedPreferences mySharedPreferences = getSharedPreferences("DongfengDataSave", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        //用putString的方法保存数据
        editor.putString("VERSON", "NONE");

        //提交当前数据
        editor.commit();

    }
}
