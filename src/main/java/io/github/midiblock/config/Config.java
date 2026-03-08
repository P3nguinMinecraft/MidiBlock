package io.github.midiblock.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final Path CONFIG_PATH = Path.of("config", "midiblock.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String FLOOR_ID = "minecraft:smooth_stone";
    public static String CONDUCTIVE_ID = "minecraft:red_concrete";
    public static String BUTTON_ID = "minecraft:oak_button";

    public static boolean ADAPT_BPM = true;
    public static int MAX_CHANGE_PERCENT = 30; // 0 to 99

    public static InstrumentMode INSTRUMENT_MODE = InstrumentMode.NONE;

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data != null) {
                FLOOR_ID = data.floorId != null ? data.floorId : FLOOR_ID;
                CONDUCTIVE_ID = data.conductiveId != null ? data.conductiveId : CONDUCTIVE_ID;
                BUTTON_ID = data.buttonId != null ? data.buttonId : BUTTON_ID;
                ADAPT_BPM = data.adaptBpm;
                MAX_CHANGE_PERCENT = data.maxChangePercent;
                INSTRUMENT_MODE = data.instrumentMode != null ? data.instrumentMode : INSTRUMENT_MODE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                ConfigData data = new ConfigData();
                data.floorId = FLOOR_ID;
                data.conductiveId = CONDUCTIVE_ID;
                data.buttonId = BUTTON_ID;
                data.adaptBpm = ADAPT_BPM;
                data.maxChangePercent = MAX_CHANGE_PERCENT;
                data.instrumentMode = INSTRUMENT_MODE;
                GSON.toJson(data, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private static class ConfigData {
        String floorId;
        String conductiveId;
        String buttonId;
        boolean adaptBpm;
        int maxChangePercent;
        InstrumentMode instrumentMode;
    }
}
