package io.github.midiblock;

import 	net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ChatUtils {
    public static void sendChatMessage(String message) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.gui.getChat().addMessage(Component.literal(message)));
    }
}
