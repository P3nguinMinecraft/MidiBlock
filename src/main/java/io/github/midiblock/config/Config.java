package io.github.midiblock.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class Config {
    public static String FLOOR_ID = "minecraft:smooth_stone";
    public static String CONDUCTIVE_ID = "minecraft:red_concrete";
    public static String BUTTON_ID = "minecraft:oak_button";

    public static boolean ADAPT_BPM = true;
    public static int MAX_CHANGE_PERCENT = 30; // 0 to 99

    public static InstrumentMode INSTRUMENT_MODE = InstrumentMode.NONE;

    public static Block getFloor() {
        return resolveBlock(FLOOR_ID, Blocks.SMOOTH_STONE);
    }

    public static Block getConductive() {
        return resolveBlock(CONDUCTIVE_ID, Blocks.RED_CONCRETE);
    }

    public static Block getButton() {
        return resolveBlock(BUTTON_ID, Blocks.OAK_BUTTON);
    }

    public static boolean isValidBlockId(String id) {
        try {
            Identifier loc = Identifier.parse(id);
            Block block = BuiltInRegistries.BLOCK.getValue(loc);
            return block != Blocks.AIR;
        } catch (Exception e) {
            return false;
        }
    }

    public static Block resolveBlock(String id, Block fallback) {
        try {
            Identifier loc = Identifier.parse(id);
            Block block = BuiltInRegistries.BLOCK.getValue(loc);
            return block != Blocks.AIR ? block : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }
}
