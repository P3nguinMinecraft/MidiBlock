package io.github.midiblock;

import com.mojang.brigadier.CommandDispatcher;
import io.github.midiblock.commands.Load;
import io.github.midiblock.commands.Gui; // Import the new class
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;

public class Command {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext buildContext) {
        Load.register(dispatcher, buildContext);
        Gui.register(dispatcher, buildContext);
    }
}