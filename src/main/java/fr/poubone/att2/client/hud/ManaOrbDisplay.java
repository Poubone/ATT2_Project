package fr.poubone.att2.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.poubone.att2.client.util.ModTextures;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;

public class ManaOrbDisplay {
    public static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        Scoreboard scoreboard = client.world.getScoreboard();
        String name = client.player.getEntityName();


        ScoreboardObjective currentObj = scoreboard.getObjectives()
                .stream()
                .filter(objective -> objective.getName().equals("DAHAL"))
                .findFirst()
                .orElse(null);

        ScoreboardObjective maxObj = scoreboard.getObjectives()
                .stream()
                .filter(objective -> objective.getName().equals("DAHALMAX"))
                .findFirst()
                .orElse(null);

        if (currentObj == null || maxObj == null) return;

        int current = scoreboard.getPlayerScore(name, currentObj).getScore();
        int max = scoreboard.getPlayerScore(name, maxObj).getScore();

        int orbSize = 64;
        int padding = 10;
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int x = screenWidth - orbSize - padding;
        int y = screenHeight - orbSize - padding - 12;

        float ratio = Math.min((float) current / max, 1.0f);
        int filledHeight = (int) (orbSize * ratio);
        int scaleFactor = (int) client.getWindow().getScaleFactor();
        int windowHeight = client.getWindow().getHeight();

        int scissorX = x * scaleFactor;
        int scissorY = (windowHeight - (y + orbSize) * scaleFactor);
        int scissorWidth = orbSize * scaleFactor;
        int scissorHeight = filledHeight * scaleFactor;

        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        context.drawTexture(ModTextures.MANA_FILL, x, y, 0, 0, orbSize, orbSize, orbSize, orbSize);
        RenderSystem.disableScissor();

        context.drawTexture(ModTextures.MANA_FRAME, x, y, 0, 0, orbSize, orbSize, orbSize, orbSize);
        String text = current + "/" + max;
        int textWidth = client.textRenderer.getWidth(text);
        context.drawText(client.textRenderer, text, x + (orbSize - textWidth) / 2, y + orbSize - 4, 0xFFFFFF, false);
    }
}
