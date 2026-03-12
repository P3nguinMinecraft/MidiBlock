package io.github.midiblock;

import io.github.midiblock.config.Config;
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
    public static String NAME = "MidiBlock";
    @Override
    public void onInitializeClient() {
        Config.load();
        ClientCommandRegistrationCallback.EVENT.register(Command::register);
    }

    public static void load(String filename) {
        File file = new File(filename + Constants.MID_EXTENSION);
        load(file);
    }
    public static void load(File file) {
        new Thread(() -> {
            String filename = file.getName().replaceFirst("\\.[^.]+$", "");

            ChatUtils.sendInfo("Loading " + filename + Constants.MID_EXTENSION + "...");
            ChatUtils.sendProgressBar("Converting", 0.0);

            if (!file.exists()) {
                ChatUtils.sendError("File not found: " + filename + Constants.MID_EXTENSION);
                ChatUtils.removeProgressBar();
                return;
            }

            long start = System.currentTimeMillis();

            ChatUtils.sendProgressBar("Parsing MIDI", 0.15);
            MIDI midi = new MIDI().fromFile(file);
            ChatUtils.sendProgressBar("Building Structure", 0.50);
            Structure structure = new Structure(filename).fromSong(midi.song);
            ChatUtils.sendProgressBar("Saving Schematic", 0.90);
            Schematic.saveStructure(structure, new File(Constants.OUTPUT_FOLDER + filename + Constants.SCHEM_EXTENSION));
            ChatUtils.sendProgressBar("Complete", 1.0);
            long elapsed = System.currentTimeMillis() - start;
            MidiBlock.LOGGER.info("Conversion completed in {}ms", elapsed);
            ChatUtils.removeProgressBar();
            ChatUtils.sendSuccess("Saved to /schematics folder (" + elapsed + "ms)");
        }).start();
    }
}