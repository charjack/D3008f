package com.youchuang.dongfeng3008;

import android.app.Service;
import android.content.Intent;
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
    private MediaPlayer mPlayer;
    private int currentPosition;
    ArrayList<Mp3Info> mp3Infos;
    private boolean isPause = false;

    public static final int RANDOM_PLAY = 0;  //随机
    public static final int ORDER_PLAY = 1;   //顺序
    public static final int RECELY_PLAY = 2;  //全部循环
    public static final int SINGLE_PLAY = 3;  //单曲循环
    public int play_mode = BaseApp.music_play_mode;

    public ArrayList<Mp3Info> getMp3Infos() {
        return mp3Infos;
    }
    public void setMp3Infos(ArrayList<Mp3Info> mp3Infos) {
        this.mp3Infos = mp3Infos;
    }
    public void setPlay_mode(int play_mode) {
        this.play_mode = play_mode;
    }
    private MusicUpdateListener musicUpdateListener;

    private ExecutorService es = Executors.newSingleThreadExecutor();
    public PlayMusicService() {
    }
    public int getCurrentPosition(){
        return currentPosition;
    }

    private Random random = new Random();

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(play_mode != BaseApp.music_play_mode){
            play_mode = BaseApp.music_play_mode;
        }
            switch(play_mode){
                case RANDOM_PLAY:
                    if(random.nextInt(mp3Infos.size()) == currentPosition) {
                        play(random.nextInt(mp3Infos.size()));
                    }
                    else{
                        play(random.nextInt(mp3Infos.size()));
                    }
                    break;
                case ORDER_PLAY:
                    nextOrder(); //判断最后一首歌的时候停止
                    break;
                case RECELY_PLAY:
                    next();
                    break;
                case SINGLE_PLAY:
                    play(currentPosition);
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
                        System.out.println("获得焦点");
                        mPlayer.setVolume(1.0f, 1.0f);
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS://长时间失去焦点
                        System.out.println("长时间失去焦点");
                        //只有退出了界面才去判定长时间对视焦点
                        if (mPlayer != null && BaseApp.exitUI) {
                            if (mPlayer.isPlaying()) {
                                mPlayer.stop();
                            }
                            //     mPlayer.release();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://暂时失去，很快重新获取，可以保留资源
                        System.out.println("暂时失去，很快重新获取，可以保留资源");
                        if (mPlayer != null) {
                            if (mPlayer.isPlaying()) {
                                mPlayer.stop();
                            }
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://暂时失去焦点，声音降低，但还是在播放
                        System.out.println("暂时失去焦点，声音降低，但还是在播放");
                        if (mPlayer != null) {
                            if (mPlayer.isPlaying()) {
                                mPlayer.setVolume(0.1f, 0.1f);
                            }
                        }
                        break;
                }
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if(position<0 || position>=mp3Infos.size()){
            position = 0;
        }
        mp3Info = mp3Infos.get(position);
        try {
            mPlayer.reset();
            mPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));
            mPlayer.prepare();
            mPlayer.start();
            currentPosition = position;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(musicUpdateListener!=null){
            musicUpdateListener.onChange(currentPosition);
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
        if(currentPosition+1 >= mp3Infos.size()){
            currentPosition = 0;
        }else
        {
            currentPosition++;
        }
        play(currentPosition);
    }

    public void nextOrder(){
        if(currentPosition+1 >= mp3Infos.size()){
            mPlayer.stop();
            musicUpdateListener.onStop(1);  //顺序播放，到最后一首停止
        }else
        {
            currentPosition++;
            play(currentPosition);
        }
    }

    public void prev(){
        if(currentPosition-1 < 0){
            currentPosition = mp3Infos.size()-1;
        }else
        {
            currentPosition--;
        }
        play(currentPosition);
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

        //下面这两个参数以后需要从sp中获取
        currentPosition = 0;
        play_mode = BaseApp.music_play_mode;

        mp3Infos = MediaUtils.getMp3Infos(this);
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
