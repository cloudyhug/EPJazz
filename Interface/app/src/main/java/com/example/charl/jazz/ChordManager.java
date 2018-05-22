package com.example.charl.jazz;

/**
 * Created by charl on 24/04/2018.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.charl.jazz.network.WiFiDirectActivity;

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

    private ArrayList<String> resultat;

    private TextView accord;
    private TextView accordSuivant;
    private TextView accordPrecedent;
    private TextView tick1;
    private TextView tick2;
    private TextView tick3;
    private TextView tick4;
    private List<TextView> ticks;
    private TextView decompte;

    private Handler handler2 = new Handler();

    private String titreMusique;
    private int bpm; // tempo choisi dans l'activité précédente

    private int bpmMs; // tempo transformé en millisecondes
    private int accordCourant;
    private boolean stop;
    private boolean decompteFini;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chord_manager_layout);

        accord = (TextView) findViewById(R.id.accord);
        accordSuivant = (TextView) findViewById(R.id.accordSuivant);
        accordPrecedent = (TextView) findViewById(R.id.accordPrecedent);

        tick1 = (TextView) findViewById(R.id.tick1);
        tick2 = (TextView) findViewById(R.id.tick2);
        tick3 = (TextView) findViewById(R.id.tick3);
        tick4 = (TextView) findViewById(R.id.tick4);

        decompte = (TextView) findViewById(R.id.decompte);

        states = new ArrayList<>();

        ticks = new ArrayList<>();
        ticks.add(tick1);
        ticks.add(tick2);
        ticks.add(tick3);
        ticks.add(tick4);

        // ========== CRASH TEST ==========

        Intent wifiIntent = new Intent(this, WiFiDirectActivity.class);
        startActivityForResult(wifiIntent, 0);

        // ========== END OF CRASH TEST ==========
    }

    // ========== CRASH TEST ==========

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // getting time from server
                long delta = data.getLongExtra("timeToWait", -1);

                // waiting the right amount of milliseconds
                try {
                    Thread.sleep(delta);
                } catch (InterruptedException ie) {
                    Log.e("chordmanager", ie.getMessage());
                }

                // starting the chord progression
                Intent intent = this.getIntent();
                Bundle extras = intent.getExtras();
                if(extras != null) {
                    titreMusique = extras.getString("titre");
                    bpm = Integer.valueOf(extras.getString("bpm"));
                    bpmMs = 60000/bpm;
                    init(titreMusique);
                }
            }
        }
    }

    // ========== END OF CRASH TEST ==========


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
            decompteFini = false;

            accordPrecedent.setText("");
            accord.setText(resultat.get(0));
            accordSuivant.setText(resultat.get(1));

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
    int dec = 3;
    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            Log.d("Ticks", "tick = " + t);
            if(!stop){

                //affichage du décompte
                if(!decompteFini){

                    if(dec==1){
                        decompteFini = true;
                    }
                    decompte.setText(""+dec);
                    dec--;
                    /*switch (dec){
                        case 1:
                            decompte.setImageResource(R.drawable.decompte_1);
                            decompteFini = true;
                            break;
                        case 2:
                            decompte.setImageResource(R.drawable.decompte_2);
                            break;
                        case 3:
                            decompte.setImageResource(R.drawable.decompte_3);
                            break;
                    }*/
                }

                else{
                    decompte.setText("PLAY !");
                    if(t>3){
                        onStopTicks();
                    }
                    ticks.get(t).setVisibility(View.VISIBLE);
                    if(t==0){
                        ticks.get(1).setVisibility(View.INVISIBLE);
                        ticks.get(2).setVisibility(View.INVISIBLE);
                        ticks.get(3).setVisibility(View.INVISIBLE);

                        if (accordCourant == resultat.size() - 1) {
                            accordPrecedent.setText(resultat.get(accordCourant-1));
                            accord.setText(resultat.get(accordCourant));
                            accordSuivant.setText("");
                        }
                        else if(accordCourant <= 0){
                            accordPrecedent.setText("");
                            accord.setText(resultat.get(accordCourant));
                            accordSuivant.setText(resultat.get(accordCourant+1));
                        }
                        else if (accordCourant >= resultat.size()) {
                            onStop();
                        } else {
                            accordPrecedent.setText(resultat.get(accordCourant-1));
                            accord.setText(resultat.get(accordCourant));
                            accordSuivant.setText(resultat.get(accordCourant + 1));
                        }
                        accordCourant++;
                    }
                    t++;

                }
                startTimer();

            }
        }
    };

    public void startTimer(){
        handler2.postDelayed(runnable2, bpmMs);
    }

    public void onStopTicks(){
        Log.d("STOP", "STOP");
        super.onStop();
        handler2.removeCallbacks(runnable2);
        t = 0;
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler2.removeCallbacks(runnable2);
        accord.setText("ZE END");
        accordSuivant.setText("");
        accordPrecedent.setText("");
        for(TextView tick : ticks){
            tick.setVisibility(View.INVISIBLE);
        }
        stop = true;
    }
}