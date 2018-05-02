package com.example.charl.jazz;


import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;

import android.content.Intent;


import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by charl on 03/12/2017.
 */

public class Display extends Activity{

    ListView mListView;

    List<String> titleList;
    List<String> artistList;

    MaBd myDb;

    private List<String[]> myList = new ArrayList<>();

    DisplayAdapter adapter;

    //ArrayList<String> selection= new ArrayList<String>();
/*
    String uniqueselection;

    CheckBox ch1;
    CheckBox ch2;
    CheckBox ch3;
    CheckBox ch4;


    TextView final_text;
*/


    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);

        myDb = new MaBd(this);

        titleList=new ArrayList<String>();
        artistList=new ArrayList<String>();


        mListView = (ListView) findViewById(R.id.listViewS);

        viewData();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(Display.this, MetroParam.class);
                String title=myList.get(position)[3];
                intent.putExtra("titre",title);

                startActivity(intent);
            }
        });


    }


    public void viewData(){

        myList.clear();

        Cursor res = myDb.getAllData();

        while(res.moveToNext()){
            String[] myTab= new String[4];

            myTab[0]=res.getString(0);
            myTab[1]=res.getString(1);
            myTab[2]=res.getString(2);
            myTab[3]=res.getString(3);

            myList.add(myTab);
        }

        for (int i=0; i<myList.size();i++) {
            String[] myTab= myList.get(i);
            Log.i("display", "DisplayAdapter: myTab : "+myTab[1]);
            String string= myTab[1];
            Log.i("display", "DisplayAdapter: string : "+string);
            titleList.add(string);
            Log.i("display", "DisplayAdapter: titleList : "+titleList.get(i));
            artistList.add(myTab[2]);
        }

        adapter = new DisplayAdapter(this,titleList,artistList);
        mListView.setAdapter(adapter);
    }








}




















