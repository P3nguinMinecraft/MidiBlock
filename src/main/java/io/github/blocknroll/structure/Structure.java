package io.github.blocknroll.structure;

import java.util.ArrayList;
import java.util.List;

public class Structure {

    private final List<Block> blocks = new ArrayList<>();

    public void addBlock(Block block) {
        blocks.add(block);
    }

    public List<Block> getBlocks() {
        return blocks;
    }
}