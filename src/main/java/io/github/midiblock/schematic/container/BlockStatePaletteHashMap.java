package io.github.midiblock.schematic.container;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockStatePaletteHashMap implements IBlockStatePalette
{
    private final CrudeIncrementalIntIdentityHashBiMap<@NotNull BlockState> statePaletteMap;
    private final IBlockStatePaletteResizer paletteResizer;
    private final int bits;

    public BlockStatePaletteHashMap(int bitsIn, IBlockStatePaletteResizer paletteResizer) {
        this.bits = bitsIn;
        this.paletteResizer = paletteResizer;
        this.statePaletteMap = CrudeIncrementalIntIdentityHashBiMap.create(1 << bitsIn);
    }

    @Override
    public int idFor(BlockState state) {
        int i = this.statePaletteMap.getId(state);

        if (i == -1)
        {
            i = this.statePaletteMap.add(state);

            if (i >= (1 << this.bits))
            {
                i = this.paletteResizer.onResize(this.bits + 1, state);
            }
        }

        return i;
    }

    private void requestNewId(BlockState state) {
        final int origId = this.statePaletteMap.add(state);

        if (origId >= (1 << this.bits))
        {
            int newId = this.paletteResizer.onResize(this.bits + 1, BlockStateContainer.AIR_BLOCK_STATE);

            if (newId <= origId)
            {
                this.statePaletteMap.add(state);
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

        for (int id = 0; id < this.statePaletteMap.size(); ++id)
        {
            BlockState state = this.statePaletteMap.byId(id);

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
