package com.youchuang.dongfeng3008.vo;

/**
 * Created by NANA on 2016/5/7.
 */
public class Contents {
    public static final int MUSIC_PROGRESS = 1;  //刷新音乐的进度条和时间
    public static final int MUSIC_LOAD_FINISH= 12;//音乐数据加载完之后的消息
//    public static final int MUSIC_NO_CHANGE= 113; //程序退出后，再次进入数据没有改变
    public static final int MUSIC_CHANGE= 114; //程序退出后，再次进入数据没有改变
    public static final int MUSIC_REFRESH_INFO_UI= 2;//刷新音乐Fragment界面
    public static final int MUSIC_REFRESH_INFO_UI2= 112;//扫描的时候刷新音乐Fragment界面
    public static final int VIDEO_LOAD_FINISH= 22; //视频数据加载完成
    public static final int VIDEO_COME_BACK= 23;  //按退出键或home键，再次返回的消息
    public static final int IMAGE_ITEM_CLICK= 30; //点击gridview的条目消息
    public static final int IMAGE_PPT_COMEBACK= 31; //ppt结束返回消息
    public static final int IMAGE_LOAD_FINISH= 32; //图片加载完成
    public static final int CHANGE_FRAGMENT_MUSCI_PLAY_MODE = 14;

    public static final int USB_STATE_MOUNTED = 1;
    public static final int USB_STATE_UNMOUNTED = 2;
    public static final int USB_STATE_SCANNER_STARTED = 3;
    public static final int USB_STATE_SCANNER_FINISHED = 4;

    public static final int DEVICE_STATE_UNMOUNTED = 0;
    public static final int DEVICE_STATE_MOUNTED = 1;
    public static final int DEVICE_STATE_SCANNER_STARTED = 2;
    public static final int DEVICE_STATE_SCANNER_FINISHED = 3;

    public static final int MSG_STATE_UNMOUNTED = 100;
    public static final int MSG_STATE_MOUNTED = 200;
    public static final int MSG_STATE_SCANNER_STARTED = 300;
    public static final int MSG_STATE_SCANNER_FINISHED = 400;

    public static final int MUSIC_REFRESH_TOTAL_NUM = 51;
}
