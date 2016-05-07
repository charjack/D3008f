package com.youchuang.dongfeng3008.vo;

/**
 * Created by NANA on 2016/5/7.
 */
public class Contents {
    public static final int MUSIC_PROGRESS = 1;  //刷新音乐的进度条和时间
    public static final int MUSIC_LOAD_FINISH= 12;//音乐数据加载完之后的消息
    public static final int MUSIC_NO_CHANGE= 113; //程序退出后，再次进入数据没有改变
    public static final int MUSIC_REFRESH_INFO_UI= 2;//刷新音乐Fragment界面
    public static final int VIDEO_LOAD_FINISH= 22; //视频数据加载完成
    public static final int VIDEO_COME_BACK= 23;  //按退出键或home键，再次返回的消息
    public static final int IMAGE_ITEM_CLICK= 30; //点击gridview的条目消息
    public static final int IMAGE_PPT_COMEBACK= 31; //ppt结束返回消息
    public static final int IMAGE_LOAD_FINISH= 32; //图片加载完成
}
