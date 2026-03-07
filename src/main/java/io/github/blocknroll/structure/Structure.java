package io.github.blocknroll.structure;

import io.github.blocknroll.Config;
import io.github.blocknroll.midi.Song;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ButtonBlock;

import java.util.ArrayList;
import java.util.List;

public class Structure {

    private List<Block> blocks;

    public Structure() {
        blocks = new ArrayList<>();
    }

    public void add(Block block) {
        blocks.add(block);
    }

    public List<Block> get() {
        return blocks;
    }

    public void shift(BlockPos pos) {
        for (Block block : blocks) block.getPosition().offset(pos);
    }

    public void init() {
        add(new Block(
                new BlockPos(0, 1, 0),
                Config.BUTTON.defaultBlockState()
                        .setValue(ButtonBlock.FACING, Direction.UP)
        ));
        add(new Block(
                new BlockPos(0, 0, 0),
                Config.BUTTON.defaultBlockState()
        ));
    }

    public Structure fromSong(Song song) {
        Structure structure = new Structure();
        structure.init();

        return structure;
    }
}