package com.youchuang.dongfeng3008;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
/**
 * A simple {@link Fragment} subclass.
 */
public class MusicFragment extends Fragment {

    public int[] music_play_mode_resource_ico = {R.mipmap.suiji_ico,R.mipmap.shunxu_ico,R.mipmap.quanbuxunhuan_ico,R.mipmap.danquxunhuan_ico};
    public String[] button_play_mode_name_ico = {"随机播放","顺序播放","全部循环","单曲播放"};
    ImageView button_play_mode_ico;
    TextView button_play_mode_name;
    ImageView album_icon;
    TextView song_name,zhuanji_name,chuangzhe_name,num_order;

    SeekBar seekBar1;
    LinearLayout progress_really_layout;
    TextView song_current_time,song_total_time;
    RelativeLayout mp3_info_ui;

    public MusicUIUpdateListener musicUIUpdateListener;

    public MusicFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  =inflater.inflate(R.layout.fragment_music, container, false);

        mp3_info_ui = (RelativeLayout) view.findViewById(R.id.mp3_info_ui);
        mp3_info_ui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BaseApp.ifopenliebiao == 1) {
                    BaseApp.ifopenliebiao = 0;
                    musicUIUpdateListener.onLieBiaoClose();
                }
            }
        });

        button_play_mode_ico = (ImageView) view.findViewById(R.id.button_play_mode_ico);
        button_play_mode_name = (TextView) view.findViewById(R.id.button_play_mode_name);

        album_icon = (ImageView) view.findViewById(R.id.album_icon);
        song_name = (TextView) view.findViewById(R.id.song_name);
        zhuanji_name = (TextView) view.findViewById(R.id.zhuanji_name);
        chuangzhe_name = (TextView) view.findViewById(R.id.chuangzhe_name);
        num_order = (TextView) view.findViewById(R.id.num_order);

        progress_really_layout = (LinearLayout) view.findViewById(R.id.progress_really_layout);
        seekBar1 = (SeekBar) view.findViewById(R.id.seekBar1);
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    BaseApp.current_music_play_progress = progress;
                    seekBar.setProgress(progress);
                    musicUIUpdateListener.onServiceCommand(1);  //拖动
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                musicUIUpdateListener.onServiceCommand(2);  //开始拖
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicUIUpdateListener.onServiceCommand(3);  //拖动结束
            }
        });

        song_current_time=(TextView) view.findViewById(R.id.song_current_time);
        song_total_time=(TextView) view.findViewById(R.id.song_total_time);
        return view;
    }

    void changeMusicPlayModeUI(int playmode){
        button_play_mode_ico.setImageResource(music_play_mode_resource_ico[playmode]);
        button_play_mode_name.setText(button_play_mode_name_ico[playmode]);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        musicUIUpdateListener = (MusicUIUpdateListener) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(BaseApp.isfirststartmusic == 0){
            musicUIUpdateListener.onUIChange(BaseApp.current_music_play_num);
            BaseApp.isfirststartmusic = 1; //只有从视频转入音乐界面的时候，才进行此项ui刷新工作
        }
    }

    public interface MusicUIUpdateListener{
        public void onUIChange(int position);
        public void onServiceCommand(int i);
        public void onLieBiaoClose();
    }

}
