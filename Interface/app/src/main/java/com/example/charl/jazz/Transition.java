package com.example.charl.jazz;

/**
 * Created by charl on 14/05/2018.
 */

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

    public int getNextState() {
        return nextState;
    }

    public int getOriginState() {
        return originState;
    }

    public String getChord() {
        return chord;
    }

    public String toString(){
        return (originState + "->" + chord + "->" + nextState);
    }

    public String toString2(){
        return (chord);
    }
}
