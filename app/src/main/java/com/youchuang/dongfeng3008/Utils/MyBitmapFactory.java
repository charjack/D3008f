package com.youchuang.dongfeng3008.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 功能：得到原始的bitmap,就是unscaledbitmap;将得到bitmap字节流
 * @author charjack
 *
 */
public class MyBitmapFactory {
	Context context;
	
	
	public MyBitmapFactory(Context context) {
	// TODO Auto-generated constructor stub
		this.context = context;
}
	public Bitmap getFileBmp(String path){//通过路径获得图片
		Bitmap bm = BitmapFactory.decodeFile(path);
		return bm;
	}
	
	public Bitmap getDrawBmp(int id){//通过本项目id获得图片
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), id);
		return bm;
	}	
	
	public Bitmap getStringBmp(InputStream inputstring){//从流中获取图片
		Bitmap bm = BitmapFactory.decodeStream(inputstring);
		return bm;
	}
	
	public Bitmap getArrayBmp(byte[] data, int offset, int length){//从字节转化成图片
		Bitmap bm = BitmapFactory.decodeByteArray(data, offset, length);
		return bm;
	}
	
	//获得带倒影的图片方法

	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap){

	final int reflectionGap = 4;

	int width = bitmap.getWidth();

	int height = bitmap.getHeight();

	Matrix matrix = new Matrix();

	matrix.preScale(1, -1);

	Bitmap reflectionImage = Bitmap.createBitmap(bitmap,

			0, height / 2, width, height / 2, matrix, false);

	Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2),

			Config.ARGB_8888);

	Canvas canvas = new Canvas(bitmapWithReflection);

	canvas.drawBitmap(bitmap, 0, 0, null);

	Paint deafalutPaint = new Paint();

	canvas.drawRect(0, height,width,height + reflectionGap,

	deafalutPaint);

	canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

	Paint paint = new Paint();

	LinearGradient shader = new LinearGradient(0,

	bitmap.getHeight(), 0, bitmapWithReflection.getHeight()

	+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);

	paint.setShader(shader);

	// Set the Transfer mode to be porter duff and destination in

	paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

	// Draw a rectangle using the paint with our linear gradient

	canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()

	+ reflectionGap, paint);

	return bitmapWithReflection;

	}

	public Bitmap stringtoBitmap(String string){//从string到bitmap
	    //将字符串转换成Bitmap类型
	    Bitmap bitmap=null;
	    try {
	    byte[]bitmapArray;
	    bitmapArray= Base64.decode(string, Base64.DEFAULT);
	bitmap= BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
	} catch (Exception e) {
	e.printStackTrace();
	}
	   
	    return bitmap;
	    }
	    
	     
	    public String bitmaptoString(Bitmap bitmap){

	//将Bitmap转换成字符串
	    String string=null;
	    ByteArrayOutputStream bStream=new ByteArrayOutputStream();
	    bitmap.compress(CompressFormat.PNG,100,bStream);
	    byte[]bytes=bStream.toByteArray();
	    string= Base64.encodeToString(bytes, Base64.DEFAULT);
	    return string;
	    }
	    
	    
	    public Bitmap returnBitMap(String url) {//从网络中获得图片
            URL myFileUrl = null;
            Bitmap bitmap = null;
            try {
                    myFileUrl = new URL(url);
            } catch (MalformedURLException e) {
                    e.printStackTrace();
            }
            try {
                    HttpURLConnection conn = (HttpURLConnection) myFileUrl
                                    .openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }           
            return bitmap;
    }
	   	
}
