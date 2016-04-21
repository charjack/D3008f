package com.youchuang.dongfeng3008;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicFragment extends Fragment {

    public int[] music_play_mode_resource_ico = {R.mipmap.suiji_ico,R.mipmap.shunxu_ico,R.mipmap.quanbuxunhuan_ico,R.mipmap.danquxunhuan_ico};
    public String[] button_play_mode_name_ico = {"随机播放","顺序播放","全部循环","单曲播放"};
    ImageView button_play_mode_ico;
    TextView button_play_mode_name;
    public MusicFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  =inflater.inflate(R.layout.fragment_music, container, false);
        button_play_mode_ico = (ImageView) view.findViewById(R.id.button_play_mode_ico);
        button_play_mode_name = (TextView) view.findViewById(R.id.button_play_mode_name);
        return view;
    }

    void changeMusicPlayModeUI(int playmode){
        button_play_mode_ico.setImageResource(music_play_mode_resource_ico[playmode]);
        button_play_mode_name.setText(button_play_mode_name_ico[playmode]);
    }

}
