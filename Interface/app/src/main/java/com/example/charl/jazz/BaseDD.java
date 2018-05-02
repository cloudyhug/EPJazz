package com.example.charl.jazz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.MultiSelectListPreference;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by charl on 28/11/2017.
 */

public class BaseDD extends Fragment {


    private static RecyclerView mRecyclerView;

    private static RecyclerViewAdapter mAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;

    private static boolean modeOn;

    static final int NO_DELETE = 1;
    static final int ADD_DATA = 2;
    static final int UPDATE_DATA = 3;


    MaBd myDb;
    FloatingActionButton fab;
    FloatingActionButton fabDel;

    private List<String[]> myList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.bdd, container, false);

        final Context context=this.getContext();

        modeOn=false;

        myDb = new MaBd(getContext());



        //FLOATING ACTION BUTTON

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fabDel = (FloatingActionButton) view.findViewById(R.id.fabDel);

        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), Ajout.class);
                        startActivityForResult(intent,ADD_DATA);
                    }
                }
        );

        fabDel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(modeOn) {
                            Intent intent = new Intent(getContext(), BddDelete.class);
                            startActivityForResult(intent,NO_DELETE);
                        }
                    }
                }
        );


        refreshData();

        //RECYCLER VIEW

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RecyclerViewAdapter(myList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        //=============================================================//

        implementRecyclerViewClickListeners();

        return view;
    }

    //Implement item click and long click over recycler view
    private void implementRecyclerViewClickListeners() {
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerView, new RecyclerClickListener() {
            @Override
            public void onClick(View view, int position) {
                //If ActionMode not null select item
                if (modeOn) {
                    onListItemSelect(position);
                }else{

                    String[] tab = myList.get(position);
                    String id=tab[0];

                    Intent intent = new Intent(getContext(), Affiche.class);

                    intent.putExtra("id", id);

                    startActivityForResult(intent,UPDATE_DATA);
                    //startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                //Select item on long click
                onListItemSelect(position);
            }
        }));
    }

    //List item select method
    @SuppressLint("ResourceAsColor")
    private void onListItemSelect(int position) {


        mAdapter.toggleSelection(position);//Toggle the selection

        boolean hasCheckedItems = mAdapter.getSelectedCount() > 0;//Check if any items are already selected or not



        if (!modeOn) {
            // there are some selected items, start the actionMode
            modeOn = true;
            ImageViewCompat.setImageTintList(
                    fabDel,
                    ColorStateList.valueOf(Color.parseColor("#311b92"))
            );
            fabDel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AEA3FF")));

        }else if(!hasCheckedItems) {
            modeOn = false;
            ImageViewCompat.setImageTintList(
                    fabDel,
                    ColorStateList.valueOf(Color.parseColor("#FF6E40"))
            );
            fabDel.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));

        }

    }
    //Set action mode null after use
    public void setNullToActionMode() {
        if (modeOn)
            modeOn=false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NO_DELETE) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_CANCELED) {

                mAdapter.removeSelection();
                modeOn = false;
                ImageViewCompat.setImageTintList(
                        fabDel,
                        ColorStateList.valueOf(Color.parseColor("#FF6E40"))
                );
                fabDel.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            }
            if (resultCode == Activity.RESULT_OK) {

                deleteRows();

                mAdapter.removeSelection();
                modeOn = false;
                ImageViewCompat.setImageTintList(
                        fabDel,
                        ColorStateList.valueOf(Color.parseColor("#FF6E40"))
                );
                fabDel.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));

                refreshData();
                mAdapter.notifyDataSetChanged();
            }
        }


        if (requestCode == ADD_DATA) {
            if (resultCode == Activity.RESULT_OK) {
                refreshData();
                mAdapter.notifyDataSetChanged();
            }
        }


        if (requestCode == UPDATE_DATA) {
            if (resultCode == Activity.RESULT_OK) {

                refreshData();
                mAdapter.notifyDataSetChanged();

            }
        }
    }

    public void refreshData(){
        myList.clear();

        Cursor res = myDb.getAllData();

        while(res.moveToNext()){
            String[] myTab= new String[3];

            myTab[0]=res.getString(0);
            myTab[1]=res.getString(1);
            myTab[2]=res.getString(2);

            myList.add(myTab);
        }

    }

    public void deleteRows() {
        SparseBooleanArray selected = mAdapter.getSelectedIds();//Get selected ids

//Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
//If current id is selected remove the item via key
                //item_models.remove(selected.keyAt(i));

                String id=myList.get(selected.keyAt(i))[0];

                myDb.deleteData(id);

                mAdapter.notifyDataSetChanged();//notify adapter

            }
        }
    }





}
