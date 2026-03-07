package io.github.blocknroll.midi;

import io.github.blocknroll.structure.Channel;

import java.util.ArrayList;
import java.util.Arrays;

public class Song {
    private final ArrayList<Note> song;

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

    public int[] getNoteCount() {
        if (song.isEmpty()) {
            return new int[0];
        }
        int maxTick = 0;
        for (Note n : song) {
            if (n.getTick() > maxTick) {
                maxTick = n.getTick();
            }
        }
        int[] counts = new int[maxTick + 1];
        for (Note n : song) {
            counts[n.getTick()]++;
        }
        return counts;
    }

    private int getMax(int[] arr) {
        return Arrays.stream(arr).max().orElse(0);
    }

    public int getMaxConcurrent() {
        return getMax(getNoteCount());
    }

    public ArrayList<Channel> partition() {
        if (song.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<Channel> channels = new ArrayList<>();
        int[] counts = getNoteCount();
        int maxTick = counts.length;

        ArrayList<Note>[] notesByTick = new ArrayList[counts.length];
        for (int i = 0; i < counts.length; i++) {
            notesByTick[i] = new ArrayList<>();
        }
        for (Note n : song) {
            notesByTick[n.getTick()].add(n);
        }

        int max = getMax(counts);

        while (max > 0) {
            Channel channel = new Channel(maxTick);

            for (int tick = 0; tick < counts.length; tick++) {
                if (counts[tick] > 0 && !notesByTick[tick].isEmpty()) {
                    Note n = notesByTick[tick].removeFirst();
                    channel.addNote(n, tick);
                    counts[tick]--;
                }
            }

            channels.add(channel);
            max = getMax(counts);
        }
        return channels;
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
