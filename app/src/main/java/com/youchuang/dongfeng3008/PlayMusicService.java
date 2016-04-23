package com.youchuang.dongfeng3008;

import android.app.Service;
import android.content.Intent;
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
    public int play_mode = RANDOM_PLAY;

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
            switch(play_mode){
                case RANDOM_PLAY:
                    play(random.nextInt(mp3Infos.size()));
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
        if(position<0&&position>=mp3Infos.size()){
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
            musicUpdateListener.onStop(1);
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
        play_mode = RANDOM_PLAY;

        mPlayer = new MediaPlayer();
        mp3Infos = MediaUtils.getMp3Infos(this);
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
