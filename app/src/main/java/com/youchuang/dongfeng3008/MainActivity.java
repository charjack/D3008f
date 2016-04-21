package com.youchuang.dongfeng3008;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener {

    ImageButton button_shangqu,button_bofang,button_xiaqu,button_play_mode,button_liebiao;
    MusicFragment musicFragment;
    RelativeLayout leibieliebiao;
    ListView musicvideolist;
    MymusiclistviewAdapter mymusiclistviewAdapter;
    public int[] music_play_mode_resource = {R.mipmap.suiji,R.mipmap.shunxu,R.mipmap.quanbuxunhuan,R.mipmap.danquxunhuan};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_shangqu = (ImageButton) findViewById(R.id.button_shangqu);
        button_bofang = (ImageButton) findViewById(R.id.button_bofang);
        button_xiaqu = (ImageButton) findViewById(R.id.button_xiaqu);
        button_play_mode = (ImageButton) findViewById(R.id.button_play_mode);
        button_liebiao = (ImageButton) findViewById(R.id.button_liebiao);

        button_shangqu.setOnClickListener(this);
        button_bofang.setOnClickListener(this);
        button_xiaqu.setOnClickListener(this);
        button_play_mode.setOnClickListener(this);
        button_liebiao.setOnClickListener(this);

        leibieliebiao = (RelativeLayout) findViewById(R.id.leibieliebiao);
        musicvideolist = (ListView) findViewById(R.id.musicvideolist);
        musicvideolist.setOnItemClickListener(this);
        addFragmentLayout();
    }

    private void addFragmentLayout() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        musicFragment = new MusicFragment();
        ft.add(R.id.media_fragment,musicFragment);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_shangqu:
                break;
            case R.id.button_bofang:
                break;
            case R.id.button_xiaqu:
                break;
            case R.id.button_play_mode:
                if(BaseApp.current_media == 0) {
                    BaseApp.music_play_mode++;
                    if (BaseApp.music_play_mode >= 4) {
                        BaseApp.music_play_mode = 0;
                    }
                    button_play_mode.setImageResource(music_play_mode_resource[BaseApp.music_play_mode]);
                    musicFragment.changeMusicPlayModeUI(BaseApp.music_play_mode);
                }else if(BaseApp.current_media == 1){

                }
                    break;
            case R.id.button_liebiao:
                if(BaseApp.ifopenliebiao == 0) {
                    BaseApp.ifopenliebiao =1;
                    leibieliebiao.setVisibility(View.VISIBLE);
                    if(BaseApp.current_media == 0) {
                        mymusiclistviewAdapter = new MymusiclistviewAdapter(this,getmusiclists());
                        musicvideolist.setAdapter(mymusiclistviewAdapter);
                    }else if(BaseApp.current_media == 1){

                    }else if(BaseApp.current_media == 2){

                    }
                }else{
                    BaseApp.ifopenliebiao = 0;
                    leibieliebiao.setVisibility(View.GONE);
                }
                break;
        }
    }


    public List<String> getmusiclists(){
        BaseApp.muscilists.add("llll");
        BaseApp.muscilists.add("2222");
        BaseApp.muscilists.add("3333");
        BaseApp.muscilists.add("4444");
        BaseApp.muscilists.add("5555");
        BaseApp.muscilists.add("6666");
        BaseApp.muscilists.add("7777");
        BaseApp.muscilists.add("8888");
        BaseApp.muscilists.add("9999");
        BaseApp.muscilists.add("aaaa");
        BaseApp.muscilists.add("bbbb");
        BaseApp.muscilists.add("cccc");
        BaseApp.muscilists.add("dddd");
        return BaseApp.muscilists;
    }

    //点击之后改变颜色
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BaseApp.current_music_play_num = position;
        mymusiclistviewAdapter.notifyDataSetChanged();
    }
}
