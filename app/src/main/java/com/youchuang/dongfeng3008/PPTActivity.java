package com.youchuang.dongfeng3008;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.youchuang.dongfeng3008.Utils.PicMediaUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;


public class PPTActivity extends Activity {

    ImageView imageView_PPT;
    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_ppt);

        imageView_PPT = (ImageView) findViewById(R.id.imageView_PPT);
        imageView_PPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PPTActivity.this,MainActivity.class);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        //启动定时器
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg =  new Message();
                msg.what = 1;
                pic_handler.sendMessage(msg);
            }
        },0,3000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    Handler pic_handler =  new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            imageView_PPT.setImageURI(Uri.parse(MainActivity.picInfos.get(BaseApp.current_pic_play_num).getData()));

            if(BaseApp.current_pic_play_num + 1 >= MainActivity.picInfos.size()){
                BaseApp.current_pic_play_num = MainActivity.picInfos.size() - 1;
                timer.cancel();
            }else{
                BaseApp.current_pic_play_num++;
            }
        }
    };

}
