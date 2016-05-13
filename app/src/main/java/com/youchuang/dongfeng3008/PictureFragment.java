package com.youchuang.dongfeng3008;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
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
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class PictureFragment extends Fragment{
    private static final String TAG = "PictureFragment";
    ImageView big_pic_show;
    MyPicHandler myPicHandler;
    TextView pic_shanglan_textview;
    boolean isshowtitle = false;
    Context ctx;
    private Bitmap myBitmap;
    private boolean ifzoom = false;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private float oldDist;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private PointF start = new PointF();
    private PointF mid = new PointF();
    DisplayMetrics dm;
    float minScaleR=0.5f;
    float MAX_SCALE = 2f;
    float bigSize = 1.25f;
    float smallSize = 0.8f;

    PicUIUpdateListener picUIUpdateListener;

    public PictureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ctx = (Context) activity;
        picUIUpdateListener =(PicUIUpdateListener)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View pic_view = inflater.inflate(R.layout.fragment_picture, container, false);
        big_pic_show = (ImageView) pic_view.findViewById(R.id.big_pic_show);
        pic_shanglan_textview = (TextView) pic_view.findViewById(R.id.pic_shanglan_textview);
        myPicHandler = new MyPicHandler();

        dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();

        String mybigPicPath = MainActivity.picInfos.get(BaseApp.current_pic_play_num).getData();
        Bitmap bm = convertToBitmap(mybigPicPath,800, 350);
        big_pic_show.setImageBitmap(bm);

        float deltaX = 0,deltaY = 0;
        Matrix newmatrix = new Matrix();
        deltaY = (175-bm.getHeight()/2);
        deltaX = 400 - bm.getWidth()/2;
        newmatrix.postTranslate(deltaX, deltaY);
        big_pic_show.setImageMatrix(newmatrix);

        big_pic_show.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ImageView view = (ImageView) v;
                if (BaseApp.ifopenliebiao == 1) {
                    BaseApp.ifopenliebiao = 0;
                    picUIUpdateListener.onPicLieBiaoClose();
                }else{
                    if(!ifzoom){
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
                            myPicHandler.sendEmptyMessageDelayed(2, 5000);
                        }
                    }else{
                        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_DOWN:
                                matrix.set(view.getImageMatrix());
                                savedMatrix.set(matrix);
                                start.set(event.getX(), event.getY());
                                mode = DRAG;
                                break;
                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_POINTER_UP:
                                mode = NONE;
                                break;
                            case MotionEvent.ACTION_POINTER_DOWN:
                                oldDist = spacing(event);
                                if (oldDist > 10f) {
                                    matrix.set(view.getImageMatrix());
                                    savedMatrix.set(matrix);
                                    midPoint(mid, event);
                                    mode = ZOOM;
                                }
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (mode == DRAG) {
                                    matrix.set(savedMatrix);
                                    matrix.postTranslate(event.getX() - start.x,event.getY() - start.y);  //
                                }else if (mode == ZOOM) {
                                    float newDist = spacing(event);
                                    if (newDist > 20f) {
                                        matrix.set(savedMatrix);
                                        float scale = newDist / oldDist;

                                        float p[] = new float[9];
                                        matrix.getValues(p);    //p会每次都变化
                                        if(BaseApp.ifdebug) {
                                            System.out.println("P[0]------:" + p[0] + "scale---:" + scale);
                                        }
                                        if((p[0] < minScaleR) && scale < 1){  //限制最小和最大视图
                                            matrix.postScale(1, 1, mid.x, mid.y);
                                        }else if((p[0] > MAX_SCALE) && scale > 1){
                                            matrix.postScale(1, 1, mid.x, mid.y);
                                        }else {
                                            matrix.postScale(scale, scale, mid.x, mid.y);
                                        }
                                    }
                                }
                                break;
                        }
                        view.setImageMatrix(matrix);
                    }
                }
                if(ifzoom){
                    return true;//true 表示进行缩放，false表示没有
                }else{
                    return false;
                }

            }

            private float spacing(MotionEvent event)
            {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return (float)(Math.sqrt(x * x + y * y));
            }

            private void midPoint(PointF point, MotionEvent event)
            {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }

        });
        return pic_view;
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
//        size = 1f;
        String mybigPicPath = MainActivity.picInfos.get(position).getData();
        Bitmap bm = convertToBitmap(mybigPicPath,800, 350);
        big_pic_show.setImageBitmap(bm);

        Matrix newmatrix = new Matrix();
       float deltaY = 175-bm.getHeight()/2;  //view的高度就是350
        if(BaseApp.ifdebug) {
            System.out.println("view Height:350" + "-----bitmap Height:" + bm.getHeight());
        }
       float deltaX = 400 - bm.getWidth()/2;  // view的宽度就是800
        if(BaseApp.ifdebug) {
            System.out.println("view width:400" + "-----bitmap width:" + bm.getWidth());
        }
        newmatrix.postTranslate(deltaX,deltaY);
        big_pic_show.setImageMatrix(newmatrix);
    }

    public void setImageShow(int position){
        ifzoom = false;
//        size = 1f;
        BaseApp.current_pic_play_num = position;
    }

    public void playPicpre(){
      //  System.out.println("current_pic_play_num: "+BaseApp.current_pic_play_num);
        ifzoom = false;
//        size = 1f;
        if(BaseApp.current_pic_play_num - 1 <0) {
            BaseApp.current_pic_play_num = MainActivity.picInfos.size()-1;
        }else{
            BaseApp.current_pic_play_num = BaseApp.current_pic_play_num - 1;
        }
        String mybigPicPath = MainActivity.picInfos.get(BaseApp.current_pic_play_num).getData();
        Bitmap bm = convertToBitmap(mybigPicPath, 800, 350);
        big_pic_show.setImageBitmap(bm);
        Matrix newmatrix = new Matrix();
        float deltaY = 175-bm.getHeight()/2;  //view的高度就是350
        if(BaseApp.ifdebug) {
            System.out.println("view Height:350" + "-----bitmap Height:" + bm.getHeight());
        }
        float deltaX = 400 - bm.getWidth()/2;  // view的宽度就是800
        if(BaseApp.ifdebug) {
            System.out.println("view width:400" + "-----bitmap width:" + bm.getWidth());
        }
        newmatrix.postTranslate(deltaX, deltaY);
        big_pic_show.setImageMatrix(newmatrix);
    }

    public void playPicnext(){
        if(BaseApp.ifdebug) {
            System.out.println(TAG+"-playPicnext-"+"current_pic_play_num: " + BaseApp.current_pic_play_num + "picInfos.size()" + PicMediaUtils.getPicCounts(ctx));
        }
        ifzoom = false;
//        size = 1f;
        if(BaseApp.current_pic_play_num + 1 > MainActivity.picInfos.size() - 1){
            BaseApp.current_pic_play_num = 0;
        } else{
            BaseApp.current_pic_play_num++;
        }
        if(BaseApp.ifdebug) {
            System.out.println(TAG+"-playPicnext-"+"current_pic_play_num: " + BaseApp.current_pic_play_num);
        }
        String mybigPicPath = MainActivity.picInfos.get(BaseApp.current_pic_play_num).getData();
        Bitmap bm = convertToBitmap(mybigPicPath, 800, 350);
        big_pic_show.setImageBitmap(bm);
        Matrix newmatrix = new Matrix();
        float deltaY = 175-bm.getHeight()/2;  //view的高度就是350
        if(BaseApp.ifdebug) {
            System.out.println("view Height:350" + "-----bitmap Height:" + bm.getHeight());
        }
        float deltaX = 400 - bm.getWidth()/2;  // view的宽度就是800
        if(BaseApp.ifdebug) {
            System.out.println("view width:400" + "-----bitmap width:" + bm.getWidth());
        }
        newmatrix.postTranslate(deltaX,deltaY);
        big_pic_show.setImageMatrix(newmatrix);
    }

    public Bitmap convertToBitmap(String path, int w, int h) {
        Bitmap bmOrg = BitmapFactory.decodeFile(path);

        float scaleWidth =  (w / (float)bmOrg.getWidth());
        float scaleHeight =  (h / (float)bmOrg.getHeight());
        int newW = 0;
        int newH = 0;
        if(scaleWidth > scaleHeight){
            newW = (int) (bmOrg.getWidth() * scaleHeight);
            newH = (int) (bmOrg.getHeight() * scaleHeight);
        }else if(scaleWidth <= scaleHeight){
            newW = (int) (bmOrg.getWidth() * scaleWidth);
            newH = (int) (bmOrg.getHeight() * scaleWidth);
        }
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bmOrg, newW, newH, true);
        return resizedBitmap;
    }

    public void pic_play_fangda(){
        if(BaseApp.ifdebug) {
            System.out.println(TAG+"-pic_play_fangda-"+"come in fangda...");
        }
        ifzoom = true;
    //    size = bigSize * size;
        myBitmap = ((BitmapDrawable) big_pic_show.getDrawable()).getBitmap();

  //      System.out.println("myBitmap.getWidth:"+myBitmap.getWidth() +"-----myBitmap.getHeight:"+myBitmap.getHeight());
        big_pic_show.setImageBitmap(myBitmap);
        Matrix newMatrix = big_pic_show.getImageMatrix();
        float p[] = new float[9];
        newMatrix.getValues(p);
        if(BaseApp.ifdebug) {
            System.out.println("P[0]------:" + p[0]);
        }
        if(p[0] >MAX_SCALE){
            newMatrix.postScale(1, 1, 400, 175);
        }else {
            newMatrix.postScale(bigSize, bigSize, 400, 175);
        }
        big_pic_show.setImageMatrix(newMatrix);


    }
    public void pic_play_suoxiao(){
        if(BaseApp.ifdebug) {
            System.out.println(TAG+"-pic_play_suoxiao-"+"come in suoxiao...");
        }
        ifzoom = true;
    //    size = smallSize * size;
        myBitmap = ((BitmapDrawable) big_pic_show.getDrawable()).getBitmap();

 //       System.out.println("myBitmap.getWidth:" + myBitmap.getWidth() + "-----myBitmap.getHeight:" + myBitmap.getHeight());

        big_pic_show.setImageBitmap(myBitmap);
        Matrix newMatrix = big_pic_show.getImageMatrix();
        float p[] = new float[9];
        newMatrix.getValues(p);    //p会每次都变化
        if(BaseApp.ifdebug) {
            System.out.println("P[0]------:" + p[0]);
        }
        if(p[0] < minScaleR){
            newMatrix.postScale(1, 1, 400, 175);
        }else {
            newMatrix.postScale(smallSize, smallSize, 400, 175);
        }
        big_pic_show.setImageMatrix(newMatrix);
    }

    class MyPicHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1://重新返回后，显示大图片
                    String mybigPicPath = MainActivity.picInfos.get(msg.arg1).getData();
                    Bitmap bm = convertToBitmap(mybigPicPath, 800, 350);
                    big_pic_show.setImageBitmap(bm);
                    Matrix newmatrix = new Matrix();
                    float deltaY = 175-bm.getHeight()/2;  //view的高度就是350
                    if(BaseApp.ifdebug) { //每次显示的都是原始图片的高宽
                        System.out.println("view Height:350" + "-----bitmap Height:" + bm.getHeight());
                    }
                    float deltaX = 400 - bm.getWidth()/2;  // view的宽度就是800
                    if(BaseApp.ifdebug) {
                        System.out.println("view width:400" + "-----bitmap width:" + bm.getWidth());
                    }
                    newmatrix.postTranslate(deltaX,deltaY);
                    big_pic_show.setImageMatrix(newmatrix);
                    break;
                case 2://隐藏标题
                    pic_shanglan_textview.setVisibility(View.GONE);
                    isshowtitle = false;
                    break;
            }
        }
    }

    public interface PicUIUpdateListener{
        public void onPicLieBiaoClose();
    }

}
