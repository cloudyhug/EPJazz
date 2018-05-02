package com.example.charl.jazz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by charl on 27/02/2018.
 */

public class Ajout extends Activity {

    MaBd myDb;
    EditText edit_title, edit_artist,edit_file;

    private Button button;

    private Context context;

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajout);
        context=this;

        button = (Button) findViewById(R.id.addbutton);

        edit_title = (EditText) findViewById(R.id.bdtitle);
        edit_artist = (EditText) findViewById(R.id.bdartist);
        edit_file = (EditText) findViewById(R.id.bdfichier);

        myDb = new MaBd(this);
        addData();

    }

    public void addData(){
        button.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        boolean isInserted=myDb.insertData(edit_title.getText().toString(),edit_artist.getText().toString(),edit_file.getText().toString());

                        if(isInserted){
                            Toast.makeText(context,"Data Inserted", Toast.LENGTH_LONG).show();
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();
                        }else{
                            Toast.makeText(context,"Data not Inserted", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

    }



}


