package io.github.midiblock.structure;

import io.github.midiblock.config.Config;
import io.github.midiblock.midi.Instrument;
import io.github.midiblock.midi.Note;
import io.github.midiblock.midi.Song;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.AttachFace;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class Structure {
    private final ArrayList<Block> blocks;
    private final ArrayList<BlockPos> blockPos;
    private final String name;

    public Structure(String name) {
        blocks = new ArrayList<>();
        blockPos = new ArrayList<>();
        this.name = name;
    }

    public void add(Block block) {
        if (blockPos.contains(block.getPosition()))
            throw new IllegalArgumentException("Block already exists at position " + block.getPosition());
        blockPos.add(block.getPosition());
        blocks.add(block);
    }
    public Vector3d getMin() {
        if (blockPos.isEmpty()) {
            return new Vector3d(0, 0, 0);
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        for (BlockPos pos : blockPos) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
        }

        return new Vector3d(minX, minY, minZ);
    }

    public Vector3d getMax() {
        if (blockPos.isEmpty()) {
            return new Vector3d(0, 0, 0);
        }

        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (BlockPos pos : blockPos) {
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
        }

        return new Vector3d(maxX, maxY, maxZ);
    }

    public Vector3d getSize() {
        Vector3d max = getMax();
        Vector3d min = getMin();
        return new Vector3d(
                max.x - min.x + 1,
                max.y - min.y + 1,
                max.z - min.z + 1
        );
    }

    public void shift() {
        Vector3d min = getMin();
        int offsetX = (int) min.x;
        int offsetY = (int) min.y;
        int offsetZ = (int) min.z;

        for (int i = 0; i < blockPos.size(); i++) {
            BlockPos oldPos = blockPos.get(i);
            BlockPos newPos = new BlockPos(
                    oldPos.getX() - offsetX,
                    oldPos.getY() - offsetY,
                    oldPos.getZ() - offsetZ
            );
            blockPos.set(i, newPos);

            Block oldBlock = blocks.get(i);
            blocks.set(i, new Block(newPos, oldBlock.getState()));
        }
    }

    public String getName() {
        return name;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    private void init(int max) {
        add(new Block(
                new BlockPos(max / 2, 1, 0),
                Config.getButton().defaultBlockState()
                        .setValue(ButtonBlock.FACING, Direction.EAST)
                        .setValue(ButtonBlock.POWERED, false)
                        .setValue(ButtonBlock.FACE, AttachFace.FLOOR)
        ));
        add(new Block(
                new BlockPos(max / 2, 0, 0),
                Config.getFloor().defaultBlockState()
        ));
        for (int i = 0; i < max; i++) {
            add(new Block(
                    new BlockPos(i, 0, 1),
                    Config.getFloor().defaultBlockState()
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
            if (x % 2 == 1) {
                add(new Block(
                        new BlockPos(x, 0, z),
                        Config.getFloor().defaultBlockState()
                ));
                add(new Block(
                        new BlockPos(x, 1, z),
                        Blocks.REPEATER.defaultBlockState()
                                .setValue(RepeaterBlock.FACING, Direction.NORTH)
                                .setValue(RepeaterBlock.DELAY, 1)
                                .setValue(RepeaterBlock.LOCKED, false)
                                .setValue(RepeaterBlock.POWERED, false)
                ));
            }
            if (note != null) {
                Instrument inst = note.getInstrument();

                add(new Block(
                        new BlockPos(x, 1, z + 1),
                        Blocks.NOTE_BLOCK.defaultBlockState()
                                .setValue(NoteBlock.INSTRUMENT, inst.getNoteBlockInstrument())
                                .setValue(NoteBlock.POWERED, false)
                                .setValue(NoteBlock.NOTE, note.getPitch())
                ));

                if (inst != Instrument.HARP) {
                    add(new Block(
                            new BlockPos(x, 0, z + 1),
                            inst.getBlock()
                    ));
                }
            }
            else {
                add(new Block(
                        new BlockPos(x, 1, z + 1),
                        Config.getConductive().defaultBlockState()
                ));
            }
        }
    }

    public Structure fromSong(Song song) {
        int max = song.getMaxConcurrent();
        init(max - 1);
        ArrayList<Channel> parts = song.partition();
        int x = 0;
        if (parts.size() < 2) x++;
        for (Channel channel : parts) {
            buildPart(channel, x, 2);
            x++;
        }
        return this;
    }
}