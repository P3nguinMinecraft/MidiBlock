package io.github.midiblock.schematic.container;

import io.github.midiblock.MidiBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStatePaletteLinear implements IBlockStatePalette
{
    private final BlockState[] states;
    private final IBlockStatePaletteResizer resizeHandler;
    private final int bits;
    private int currentSize;

    public BlockStatePaletteLinear(int bitsIn, IBlockStatePaletteResizer resizeHandler) {
        this.states = new BlockState[1 << bitsIn];
        this.bits = bitsIn;
        this.resizeHandler = resizeHandler;
    }

    @Override
    public int idFor(BlockState state) {
        for (int i = 0; i < this.currentSize; ++i)
        {
            if (this.states[i] == state)
            {
                return i;
            }
        }

        final int size = this.currentSize;

        if (size < this.states.length)
        {
            this.states[size] = state;
            ++this.currentSize;
            return size;
        }
        else
        {
            return this.resizeHandler.onResize(this.bits + 1, state);
        }
    }

    private void requestNewId(BlockState state) {
        final int size = this.currentSize;

        if (size < this.states.length)
        {
            this.states[size] = state;
            ++this.currentSize;
        }
        else
        {
            int newId = this.resizeHandler.onResize(this.bits + 1, BlockStateContainer.AIR_BLOCK_STATE);

            try {
                if (newId <= size) {
                    this.states[size] = state;
                    ++this.currentSize;
                }
            }
            catch (ArrayIndexOutOfBoundsException e) {
                MidiBlock.LOGGER.error("BlockStatePaletteLinear array index out of bounds exception");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void readFromNBT(ListTag tagList) {
        final int size = tagList.size();

        for (int i = 0; i < size; ++i)
        {
            CompoundTag tag = tagList.getCompoundOrEmpty(i);
            BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK, tag);

            if (i > 0 || state != BlockStateContainer.AIR_BLOCK_STATE)
            {
                this.requestNewId(state);
            }
        }
    }

    @Override
    public ListTag writeToNBT() {
        ListTag tagList = new ListTag();

        for (int id = 0; id < this.currentSize; ++id)
        {
            BlockState state = this.states[id];

            if (state == null)
            {
                state = BlockStateContainer.AIR_BLOCK_STATE;
            }

            CompoundTag tag = NbtUtils.writeBlockState(state);
            tagList.add(tag);
        }

        return tagList;
    }

}
