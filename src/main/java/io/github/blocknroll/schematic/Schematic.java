package io.github.blocknroll.schematic;

import fi.dy.masa.litematica.schematic.container.LitematicaBlockStateContainer;
import io.github.blocknroll.config.Constants;
import io.github.blocknroll.structure.Structure;
import net.minecraft.nbt.*;

import java.io.*;
public class Schematic {

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

        ListTag tileEntitiesList = new ListTag();
        ListTag pendingBlockTicksList = new ListTag();
        ListTag pendingFluidTicksList = new ListTag();

        region.put("Entities", new ListTag());

        CompoundTag position = new CompoundTag();
        position.putInt("x", 0);
        position.putInt("y", 0);
        position.putInt("z", 0);
        region.put("Position", position);

        CompoundTag size = new CompoundTag();
        size.putInt("x", (int) structure.getSize().x);
        size.putInt("y", (int) structure.getSize().y);
        size.putInt("z", (int) structure.getSize().z);
        region.put("Size", size);

        LitematicaBlockStateContainer container = new LitematicaBlockStateContainer(
                (int) structure.getSize().x,
                (int) structure.getSize().y,
                (int) structure.getSize().z
        );

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        for (var block : structure.getBlocks()) {
            minX = Math.min(minX, block.getPosition().getX());
            minY = Math.min(minY, block.getPosition().getY());
            minZ = Math.min(minZ, block.getPosition().getZ());
        }

        for (var block : structure.getBlocks()) {
            int x = block.getPosition().getX() - minX;
            int y = block.getPosition().getY() - minY;
            int z = block.getPosition().getZ() - minZ;
            container.set(x, y, z, block.getState());
        }

        region.put("BlockStatePalette", container.getPalette().writeToNBT());
        region.put("BlockStates", new LongArrayTag(container.getBackingLongArray()));

        region.put("TileEntities", tileEntitiesList);

        region.put("PendingBlockTicks", pendingBlockTicksList);
        region.put("PendingFluidTicks", pendingFluidTicksList);

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

            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            NbtIo.writeCompressed(root, file.toPath());

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
