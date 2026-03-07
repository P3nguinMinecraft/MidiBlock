package io.github.blocknroll.midi;

import io.github.blocknroll.structure.Channel;

import java.util.ArrayList;
import java.util.Arrays;

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
        int max = getMax(counts);

        while (max > 0) {
            for (int tick = 0; tick < counts.length; tick++) {
                if (counts[tick] == max) {
                    Channel channel = new Channel(counts.length);

                    for (Note n : song) {
                        if (n.getTick() == tick && counts[tick] > 0) {
                            channel.addNote(n, n.getTick());
                            counts[tick]--;
                        }
                    }
                    channels.add(channel);
                    break;
                }
            }
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
