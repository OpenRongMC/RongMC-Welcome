package dev.rongmc.welcome.mixin;

import dev.rongmc.welcome.screen.WelcomeScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init" , at = @At("RETURN"))
    private void rongmc$addWelcomeButton(CallbackInfo ci) {
        int x = this.width / 2 - 100;
        int y = this.height / 4 + 168;
        this.addRenderableWidget(Button.builder(Component.translatable("rongmc_welcome.open"), button -> {
            this.minecraft.gui.setScreen(new WelcomeScreen(this));
        }).bounds(x, y, 200, 20).build());
    }
}
