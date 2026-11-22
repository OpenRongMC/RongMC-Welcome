package dev.rongmc.welcome.screen;

import dev.rongmc.welcome.OptionalCatalog;
import dev.rongmc.welcome.WelcomeConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public final class WelcomeScreen extends Screen {
    private final Screen parent;
    private final List<FormattedCharSequence> wrapped = new ArrayList<>();

    public WelcomeScreen(Screen parent) {
        super(Component.translatable("rongmc_welcome.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        wrapped.clear();
        int textWidth = Math.min(360, this.width - 40);
        for (String line : OptionalCatalog.root().welcomeBody) {
            wrapped.addAll(this.font.split(Component.literal(line), textWidth));
            wrapped.add(FormattedCharSequence.EMPTY);
        }

        int cx = this.width / 2;
        int bottom = this.height - 52;
        this.addRenderableWidget(Button.builder(Component.translatable("rongmc_welcome.optional"), b -> {
            this.minecraft.gui.setScreen(new OptionalModsScreen(this));
        }).bounds(cx - 155, bottom, 150, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("rongmc_welcome.done"), b -> {
            WelcomeConfig.markWelcomeCompleted();
            this.minecraft.gui.setScreen(parent);
        }).bounds(cx + 5, bottom, 150, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // 1.21.11: GameRenderer already blurs once; calling renderBackground again crashes with "Can only blur once per frame".
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, this.title, this.width / 2, 24, 0xFFFFFFFF);
        int y = 48;
        for (FormattedCharSequence line : wrapped) {
            graphics.centeredText(this.font, line, this.width / 2, y, 0xFFCFCFCF);
            y += 12;
            if (y > this.height - 70) {
                break;
            }
        }
    }

    @Override
    public void onClose() {
        WelcomeConfig.markWelcomeCompleted();
        this.minecraft.gui.setScreen(parent);
    }
}
