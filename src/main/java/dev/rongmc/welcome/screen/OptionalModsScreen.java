package dev.rongmc.welcome.screen;

import dev.rongmc.welcome.ClientRestarter;
import dev.rongmc.welcome.OptionalCatalog;
import dev.rongmc.welcome.OptionalModManager;
import dev.rongmc.welcome.RongMcWelcomeClient;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.LinkedHashSet;
import java.util.Set;

public final class OptionalModsScreen extends Screen {
    private final Screen parent;
    private final Set<String> selected = new LinkedHashSet<>();
    private String statusMessage = "";
    private boolean applying;

    public OptionalModsScreen(Screen parent) {
        super(Component.translatable("rongmc_welcome.optional"));
        this.parent = parent;
        for (OptionalCatalog.ModEntry entry : OptionalCatalog.mods()) {
            if (OptionalModManager.stateOf(entry) == OptionalModManager.State.ENABLED) {
                selected.add(entry.id);
            }
        }
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int px = cx - 200;
        int y = 34;
        for (OptionalCatalog.Preset preset : OptionalCatalog.presets()) {
            String label = Component.translatable(preset.nameKey).getString();
            this.addRenderableWidget(Button.builder(Component.literal(label), b -> {
                if (applying) {
                    return;
                }
                selected.clear();
                if (preset.mods != null) {
                    selected.addAll(preset.mods);
                }
                statusMessage = preset.description == null ? "" : preset.description;
                this.rebuildWidgets();
            }).bounds(px, y, 98, 20).build());
            px += 102;
            if (px > cx + 102) {
                px = cx - 200;
                y += 24;
            }
        }

        y = Math.max(y + 30, 86);
        int listBottom = this.height - 78;
        for (OptionalCatalog.ModEntry entry : OptionalCatalog.mods()) {
            OptionalModManager.State state = OptionalModManager.stateOf(entry);
            boolean on = selected.contains(entry.id);
            String stateLabel = switch (state) {
                case MISSING -> Component.translatable("rongmc_welcome.missing_jar").getString();
                case ENABLED, DISABLED -> on
                        ? Component.translatable("rongmc_welcome.enabled").getString()
                        : Component.translatable("rongmc_welcome.disabled").getString();
            };
            String text = entry.name + " [" + stateLabel + "]";
            Button button = Button.builder(Component.literal(text), b -> {
                if (applying) {
                    return;
                }
                if (state == OptionalModManager.State.MISSING) {
                    statusMessage = entry.name + " — " + Component.translatable("rongmc_welcome.missing_jar").getString();
                    return;
                }
                if (!selected.add(entry.id)) {
                    selected.remove(entry.id);
                }
                this.rebuildWidgets();
            }).bounds(cx - 200, y, 400, 20).build();
            button.active = !applying && state != OptionalModManager.State.MISSING;
            this.addRenderableWidget(button);
            y += 22;
            if (y > listBottom) {
                break;
            }
        }

        Button apply = Button.builder(Component.translatable("rongmc_welcome.apply_restart"), b -> onApplyRestart())
                .bounds(cx - 155, this.height - 36, 150, 20)
                .build();
        apply.active = !applying;
        this.addRenderableWidget(apply);

        Button back = Button.builder(Component.translatable("rongmc_welcome.back"), b -> {
            if (!applying) {
                this.minecraft.gui.setScreen(parent);
            }
        }).bounds(cx + 5, this.height - 36, 150, 20).build();
        back.active = !applying;
        this.addRenderableWidget(back);
    }

    private void onApplyRestart() {
        if (applying || this.minecraft == null) {
            return;
        }
        applying = true;
        statusMessage = Component.translatable("rongmc_welcome.applying").getString();
        this.rebuildWidgets();
        try {
            OptionalModManager.applySelection(selected);
            RongMcWelcomeClient.LOGGER.info("Applied optional mods selection: {}", selected);
            statusMessage = Component.translatable("rongmc_welcome.restarting").getString();
            this.rebuildWidgets();
            // Defer one tick so the status text can paint before we quit.
            this.minecraft.execute(() -> {
                if (!ClientRestarter.restartNow()) {
                    applying = false;
                    statusMessage = Component.translatable("rongmc_welcome.restart_hint").getString();
                    this.rebuildWidgets();
                }
            });
        } catch (Exception e) {
            applying = false;
            RongMcWelcomeClient.LOGGER.warn("Failed applying optional mods", e);
            statusMessage = Component.translatable("rongmc_welcome.apply_failed", e.getMessage()).getString();
            this.rebuildWidgets();
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // 1.21.11: do not call renderBackground here; super.render already blurs once.
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, this.title, this.width / 2, 12, 0xFFFFFFFF);
        if (statusMessage != null && !statusMessage.isEmpty()) {
            graphics.centeredText(this.font, statusMessage, this.width / 2, this.height - 56, 0xFFFFE08A);
        }
    }

    @Override
    public void onClose() {
        if (!applying) {
            this.minecraft.gui.setScreen(parent);
        }
    }
}
