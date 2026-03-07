package io.github.blocknroll.midi;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import javax.sound.midi.*;
import java.io.File;
import java.util.Comparator;

public class MIDI {
    public Song song;

    public MIDI() {
        song = new Song();
    }

    public MIDI fromFile(File file) {
        MIDI midi = new MIDI();
        try {
            Sequence sequence = MidiSystem.getSequence(file);
            int resolution = sequence.getResolution(); // Ticks per quarter note

            // A 16th note is Resolution / 4
            double midiTicksPer16th = resolution / 4.0;

            // We want every 16th note to take 8 Game Ticks (4 Redstone Ticks)
            double scaleFactor = 8.0 / midiTicksPer16th;

            for (Track track : sequence.getTracks()) {
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    if (event.getMessage() instanceof ShortMessage sm) {
                        if (sm.getCommand() == ShortMessage.NOTE_ON && sm.getData2() > 0) {

                            // Apply the scale factor to the raw MIDI tick
                            int tick = (int) Math.round(event.getTick() * scaleFactor);

                            int rawPitch = sm.getData1();
                            int pitchInOctave = rawPitch % 12;
                            int octave = (rawPitch / 12) - 1;

                            Note note = new Note(pitchInOctave, octave, NoteBlockInstrument.HARP, tick);
                            song.addNote(note);
                        }
                    }
                }
            }
            song.getNotes().sort(Comparator.comparingInt(Note::getTick));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return midi;
    }
}