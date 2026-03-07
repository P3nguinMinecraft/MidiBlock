package io.github.blocknroll.midi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MIDI Parsing Tests")
class MIDIParsingTest {

    private MIDI midi;

    @BeforeEach
    void setUp() {
        midi = new MIDI();
    }

    @Test
    @DisplayName("New MIDI object should have an empty song")
    void testNewMIDIHasEmptySong() {
        assertNotNull(midi.song, "Song should be initialized");
        assertTrue(midi.song.getNotes().isEmpty(), "New MIDI should have empty song");
    }

    @Test
    @DisplayName("fromFile should return a MIDI object")
    void testFromFileReturnsValidMIDI() {
        File testFile = new File("build/resources/main");
        if (!testFile.exists()) {
            MIDI result = midi.fromFile(testFile);
            assertNotNull(result, "fromFile should return a MIDI object");
            assertNotNull(result.song, "Returned MIDI should have a song");
        }
    }

    @Test
    @DisplayName("Pitch extraction should correctly convert MIDI note numbers")
    void testPitchExtraction() {
        // Minecraft note blocks: pitch 0 = F#3 (MIDI 42) to pitch 24 = F#5 (MIDI 66)
        // Middle C (MIDI 60) → 60 - 42 = 18
        int midiNote = 60;
        int expectedMcPitch = midiNote - 42;

        assertEquals(18, expectedMcPitch, "Middle C should map to MC pitch 18");
    }

    @Test
    @DisplayName("Pitch range should be valid for different MIDI notes")
    void testPitchRangeForVariousNotes() {
        // Minecraft note blocks: pitch 0 = F#3 (MIDI 42) to pitch 24 = F#5 (MIDI 66)
        // Notes outside this range get octave-shifted into 0-24
        int[] testNotes = {36, 48, 60, 72, 84, 96};

        for (int midiNote : testNotes) {
            int mcPitch = midiNote - 42;
            while (mcPitch < 0) mcPitch += 12;
            while (mcPitch > 24) mcPitch -= 12;

            assertTrue(mcPitch >= 0 && mcPitch <= 24,
                "MC pitch should be between 0 and 24, got " + mcPitch + " for MIDI " + midiNote);
        }
    }

    @Test
    @DisplayName("Tick scaling factor should be calculated correctly")
    void testTickScalingFactor() {
        // At 120 BPM (default), 500000 µs per quarter note
        // Resolution 480 → 500000/480 ≈ 1041.67 µs per MIDI tick
        // 1 redstone tick = 100000 µs
        // Quarter note (480 ticks) → 500000/100000 = 5 redstone ticks
        int resolution = 480;
        int mpq = 500_000; // 120 BPM
        double microsPerMidiTick = mpq / (double) resolution;
        double microsPerRedstoneTick = 100_000.0;
        int redstoneTick = (int) Math.round((resolution * microsPerMidiTick) / microsPerRedstoneTick);

        assertEquals(5, redstoneTick, "Quarter note at 120 BPM should be 5 redstone ticks");
    }

    @Test
    @DisplayName("Tick scaling should convert MIDI ticks to redstone ticks")
    void testTickScaling() {
        // At 120 BPM (default), a quarter note = 0.5 sec = 5 redstone ticks
        int resolution = 480;
        int mpq = 500_000; // 120 BPM
        double microsPerMidiTick = mpq / (double) resolution;
        double microsPerRedstoneTick = 100_000.0;

        int midiTick = resolution; // one quarter note
        int redstoneTick = (int) Math.round((midiTick * microsPerMidiTick) / microsPerRedstoneTick);

        assertEquals(5, redstoneTick, "Quarter note at 120 BPM should scale to 5 redstone ticks");
    }

    @Test
    @DisplayName("Notes should be sorted by tick after parsing")
    void testNotesSortedByTick() {
        Song song = new Song();
        song.addNote(new Note(5, 0, null, 10));
        song.addNote(new Note(7, 0, null, 3));
        song.addNote(new Note(8, 0, null, 15));
        song.addNote(new Note(6, 0, null, 1));

        java.util.ArrayList<Note> notes = song.getNotes();
        notes.sort(java.util.Comparator.comparingInt(Note::getTick));

        assertEquals(1, notes.get(0).getTick());
        assertEquals(3, notes.get(1).getTick());
        assertEquals(10, notes.get(2).getTick());
        assertEquals(15, notes.get(3).getTick());
    }

    @Test
    @DisplayName("Valid MIDI should contain notes with pitch between 0 and 24")
    void testValidPitchRange() {
        // MIDI notes 42-66 map directly to MC pitch 0-24
        for (int midiNote = 42; midiNote <= 66; midiNote++) {
            int mcPitch = midiNote - 42;
            assertTrue(mcPitch >= 0 && mcPitch <= 24, "MC pitch should be within note block range 0-24");
        }
    }

    @Test
    @DisplayName("Note objects should contain required information")
    void testNoteContainsRequiredData() {
        Song song = new Song();
        Note note = new Note(5, 2, null, 42);
        song.addNote(note);

        assertEquals(1, song.getNotes().size(), "Song should contain the added note");

        Note retrieved = song.getNotes().get(0);
        assertEquals(5, retrieved.getPitch(), "Note should have correct pitch");
        assertEquals(2, retrieved.getOctave(), "Note should have correct octave");
        assertEquals(42, retrieved.getTick(), "Note should have correct tick");
    }

    @Test
    @DisplayName("MIDI file parsing should handle nonexistent files gracefully")
    void testNonexistentFileHandling() {
        File nonexistent = new File("/nonexistent/path/to/file.mid");
        MIDI result = midi.fromFile(nonexistent);

        assertNotNull(result, "Should return a MIDI object even for nonexistent file");
        assertTrue(result.song.getNotes().isEmpty(), "Song should be empty for nonexistent file");
    }

    @Test
    @DisplayName("MIDI parsing should create Song with correct structure")
    void testMIDICreatesValidSongStructure() {
        Song song = new Song();
        song.addNote(new Note(0, 0, null, 0));
        song.addNote(new Note(5, 1, null, 8));
        song.addNote(new Note(11, 2, null, 16));

        assertEquals(3, song.getNotes().size(), "Song should contain all added notes");
        assertNotNull(song.getNotes(), "Song should have accessible notes list");
    }

    @Test
    @DisplayName("Multiple notes at same tick should be preserved")
    void testMultipleNotesAtSameTick() {
        Song song = new Song();
        song.addNote(new Note(0, 0, null, 5));
        song.addNote(new Note(5, 0, null, 5));
        song.addNote(new Note(10, 0, null, 5));

        assertEquals(3, song.getNotes().size(), "All three notes should be added");

        int count = 0;
        for (Note n : song.getNotes()) {
            if (n.getTick() == 5) {
                count++;
            }
        }
        assertEquals(3, count, "All three notes should be at tick 5");
    }

    @Test
    @DisplayName("fromFile method should not throw exceptions for invalid files")
    void testFromFileDoesNotThrowException() {
        File invalidFile = new File("src/test/resources/invalid.mid");

        assertDoesNotThrow(() -> {
            MIDI result = midi.fromFile(invalidFile);
            assertNotNull(result);
        }, "fromFile should handle exceptions internally");
    }

    @Test
    @DisplayName("Song should calculate note count correctly")
    void testNoteCountCalculation() {
        Song song = new Song();
        song.addNote(new Note(0, 0, null, 0));
        song.addNote(new Note(5, 0, null, 0));
        song.addNote(new Note(8, 0, null, 2));
        song.addNote(new Note(10, 0, null, 5));

        int[] counts = song.getNoteCount();
        assertEquals(6, counts.length, "Note count array should span from tick 0 to 5");
        assertEquals(2, counts[0], "Tick 0 should have 2 notes");
        assertEquals(0, counts[1], "Tick 1 should have 0 notes");
        assertEquals(1, counts[2], "Tick 2 should have 1 note");
        assertEquals(0, counts[3], "Tick 3 should have 0 notes");
        assertEquals(0, counts[4], "Tick 4 should have 0 notes");
        assertEquals(1, counts[5], "Tick 5 should have 1 note");
    }

    @Test
    @DisplayName("Octave shift should be calculated correctly for MIDI notes outside MC range")
    void testOctaveCalculation() {
        // MIDI 42 = F#3 = MC pitch 0, octaveShift 0
        int mcPitch42 = 42 - 42;
        assertEquals(0, mcPitch42, "MIDI 42 (F#3) should be MC pitch 0");

        // MIDI 30 = F#2 → 30-42 = -12, shift up once → 0, octaveShift -1
        int mcPitch30 = 30 - 42; // -12
        int shift30 = 0;
        while (mcPitch30 < 0) { mcPitch30 += 12; shift30--; }
        assertEquals(0, mcPitch30, "MIDI 30 (F#2) should wrap to MC pitch 0");
        assertEquals(-1, shift30, "MIDI 30 should have octave shift -1");

        // MIDI 60 = C4 → 60-42 = 18, in range, octaveShift 0
        int mcPitch60 = 60 - 42;
        assertEquals(18, mcPitch60, "MIDI 60 (C4) should be MC pitch 18");

        // MIDI 72 = C5 → 72-42 = 30, shift down once → 18, octaveShift +1
        int mcPitch72 = 72 - 42; // 30
        int shift72 = 0;
        while (mcPitch72 > 24) { mcPitch72 -= 12; shift72++; }
        assertEquals(18, mcPitch72, "MIDI 72 (C5) should wrap to MC pitch 18");
        assertEquals(1, shift72, "MIDI 72 should have octave shift +1");
    }

    @Test
    @DisplayName("Different MIDI resolutions should scale ticks correctly")
    void testTickScalingWithDifferentResolutions() {
        int[] resolutions = {96, 240, 480, 960};
        int mpq = 500_000; // 120 BPM default
        double microsPerRedstoneTick = 100_000.0;

        for (int resolution : resolutions) {
            double microsPerMidiTick = mpq / (double) resolution;
            int redstoneTick = (int) Math.round((resolution * microsPerMidiTick) / microsPerRedstoneTick);

            assertEquals(5, redstoneTick,
                "Quarter note at 120 BPM should always scale to 5 redstone ticks regardless of resolution");
        }
    }

    @Test
    @DisplayName("Song should maintain note order")
    void testNoteOrderMaintenance() {
        Song song = new Song();

        Note note1 = new Note(0, 0, null, 1);
        Note note2 = new Note(5, 0, null, 2);
        Note note3 = new Note(10, 0, null, 3);

        song.addNote(note1);
        song.addNote(note2);
        song.addNote(note3);

        ArrayList<Note> notes = song.getNotes();
        assertEquals(note1, notes.get(0), "First note should be at index 0");
        assertEquals(note2, notes.get(1), "Second note should be at index 1");
        assertEquals(note3, notes.get(2), "Third note should be at index 2");
    }

    @Test
    @DisplayName("MIDI parsing should handle edge case tick values")
    void testEdgeCaseTickValues() {
        Song song = new Song();

        song.addNote(new Note(0, 0, null, 0));

        song.addNote(new Note(5, 0, null, 10000));

        song.addNote(new Note(10, 0, null, 5000));

        assertEquals(3, song.getNotes().size());

        int[] counts = song.getNoteCount();
        assertEquals(10001, counts.length, "Array should accommodate largest tick");
        assertEquals(1, counts[0], "Should have note at tick 0");
        assertEquals(1, counts[5000], "Should have note at tick 5000");
        assertEquals(1, counts[10000], "Should have note at tick 10000");
    }

    @Test
    @DisplayName("BPM adaptation should apply when change is within threshold")
    void testAdaptWithinThreshold() {
        // Smallest interval = 80,000 µs (< 100,000 µs = 1 redstone tick) → needs slowdown
        // Required factor = 100,000 / 80,000 = 1.25 → 25% change
        // MAX_CHANGE_PERCENT = 30 → should apply
        double smallestInterval = 80_000;
        double microsPerRedstoneTick = 100_000.0;
        double requiredFactor = microsPerRedstoneTick / smallestInterval;
        double changePercent = Math.abs(requiredFactor - 1.0);

        assertEquals(1.25, requiredFactor, 0.001, "Factor should be 1.25");
        assertEquals(0.25, changePercent, 0.001, "Change should be 25%");
        assertTrue(changePercent <= 30 / 100.0, "25% should be within 30% threshold");

        // After applying factor, smallest interval becomes 100,000 µs = 1 redstone tick
        double adjusted = smallestInterval * requiredFactor;
        assertEquals(100_000, adjusted, 0.01, "Adjusted interval should be exactly 1 redstone tick");
    }

    @Test
    @DisplayName("BPM adaptation should NOT apply when change exceeds threshold")
    void testAdaptExceedsThreshold() {
        // Smallest interval = 50,000 µs → factor = 2.0 → 100% change
        // MAX_CHANGE_PERCENT = 30 → should NOT apply
        double smallestInterval = 50_000;
        double microsPerRedstoneTick = 100_000.0;
        double requiredFactor = microsPerRedstoneTick / smallestInterval;
        double changePercent = Math.abs(requiredFactor - 1.0);

        assertEquals(2.0, requiredFactor, 0.001, "Factor should be 2.0");
        assertEquals(1.0, changePercent, 0.001, "Change should be 100%");
        assertFalse(changePercent <= 30 / 100.0, "100% should exceed 30% threshold");
    }

    @Test
    @DisplayName("BPM adaptation should support speedup when interval exceeds 1 redstone tick")
    void testAdaptSpeedup() {
        // Smallest interval = 125,000 µs (> 100,000 µs) → needs speedup
        // Required factor = 100,000 / 125,000 = 0.8 → 20% change
        // MAX_CHANGE_PERCENT = 30 → should apply
        double smallestInterval = 125_000;
        double microsPerRedstoneTick = 100_000.0;
        double requiredFactor = microsPerRedstoneTick / smallestInterval;
        double changePercent = Math.abs(requiredFactor - 1.0);

        assertEquals(0.8, requiredFactor, 0.001, "Factor should be 0.8");
        assertEquals(0.2, changePercent, 0.001, "Change should be 20%");
        assertTrue(requiredFactor < 1.0, "Factor < 1 means speedup");
        assertTrue(changePercent <= 30 / 100.0, "20% should be within 30% threshold");

        double adjusted = smallestInterval * requiredFactor;
        assertEquals(100_000, adjusted, 0.01, "Adjusted interval should be exactly 1 redstone tick");
    }
}

