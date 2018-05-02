package com.example.charl.jazz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by charl on 27/02/2018.
 */

public class AfficheUpdate extends Activity {

    MaBd myDb;
    EditText edit_title, edit_artist,edit_file;

    String title;
    String artist;
    String file;
    String id;

    String titleR;
    String artistR;
    String fileR;

    private Button button;

    private Context context;

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.affiche_update);
        context=this;

        Log.i("startIntent", "updaaaaate");

        button = (Button) findViewById(R.id.updatebutton);

        edit_title = (EditText) findViewById(R.id.bdtitle2);
        edit_artist = (EditText) findViewById(R.id.bdartist2);
        edit_file = (EditText) findViewById(R.id.bdfichier2);

        myDb = new MaBd(this);

        Intent intent = getIntent();

        id = intent.getStringExtra("id");
        int idint=Integer.parseInt(id);

        Cursor cursor=myDb.getOneData(idint);
        cursor.moveToNext();

        title=cursor.getString(1);
        artist=cursor.getString(2);
        file=cursor.getString(3);

        edit_title.setText(title);
        edit_artist.setText(artist);
        edit_file.setText(file);

        updateData();

    }

    public void updateData(){
        button.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){

                        //Recuperation des modifications
                        titleR = edit_title.getText().toString();
                        Log.i("startIntent", "update: titleR: "+titleR);
                        artistR=edit_artist.getText().toString();
                        fileR=edit_file.getText().toString();


                        //mise a jour de la bdd
                        boolean isUpdate= myDb.updateData(id,titleR,artistR,fileR);
                        if(isUpdate==true){
                            Toast.makeText(context,"Data Updated", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(context,"Data is not Updated", Toast.LENGTH_LONG).show();
                        }

                        //Retour a 'affiche'
                        Intent returnIntent = new Intent();

                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();

                    }
                }
        );

    }

}


