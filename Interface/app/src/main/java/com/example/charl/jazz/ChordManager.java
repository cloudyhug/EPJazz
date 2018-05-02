package com.example.charl.jazz;

/**
 * Created by charl on 24/04/2018.
 */


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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
                            if (s.number == originState) present = true;
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

            final ArrayList<String> resultat = new ArrayList<>();

            int i = 0;
            for (State state : arcs){
                resultat.add(state.getNext().toString2());
                i++;
            }
/**
 Intent retour = new Intent(ChordManager.this, MainActivity.class);
 for (State state : arcs){
 retour.putExtra("accord" + i, state.getNext().toString2());
 i++;
 }
 retour.putExtra("nbStates", i);*/

            int nbSecondes = i*2000;

            new CountDownTimer(nbSecondes, 2000){
                int i = 0;
                public void onTick(long millisUntilFinished) {
                    // afficher l'accord
                    accord.setText(resultat.get(i));
                    accordSuivant.setText(resultat.get(++i));

                    new CountDownTimer(2000, 500){
                        int a = 0;
                        public void onTick(long millisUntilFinished) {
                            ticks.get(a).setTextColor(Color.RED);
                            a++;
                        }
                        public void onFinish() {
                            ticks.get(0).setTextColor(Color.GRAY);
                            ticks.get(1).setTextColor(Color.GRAY);
                            ticks.get(2).setTextColor(Color.GRAY);
                            ticks.get(3).setTextColor(Color.GRAY);
                        }
                    }.start();
                }
                public void onFinish() {
                    // message de fin
                    accord.setText("ZE END");
                    accordSuivant.setText("");
                }
            }.start();

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


    /**
     Classe interne Transition qui correspond à un arc du diagramme. Elle décrit l'accord courant et l'état suivant.
     Ex : 0-> A -> 1 donnera Transition(A,1)
     */
    public class Transition {
        private String chord;
        private int originState;
        private int nextState;

        public Transition(String chord, int ostate, int nstate) {
            this.chord = chord;
            this.originState = ostate;
            this.nextState = nstate;
        }

        public String toString(){
            return (originState + "->" + chord + "->" + nextState);
        }

        public String toString2(){
            return (chord);
        }
    }


    /**
     Classe interne State qui décrit un état et toutes les transitions qu'on peut lui associer.
     Ex : 0 -> A -> 1
     0 -> B -> 2
     A l'état 0 sont associées deux transitions : (A,1) et (B,2)
     */
    public class State {
        private int number; // Numéro de l'état
        private ArrayList<Transition> possibleTransitions; // Liste des transitions possibles

        public State(int number) {
            this.number = number;
            possibleTransitions = new ArrayList<>();
        }

        /**
         Méthode qui renvoie une transition au hasard parmi la liste des transitions possibles
         */
        public Transition getNext() {

            Random rd = new Random(); // générateur de nombres au hasard
            int index = rd.nextInt(possibleTransitions.size()); // on génère un entier entre 0 et la taille de la liste des transitions possibles
            //System.out.println(index);

            return possibleTransitions.get(index);
        }

        /**
         Méthode qui ajoute une transition dans la liste des transitions possibles de cet état
         */
        public void addTransition(Transition t) {
            possibleTransitions.add(t);
        }

        public String toString(){
            String res = "";
            for(Transition t : possibleTransitions){
                res += t.toString() + "\n";
            }
            return res;
        }

    }

    public static void main(String[] args){

        ChordManager cm = new ChordManager();
        cm.init("/home/laolao/Documents/INSA/3ANNEE/ETUDES PRATIQUES/Grilles/Autumn_leaves.txt");
        ArrayList<State> arcs = cm.getStates();
        for (State state : arcs){
            System.out.println(state.getNext().toString2());
        }
    }
}
