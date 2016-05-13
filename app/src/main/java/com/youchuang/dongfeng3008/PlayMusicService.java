package com.youchuang.dongfeng3008;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.youchuang.dongfeng3008.Utils.MediaUtils;
import com.youchuang.dongfeng3008.vo.Mp3Info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayMusicService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener{
    private static final String TAG = "PlayMusicService";
    private MediaPlayer mPlayer;
    public  ArrayList<Mp3Info> mp3Infos;
    private boolean isPause = false;

    public static final int RANDOM_PLAY = 0;  //随机
    public static final int ORDER_PLAY = 1;   //顺序
    public static final int RECELY_PLAY = 2;  //全部循环
    public static final int SINGLE_PLAY = 3;  //单曲循环
    public int play_mode = BaseApp.music_play_mode;

    public void setPlay_mode(int play_mode) {
        this.play_mode = play_mode;
    }
    private MusicUpdateListener musicUpdateListener;

    private ExecutorService es = Executors.newSingleThreadExecutor();
    public PlayMusicService() {
    }
    public int getCurrentPosition(){
        return BaseApp.current_music_play_num;
    }

    private Random random = new Random();

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(play_mode != BaseApp.music_play_mode){
            play_mode = BaseApp.music_play_mode;
        }

            switch(play_mode){
                case RANDOM_PLAY:
                    if(random.nextInt(BaseApp.mp3Infos.size()) == BaseApp.current_music_play_num) {
                        play(random.nextInt(BaseApp.mp3Infos.size()));
                    }
                    else{
                        play(random.nextInt(BaseApp.mp3Infos.size()));
                    }
                    break;
                case ORDER_PLAY:
                    nextOrder(); //判断最后一首歌的时候停止
                    break;
                case RECELY_PLAY:
                    next();
                    break;
                case SINGLE_PLAY:
                    play(BaseApp.current_music_play_num);
                    break;
                default:break;
            }
    }

    public void play(int position){
        Mp3Info mp3Info = null;
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        am.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:  //获得焦点
                        if(BaseApp.ifdebug) {
                            System.out.println(TAG+"-play-"+"获得焦点");
                        }
                        mPlayer.setVolume(1.0f, 1.0f);
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS://长时间失去焦点
                        if(BaseApp.ifdebug){
                            System.out.println(TAG+"-play-"+"长时间失去焦点");
                        }
                        //只有退出了界面才去判定长时间对视焦点
                        if (mPlayer != null && BaseApp.exitUI) {
                            if (mPlayer.isPlaying()) {
                                mPlayer.stop();
                            }
                            //     mPlayer.release();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://暂时失去，很快重新获取，可以保留资源
                        if(BaseApp.ifdebug) {
                            System.out.println(TAG+"-play-"+"暂时失去，很快重新获取，可以保留资源");
                        }
                        if (mPlayer != null) {
                            if (mPlayer.isPlaying()) {
                                mPlayer.stop();
                            }
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://暂时失去焦点，声音降低，但还是在播放
                        if(BaseApp.ifdebug) {
                            System.out.println(TAG+"-play-"+"暂时失去焦点，声音降低，但还是在播放");
                        }
                        if (mPlayer != null) {
                            if (mPlayer.isPlaying()) {
                                mPlayer.setVolume(0.1f, 0.1f);
                            }
                        }
                        break;
                }
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        mp3Info = BaseApp.mp3Infos.get(position);
        try {
            mPlayer.reset();
            mPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));
            mPlayer.prepare();
            mPlayer.start();
            BaseApp.current_music_play_num = position;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(musicUpdateListener!=null){
            System.out.println("currentposition-----"+BaseApp.current_music_play_num);
            musicUpdateListener.onChange(BaseApp.current_music_play_num);
        }

        isPause = false;

    }

    public boolean isPlaying(){
        if(mPlayer !=null){
            return mPlayer.isPlaying();
        }
        return false;
    }

    public void pause(){
        if(mPlayer.isPlaying()){
            mPlayer.pause();
            isPause = true;
        }
    }

    public boolean isPause(){return isPause;}

    public void next(){

        if(BaseApp.current_music_play_num+1 >= BaseApp.mp3Infos.size()){
            BaseApp.current_music_play_num = 0;
        }else
        {
            BaseApp.current_music_play_num++;
        }
        play(BaseApp.current_music_play_num);
    }

    public void nextOrder(){
        if(BaseApp.current_music_play_num+1 >= BaseApp.mp3Infos.size()){
            mPlayer.pause();
            musicUpdateListener.onStop(1);  //顺序播放，到最后一首暂停，不能停止，否则重新开始播放会出问题
        }else
        {
            BaseApp.current_music_play_num++;

            play(BaseApp.current_music_play_num);
        }
    }

    public void prev(){

        if(BaseApp.current_music_play_num-1 < 0){
            BaseApp.current_music_play_num = BaseApp.mp3Infos.size()-1;
        }else
        {
            BaseApp.current_music_play_num--;
        }
        play(BaseApp.current_music_play_num);
    }


    public void start(){
        if(mPlayer!=null && (!mPlayer.isPlaying())){
            mPlayer.start();//继续播放
            isPause = false;
        }
    }

    public int getcurrentProgress(){
        if(mPlayer!=null && mPlayer.isPlaying()){
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }
    public int getDuration(){
        return mPlayer.getDuration();
    }

    public void seek (int msec){
        mPlayer.seekTo(msec);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        mediaPlayer.reset();
        return false;
    }
    class PlayMusicBinder extends Binder {
        public PlayMusicService getPlayService(){
            return PlayMusicService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return new PlayMusicBinder();
    }


    @Override
    public void onCreate() {
        super.onCreate();

        //下面这个参数以后需要从sp中获取
        SharedPreferences sharedPreferences = getSharedPreferences("DongfengDataSave", Activity.MODE_PRIVATE);
        play_mode = sharedPreferences.getInt("MUSICPLAYMODE", 0);

        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);

        es.execute(updateStatusRunnable);
    }
    Runnable updateStatusRunnable = new Runnable() {
        @Override
        public void run() {
            while(true){
                if(musicUpdateListener!=null &&mPlayer!=null&& mPlayer.isPlaying()){
                    musicUpdateListener.onPublic(getcurrentProgress());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(es!=null&& !es.isShutdown()){
            es.shutdown();
            es=null;
        }
    }

    public interface MusicUpdateListener{
        public void onPublic(int progress);
        public void onChange(int position);
        public void onStop(int isstop);
    }

    public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener){
        this.musicUpdateListener = musicUpdateListener;
    }
}
