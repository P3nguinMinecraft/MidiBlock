package io.github.blocknroll.structure;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Block {

    private final BlockPos position;
    private final BlockState state;
    private final Map<String, Object> attributes;

    public Block(BlockPos position, BlockState state) {
        this.position = position;
        this.state = state;
        this.attributes = new HashMap<>();
    }

    public BlockPos getPosition() {
        return position;
    }

    public BlockState getState() {
        return state;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }
}