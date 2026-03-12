package io.github.midiblock;

import io.github.midiblock.accessor.IChatComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class ChatUtils {

    private static final String PROGRESS_MARKER = "\u200B\u200B\u200B";

    private static MutableComponent prefix() {
        return Component.literal("[").withStyle(ChatFormatting.DARK_GRAY)
                .append(Component.literal(MidiBlock.NAME).withStyle(ChatFormatting.GOLD))
                .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY));
    }

    public static void sendChatMessage(String message) {
        sendFormattedMessage(Component.literal(message).withStyle(ChatFormatting.WHITE));
    }

    public static void send(String message, ChatFormatting... formatting) {
        sendFormattedMessage(Component.literal(message).withStyle(formatting));
    }

    public static void sendFormattedMessage(Component component) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.gui.getChat().addMessage(
                prefix().append(component)
        ));
    }

    public static void sendSuccess(String message) {
        sendFormattedMessage(Component.literal(message).withStyle(ChatFormatting.GREEN));
    }

    public static void sendError(String message) {
        sendFormattedMessage(Component.literal(message).withStyle(ChatFormatting.RED));
    }

    public static void sendInfo(String message) {
        sendFormattedMessage(Component.literal(message).withStyle(ChatFormatting.GRAY));
    }


    /**
     * Shows or updates a progress bar in chat.
     * Removes the previous progress bar message before adding the new one.
     */
    public static void sendProgressBar(String label, double progress) {
        int totalBars = 20;
        int filled = (int) Math.round(progress * totalBars);
        filled = Math.max(0, Math.min(totalBars, filled));
        int empty = totalBars - filled;
        int percent = (int) Math.round(progress * 100);

        MutableComponent bar = prefix()
                .append(Component.literal(label + " ").withStyle(ChatFormatting.AQUA))
                .append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal("+".repeat(filled)).withStyle(ChatFormatting.GREEN))
                .append(Component.literal("-".repeat(empty)).withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal(percent + "%").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(PROGRESS_MARKER).withStyle(ChatFormatting.RESET));

        Minecraft client = Minecraft.getInstance();
        client.execute(() -> {
            removeProgressMessages(client);
            client.gui.getChat().addMessage(bar);
        });
    }

    /**
     * Removes any existing progress bar messages from chat.
     */
    public static void removeProgressBar() {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> removeProgressMessages(client));
    }

    private static void removeProgressMessages(Minecraft client) {
        ChatComponent chat = client.gui.getChat();
        IChatComponent chatComponent = (IChatComponent) chat;

        List<GuiMessage> allMessages = chatComponent.midiblock$getMessages();
        allMessages.removeIf(msg -> msg.content().getString().contains(PROGRESS_MARKER));
        chatComponent.midiBlock$refresh();
    }
}
