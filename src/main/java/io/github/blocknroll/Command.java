package io.github.blocknroll;

import com.mojang.brigadier.CommandDispatcher;
import io.github.blocknroll.commands.Load;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;

public class Command {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext buildContext) {
        Load.register(dispatcher, buildContext);
    }

}
