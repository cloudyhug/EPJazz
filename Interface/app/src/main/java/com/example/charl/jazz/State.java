package com.example.charl.jazz;

/**
 * Created by charl on 14/05/2018.
 */

import java.util.ArrayList;
import java.util.Random;

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

    public int getNumber(){
        return this.number;
    }

    public ArrayList<Transition> getPossibleTransitions() {
        return this.possibleTransitions;
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
