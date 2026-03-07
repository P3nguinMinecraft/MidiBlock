package io.github.blocknroll.gui;

import io.github.blocknroll.BlockNRoll;
import io.github.blocknroll.ChatUtils;
import io.github.blocknroll.Constants;
import io.github.blocknroll.midi.MIDI;
import io.github.blocknroll.schematic.Schematic;
import io.github.blocknroll.structure.Structure;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class MidiDropScreen extends Screen {

    public MidiDropScreen(Screen parent) {
        super(Component.literal("Block N Roll"));
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(Button.builder(Component.literal("Close"), (button) -> {
                    this.minecraft.setScreen(null);
                })
                .bounds(this.width / 2 + 6, this.height - 25, this.width / 2 - 12, 20)
                .build());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.fill(this.width / 2, 4, this.width / 2 + 1, this.height - 5, 0xFFC7C0C0);

        guiGraphics.fill(this.width / 2 + 6, 5, this.width - 5, 20, 0x3FC7C0C0);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("Drag and Drop here"), this.width / 2 + 10, 9, 0xFFFFFFFF);

        guiGraphics.drawCenteredString(
                this.font,
                Component.literal("Drag & Drop your .mid file here!").withStyle(ChatFormatting.AQUA),
                this.width / 4,
                this.height / 2,
                0xFFFFFF
        );
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft.level == null) {
            super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        } else {
            guiGraphics.fill(0, 0, this.width, this.height, 0x90202020);
        }
    }

    @Override
    public void onFilesDrop(List<Path> paths) {
        for (Path path : paths) {
            File file = path.toFile();
            String filename = file.getName().toLowerCase();

            if (filename.endsWith(".mid")) {
                BlockNRoll.load(filename);
                this.minecraft.setScreen(null);
                return;
            }
        }
        ChatUtils.sendChatMessage("Invalid file format. Please drop a .mid file.");
    }
}