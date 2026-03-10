package io.github.midiblock.schematic;

import io.github.midiblock.structure.Structure;
import net.minecraft.nbt.*;

import java.io.*;
public class Schematic {

    public static boolean saveStructure(Structure structure, File file) {
        structure.shift();
        CompoundTag root = Builder.build(structure);
        try {
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
