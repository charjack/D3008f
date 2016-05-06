package com.youchuang.dongfeng3008.Utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;

import com.youchuang.dongfeng3008.vo.Mp4Info;
import com.youchuang.dongfeng3008.vo.PicInfo;

import java.util.ArrayList;

/**
 * Created by NANA on 2016/4/29.
 */
public class PicMediaUtils {

    //查询所有的图片文件信息
    public static ArrayList<PicInfo> getPic100Infos(Context context) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
        MediaStore.Images.Media.DEFAULT_SORT_ORDER
        );

        ArrayList<PicInfo> picInfos = new ArrayList<PicInfo>();
        int pic_count = 0;
        while (cursor.moveToNext()) {
            pic_count++;
            if(pic_count >= 100){
                pic_count = 0;
                break;
            }

            PicInfo picInfo = new PicInfo();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

            picInfo.setId(id);
            picInfo.setDisplay_name(display_name);
            picInfo.setData(data);

            picInfos.add(picInfo);
        }
        cursor.close();
        return picInfos;
    }

    public static ArrayList<PicInfo> getPicInfos(Context context) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DEFAULT_SORT_ORDER
        );

        ArrayList<PicInfo> picInfos = new ArrayList<PicInfo>();
        while (cursor.moveToNext()) {

            PicInfo picInfo = new PicInfo();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

            picInfo.setId(id);
            picInfo.setDisplay_name(display_name);
            picInfo.setData(data);

            picInfos.add(picInfo);
        }
        cursor.close();
        return picInfos;
    }

    //读取媒体的的条数
        public static int getPicCounts(Context context){
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                return 0;
            }
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png"},
                    MediaStore.Images.Media.DEFAULT_SORT_ORDER
            );
            return cursor.getCount();
        }

    public static PicInfo getPicInfo(Context context,int _id){
        PicInfo picInfo = new PicInfo();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DEFAULT_SORT_ORDER);

        cursor.move(_id);
        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
        String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

        picInfo.setId(id);
        picInfo.setDisplay_name(display_name);
        picInfo.setData(data);

        return picInfo;
    }

}
