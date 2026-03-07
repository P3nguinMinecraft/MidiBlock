package io.github.blocknroll.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.blocknroll.ChatUtils;
import io.github.blocknroll.midi.MIDI;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

import java.io.File;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Load {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext buildContext) {
        var command = ClientCommandManager.literal("load")
            .then(ClientCommandManager.argument("filename", StringArgumentType.greedyString())
                .executes((context) -> {
                    String filename = StringArgumentType.getString(context, "filename");
                    File file = new File(filename);
                    MIDI midi = new MIDI().fromFile(file);
                    // TODO: Implement load logic
                    ChatUtils.sendChatMessage("Done!");
                    return SINGLE_SUCCESS;
                })
            );

        dispatcher.register(ClientCommandManager.literal("blocknroll").then(command));
        dispatcher.register(ClientCommandManager.literal("br").then(command));
    }

}

