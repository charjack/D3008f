package com.youchuang.dongfeng3008;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;

/**
 * Created by NANA on 2016/4/22.
 */
public abstract class BaseActivity extends Activity{

    public PlayMusicService playMusicService;
    public boolean isbound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            PlayMusicService.PlayMusicBinder playMusicBinder = (PlayMusicService.PlayMusicBinder) service;
            playMusicService = playMusicBinder.getPlayService();

            playMusicService.setMusicUpdateListener(musicUpdateListener);
            musicUpdateListener.onChange(playMusicService.getCurrentPosition());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            playMusicService =null;
        }
    };
    public void bindPlayMusicService(){
        if(isbound == false)
        {
            Intent intent = new Intent(this, PlayMusicService.class);
            bindService(intent,conn, Context.BIND_AUTO_CREATE);
            isbound = true;
        }
    }

    public void unbindPlayMusicService(){
        if(isbound==true)
        {
            unbindService(conn);
            isbound = false;
        }
    }

    private PlayMusicService.MusicUpdateListener musicUpdateListener = new PlayMusicService.MusicUpdateListener(){
        @Override
        public void onPublic(int progress) {
                publish(progress);
        }

        @Override
        public void onChange(int position) {
                change(position);
        }

        @Override
        public void onStop(int isstop) {stop(isstop);}
    };


    public abstract void publish(int progress);
    public abstract void change(int position);
    public abstract void stop(int position);
}
