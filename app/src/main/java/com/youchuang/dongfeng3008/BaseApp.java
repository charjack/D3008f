package com.youchuang.dongfeng3008;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.lidroid.xutils.DbUtils;
import com.youchuang.dongfeng3008.vo.Mp3Info;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NANA on 2016/4/21.
 */
public class BaseApp extends Application{
    public static boolean ifFullScreenState = false;
    public static int ifopenliebiao = 0;
    public static boolean ifdebug = false;
    public static int current_media = 0; //0:music 1: video 2:picture
    public static int music_play_mode = 0;
    public static int video_play_mode = 1;
    public static int current_music_play_num = -1;
    public static int current_music_play_progress = 0;
    public static int current_video_play_num = -1;
    public static int current_video_play_progress = 0;
    public static int current_fragment = 0;
    public static int isfirststartmusic = 1;
    public static int current_pic_play_num = -1;
    public static boolean iffirststart = true;
    public static Context context;
    public static boolean exitUI = false;
    public static DbUtils dbUtils;
    public static int statebarheight = 0;
    public static int dibuheight = 0;
    public static boolean isVideostop = false;   //暂停状态下返回来之后依然，暂停
    static ArrayList<Mp3Info>  mp3Infos = new ArrayList<>();
    public static boolean media_already_ok = false;
    public static int music_media_state_scan = 0; //
    public static int video_media_state_scan = 0; //
    public static int pic_media_state_scan = 0; //
    public static int first_select_music = 0;    //避免每次都去刷新ui，而只是去刷新曲目就可以了
    public static boolean when_scan_click = false;  //判断scan的时候，是否点击了item

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        dbUtils = DbUtils.create(getApplicationContext(),"DongfengVideoSave.db");

//        SharedPreferences mySharedPreferences = getSharedPreferences("DongfengDataSave", Activity.MODE_PRIVATE);
//        SharedPreferences.Editor editor = mySharedPreferences.edit();
        //用putString的方法保存数据
//        editor.putString("VERSON", "NONE");
//        editor.putString("MUSICID", "0");
        //提交当前数据
//        editor.commit();

    }
}
