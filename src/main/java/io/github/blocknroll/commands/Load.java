package io.github.blocknroll.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.blocknroll.ChatUtils;
import io.github.blocknroll.Constants;
import io.github.blocknroll.schematic.Schematic;
import io.github.blocknroll.midi.MIDI;
import io.github.blocknroll.structure.Structure;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import java.io.File;


public class Load {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext buildContext) {
        var command = ClientCommandManager.literal("load")
            .then(ClientCommandManager.argument("filename", StringArgumentType.greedyString())
                .executes((context) -> {
                    String filename = StringArgumentType.getString(context, "filename");
                    File file = new File(filename + Constants.MID_EXTENSION);
                    if (!file.exists()) {
                        ChatUtils.sendChatMessage("File not found: " + filename + Constants.MID_EXTENSION);
                        return Command.SINGLE_SUCCESS;
                    }
                    MIDI midi = new MIDI().fromFile(file);
                    Structure structure = new Structure(filename).fromSong(midi.song);
                    Schematic.saveStructure(structure, new File(filename + Constants.SCHEM_EXTENSION));
                    ChatUtils.sendChatMessage("Done!");
                    return Command.SINGLE_SUCCESS;
                })
            );

        dispatcher.register(ClientCommandManager.literal("blocknroll").then(command));
        dispatcher.register(ClientCommandManager.literal("br").then(command));

    }

}

