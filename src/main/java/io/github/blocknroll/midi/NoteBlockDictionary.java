package io.github.blocknroll.midi;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.List;
// Notice that java.util.Properties is gone!

public class NoteBlockDictionary {

    // This list will hold our 25 pre-configured BlockStates
    private final List<BlockState> tunedBlocks;

    public NoteBlockDictionary() {
        this.tunedBlocks = generateTunedBlocks();
    }

    private List<BlockState> generateTunedBlocks() {
        List<BlockState> list = new ArrayList<>();

        // Loop through all 25 possible Minecraft pitches (0 to 24)
        for (int i = 0; i <= 24; i++) {

            // Step A: Get the default state using the Mojmap method
            BlockState defaultState = Blocks.NOTE_BLOCK.defaultBlockState();

            // Step B: Apply the note value using setValue and BlockStateProperties
            BlockState tunedState = defaultState.setValue(BlockStateProperties.NOTE, i);

            // Step C: Add the modified BlockState to our list
            list.add(tunedState);
        }

        return list;
    }

    // Method to retrieve a specific BlockState later
    public BlockState getNoteBlockForPitch(int pitch) {
        if (pitch < 0 || pitch > 24) {
            throw new IllegalArgumentException("Pitch must be between 0 and 24");
        }
        return tunedBlocks.get(pitch);
    }
}