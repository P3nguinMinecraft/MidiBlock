package io.github.blocknroll;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.blocknroll.midi.MIDI;
import io.github.blocknroll.schematic.Schematic;
import io.github.blocknroll.structure.Structure;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class BlockNRoll implements ClientModInitializer {
	public static Logger LOGGER = LoggerFactory.getLogger("BlockNRoll");
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(Command::register);
    }

    public static int load(String filename) {
        File file = new File(filename + Constants.MID_EXTENSION);
        ChatUtils.sendChatMessage("Loading file " + filename + Constants.MID_EXTENSION);
        if (!file.exists()) {
            ChatUtils.sendChatMessage("File not found: " + filename + Constants.MID_EXTENSION);
            return 0;
        }
        long start = System.currentTimeMillis();
        BlockNRoll.LOGGER.info("Starting: {}ms", System.currentTimeMillis() - start);
        MIDI midi = new MIDI().fromFile(file);
        BlockNRoll.LOGGER.info("Loaded from MIDI: {}ms", System.currentTimeMillis() - start);
        Structure structure = new Structure(filename).fromSong(midi.song);
        BlockNRoll.LOGGER.info("Built Structure: {}ms", System.currentTimeMillis() - start);
        Schematic.saveStructure(structure, new File(filename + Constants.SCHEM_EXTENSION));
        BlockNRoll.LOGGER.info("Saved Schematic: {}ms", System.currentTimeMillis() - start);
        ChatUtils.sendChatMessage("Done!");
        return 1;
    }
}