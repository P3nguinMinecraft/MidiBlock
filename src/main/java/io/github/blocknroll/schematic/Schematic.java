package io.github.blocknroll.schematic;

import fi.dy.masa.litematica.schematic.container.LitematicaBlockStateContainer;
import io.github.blocknroll.Constants;
import io.github.blocknroll.structure.Block;
import io.github.blocknroll.structure.Structure;
import net.minecraft.nbt.*;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class Schematic {

    public static class RegionData {
        public LitematicaBlockStateContainer blockStateContainer;
        public CompoundTag regionTag;

        public RegionData(LitematicaBlockStateContainer container, CompoundTag tag) {
            this.blockStateContainer = container;
            this.regionTag = tag;
        }
    }

    private static CompoundTag createMetadata(Structure structure) {
        CompoundTag metadata = new CompoundTag();
        metadata.putString("Author", "BlockNRoll");
        metadata.putString("Description", "");

        CompoundTag size = new CompoundTag();
        size.putInt("x", (int) structure.getSize().x);
        size.putInt("y", (int) structure.getSize().y);
        size.putInt("z", (int) structure.getSize().z);

        metadata.put("EnclosingSize", size);

        metadata.putString("Name", structure.getName());
        metadata.putInt("RegionCount", 1);
        metadata.putLong("TimeCreated", System.currentTimeMillis());
        metadata.putLong("TimeModified", System.currentTimeMillis());
        metadata.putInt("TotalBlocks", structure.getBlocks().size());
        metadata.putInt("TotalVolume", (int) (structure.getSize().x * structure.getSize().y * structure.getSize().z));

        return metadata;
    }

    public static CompoundTag createRegions(Structure structure) {
        CompoundTag regions = new CompoundTag();
        CompoundTag region = new CompoundTag();

        int sizeX = (int) structure.getSize().x;
        int sizeY = (int) structure.getSize().y;
        int sizeZ = (int) structure.getSize().z;

        LitematicaBlockStateContainer container = new LitematicaBlockStateContainer(sizeX, sizeY, sizeZ);

        // Create tile entity list
        ListTag tileEntities = new ListTag();

        // Add all blocks from the structure to the container
        for (Block block : structure.getBlocks()) {
            net.minecraft.core.BlockPos pos = block.getPosition();
            net.minecraft.world.level.block.state.BlockState state = block.getState();

            // Add the block to the container at its position
            container.set(pos.getX(), pos.getY(), pos.getZ(), state);

            // Handle tile entities if the block state has one
            if (state.hasBlockEntity()) {
                CompoundTag teTag = new CompoundTag();
                teTag.putInt("x", pos.getX());
                teTag.putInt("y", pos.getY());
                teTag.putInt("z", pos.getZ());
                tileEntities.add(teTag);
            }
        }

        region.put("Entities", new ListTag());
        region.put("PendingBlockTicks", new ListTag());
        region.put("PendingFluidTicks", new ListTag());

        CompoundTag position = new CompoundTag();
        position.putInt("x", 0);
        position.putInt("y", 0);
        position.putInt("z", 0);
        region.put("Position", position);

        CompoundTag sizeTag = new CompoundTag();
        sizeTag.putInt("x", sizeX);
        sizeTag.putInt("y", sizeY);
        sizeTag.putInt("z", sizeZ);
        region.put("Size", sizeTag);

        region.put("TileEntities", tileEntities);

        regions.put(structure.getName(), region);
        return regions;
    }

    public static boolean saveStructure(Structure structure, File file) {
        try {
            CompoundTag root = new CompoundTag();

            root.putInt("MinecraftDataVersion", Constants.DATA_VERSION);
            root.putInt("Version", Constants.SCHEMATIC_VERSION);
            root.putInt("SubVersion", Constants.SCHEMATIC_VERSION_SUB);

            root.put("Metadata", createMetadata(structure));

            root.put("Regions", createRegions(structure));

            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            try (DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)))) {
                root.write(dos);
            }

            return true;
        } catch (IOException e) {
            System.err.println("Failed to save structure: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
