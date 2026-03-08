package io.github.midiblock.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.midiblock.MidiBlock;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;


public class Load {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext buildContext) {
        var command = ClientCommandManager.literal("load")
            .then(ClientCommandManager.argument("filename", StringArgumentType.greedyString())
                .executes((context) -> {
                    String filename = StringArgumentType.getString(context, "filename");
                    MidiBlock.load(filename);
                    return Command.SINGLE_SUCCESS;
                })
            );

        dispatcher.register(ClientCommandManager.literal("midiblock").then(command));
        dispatcher.register(ClientCommandManager.literal("mb").then(command));

    }

}

