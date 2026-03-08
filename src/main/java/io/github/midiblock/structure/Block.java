package io.github.midiblock.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Block {

    private final BlockPos position;
    private final BlockState state;

    public Block(BlockPos position, BlockState state) {
        this.position = position;
        this.state = state;
    }

    public BlockPos getPosition() {
        return position;
    }

    public BlockState getState() {
        return state;
    }
}