package com.example.charl.jazz;

/**
 * Created by charl on 24/04/2018.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.*;
import java.io.*;
import java.util.regex.*;

/**
 Code pour lire un fichier contenant une grille de jazz sous la forme d'un diagramme à états finis :
 0 -> chord1 -> 1 (= un arc)
 1 -> chord2 -> 2
 1 -> chord3 -> 3
 2 -> chord4 -> 4
 3 -> chord5 -> 4
 Si la grille contient plusieurs transitions partant du même état (ex : 1), l'application en choisit une au hasard
 */
public class ChordManager extends Activity {

    // Liste de tous les états possibles (= arcs du diagramme) de la grille
    private ArrayList<State> states;

    private String titreMusique;

    private TextView accord;
    private TextView accordSuivant;
    private TextView tick1;
    private TextView tick2;
    private TextView tick3;
    private TextView tick4;

    private List<TextView> ticks;

    private Handler handler2 = new Handler();

    private ArrayList<String> resultat;
    private int accordCourant;

    private boolean stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chord_manager_layout);

        accord = (TextView) findViewById(R.id.accord);
        accordSuivant = (TextView) findViewById(R.id.accordSuivant);
        tick1 = (TextView) findViewById(R.id.tick1);
        tick2 = (TextView) findViewById(R.id.tick2);
        tick3 = (TextView) findViewById(R.id.tick3);
        tick4 = (TextView) findViewById(R.id.tick4);

        states = new ArrayList<>();

        ticks = new ArrayList<>();
        ticks.add(tick1);
        ticks.add(tick2);
        ticks.add(tick3);
        ticks.add(tick4);


        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        Intent intent = this.getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            titreMusique = extras.getString("titre");
            init(titreMusique);
        }
    }


    public InputStream ouvrirFichier(String s){
        InputStream res = this.getResources().openRawResource(R.raw.test);
        switch (s) {
            case "all_of_me" :
                res = this.getResources().openRawResource(R.raw.all_of_me);
                break;
            case "autumn_leaves" :
                res = this.getResources().openRawResource(R.raw.autumn_leaves);
                break;
            case "blues_en_fa" :
                res = this.getResources().openRawResource(R.raw.blues_en_fa);
                break;
            case "caravan" :
                res = this.getResources().openRawResource(R.raw.caravan);
                break;
            case "minor_swing" :
                res = this.getResources().openRawResource(R.raw.minor_swing);
                break;
            case "stand_by_me" :
                res = this.getResources().openRawResource(R.raw.stand_by_me);
                break;
        }
        return res;
    }

    public void init(String f){

        // Création de l'expression régulière regex pour capturer l'état de début, l'accord et l'état de fin de l'arc : int->caractères->int
        Pattern regex = Pattern.compile("(\\d+)->(.*)->(\\d+)");

        try {

            InputStream is = ouvrirFichier(f);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;

            if (is != null){
                while ((line = br.readLine()) != null) {

                    // Création d'un matcher à partir de la ligne à laquelle on applique l'expression régulière regex
                    Matcher matcher = regex.matcher(line);

                    if (matcher.find()) {

                        int originState = Integer.valueOf(matcher.group(1)); // 1ère capture : état de début
                        String chord = matcher.group(2); // 2ème capture : accord
                        int endState = Integer.valueOf(matcher.group(3)); // 3ème capture : état de fin de l'arc

                        // On ajoute le state lu à la liste de states

                        boolean present = false; // variable pour voir si la liste d'états contient déjà un état avec le même nombre de départ (originState)
                        for (State s : states) {
                            if (s.getNumber() == originState) present = true;
                        }

                        // Si il n'y a aucun état avec le même nombre de départ, on en crée un
                        if (!present) {
                            states.add(new State(originState));
                        }

                        // On ajoute la transition actuelle à l'état dont le nombre de départ est originState
                        states.get(originState).addTransition(new Transition(chord, originState, endState));

                    } else { // Si la regex ne matche pas
                        System.out.println("ça matcheuh paaaaaaaaaas !!!!!!!!!!!!!!!!!");
                    }

                }
            }
            is.close();


            ArrayList<State> arcs = getStates();

            resultat = new ArrayList<>();


            for (State state : arcs){
                resultat.add(state.getNext().toString2());
            }

            accordCourant = 0;
            stop = false;
            startTimer();


        }
        catch (IOException e) {
            // Affichage d'un message d'erreur si le fichier est impossible à lire
            Toast.makeText(getApplicationContext(), "Erreur de lecture", Toast.LENGTH_LONG).show();
        }
    }


    /**
     Méthode qui renvoie la liste des différents états de la grille
     */
    public ArrayList<State> getStates(){
        return this.states;
    }



    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ CODE DU TIMER ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    int t = 0;
    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            if(!stop){
                ticks.get(t).setVisibility(View.VISIBLE);
                if(t==0){
                    ticks.get(1).setVisibility(View.INVISIBLE);
                    ticks.get(2).setVisibility(View.INVISIBLE);
                    ticks.get(3).setVisibility(View.INVISIBLE);
                }
                t++;

                if(t>3){
                    onStopTicks();
                }
                startTimer();
            }
        }
    };

    public void startTimer(){
        handler2.postDelayed(runnable2, 1000);
    }

    public void onStopTicks(){
        super.onStop();
        handler2.removeCallbacks(runnable2);
        t = 0;
        if (accordCourant == resultat.size() - 1) {
            accord.setText(resultat.get(accordCourant));
            accordSuivant.setText("");
        } else if (accordCourant >= resultat.size()) {
            onStop();
        } else {
            accord.setText(resultat.get(accordCourant));
            accordSuivant.setText(resultat.get(accordCourant + 1));
        }
        accordCourant++;

    }

    @Override
    protected void onStop() {
        super.onStop();
        handler2.removeCallbacks(runnable2);
        accord.setText("ZE");
        accordSuivant.setText("END");
        for(TextView tick : ticks){
            tick.setVisibility(View.INVISIBLE);
        }
        stop = true;
    }

}