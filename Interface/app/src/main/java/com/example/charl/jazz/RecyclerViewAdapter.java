package com.example.charl.jazz;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.DemoViewHolder>{

    private List<String[]> arrayList;

    private int myId;

    private HashMap<Integer,String[]> myHashMap;

    //private Context context;

    private SparseBooleanArray mSelectedItemsIds;

    public RecyclerViewAdapter(List<String[]> arrayList) {
        this.arrayList=arrayList;


        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public DemoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_row, viewGroup, false);

        return new DemoViewHolder(mainGroup);

    }

    public static class DemoViewHolder extends RecyclerView.ViewHolder {


        public TextView title;
        public TextView artist;


        public DemoViewHolder(View view) {
            super(view);


            this.title = (TextView) view.findViewById(R.id.title);
            this.artist = (TextView) view.findViewById(R.id.artist);

        }
    }


    public void onBindViewHolder(DemoViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


        String[] myTab;

        myTab=arrayList.get(position);


        holder.title.setText(myTab[1]);

        holder.artist.setText(myTab[2]);

        holder.itemView.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x87AEA3FF : Color.TRANSPARENT);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();

    }


    /***
     * Methods required for do selections, remove selections, etc.
     */



    //Toggle selection methods
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }


    //Remove selected selections
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    //Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
            //holder.
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }



}