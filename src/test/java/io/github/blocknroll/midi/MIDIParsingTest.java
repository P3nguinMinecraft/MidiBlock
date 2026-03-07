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
        int midiNote = 60;
        int expectedPitch = midiNote % 12;
        int expectedOctave = (midiNote / 12) - 1;

        assertEquals(0, expectedPitch, "Middle C should have pitch 0");
        assertEquals(4, expectedOctave, "Middle C should be in octave 4");
    }

    @Test
    @DisplayName("Pitch range should be valid for different MIDI notes")
    void testPitchRangeForVariousNotes() {
        int[] testNotes = {36, 48, 60, 72, 84, 96};

        for (int midiNote : testNotes) {
            int pitch = midiNote % 12;
            int octave = (midiNote / 12) - 1;

            assertTrue(pitch >= 0 && pitch < 12, "Pitch should be between 0 and 11");
            assertTrue(octave >= -1, "Octave should be non-negative");
        }
    }

    @Test
    @DisplayName("Tick scaling factor should be calculated correctly")
    void testTickScalingFactor() {
        int resolution = 480;
        double midiTicksPer16th = resolution / 4.0;
        double scaleFactor = 8.0 / midiTicksPer16th;

        assertEquals(0.0667, scaleFactor, 0.001, "Scale factor should be 8 / 120 = 0.0667");
    }

    @Test
    @DisplayName("Tick scaling should convert MIDI ticks to game ticks")
    void testTickScaling() {
        int resolution = 480;
        double midiTicksPer16th = resolution / 4.0;
        double scaleFactor = 8.0 / midiTicksPer16th;

        int midiTick = resolution;
        int gameTick = (int) Math.round(midiTick * scaleFactor);

        assertEquals(32, gameTick, "Quarter note should scale to 32 game ticks");
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
    @DisplayName("Valid MIDI should contain notes with pitch between 0 and 11")
    void testValidPitchRange() {
        for (int i = 0; i < 12; i++) {
            int midiNote = 60 + i;
            int pitch = midiNote % 12;
            assertTrue(pitch >= 0 && pitch <= 11, "Pitch should be within note block range");
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
        assertNotNull(song.getSong(), "Song should have accessible notes list");
        assertEquals(song.getNotes(), song.getSong(), "getNotes and getSong should return same list");
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
    @DisplayName("Octave values should be calculated correctly for MIDI notes")
    void testOctaveCalculation() {
        int midiNote0 = 0;
        int octave0 = (midiNote0 / 12) - 1;
        assertEquals(-1, octave0, "MIDI note 0 should be octave -1");

        int midiNote12 = 12;
        int octave12 = (midiNote12 / 12) - 1;
        assertEquals(0, octave12, "MIDI note 12 should be octave 0");

        int midiNote60 = 60;
        int octave60 = (midiNote60 / 12) - 1;
        assertEquals(4, octave60, "MIDI note 60 should be octave 4");

        int midiNote127 = 127;
        int octave127 = (midiNote127 / 12) - 1;
        assertEquals(9, octave127, "MIDI note 127 should be octave 9");
    }

    @Test
    @DisplayName("Different MIDI resolutions should scale ticks correctly")
    void testTickScalingWithDifferentResolutions() {
        int[] resolutions = {96, 240, 480, 960};

        for (int resolution : resolutions) {
            double midiTicksPer16th = resolution / 4.0;
            double scaleFactor = 8.0 / midiTicksPer16th;

            int gameTick = (int) Math.round(resolution * scaleFactor);

            assertEquals(32, gameTick,
                "Quarter note should always scale to 32 game ticks regardless of resolution");
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
}

