package io.github.midiblock.schematic.container;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateContainer implements IBlockStatePaletteResizer {

    public static final BlockState AIR_BLOCK_STATE = Blocks.AIR.defaultBlockState();
    protected BitArray storage;
    protected IBlockStatePalette palette;
    protected final Vec3i size;
    protected final int sizeX;
    protected final int sizeY;
    protected final int sizeZ;
    protected final int sizeLayer;
    protected final long totalVolume;
    protected int bits;

    public BlockStateContainer(int sizeX, int sizeY, int sizeZ) {
        this(sizeX, sizeY, sizeZ, 2);
    }

    public BlockStateContainer(int sizeX, int sizeY, int sizeZ, int bits) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.sizeLayer = sizeX * sizeZ;
        this.totalVolume = (long) this.sizeX * (long) this.sizeY * (long) this.sizeZ;
        this.size = new Vec3i(this.sizeX, this.sizeY, this.sizeZ);

        this.setBits(bits);
    }

    public void set(int x, int y, int z, BlockState state) {
        int id = this.palette.idFor(state);
        this.storage.setAt(this.getIndex(x, y, z), id);
    }

    protected int getIndex(int x, int y, int z) {
        return (y * this.sizeLayer) + z * this.sizeX + x;
    }

    protected void setBits(int bitsIn) {
        if (bitsIn != this.bits)
        {
            this.bits = bitsIn;

            if (this.bits <= 4)
            {
                this.bits = Math.max(2, this.bits);
                this.palette = new BlockStatePaletteLinear(this.bits, this);
            }
            else
            {
                this.palette = new BlockStatePaletteHashMap(this.bits, this);
            }

            this.palette.idFor(AIR_BLOCK_STATE);
            this.storage = new BitArray(this.bits, this.totalVolume);
        }
    }

    @Override
    public int onResize(int bits, BlockState state) {
        BitArray oldStorage = this.storage;
        IBlockStatePalette oldPalette = this.palette;
        final long storageLength = oldStorage.size();

        this.setBits(bits);

        BitArray newStorage = this.storage;

        for (long index = 0; index < storageLength; ++index)
        {
            newStorage.setAt(index, oldStorage.getAt(index));
        }

        this.palette.readFromNBT(oldPalette.writeToNBT());

        return this.palette.idFor(state);
    }

    public long[] getBackingLongArray() {
        return this.storage.getBackingLongArray();
    }

    public IBlockStatePalette getPalette() {
        return this.palette;
    }
}
