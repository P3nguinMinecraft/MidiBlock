package io.github.midiblock.config;

import io.github.midiblock.ChatUtils;
import io.github.midiblock.MidiBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class ConfigScreen extends Screen {

    public ConfigScreen(Screen parent) {
        super(Component.literal("MidiBlock Config"));
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int widgetWidth = 200;
        int leftCol = centerX - widgetWidth - 4;
        int rightCol = centerX + 4;

        int y = 40;
        int labelGap = 12;

        EditBox floorBox = new EditBox(this.font, leftCol, y + labelGap, widgetWidth, 18, Component.literal("Floor Block"));
        floorBox.setMaxLength(64);
        floorBox.setValue(Config.FLOOR_ID);
        floorBox.setResponder(val -> {
            if (Config.isValidBlockId(val)) {
                Config.FLOOR_ID = val;
                floorBox.setTextColor(0xFFFFFFFF);
            } else {
                floorBox.setTextColor(0xFFFF5555);
            }
        });
        this.addRenderableWidget(floorBox);

        y += labelGap + 22;

        EditBox conductiveBox = new EditBox(this.font, leftCol, y + labelGap, widgetWidth, 18, Component.literal("Conductive Block"));
        conductiveBox.setMaxLength(64);
        conductiveBox.setValue(Config.CONDUCTIVE_ID);
        conductiveBox.setResponder(val -> {
            if (Config.isValidBlockId(val)) {
                Config.CONDUCTIVE_ID = val;
                conductiveBox.setTextColor(0xFFFFFFFF);
            } else {
                conductiveBox.setTextColor(0xFFFF5555);
            }
        });
        this.addRenderableWidget(conductiveBox);

        y += labelGap + 22;

        EditBox buttonBox = new EditBox(this.font, leftCol, y + labelGap, widgetWidth, 18, Component.literal("Button Block"));
        buttonBox.setMaxLength(64);
        buttonBox.setValue(Config.BUTTON_ID);
        buttonBox.setResponder(val -> {
            if (Config.isValidBlockId(val)) {
                Config.BUTTON_ID = val;
                buttonBox.setTextColor(0xFFFFFFFF);
            } else {
                buttonBox.setTextColor(0xFFFF5555);
            }
        });
        this.addRenderableWidget(buttonBox);

        y = 40;

        this.addRenderableWidget(Button.builder(getAdaptBpmLabel(), (button) -> {
                    Config.ADAPT_BPM = !Config.ADAPT_BPM;
                    button.setMessage(getAdaptBpmLabel());
                })
                .bounds(rightCol, y, widgetWidth, 20)
                .build());

        y += 24;

        this.addRenderableWidget(new AbstractSliderButton(
                rightCol, y, widgetWidth, 20,
                Component.literal("Max Change: " + Config.MAX_CHANGE_PERCENT + "%"), Config.MAX_CHANGE_PERCENT / 99.0) {
            @Override
            protected void updateMessage() {
                this.setMessage(getSliderLabel());
            }

            @Override
            protected void applyValue() {
                Config.MAX_CHANGE_PERCENT = (int) Math.round(this.value * 99.0);
            }

            private Component getSliderLabel() {
                return Component.literal("Max Change: " + Config.MAX_CHANGE_PERCENT + "%");
            }
        });

        y += 24;

        this.addRenderableWidget(Button.builder(getInstrumentModeLabel(), (button) -> {
                    Config.INSTRUMENT_MODE = Config.INSTRUMENT_MODE.next();
                    button.setMessage(getInstrumentModeLabel());
                })
                .bounds(rightCol, y, widgetWidth, 20)
                .build());

        this.addRenderableWidget(Button.builder(Component.literal("Close"), (button) ->
                        this.minecraft.setScreen(null))
                .bounds(centerX - widgetWidth / 2, this.height - 28, widgetWidth, 20)
                .build());
    }

    private Component getAdaptBpmLabel() {
        return Component.literal("Adapt BPM: " + (Config.ADAPT_BPM ? "ON" : "OFF"));
    }

    private Component getInstrumentModeLabel() {
        return Component.literal("Instruments: " + Config.INSTRUMENT_MODE.getDisplayName());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int widgetWidth = 200;
        int leftCol = centerX - widgetWidth - 4;
        int rightCol = centerX + 4;
        int labelGap = 12;

        guiGraphics.vLine(this.width / 2, 15, this.height - 50, 0x20FFFFFF);

        guiGraphics.drawCenteredString(
                this.font,
                Component.literal("MidiBlock").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD),
                centerX, 10, 0xFFFFFFFF
        );

        final int floorLabelY = 40;
        final int conductiveLabelY = floorLabelY + labelGap + 22;
        final int buttonLabelY = conductiveLabelY + labelGap + 22;

        guiGraphics.drawString(this.font, Component.literal("Floor Block").withStyle(ChatFormatting.GRAY), leftCol, floorLabelY, 0xFFFFFFFF);
        guiGraphics.drawString(this.font, Component.literal("Conductive Block").withStyle(ChatFormatting.GRAY), leftCol, conductiveLabelY, 0xFFFFFFFF);
        guiGraphics.drawString(this.font, Component.literal("Button Block").withStyle(ChatFormatting.GRAY), leftCol, buttonLabelY, 0xFFFFFFFF);


        guiGraphics.drawString(this.font, Component.literal("Settings").withStyle(ChatFormatting.GRAY), rightCol, 30, 0xFFFFFFFF);


        guiGraphics.drawCenteredString(
                this.font,
                Component.literal("Drag & Drop your .mid file here!").withStyle(ChatFormatting.AQUA),
                centerX, this.height - 50, 0xFFFFFFFF
        );
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0x90202020);
    }

    @Override
    public void onFilesDrop(List<Path> paths) {
        for (Path path : paths) {
            File file = path.toFile();
            String filename = file.getName().toLowerCase();

            if (filename.endsWith(".mid")) {
                MidiBlock.load(file);
                this.minecraft.setScreen(null);
                return;
            }
        }
        ChatUtils.sendChatMessage("Invalid file format. Please drop a .mid file.");
    }
}