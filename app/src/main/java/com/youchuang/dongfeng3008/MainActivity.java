package com.youchuang.dongfeng3008;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.youchuang.dongfeng3008.Utils.MediaUtils;
import com.youchuang.dongfeng3008.vo.Mp3Info;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener,SeekBar.OnSeekBarChangeListener {

    ImageButton button_shangqu,button_bofang,button_xiaqu,button_play_mode,button_liebiao;
    SeekBar seekBar1;
    static TextView song_current_time,song_total_time;
    MusicFragment musicFragment;
    RelativeLayout leibieliebiao;
    ListView musicvideolist;
    TextView no_music_resource;
    MymusiclistviewAdapter mymusiclistviewAdapter;
    ArrayList<Mp3Info>  mp3Infos;
    private static MyHandler myHandler;
    public int seekbarmin,seekbarmax;

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
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar1.setOnSeekBarChangeListener(this);
//        seekBar1.setMax(100);
//        seekBar1.setProgress(4);
        song_current_time=(TextView) findViewById(R.id.song_current_time);
        song_total_time=(TextView) findViewById(R.id.song_total_time);

        button_shangqu.setOnClickListener(this);
        button_bofang.setOnClickListener(this);
        button_xiaqu.setOnClickListener(this);
        button_play_mode.setOnClickListener(this);
        button_liebiao.setOnClickListener(this);

        leibieliebiao = (RelativeLayout) findViewById(R.id.leibieliebiao);
        musicvideolist = (ListView) findViewById(R.id.musicvideolist);
        musicvideolist.setOnItemClickListener(this);
        no_music_resource = (TextView) findViewById(R.id.no_music_resource);
        addFragmentLayout();
        myHandler = new MyHandler();
        Intent intent = new Intent(this, PlayMusicService.class);
        startService(intent); //启动服务
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindPlayMusicService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindPlayMusicService();
    }

    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 1:
                    song_current_time.setText(MediaUtils.formatTime(msg.arg1));
                    break;
            }
        }
    }
    @Override
    public void stop(int isstop) {
        if(isstop == 1){
            button_bofang.setImageResource(R.mipmap.zanting);
        }
    }



    @Override
    public void publish(int progress) {
        seekBar1.setProgress(progress);
//        System.out.println("progress-------" + progress);
//        Toast.makeText(getApplicationContext(),"progress-----" + progress,Toast.LENGTH_SHORT).show();
//        song_current_time.setText(MediaUtils.formatTime(progress));
//        if(seekbarmin>progress){
//            seekBar1.setProgress(seekbarmin);
//        }else if(progress>seekbarmax){
//            seekBar1.setProgress(seekbarmax);
//        }else{
//            seekBar1.setProgress(progress);
//        }
        seekBar1.setProgress(progress);
        Message msg = myHandler.obtainMessage(1);
        msg.arg1 = progress;
        myHandler.sendMessage(msg);
    }

    @Override
    public void change(int position) {

        Mp3Info mp3Info = new Mp3Info();
        if(playMusicService.mp3Infos.size()>0) {
            BaseApp.current_music_play_num = position;
            if (BaseApp.ifopenliebiao == 1) {
                mymusiclistviewAdapter.notifyDataSetChanged();
            }

            mp3Info = playMusicService.mp3Infos.get(BaseApp.current_music_play_num);

            Bitmap albumBitmap = MediaUtils.getArtwork(this, mp3Info.getId(), mp3Info.getAlbumId(), true, false);
//        ImageView album_icon;
//        TextView song_name,zhuanji_name,chuangzhe_name;
            musicFragment.album_icon.setImageBitmap(albumBitmap);
            musicFragment.song_name.setText(mp3Info.getTittle());
            musicFragment.zhuanji_name.setText(mp3Info.getAlbum());
            musicFragment.chuangzhe_name.setText(mp3Info.getArtist());
            musicFragment.num_order.setText((BaseApp.current_music_play_num + 1) + "/" + playMusicService.mp3Infos.size());

            song_total_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));

            seekbarmin = (int) (mp3Info.getDuration() / 25);
            seekbarmax = (int) (mp3Info.getDuration() - seekbarmin);
            seekBar1.setProgress(seekbarmin);
            seekBar1.setMax((int) mp3Info.getDuration());

            if (playMusicService.isPlaying()) {
                button_bofang.setImageResource(R.mipmap.bofang);
            } else {
                button_bofang.setImageResource(R.mipmap.zanting);
            }
        }
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

        mp3Infos = MediaUtils.getMp3Infos(this);

        switch (v.getId()){
            case R.id.button_shangqu:
                BaseApp.ifopenliebiao = 0;
                leibieliebiao.setVisibility(View.GONE);
                playMusicService.prev();
                break;
            case R.id.button_bofang:
                BaseApp.ifopenliebiao = 0;
                leibieliebiao.setVisibility(View.GONE);

                if(playMusicService.isPlaying()){
                    button_bofang.setImageResource(R.mipmap.zanting);
                    playMusicService.pause();
                }else{
                    if(playMusicService.isPause()){
                        button_bofang.setImageResource(R.mipmap.bofang);
                        playMusicService.start();}
                    else{
                        playMusicService.play(BaseApp.current_music_play_num);
                    }
                }

                break;
            case R.id.button_xiaqu:
                BaseApp.ifopenliebiao = 0;
                leibieliebiao.setVisibility(View.GONE);
                playMusicService.next();
                break;
            case R.id.button_play_mode:
                BaseApp.ifopenliebiao = 0;
                leibieliebiao.setVisibility(View.GONE);
                if(BaseApp.current_media == 0) {
                    BaseApp.music_play_mode++;
                    if (BaseApp.music_play_mode >= 4) {
                        BaseApp.music_play_mode = 0;
                    }
                    button_play_mode.setImageResource(music_play_mode_resource[BaseApp.music_play_mode]);
                    musicFragment.changeMusicPlayModeUI(BaseApp.music_play_mode);
                    playMusicService.setPlay_mode(BaseApp.music_play_mode);
                }else if(BaseApp.current_media == 1){

                }
                    break;
            case R.id.button_liebiao:
                if(BaseApp.ifopenliebiao == 0) {
                    BaseApp.ifopenliebiao =1;
                    leibieliebiao.setVisibility(View.VISIBLE);
                    if(BaseApp.current_media == 0) {
                        mp3Infos = MediaUtils.getMp3Infos(this);
                       if( mp3Infos.size() == 0) {
                           no_music_resource.setVisibility(View.VISIBLE);
                           musicvideolist.setVisibility(View.GONE);
                       }else {
                           no_music_resource.setVisibility(View.GONE);
                           musicvideolist.setVisibility(View.VISIBLE);
                           mymusiclistviewAdapter = new MymusiclistviewAdapter(this, mp3Infos);
                           musicvideolist.setAdapter(mymusiclistviewAdapter);
                           if(BaseApp.current_music_play_num<0){
                                musicvideolist.setSelection(BaseApp.current_music_play_num+1);
                               mymusiclistviewAdapter.notifyDataSetChanged();
                           } else{
                               musicvideolist.setSelection(BaseApp.current_music_play_num);
                               mymusiclistviewAdapter.notifyDataSetChanged();
                           }
                       }
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

    //点击之后改变颜色
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BaseApp.current_music_play_num = position;
        mymusiclistviewAdapter.notifyDataSetChanged();
        playMusicService.play(BaseApp.current_music_play_num);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){

            seekBar.setProgress(progress);
            playMusicService.seek(progress);

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        playMusicService.pause();
        button_bofang.setImageResource(R.mipmap.zanting);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        playMusicService.start();
        button_bofang.setImageResource(R.mipmap.bofang);
    }
}
