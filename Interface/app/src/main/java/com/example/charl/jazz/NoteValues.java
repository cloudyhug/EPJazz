package com.example.charl.jazz;

/**
 * Created by charl on 21/01/2018.
 */

public enum NoteValues {
    four("4");

    private String noteValue;

    NoteValues(String noteValue) {
        this.noteValue = noteValue;
    }

    @Override public String toString() {
        return noteValue;
    }

}
