package com.example.charl.jazz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;

/**
 * Created by charl on 28/11/2017.
 */

public class Pp extends Fragment {


    private FloatingActionButton fab;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pp, container, false);

        context = this.getContext();

        fab = (FloatingActionButton) view.findViewById(R.id.connexionButton);

        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent().setClass(context,Connexion.class);
                        startActivity(intent);
                    }
                }
        );




        return view;
    }

}
