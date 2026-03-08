package io.github.midiblock;

import io.github.midiblock.config.Constants;
import io.github.midiblock.midi.MIDI;
import io.github.midiblock.schematic.Schematic;
import io.github.midiblock.structure.Structure;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class MidiBlock implements ClientModInitializer {
	public static Logger LOGGER = LoggerFactory.getLogger("MidiBlock");
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(Command::register);
    }

    public static void load(String filename) {
        File file = new File(filename + Constants.MID_EXTENSION);
        load(file);
    }
    public static void load(File file) {
        new Thread(() -> {
            String filename = file.getName().replaceFirst("\\.[^.]+$", "");
            ChatUtils.sendChatMessage("Loading file " + filename + Constants.MID_EXTENSION);
            if (!file.exists()) {
                ChatUtils.sendChatMessage("File not found: " + filename + Constants.MID_EXTENSION);
                return;
            }
            long start = System.currentTimeMillis();
            MIDI midi = new MIDI().fromFile(file);
            Structure structure = new Structure(filename).fromSong(midi.song);
            Schematic.saveStructure(structure, new File(Constants.OUTPUT_FOLDER + filename + Constants.SCHEM_EXTENSION));
            io.github.midiblock.MidiBlock.LOGGER.info("Conversion completed in {}ms", System.currentTimeMillis() - start);
            ChatUtils.sendChatMessage("Schematic saved to /schematic");
        }).start();
    }
}