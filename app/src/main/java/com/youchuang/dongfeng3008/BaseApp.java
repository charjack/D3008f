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
    public static boolean ifFullScreenState = false;     //记录视频是否是全屏的状态
    public static int ifopenliebiao = 0;                 //判断列表是否打开了
    public static boolean ifdebug = true;                //调试模式是否打开
    public static int current_media = 0;                 //0:music 1: video 2:picture
    public static int music_play_mode = 0;               //记录音乐播放模式 具体播放模式见 service
    public static int video_play_mode = 1;               //记录视频播放模式  具体播放模式和音乐常量差不多
    public static int current_music_play_num = -1;       //当前的播放曲目，开始时赋值为-1，表示没有选中的曲目，这点很重要，不能设置为0
    public static int current_music_play_progress = 0;   //记录当前音乐播放的进度
    public static int current_video_play_num = -1;       //当前视频播放的曲目
    public static int current_video_play_progress = 0;   //记录当前视频播放的进度
    public static int current_fragment = 0;              //当前fragment是哪个  0:音乐 1:视频 2:图片
    public static int isfirststartmusic = 1;             //判断是否是第一次启动音乐，主要是从视频进入音乐的fragment需要刷新音乐的ui
    public static int current_pic_play_num = -1;         //记录当前图片的number
    public static boolean iffirststart = true;           //判断是否是第一次启动，如果是需要扫描sp和数据库文件
    public static Context context;
    public static boolean exitUI = false;                //音频焦点的得失的一个判断条件
    public static DbUtils dbUtils;                       //
    public static int statebarheight = 0;                //状态栏的高度
    public static int dibuheight = 0;                    //activity的底部按钮布局的高度
    public static boolean isVideostop = false;           //暂停状态下返回来之后依然，暂停
    static ArrayList<Mp3Info>  mp3Infos = new ArrayList<>();
    public static boolean media_already_ok = false;      //判断是否进入的时候，contentprovider中的数据就已经好了
    public static int music_media_state_scan = 0;        //判断当前媒体文件的扫描状态，主要是在扫描的是否，防止多次去刷新ui界面
    public static int video_media_state_scan = 0;        //判断当前媒体文件的扫描状态，主要是在扫描的是否，防止多次去刷新ui界面
    public static int pic_media_state_scan = 0;          //判断当前媒体文件的扫描状态，主要是在扫描的是否，防止多次去刷新ui界面
    public static int first_select_music = 0;            //避免每次都去刷新ui，而只是去刷新曲目就可以了
    public static boolean when_scan_click = false;       //判断scan的时候，是否点击了item

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        dbUtils = DbUtils.create(getApplicationContext(),"DongfengVideoSave.db");
    }
}
