package fr.poubone.att2.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.poubone.att2.client.data.StatManager;
import fr.poubone.att2.client.util.ModTextures;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class StatIconsDisplay {
    public static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        TextRenderer textRenderer = client.textRenderer;
        int iconSize = 8;
        int spacingX = 50;
        int spacingY = 10;
        int columns = 3;
        int startX = 8;
        int startY = client.getWindow().getScaledHeight() - 10 - (StatManager.getKeys().length / columns + 1) * spacingY;

        for (int i = 0; i < StatManager.getKeys().length; i++) {
            String key = StatManager.getKeys()[i];
            String label = key.replace("_TOT", "");
            int value = StatManager.stats.getOrDefault(key, 0);

            int col = i % columns;
            int row = i / columns;
            int x = startX + col * spacingX;
            int y = startY + row * spacingY;

            if (label.equals("DAR")) {
                int frame = (int) ((System.currentTimeMillis() / 100) % 32); 
                int v = frame * 8;

                context.getMatrices().push();
                context.getMatrices().translate(x, y, 0);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                context.drawTexture(ModTextures.DAR_SPRITE, 0, 0, 0, v, 8, 8, 8, 256);
                context.getMatrices().pop();
            }
            else {
                var icon = ModTextures.STAT_ICONS.get(label);
                if (icon != null) {
                    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                    context.drawTexture(icon, x, y, 0, 0, 8, 8, 8, 8);
                }
            }

            context.drawText(textRenderer, Text.literal(label + ": " + value), x + 10, y, 0xFFFFFF, false);
        }
    }
}
