package io.github.blocknroll.config;

import io.github.blocknroll.BlockNRoll;
import io.github.blocknroll.ChatUtils;
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

public class MidiDropScreen extends Screen {

    private EditBox floorBox;
    private EditBox conductiveBox;
    private EditBox buttonBox;

    public MidiDropScreen(Screen parent) {
        super(Component.literal("Block N Roll"));
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int widgetWidth = 200;
        int leftCol = centerX - widgetWidth - 4;
        int rightCol = centerX + 4;

        // ---- Left column: Block config ----
        int y = 40;
        int labelGap = 12;

        // Floor block
        floorBox = new EditBox(this.font, leftCol, y + labelGap, widgetWidth, 18, Component.literal("Floor Block"));
        floorBox.setMaxLength(64);
        floorBox.setValue(Config.FLOOR_ID);
        floorBox.setResponder(val -> Config.FLOOR_ID = val);
        this.addRenderableWidget(floorBox);

        y += labelGap + 22;

        // Conductive block
        conductiveBox = new EditBox(this.font, leftCol, y + labelGap, widgetWidth, 18, Component.literal("Conductive Block"));
        conductiveBox.setMaxLength(64);
        conductiveBox.setValue(Config.CONDUCTIVE_ID);
        conductiveBox.setResponder(val -> Config.CONDUCTIVE_ID = val);
        this.addRenderableWidget(conductiveBox);

        y += labelGap + 22;

        // Button block
        buttonBox = new EditBox(this.font, leftCol, y + labelGap, widgetWidth, 18, Component.literal("Button Block"));
        buttonBox.setMaxLength(64);
        buttonBox.setValue(Config.BUTTON_ID);
        buttonBox.setResponder(val -> Config.BUTTON_ID = val);
        this.addRenderableWidget(buttonBox);

        // ---- Right column: Settings ----
        y = 40;

        // Adapt BPM toggle
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

        // Instrument mode toggle
        this.addRenderableWidget(Button.builder(getInstrumentModeLabel(), (button) -> {
                    Config.INSTRUMENT_MODE = Config.INSTRUMENT_MODE.next();
                    button.setMessage(getInstrumentModeLabel());
                })
                .bounds(rightCol, y, widgetWidth, 20)
                .build());

        // ---- Bottom: drop zone text + close button ----
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

        // Title
        guiGraphics.drawCenteredString(
                this.font,
                Component.literal("Block N Roll").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD),
                centerX, 10, 0xFFFFFF
        );

        // Left column labels
        int y = 40;
        int labelGap = 12;
        guiGraphics.drawString(this.font, Component.literal("Floor Block").withStyle(ChatFormatting.GRAY), leftCol, y, 0xFFFFFF);
        y += labelGap + 22;
        guiGraphics.drawString(this.font, Component.literal("Conductive Block").withStyle(ChatFormatting.GRAY), leftCol, y, 0xFFFFFF);
        y += labelGap + 22;
        guiGraphics.drawString(this.font, Component.literal("Button Block").withStyle(ChatFormatting.GRAY), leftCol, y, 0xFFFFFF);

        // Column divider
        guiGraphics.fill(centerX - 1, 30, centerX, this.height - 40, 0x40FFFFFF);

        // Right column header
        guiGraphics.drawString(this.font, Component.literal("Settings").withStyle(ChatFormatting.GRAY), rightCol, 30, 0xFFFFFF);

        // Drop zone prompt
        guiGraphics.drawCenteredString(
                this.font,
                Component.literal("Drag & Drop your .mid file here!").withStyle(ChatFormatting.AQUA),
                centerX, this.height - 50, 0xFFFFFF
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
                BlockNRoll.load(file);
                this.minecraft.setScreen(null);
                return;
            }
        }
        ChatUtils.sendChatMessage("Invalid file format. Please drop a .mid file.");
    }
}