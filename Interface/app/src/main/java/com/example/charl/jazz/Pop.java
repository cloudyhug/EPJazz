package com.example.charl.jazz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

/**
 * Created by charl on 04/12/2017.
 */

public class Pop extends Activity{

    TextView pop_f_text;
    TextView final_text;
    //Display d;
    //String u = d.uniqueselection;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popwindow);

        DisplayMetrics dm =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        /*
        Intent i= getIntent();
        String monstring ="";
        monstring = i.getStringExtra("textefinal");
        */



        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.6));
/*
        pop_f_text= (TextView) findViewById(R.id.poptext);
        pop_f_text.setEnabled(false);
        pop_f_text.setText(monstring);
        pop_f_text.setEnabled(true);

        */



    }
}
































