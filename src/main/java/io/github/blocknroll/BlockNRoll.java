package io.github.blocknroll;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BlockNRoll implements ClientModInitializer {
	public static Logger LOGGER = LoggerFactory.getLogger("BlockNRoll");
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(Command::register);
    }
}