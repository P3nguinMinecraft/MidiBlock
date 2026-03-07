package io.github.blocknroll.structure;

import io.github.blocknroll.Config;
import io.github.blocknroll.midi.Note;
import io.github.blocknroll.midi.Song;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

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

    private void init(int middle) {
        add(new Block(
                new BlockPos(middle, 1, 0),
                Config.BUTTON.defaultBlockState()
                        .setValue(ButtonBlock.FACING, Direction.UP)
        ));
        add(new Block(
                new BlockPos(middle, 0, 0),
                Config.BUTTON.defaultBlockState()
        ));
        for (int i = 0; i <= middle * 2; i++) {
            add(new Block(
                    new BlockPos(i, 0, 1),
                    Config.FLOOR.defaultBlockState()
            ));
            add(new Block(
                    new BlockPos(i, 1, 1),
                    Blocks.REDSTONE_WIRE.defaultBlockState()
            ));
        }
    }

    private void buildPart(Channel channel, int x, int startZ) {
        for (int i = 0; i < channel.getLength(); i++) {
            Note note = channel.getNote(i);
            int z = i * 2 + startZ;
            add(new Block(
                    new BlockPos(x, 0, z),
                    Config.FLOOR.defaultBlockState()
            ));
            add(new Block(
                    new BlockPos(x, 1, z),
                    Blocks.REPEATER.defaultBlockState()
                            .setValue(RepeaterBlock.FACING, Direction.SOUTH)
                            .setValue(RepeaterBlock.DELAY, 1)
                            .setValue(RepeaterBlock.LOCKED, false)
                            .setValue(RepeaterBlock.POWERED, false)
            ));

            if (note != null) {
                add(new Block(
                        new BlockPos(x, 1, z + 1),
                        Blocks.NOTE_BLOCK.defaultBlockState()
                                .setValue(NoteBlock.INSTRUMENT, note.getInstrument())
                                .setValue(NoteBlock.POWERED, false)
                                .setValue(NoteBlock.NOTE, note.getPitch())
                ));

                if (!note.getInstrument().equals(NoteBlockInstrument.HARP)) {
                    add(new Block(
                            new BlockPos(x, 0, z + 1),
                            Blocks.AIR.defaultBlockState() // TODO: map instrument -> block
                    ));
                }
            }
            else {
                add(new Block(
                        new BlockPos(x, 1, z + 1),
                        Config.CONDUCTIVE.defaultBlockState()
                ));
            }
        }
    }

    public Structure fromSong(Song song) {
        int max = song.getMaxConcurrent();
        init(max - 1);
        ArrayList<Channel> parts = song.partition();
        int x = 0;
        for (Channel channel : parts) {
            buildPart(channel, x, 2);
            x += 2;
        }

        return this;
    }
}