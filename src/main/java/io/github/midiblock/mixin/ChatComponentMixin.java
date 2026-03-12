package io.github.midiblock.mixin;

import io.github.midiblock.accessor.IChatComponent;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin implements IChatComponent {

    @Shadow
    protected abstract void refreshTrimmedMessages();

    @Shadow
    @Final
    private List<GuiMessage> allMessages;

    @Override
    public List<GuiMessage> midiblock$getMessages() {
        return this.allMessages;
    }

    @Override
    public void midiBlock$refresh() {
        this.refreshTrimmedMessages();
    }
}