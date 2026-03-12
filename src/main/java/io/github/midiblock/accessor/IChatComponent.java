package io.github.midiblock.accessor;

import net.minecraft.client.GuiMessage;

import java.util.List;

public interface IChatComponent {
    List<GuiMessage> midiblock$getMessages();
    void midiBlock$refresh();
}
