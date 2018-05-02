package com.example.charl.jazz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ImageViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by charl on 27/02/2018.
 */

public class Affiche extends Activity {

    String title;
    String artist;
    String file;

    String id;

    Context context;

    TextView textTitle;
    TextView textArtist;
    TextView textFile;

    MaBd myBd;
    FloatingActionButton fab;
    int UPDATE=0;

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.affiche);
        context = this;

        myBd=new MaBd(context);

        fab = (FloatingActionButton) findViewById(R.id.edit);
        textTitle = (TextView) findViewById(R.id.affichetitre);
        textArtist = (TextView) findViewById(R.id.afficheartiste);
        textFile = (TextView) findViewById(R.id.affichefichier);


        Intent intent = getIntent();

        id = intent.getStringExtra("id");
        int idint=Integer.parseInt(id);

        //get data with id
        Cursor cursor=myBd.getOneData(idint);
        cursor.moveToNext();

        title=cursor.getString(1);
        artist=cursor.getString(2);
        file=cursor.getString(3);

        textTitle.setText(title);
        textArtist.setText(artist);
        textFile.setText(file);


        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intentUp = new Intent(context, AfficheUpdate.class);
                        intentUp.putExtra("id", id);
                        startActivityForResult(intentUp, UPDATE);

                    }
                }
        );
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == UPDATE) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {

                //retour a BaseDD
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();


            }
        }

    }

    /* TEST GET ONE DATA

        Cursor cursor=myDb.getOneData(1);
        String[] salade= new String[3];
        cursor.moveToNext();

        salade[0]=cursor.getString(0);
        salade[1]=cursor.getString(1);
        salade[2]=cursor.getString(2);

        */



}


