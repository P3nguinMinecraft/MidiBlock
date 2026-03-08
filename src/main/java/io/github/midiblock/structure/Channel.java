package io.github.midiblock.structure;

import io.github.midiblock.midi.Note;

public class Channel {
    private Note[] notes;

    public Channel(int length) {
        this.notes = new Note[length];
    }

    public void addNote(Note note, int tick) {
        if (tick >= 0 && tick < notes.length) {
            notes[tick] = note;
        }
    }

    public Note[] getNotes() {
        return notes;
    }

    public Note getNote(int tick) {
        if (tick >= 0 && tick < notes.length) {
            return notes[tick];
        }
        return null;
    }

    public int getLength() {
        return notes.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Channel[");
        boolean first = true;
        for (int i = 0; i < notes.length; i++) {
            if (notes[i] != null) {
                if (!first) sb.append(", ");
                sb.append("tick ").append(i).append(": ").append(notes[i]);
                first = false;
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
