package fr.poubone.att2.client.hud;

import fr.poubone.att2.client.data.StatManager;
import fr.poubone.att2.client.input.KeybindManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class HudRenderer {
    public static void render(DrawContext context, float tickDelta) {
        if (!KeybindManager.showCustomHUD) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        StatManager.updateAll();
        HUDConfig config = HUDConfig.get();

        if (config.showChronoton) ChronotonDisplay.render(context);
        if (config.showXP) XPDisplay.render(context);
        if (config.showMana) ManaOrbDisplay.render(context);
        if (config.showStats) StatIconsDisplay.render(context);
        if (config.showArrows) ArrowDisplay.render(context);
    }
}

