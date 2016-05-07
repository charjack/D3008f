package com.youchuang.dongfeng3008.Utils;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;

import com.youchuang.dongfeng3008.BaseApp;
import com.youchuang.dongfeng3008.R;
import com.youchuang.dongfeng3008.vo.Mp3Info;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;


/**
 * Created by Administrator on 2016/2/29.
 */
public class MediaUtils {


    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

    public static Mp3Info getMp3Info(Context context,long _id){
        System.out.println(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,
                MediaStore.Audio.Media._ID+"=?",new String[]{String.valueOf(_id)},
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        Mp3Info mp3Info = null;
        if(cursor.moveToNext()){
            mp3Info = new Mp3Info();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            long duration =cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            long size =cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));

            if(isMusic != 0 ){
                mp3Info.setId(id);
                mp3Info.setTittle(title);
                mp3Info.setArtist(artist);
                mp3Info.setAlbum(album);
                mp3Info.setAlbumId(albumId);
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
                mp3Info.setUrl(url);
            }
        }

        cursor.close();
        return mp3Info;
    }

    public static int getMp3Nums(Context context){
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID},
                MediaStore.Audio.Media.DURATION+">=180000",null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        int nums = cursor.getCount();
        cursor.close();
        return nums;
    }


    public static long[] getMp3InfoIds(Context context){
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID},
                MediaStore.Audio.Media.DURATION+">=180000",null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        long[]ids = null;
        if(cursor!=null){
            ids = new long[cursor.getCount()];
            for(int i = 0;i<cursor.getCount();i++){
                cursor.moveToNext();
                ids[i]=cursor.getLong(0);
            }
        }
        cursor.close();
        return ids;
    }



    public static ArrayList<Mp3Info> getMp3Infos(Context context){
        System.out.println(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,
                MediaStore.Audio.Media.DURATION+">=180000",null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        );

        ArrayList<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
        for(int i=0;i<cursor.getCount();i++){
            cursor.moveToNext();
            Mp3Info mp3Info = new Mp3Info();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            long duration =cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            long size =cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));

            if(isMusic != 0 ){
                mp3Info.setId(id);
                mp3Info.setTittle(title);
                mp3Info.setArtist(artist);
                mp3Info.setAlbum(album);
                mp3Info.setAlbumId(albumId);
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
                mp3Info.setUrl(url);

                mp3Infos.add(mp3Info);
            }
        }
        cursor.close();
        return mp3Infos;

    }


    public static String formatTime(long time){
        String hour = time/(1000*60*60)+"";
        String min = time/(1000*60)+"";//
        String sec = time%(1000*60)+"";

        if(hour.length()<1){
            hour ="00";
        } else if(hour.length()<2){
            hour = "0"+time/(1000*60*60);
        }else{
            hour = time/(1000*60*60)+"";
        }


        if(min.length()<2){
            min = "0"+time/(1000*60);
        }else{
            min = time/(1000*60)+"";
        }

        if(sec.length() == 4) {
            sec = "0"+(time%(1000*60)+"");
        }else if(sec.length() ==3){
            sec = "00"+(time%(1000*60)+"");
        }else if(sec.length() == 2){
            sec = "000"+(time%(1000*60)+"");
        }else if(sec.length() == 1){
            sec = "0000"+(time%(1000*60)+"");
        }
        return hour+":"+min+":"+sec.trim().substring(0,2);
    }



    public static List<HashMap<String,String>>getMusicMaps(List<Mp3Info> mp3Infos){
        List<HashMap<String ,String>> mp3list = new ArrayList<HashMap<String ,String>>();

        for(Iterator iterator = mp3Infos.iterator();iterator.hasNext();){
            Mp3Info mp3Info = (Mp3Info) iterator.next();
            HashMap<String ,String> map = new HashMap<>();
            map.put("title",mp3Info.getTittle());
            map.put("Artist",mp3Info.getArtist());
            map.put("album",mp3Info.getAlbum());
            map.put("albumid",String.valueOf(mp3Info.getAlbumId()));
            map.put("duration",formatTime(mp3Info.getDuration()));
            map.put("size",String.valueOf(mp3Info.getSize()));
            map.put("url",mp3Info.getUrl());
            mp3list.add(map);
        }
        return mp3list;
    }


    public static Bitmap getDefaultArtwork(Context context,boolean small){
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        if(small){
            return BitmapFactory.decodeStream(context.getResources()
                    .openRawResource(R.mipmap.yinyue),null,opts);
        }
        return BitmapFactory.decodeStream(context.getResources()
                .openRawResource(R.mipmap.yinyue),null,opts);
    }



    private static Bitmap getArtworkFromFile(Context context,long songid,long albumid){
        Bitmap bm =null;
        if(albumid<0 && songid<0){
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor fd = null;
            if(albumid<0){
                Uri uri =Uri.parse("content://media/external/audio/media/"
                        +songid+"/albumart");

                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if(pfd!=null){
                    fd = pfd.getFileDescriptor();
                }

            }else{
                Uri uri = ContentUris.withAppendedId(albumArtUri,albumid);
                ParcelFileDescriptor pfd  =context.getContentResolver()
                        .openFileDescriptor(uri, "r");
                if(pfd!=null){
                    fd = pfd.getFileDescriptor();
                }
            }

            options.inSampleSize = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fd,null,options);
            options.inSampleSize = 100;
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            bm = BitmapFactory.decodeFileDescriptor(fd,null,options);
        } catch (FileNotFoundException e) {
             e.printStackTrace();
        }
        return  bm;
    }


    public static Bitmap getArtwork(Context context,long song_id,
                                    long album_id,boolean allowdefault,boolean small){
        if(BaseApp.ifdebug) {
            System.out.println("MediaUtils-getArtwork---------enter getartwork");
        }
        if(album_id<0){
            if(song_id<0){
                Bitmap bm = getArtworkFromFile(context,song_id,-1);
                if(bm!=null){
                    return bm;
                }
            }

            if(allowdefault){
                return getDefaultArtwork(context,small);
            }
            return null;
        }

        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(albumArtUri,album_id);
        if(BaseApp.ifdebug) {
            System.out.println("MediaUtils-getArtwork----------uri" + uri);
        }
        if(uri!=null){
            InputStream in = null;

            try {
                if(BaseApp.ifdebug) {
                    System.out.println("MediaUtils-getArtwork----------11");
                }
                in= res.openInputStream(uri);
//                try{
//                    in= res.openInputStream(uri);
//                }catch(FileNotFoundException e){
//                    System.out.println(e);
//                    e.printStackTrace();
//                }
                if(BaseApp.ifdebug){
                    System.out.println("MediaUtils-getArtwork----------12"+in);
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
                if(BaseApp.ifdebug) {
                    System.out.println("MediaUtils-getArtwork----------13" + "----" + small);
                }
                if(small){
                    options.inSampleSize = computeSampleSize(options,40);
                }else{
                    options.inSampleSize = computeSampleSize(options,600);
                }

                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in,null,options);


            } catch (FileNotFoundException e) {
                if(BaseApp.ifdebug) {
                    System.out.println("MediaUtils-getArtwork----------21" + "----");
                }
                Bitmap bm = getArtworkFromFile(context,song_id,album_id);
                if(bm!=null){
                    if (bm.getConfig() ==null){
                        bm = bm.copy(Bitmap.Config.RGB_565,false);
                        if(bm==null&&allowdefault){
                            return getDefaultArtwork(context,small);
                        }
                    }

                }else if(allowdefault){
                    bm=getDefaultArtwork(context,small);
                }
                return bm;
            }finally{
                try{
                    if(in!=null){
                        in.close();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static int computeSampleSize(BitmapFactory.Options options, int i) {

        if(BaseApp.ifdebug) {
            System.out.println("MediaUtils-computeSampleSize----enter computesamplesize");
        }
        //获取位图的原宽高
        int w = options.outWidth;
        int h = options.outHeight;
        System.out.println("w="+w);
        System.out.println("h="+h);
        int inSampleSize = 1;
        if(w>i || h>i){
            if(w>h){
                inSampleSize = Math.round((float)h / (float)i);
            }else{
                inSampleSize = Math.round((float)w / (float)i);
            }
        }
        if(BaseApp.ifdebug) {
            System.out.println("MediaUtils-computeSampleSize--inSampleSize=" + inSampleSize);
        }
        return inSampleSize;

    }


}














