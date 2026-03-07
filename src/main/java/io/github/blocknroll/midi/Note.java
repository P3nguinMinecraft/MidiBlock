package io.github.blocknroll.midi;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import java.util.Objects;

public class Note {
    private int pitch; // 0 to 24
    private int octave; // octave shift, leave 0 for now
    private NoteBlockInstrument instrument;
    private int tick;

    public Note(int pitch, int octave, NoteBlockInstrument instrument, int tick) {
        this.pitch = pitch;
        this.octave = octave;
        this.instrument = instrument;
        this.tick = tick;
    }

    public int getPitch() {
        return pitch;
    }

    public int getOctave() {
        return octave;
    }

    public NoteBlockInstrument getInstrument() {
        return instrument;
    }

    public int getTick() {
        return tick;
    }

    @Override
    public String toString() {
        return "Note{" +
               "pitch=" + pitch +
               ", octave=" + octave +
               ", instrument='" + instrument + '\'' +
               ", tick=" + tick +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return pitch == note.pitch &&
               octave == note.octave &&
               tick == note.tick &&
               Objects.equals(instrument, note.instrument);
    }
}
