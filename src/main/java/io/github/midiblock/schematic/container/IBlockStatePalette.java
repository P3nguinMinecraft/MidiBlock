package io.github.midiblock.schematic.container;

import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockStatePalette {

    /**
     * Gets the palette id for the given block state and adds
     * the state to the palette if it doesn't exist there yet.
     */
    int idFor(BlockState state);

    void readFromNBT(ListTag tagList);

    ListTag writeToNBT();

}
