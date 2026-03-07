package io.github.blocknroll.midi;

import io.github.blocknroll.structure.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Song Partitioning Tests")
class SongPartitionTest {
    private Song song;

    @BeforeEach
    void setUp() {
        song = new Song();
    }

    @Test
    @DisplayName("Empty song should produce no channels")
    void testEmptySongPartition() {
        ArrayList<Channel> channels = song.partition();
        assertNotNull(channels);
        assertTrue(channels.isEmpty(), "Empty song should produce no channels");
    }

    @Test
    @DisplayName("Single note should produce one channel")
    void testSingleNotePartition() {
        song.addNote(new Note(5, 0, null, 0));

        ArrayList<Channel> channels = song.partition();
        assertEquals(1, channels.size(), "Single note should produce 1 channel");

        Channel channel = channels.get(0);
        assertEquals(1, channel.getLength(), "Channel should have length 1");
        assertNotNull(channel.getNote(0), "Note should be present at tick 0");
    }

    @Test
    @DisplayName("Multiple notes at different ticks should produce one channel")
    void testMultipleNotesAtDifferentTicks() {
        song.addNote(new Note(5, 0, null, 0));
        song.addNote(new Note(7, 0, null, 2));
        song.addNote(new Note(10, 0, null, 5));

        ArrayList<Channel> channels = song.partition();
        assertEquals(1, channels.size(), "Notes at different ticks should produce 1 channel");

        Channel channel = channels.get(0);
        assertEquals(6, channel.getLength(), "Channel should span from tick 0 to 5");
        assertNotNull(channel.getNote(0), "Note should be at tick 0");
        assertNotNull(channel.getNote(2), "Note should be at tick 2");
        assertNotNull(channel.getNote(5), "Note should be at tick 5");
    }

    @Test
    @DisplayName("Multiple notes at same tick should produce multiple channels")
    void testMultipleNotesAtSameTick() {
        song.addNote(new Note(5, 0, null, 0));
        song.addNote(new Note(7, 0, null, 0));
        song.addNote(new Note(10, 0, null, 0));

        ArrayList<Channel> channels = song.partition();
        assertEquals(3, channels.size(), "Three concurrent notes should produce 3 channels");

        for (Channel channel : channels) {
            assertEquals(1, channel.getLength(), "Each channel should have length 1");
            assertNotNull(channel.getNote(0), "Each channel should have a note at tick 0");
        }
    }

    @Test
    @DisplayName("Complex partitioning with varying concurrent notes")
    void testComplexPartitioningScenario() {
        song.addNote(new Note(5, 0, null, 0));
        song.addNote(new Note(7, 0, null, 0));

        song.addNote(new Note(8, 0, null, 1));

        song.addNote(new Note(9, 0, null, 2));
        song.addNote(new Note(10, 0, null, 2));
        song.addNote(new Note(11, 0, null, 2));

        song.addNote(new Note(12, 0, null, 3));

        ArrayList<Channel> channels = song.partition();
        assertEquals(3, channels.size(), "Maximum concurrent notes is 3, should produce 3 channels");

        for (Channel channel : channels) {
            assertEquals(4, channel.getLength(), "All channels should have length 4 (max tick is 3)");
        }
    }

    @Test
    @DisplayName("getMaxConcurrent should return correct maximum")
    void testGetMaxConcurrent() {
        assertEquals(0, song.getMaxConcurrent(), "Empty song should have max concurrent 0");

        song.addNote(new Note(5, 0, null, 0));
        assertEquals(1, song.getMaxConcurrent(), "One note should give max concurrent 1");

        song.addNote(new Note(7, 0, null, 0));
        song.addNote(new Note(8, 0, null, 0));
        assertEquals(3, song.getMaxConcurrent(), "Three notes at same tick should give max concurrent 3");

        song.addNote(new Note(9, 0, null, 5));
        assertEquals(3, song.getMaxConcurrent(), "Adding note at different tick shouldn't increase max concurrent");
    }

    @Test
    @DisplayName("getNoteCount should track notes per tick")
    void testNoteCountArray() {
        song.addNote(new Note(5, 0, null, 0));
        song.addNote(new Note(7, 0, null, 0));
        song.addNote(new Note(8, 0, null, 2));

        int[] counts = song.getNoteCount();
        assertEquals(3, counts.length, "Note count should have length 3 (max tick is 2)");
        assertEquals(2, counts[0], "Tick 0 should have 2 notes");
        assertEquals(0, counts[1], "Tick 1 should have 0 notes");
        assertEquals(1, counts[2], "Tick 2 should have 1 note");
    }

    @Test
    @DisplayName("Partitioning should distribute all notes across channels")
    void testPartitioningDistributesNotesCorrectly() {
        song.addNote(new Note(5, 0, null, 0));
        song.addNote(new Note(7, 0, null, 0));
        song.addNote(new Note(8, 0, null, 0));

        ArrayList<Channel> channels = song.partition();
        assertEquals(3, channels.size());

        int totalNotes = 0;
        for (Channel channel : channels) {
            for (Note note : channel.getNotes()) {
                if (note != null) {
                    totalNotes++;
                }
            }
        }
        assertEquals(3, totalNotes, "Total notes in all channels should equal original note count");
    }

    @Test
    @DisplayName("Partitioning should handle large tick distances")
    void testPartitioningWithLargeTick() {
        song.addNote(new Note(5, 0, null, 0));
        song.addNote(new Note(7, 0, null, 100));

        ArrayList<Channel> channels = song.partition();
        assertEquals(1, channels.size(), "Two notes at different ticks should produce 1 channel");

        Channel channel = channels.get(0);
        assertEquals(101, channel.getLength(), "Channel should span from tick 0 to 100");
    }

    @Test
    @DisplayName("Notes should be placed at correct tick positions")
    void testNotePositioningInChannels() {
        song.addNote(new Note(5, 0, null, 0));
        song.addNote(new Note(7, 0, null, 0));
        song.addNote(new Note(8, 0, null, 3));

        ArrayList<Channel> channels = song.partition();
        assertEquals(2, channels.size());

        boolean noteAt0Found = false;
        boolean noteAt3Found = false;

        for (Channel channel : channels) {
            if (channel.getNote(0) != null) {
                noteAt0Found = true;
            }
            if (channel.getNote(3) != null) {
                noteAt3Found = true;
            }
        }

        assertTrue(noteAt0Found, "At least one note should be at tick 0");
        assertTrue(noteAt3Found, "At least one note should be at tick 3");
    }

    @Test
    @DisplayName("Partitioning should handle single concurrent notes")
    void testPartitioningWithSpacedNotes() {
        song.addNote(new Note(5, 0, null, 0));
        song.addNote(new Note(7, 0, null, 1));
        song.addNote(new Note(8, 0, null, 2));

        ArrayList<Channel> channels = song.partition();
        assertEquals(1, channels.size(), "Non-concurrent notes should produce 1 channel");

        Channel channel = channels.get(0);
        assertEquals(3, channel.getLength(), "Channel length should be 3");
        assertEquals(3, countNonNullNotes(channel), "Should contain all 3 notes");
    }

    @Test
    @DisplayName("Each channel should have consistent length")
    void testChannelLengthConsistency() {
        song.addNote(new Note(5, 0, null, 0));
        song.addNote(new Note(7, 0, null, 0));
        song.addNote(new Note(8, 0, null, 0));
        song.addNote(new Note(9, 0, null, 5));

        ArrayList<Channel> channels = song.partition();
        int expectedLength = 6;

        for (Channel channel : channels) {
            assertEquals(expectedLength, channel.getLength(),
                "All channels should have the same length equal to max tick + 1");
        }
    }

    @Test
    @DisplayName("getNoteCount should return empty array for empty song")
    void testNoteCountEmptySong() {
        int[] counts = song.getNoteCount();
        assertEquals(0, counts.length, "Empty song should return empty note count array");
    }

    private int countNonNullNotes(Channel channel) {
        int count = 0;
        for (Note note : channel.getNotes()) {
            if (note != null) {
                count++;
            }
        }
        return count;
    }
}


