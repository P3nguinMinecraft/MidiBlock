package io.github.blocknroll.midi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public int getMaxConcurrent() {
        if (song.isEmpty()) {
            return 0;
        }

        Map<Integer, Integer> tickCounts = new HashMap<>();
        for (Note note : song) {
            int tick = note.getTick();
            tickCounts.put(tick, tickCounts.getOrDefault(tick, 0) + 1);
        }
        return tickCounts.values().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Song [\n");
        for (Note note : song) {
            sb.append("  ").append(note).append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
}
