package io.github.blocknroll.structure;

import io.github.blocknroll.Config;
import io.github.blocknroll.midi.Song;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ButtonBlock;

import java.util.ArrayList;
import java.util.List;

public class Structure {
    private ArrayList<Block> blocks;
    private ArrayList<BlockPos> blockPos;

    public Structure() {
        blocks = new ArrayList<>();
        blockPos = new ArrayList<>();
    }

    public void add(Block block) {
        if (blockPos.contains(block.getPosition())) throw new IllegalArgumentException("Block already exists at position " + block.getPosition());
        blockPos.add(block.getPosition());
        blocks.add(block);
    }

    public List<Block> get() {
        return blocks;
    }

    public void shift(BlockPos pos) {
        for (Block block : blocks) block.getPosition().offset(pos);
    }

    public void init(int middle) {
        add(new Block(
                new BlockPos(middle, 1, 0),
                Config.BUTTON.defaultBlockState()
                        .setValue(ButtonBlock.FACING, Direction.UP)
        ));
        add(new Block(
                new BlockPos(middle, 0, 0),
                Config.BUTTON.defaultBlockState()
        ));
    }

    public Structure fromSong(Song song) {
        Structure structure = new Structure();
        int max = song.getMaxConcurrent();
        structure.init(max);
        ArrayList<Channel> parts = song.partition();


        return structure;
    }
}