package io.github.midiblock.midi;

import io.github.midiblock.MidiBlock;
import io.github.midiblock.config.Config;
import io.github.midiblock.config.InstrumentMode;

import javax.sound.midi.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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

            Map<Integer, ArrayList<long[]>> programChanges = new HashMap<>();
            // rawEvents: {midiTick, rawPitch, midiChannel}
            ArrayList<long[]> rawEvents = new ArrayList<>();

            for (Track track : sequence.getTracks()) {
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    if (event.getMessage() instanceof ShortMessage sm) {
                        if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
                            programChanges
                                    .computeIfAbsent(sm.getChannel(), k -> new ArrayList<>())
                                    .add(new long[]{event.getTick(), sm.getData1()});
                        } else if (sm.getCommand() == ShortMessage.NOTE_ON && sm.getData2() > 0) {
                            rawEvents.add(new long[]{event.getTick(), sm.getData1(), sm.getChannel()});
                        }
                    }
                }
            }
            for (ArrayList<long[]> changes : programChanges.values()) {
                changes.sort(Comparator.comparingLong(a -> a[0]));
            }

            ArrayList<double[]> microEvents = new ArrayList<>(); // {micros, rawPitch, midiChannel}
            for (long[] raw : rawEvents) {
                double micros = midiTickToMicros(raw[0], resolution, tempoChanges);
                microEvents.add(new double[]{micros, raw[1], raw[2]});
            }

            microEvents.sort(Comparator.comparingDouble(a -> a[0]));
            double smallestInterval = Double.MAX_VALUE;
            for (int i = 1; i < microEvents.size(); i++) {
                double gap = microEvents.get(i)[0] - microEvents.get(i - 1)[0];
                if (gap > 0 && gap < smallestInterval) {
                    smallestInterval = gap;
                }
            }

            double adaptFactor = 1.0;
            double microsPerRedstoneTick = 100_000.0;
            if (Config.ADAPT_BPM && smallestInterval > 0 && smallestInterval != Double.MAX_VALUE) {
                double requiredFactor = microsPerRedstoneTick / smallestInterval;
                double changePercent = Math.abs(requiredFactor - 1.0);
                if (changePercent <= Config.MAX_CHANGE_PERCENT / 100.0) {
                    adaptFactor = requiredFactor;
                    String direction = requiredFactor > 1.0 ? "Slowing down" : "Speeding up";
                    MidiBlock.LOGGER.info("{} song by {}% to align smallest interval to 1 redstone tick",
                            direction, String.format("%.1f", changePercent * 100));
                } else {
                    MidiBlock.LOGGER.info("Smallest interval ({} µs) would require {}% change, exceeding max {}%. Not adjusting.",
                            String.format("%.0f", smallestInterval),
                            String.format("%.1f", changePercent * 100),
                            Config.MAX_CHANGE_PERCENT);
                }
            }

            InstrumentMode mode = Config.INSTRUMENT_MODE;

            for (int idx = 0; idx < rawEvents.size(); idx++) {
                long[] raw = rawEvents.get(idx);
                long midiTick = raw[0];
                int rawPitch = (int) raw[1];
                int midiChannel = (int) raw[2];

                double micros = midiTickToMicros(midiTick, resolution, tempoChanges) * adaptFactor;
                int tick = (int) Math.round(micros / microsPerRedstoneTick);

                Instrument instrument;
                int mcPitch;

                switch (mode) {
                    case MIDI -> {
                        int program = getActiveProgram(programChanges, midiChannel, midiTick);
                        instrument = Instrument.fromMidiProgram(program);
                        mcPitch = instrument.toMcPitch(rawPitch);
                    }
                    case PITCH -> {
                        int program = getActiveProgram(programChanges, midiChannel, midiTick);
                        Instrument hint = Instrument.fromMidiProgram(program);
                        instrument = Instrument.bestForPitch(rawPitch, hint);
                        mcPitch = instrument.toMcPitch(rawPitch);
                    }
                    default -> {
                        instrument = Instrument.HARP;
                        mcPitch = rawPitch - 42;
                        while (mcPitch < 0) {
                            mcPitch += 12;
                        }
                        while (mcPitch > 24) {
                            mcPitch -= 12;
                        }
                    }
                }

                mcPitch = Math.max(0, Math.min(24, mcPitch));

                Note note = new Note(mcPitch, 0, instrument, tick);
                song.addNote(note);
            }
            song.getNotes().sort(Comparator.comparingInt(Note::getTick));

        } catch (Exception e) {
            MidiBlock.LOGGER.error("Error loading MIDI.", e);
            return new MIDI();
        }
        return this;
    }

    private int getActiveProgram(Map<Integer, ArrayList<long[]>> programChanges, int midiChannel, long midiTick) {
        ArrayList<long[]> changes = programChanges.get(midiChannel);
        if (changes == null || changes.isEmpty()) return 0;
        int program = 0;
        for (long[] change : changes) {
            if (change[0] > midiTick) break;
            program = (int) change[1];
        }
        return program;
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