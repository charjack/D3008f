package com.youchuang.dongfeng3008;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by NANA on 2016/4/21.
 */
public class MymusiclistviewAdapter extends BaseAdapter{
    public List<String> lists;
    Context ctx;

    public MymusiclistviewAdapter(Context ctx, List<String> lists){
        this.ctx = ctx;
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder  vh;
        if(convertView == null){
            vh = new ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.listviewitem_layout,null);
            vh.imageView = (ImageView) convertView.findViewById(R.id.music_item_pic);
            vh.textView = (TextView) convertView.findViewById(R.id.music_item_text);
            convertView.setTag(vh);
        }
        vh = (ViewHolder) convertView.getTag();

        if(BaseApp.current_music_play_num == position){
            vh.imageView.setImageResource(R.mipmap.yinyue_p);
            vh.textView.setText(lists.get(position));
            vh.textView.setTextColor(Color.rgb(01, 66, 255));
        }else {
            vh.imageView.setImageResource(R.mipmap.yinyue_n);
            vh.textView.setText(lists.get(position));
            vh.textView.setTextColor(Color.rgb(255, 255, 255));
        }
        return convertView;
    }

    public class ViewHolder{
        ImageView imageView;
        TextView textView;
    }

}
