package com.youchuang.dongfeng3008;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.youchuang.dongfeng3008.Utils.MyBitMap;
import com.youchuang.dongfeng3008.Utils.MyBitmapFactory;
import com.youchuang.dongfeng3008.Utils.NativeImageLoader;
import com.youchuang.dongfeng3008.Utils.PicMediaUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class PictureFragment extends Fragment{

    ImageView big_pic_show;
    MyPicHandler myPicHandler;
    TextView pic_shanglan_textview;
    boolean isshowtitle = false;
    Context ctx;

    private MyBitmapFactory myBitmapFactory;
    private Bitmap myBitmap;
    private double bigSize = 1.25;
    private double smallSize = 0.8;
    double size = 1;
    double pixel = 30.00;
    int bmpWidth;
    int bmpHight;
    int bmpSizeWidth;
    int bmpSizeHight;
    int x ;
    int y ;
    int screenWidth;      //
    int screenHeight;     //
    int dstHeight;
    int dstWidth;
    private boolean ifzoom = false;

    public PictureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ctx = (Context) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View pic_view = inflater.inflate(R.layout.fragment_picture, container, false);
        big_pic_show = (ImageView) pic_view.findViewById(R.id.big_pic_show);
        pic_shanglan_textview = (TextView) pic_view.findViewById(R.id.pic_shanglan_textview);
        big_pic_show.setOnTouchListener(new View.OnTouchListener() {

            int lastX;
            int lastY;
            int left;
            int right;
            int top;
            int bottom;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(ifzoom){

                }else {  //仅仅在不放大的情况下显示
                    if (isshowtitle) {//标题显示了，需要隐藏
                        pic_shanglan_textview.setVisibility(View.GONE);
                        isshowtitle = false;
                    } else {
                        String path = MainActivity.picInfos.get(BaseApp.current_pic_play_num).getData();
                        String parentName = new File(path).getParentFile().getName();
                        pic_shanglan_textview.setVisibility(View.VISIBLE);
                        pic_shanglan_textview.setText(parentName);
                        System.out.println(parentName);
                        isshowtitle = true;

                        Timer pic_timer = new Timer();
                        pic_timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                myPicHandler.sendEmptyMessage(2);
                            }
                        },5000);
                    }
                }
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int)event.getRawX();
                        lastY = (int)event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int)event.getRawX() - lastX;
                        int dy = (int)event.getRawY() - lastY;
                        Bitmap newBitmap;
                        if(bmpSizeWidth > screenWidth||bmpSizeHight > screenHeight){

                            if(bmpSizeWidth > screenWidth){
                                left = 0;
                                right = screenWidth;
                            }else{
                                left = v.getLeft() + dx;
                                right = v.getRight() + dx;

                            }

                            if(bmpSizeHight > screenHeight){
                                top = 0;
                                bottom = screenHeight;
                            }else {
                                top = v.getTop() + dy;
                                bottom = v.getBottom() + dy;
                            }
                            if((dx > 3 || dx < -3) && (dy > 3 ||dy  < -3)){//
                                newBitmap = movCal(myBitmap, dx, dy);
                                big_pic_show.setImageBitmap(newBitmap);
                            }
                        }
                        else{
                            left = v.getLeft() + dx;
                            top = v.getTop() + dy;
                            bottom = v.getBottom() + dy;
                            right = v.getRight() + dx;
                        }

                        v.layout(left, top, right, bottom);
                        lastX = (int)event.getRawX();
                        lastY = (int)event.getRawY();

                        break;
                }
                if(ifzoom) {
                    return true;
                }else{
                    return false;
                }
            }
        });
        myPicHandler = new MyPicHandler();
        init();
        return pic_view;
    }

    public double rowOrCowNum(int sizeBitmapWH, int screenWH){//
        double num = (sizeBitmapWH * size)/screenWH;
        return num;
    }

    public Bitmap movCal(Bitmap bitmap, int dx, int dy){//
        double coordinateX = rowOrCowNum(myBitmap.getWidth() , screenWidth);
        double coordinateY = rowOrCowNum(myBitmap.getHeight(), screenHeight);

        if(coordinateX > 1){
            if(dx > 0){
                x -= (myBitmap.getWidth()/(coordinateX * 4));
                if(x < 0){
                    x = 0;
                }

            }
            if(dx < 0){
                x += (myBitmap.getWidth()/(coordinateX *4));
                if(x > (myBitmap.getWidth() - bmpWidth)){
                    x = myBitmap.getWidth() - bmpWidth;
                }
            }
        }

        if(coordinateY > 1){
            if(dy > 0){
                y -= (myBitmap.getHeight()/(coordinateY * 4));
                if(y < 0){
                    y = 0;
                }

            }
            if(dy < 0){
                y += (myBitmap.getHeight()/(coordinateY *4));
                if(y > (myBitmap.getHeight() - bmpHight)){
                    y = myBitmap.getHeight() - bmpHight;
                }
            }
        }
        bitmap = Bitmap.createBitmap(bitmap, x, y, bmpWidth, bmpHight);
        bitmap = MyBitMap.createScaledBitmap(bitmap, dstWidth, dstHeight, ImageView.ScaleType.FIT_XY);
        return bitmap;
    }

    public void init(){
        myBitmapFactory = new MyBitmapFactory(ctx);
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        screenWidth  = dm.widthPixels;      //
        screenHeight = dm.heightPixels;     //
    }

    @Override
    public void onResume() {
        super.onResume();
        Message picture_msg = myPicHandler.obtainMessage(1);
        picture_msg.arg1 = BaseApp.current_pic_play_num ;
        myPicHandler.sendMessage(picture_msg);
    }

    public void changeImageShow(int position){   //已经在图片的fragment了
        ifzoom = false;
        size = 1;
         big_pic_show.setImageURI(Uri.parse(MainActivity.picInfos.get(position).getData()));
    }

    public void setImageShow(int position){
        ifzoom = false;
        size = 1;
        BaseApp.current_pic_play_num = position;
    }

    public void playPicpre(){
      //  System.out.println("current_pic_play_num: "+BaseApp.current_pic_play_num);
        ifzoom = false;
        size = 1;
        if(BaseApp.current_pic_play_num - 1 <0) {
            BaseApp.current_pic_play_num = MainActivity.picInfos.size()-1;
        }else{
            BaseApp.current_pic_play_num = BaseApp.current_pic_play_num - 1;
        }
     //   System.out.println("current_pic_play_num: "+BaseApp.current_pic_play_num);
        big_pic_show.setImageURI(Uri.parse(MainActivity.picInfos.get(BaseApp.current_pic_play_num).getData()));
    }

    public void playPicnext(){
        System.out.println("current_pic_play_num: "+BaseApp.current_pic_play_num+"picInfos.size()"+PicMediaUtils.getPicCounts(ctx));
        ifzoom = false;
        size = 1;
        if(BaseApp.current_pic_play_num + 1 > MainActivity.picInfos.size() - 1){
            BaseApp.current_pic_play_num = 0;
        } else{
            BaseApp.current_pic_play_num++;
        }
        System.out.println("current_pic_play_num: "+BaseApp.current_pic_play_num);
        big_pic_show.setImageURI(Uri.parse(MainActivity.picInfos.get(BaseApp.current_pic_play_num).getData()));
    }
    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[2*1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }
    public static Bitmap GetLocalOrNetBitmap(String url)
    {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream(), 2*1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream,2*1024);
            copy(in, out);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    //http://blog.csdn.net/gf771115/article/details/40871893 //缩放
    public void pic_play_fangda(){
        System.out.println("come in fangda...");
        ifzoom = true;
        size = bigSize * size;
      //  System.out.println(MainActivity.picInfos.get(BaseApp.current_pic_play_num).getData());
        myBitmap = GetLocalOrNetBitmap("file://" + MainActivity.picInfos.get(BaseApp.current_pic_play_num).getData());
        Bitmap newBitmap = myBitmap;
        newBitmap = bigCal(myBitmap);
      //  System.out.println(dstWidth+":"+dstHeight);
         newBitmap = MyBitMap.createScaledBitmap(newBitmap, dstWidth, dstHeight, ImageView.ScaleType.FIT_XY);
         big_pic_show.setImageBitmap(newBitmap);
    }
    public void pic_play_suoxiao(){
        System.out.println("come in suoxiao...");
        ifzoom = true;
        size = smallSize * size;
        myBitmap = GetLocalOrNetBitmap("file://"+MainActivity.picInfos.get(BaseApp.current_pic_play_num).getData());
        Bitmap newBitmap =myBitmap;
        newBitmap = bigCal(newBitmap);
        newBitmap = MyBitMap.createScaledBitmap(newBitmap, dstWidth, dstHeight, ImageView.ScaleType.FIT_XY);
        big_pic_show.setImageBitmap(newBitmap);
    }

    public Bitmap bigCal(Bitmap bitmap){
        bmpWidth = bitmap.getWidth();
        bmpHight = bitmap.getHeight();
        int sizeMax = Math.min(myBitmap.getWidth()/16, myBitmap.getHeight()/16);
        double sizeMin = Math.max(pixel/myBitmap.getWidth(), pixel/myBitmap.getHeight());
        if(size > sizeMax){
            size = sizeMax;
        }
        if(size < sizeMin){
            size = sizeMin;
        }
        bmpSizeWidth = (int)(bmpWidth*size);
        bmpSizeHight = (int)(bmpHight*size);


        if(screenWidth > bmpSizeWidth){
            x = 0;
            dstWidth = bmpSizeWidth;
        }else{
            x = (int)((bmpSizeWidth - screenWidth)/(2*size));
            bmpWidth = (int)(screenWidth / size);
            dstWidth = screenWidth;
        }
        if(screenHeight > bmpSizeHight){
            y = 0;
            dstHeight = bmpSizeHight;
        }else{
            y = (int)((bmpSizeHight - screenHeight)/(2*size));//
            bmpHight = (int)(screenHeight / size);
            dstHeight = screenHeight;
        }
        bitmap = Bitmap.createBitmap(bitmap, x, y, bmpWidth, bmpHight);
        return bitmap;
    }

    class MyPicHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    big_pic_show.setImageURI(Uri.parse(MainActivity.picInfos.get(msg.arg1).getData()));
                    break;
                case 2://隐藏标题
                    pic_shanglan_textview.setVisibility(View.GONE);
                    isshowtitle = false;
                    break;
            }
        }
    }


}
