package dev.rongmc.welcome;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RongMcWelcomeClient implements ClientModInitializer {
    public static final String MOD_ID = "rongmc_welcome";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static boolean firstRunChecked;

    @Override
    public void onInitializeClient() {
        OptionalCatalog.load();
        WelcomeConfig.load();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (firstRunChecked || client.gui.screen() == null) {
                return;
            }
            if (!(client.gui.screen() instanceof TitleScreen)) {
                return;
            }
            firstRunChecked = true;
            if (!WelcomeConfig.get().welcomeCompleted) {
                client.gui.setScreen(new dev.rongmc.welcome.screen.WelcomeScreen(client.gui.screen()));
            }
        });
        LOGGER.info("RongMC Welcome ready (optional mods catalog size={})", OptionalCatalog.mods().size());
    }

    public static Minecraft mc() {
        return Minecraft.getInstance();
    }
}
