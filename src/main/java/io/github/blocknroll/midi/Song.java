package io.github.blocknroll.midi;

import java.util.ArrayList;

public class Song {
    private ArrayList<Note> song;

    public Song() {
        this.song = new ArrayList<>();
    }

    public void addNote(Note n) {
        this.song.add(n);
    }

    public ArrayList<Note> getNotes() {
        return song;
    }

    public ArrayList<Note> getSong() {
        return song;
    }
}
