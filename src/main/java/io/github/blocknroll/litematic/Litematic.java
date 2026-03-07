package io.github.blocknroll.litematic;

import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.container.LitematicaBlockStateContainer;
import fi.dy.masa.litematica.selection.AreaSelection;
import io.github.blocknroll.structure.Block;
import io.github.blocknroll.structure.Structure;
import net.minecraft.core.BlockPos;

import java.io.File;
import java.nio.file.Path;

public class Litematic {

    public static LitematicaSchematic fromStructure(Structure structure) {
        LitematicaSchematic schematic = LitematicaSchematic.createEmptySchematic(new AreaSelection(), "blocknroll");

        String mainRegionName = schematic.getMetadata().getName();
        LitematicaBlockStateContainer container = schematic.getSubRegionContainer(mainRegionName);

        if (container != null) {
            for (Block block : structure.get()) {
                BlockPos pos = block.getPosition();
                container.set(pos.getX(), pos.getY(), pos.getZ(), block.getState());
            }
        }

        return schematic;
    }

    public static void save(LitematicaSchematic schematic, File file) {
        Path directory = file.getParentFile().toPath();
        String fileName = file.getName();

        schematic.writeToFile(directory, fileName, true);
    }
}
