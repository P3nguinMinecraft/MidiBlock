package io.github.midiblock.schematic.container;

import net.minecraft.world.level.block.state.BlockState;

public interface IBlockStatePaletteResizer {
    int onResize(int bits, BlockState state);
}
