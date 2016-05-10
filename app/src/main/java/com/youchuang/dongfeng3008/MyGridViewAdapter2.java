package com.youchuang.dongfeng3008;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.youchuang.dongfeng3008.Utils.MyImageView;
import com.youchuang.dongfeng3008.vo.PicInfo;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by NANA on 2016/5/4.
 */
//使用picasso三方进行加载
public class MyGridViewAdapter2 extends BaseAdapter {
    private Context context;
    private ArrayList<PicInfo> pic_lists = new ArrayList<PicInfo>();

    public MyGridViewAdapter2(Context ctx,ArrayList<PicInfo> lists){
        this.context = ctx;
        pic_lists = lists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//      MyImageView view = (MyImageView)convertView;
        ImageView view = (ImageView) convertView;
        if(view == null){
            view = new MyImageView(context);
            view.setLayoutParams(new GridView.LayoutParams(131, 74));//重点行
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setPadding(2,2,2,2);
        }
        String url = ((PicInfo)getItem(position)).getData();

        if(BaseApp.current_pic_play_num == position)
        {
            view.setBackgroundResource(R.mipmap.tupian_p);
        }else {
            view.setBackgroundResource(0);
        }

        Picasso.with(context) //
                .load(new File(url)) //
               // .placeholder(R.mipmap.loading2) //
               // .error(R.mipmap.loading2) //
                .fit() //
                .tag(context) //
                .into(view);
        return view;
    }

    @Override
    public int getCount() {
        return pic_lists.size();
    }

    @Override
    public Object getItem(int position) {
        return pic_lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}