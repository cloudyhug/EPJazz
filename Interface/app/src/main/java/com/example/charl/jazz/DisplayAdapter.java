package com.example.charl.jazz;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by charl on 23/04/2018.
 */

public class DisplayAdapter extends BaseAdapter {

    LayoutInflater inflater;
    List<String> titleList;
    List<String> artistList;

    public DisplayAdapter (Context context, List<String> titleList,List<String> artistList){
        this.titleList=titleList;
        this.artistList=artistList;
        inflater=LayoutInflater.from(context);

        Log.i("display", "DisplayAdapter: iniiiiiiittt");


    }

    public int getCount(){
        return this.titleList.size();
    }

    public Object getItem(int position){
        return titleList.get(position);
    }

    public long getItemId(int position){
        return position;

    }

    private class ViewHolder{
        TextView title;
        TextView artist;
    }

    public View getView(int position,View convertView, ViewGroup parent){
        ViewHolder holder;

        if(convertView==null){
            holder = new ViewHolder();
            convertView=inflater.inflate(R.layout.item_row,null);

            holder.title=(TextView)convertView.findViewById(R.id.title);
            holder.artist=(TextView)convertView.findViewById(R.id.artist);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.title.setText(titleList.get(position));

        holder.artist.setText(artistList.get(position));
        return convertView;


    }
}
