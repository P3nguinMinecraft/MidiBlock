package io.github.blocknroll.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import io.github.blocknroll.gui.MidiDropScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;

public class Gui {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext buildContext) {

        var command = ClientCommandManager.literal("gui")
            .executes(context -> {
                Minecraft.getInstance().execute(() -> {
                    Minecraft.getInstance().setScreen(new MidiDropScreen(Minecraft.getInstance().screen));
                });
                return Command.SINGLE_SUCCESS;
            });

        dispatcher.register(ClientCommandManager.literal("blocknroll").then(command));
        dispatcher.register(ClientCommandManager.literal("br").then(command));
    }
}