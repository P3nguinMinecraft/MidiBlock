package io.github.blocknroll.midi;

import io.github.blocknroll.BlockNRoll;
import io.github.blocknroll.Config;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import javax.sound.midi.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

public class MIDI {
    public Song song;

    public MIDI() {
        song = new Song();
    }

    public MIDI fromFile(File file) {
        try {
            Sequence sequence = MidiSystem.getSequence(file);
            int resolution = sequence.getResolution();

            ArrayList<long[]> tempoChanges = new ArrayList<>();
            for (Track track : sequence.getTracks()) {
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    if (event.getMessage() instanceof MetaMessage meta && meta.getType() == 0x51) {
                        byte[] data = meta.getData();
                        int mpq = ((data[0] & 0xFF) << 16) | ((data[1] & 0xFF) << 8) | (data[2] & 0xFF);
                        tempoChanges.add(new long[]{event.getTick(), mpq});
                    }
                }
            }
            tempoChanges.sort(Comparator.comparingLong(a -> a[0]));
            if (tempoChanges.isEmpty()) {
                tempoChanges.add(new long[]{0, 500_000});
            }

            ArrayList<long[]> rawEvents = new ArrayList<>();
            for (Track track : sequence.getTracks()) {
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    if (event.getMessage() instanceof ShortMessage sm) {
                        if (sm.getCommand() == ShortMessage.NOTE_ON && sm.getData2() > 0) {
                            rawEvents.add(new long[]{event.getTick(), sm.getData1()});
                        }
                    }
                }
            }

            ArrayList<double[]> microEvents = new ArrayList<>();
            for (long[] raw : rawEvents) {
                double micros = midiTickToMicros(raw[0], resolution, tempoChanges);
                microEvents.add(new double[]{micros, raw[1]});
            }

            microEvents.sort(Comparator.comparingDouble(a -> a[0]));
            double smallestInterval = Double.MAX_VALUE;
            for (int i = 1; i < microEvents.size(); i++) {
                double gap = microEvents.get(i)[0] - microEvents.get(i - 1)[0];
                if (gap > 0 && gap < smallestInterval) {
                    smallestInterval = gap;
                }
            }

            double slowdownFactor = 1.0;
            double microsPerRedstoneTick = 100_000.0;
            if (smallestInterval < microsPerRedstoneTick && smallestInterval > 0) {
                double requiredFactor = microsPerRedstoneTick / smallestInterval;
                double slowdownPercent = requiredFactor - 1.0;
                if (slowdownPercent <= Config.MAX_SLOWDOWN_PERCENT) {
                    slowdownFactor = requiredFactor;
                    BlockNRoll.LOGGER.info("Slowing down song by {}% to align smallest interval to 1 redstone tick",
                            String.format("%.1f", slowdownPercent * 100));
                } else {
                    BlockNRoll.LOGGER.info("Smallest interval ({} µs) would require {}% slowdown, exceeding max {}%. Not adjusting.",
                            String.format("%.0f", smallestInterval),
                            String.format("%.1f", slowdownPercent * 100),
                            String.format("%.1f", Config.MAX_SLOWDOWN_PERCENT * 100));
                }
            }

            for (double[] event : microEvents) {
                double micros = event[0] * slowdownFactor;
                int tick = (int) Math.round(micros / microsPerRedstoneTick);

                int rawPitch = (int) event[1];
                int mcPitch = rawPitch - 42;
                int octaveShift = 0;
                while (mcPitch < 0) {
                    mcPitch += 12;
                    octaveShift--;
                }
                while (mcPitch > 24) {
                    mcPitch -= 12;
                    octaveShift++;
                }

                Note note = new Note(mcPitch, octaveShift, NoteBlockInstrument.HARP, tick);
                song.addNote(note);
            }
            song.getNotes().sort(Comparator.comparingInt(Note::getTick));

        } catch (Exception e) {
            BlockNRoll.LOGGER.error("Error loading MIDI.");
            return new MIDI();
        }
        return this;
    }

    private double midiTickToMicros(long midiTick, int resolution, ArrayList<long[]> tempoChanges) {
        double totalMicros = 0;
        long prevTick = 0;
        int currentMpq = (int) tempoChanges.getFirst()[1];

        for (long[] change : tempoChanges) {
            long changeTick = change[0];
            if (changeTick >= midiTick) break;

            if (changeTick > prevTick) {
                double microsPerMidiTick = currentMpq / (double) resolution;
                totalMicros += (changeTick - prevTick) * microsPerMidiTick;
            }
            prevTick = changeTick;
            currentMpq = (int) change[1];
        }

        if (midiTick > prevTick) {
            double microsPerMidiTick = currentMpq / (double) resolution;
            totalMicros += (midiTick - prevTick) * microsPerMidiTick;
        }

        return totalMicros;
    }
}