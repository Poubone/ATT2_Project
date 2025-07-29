package fr.poubone.att2.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;

public class ChronotonDisplay {
    private static int displayedValue = 0;
    private static int animationTimer = 0;
    private static final int MAX_TICKS = 20;

    public static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        Scoreboard scoreboard = client.world.getScoreboard();
        String playerName = client.player.getEntityName();

        ScoreboardObjective obj = scoreboard.getObjectives()
                .stream()
                .filter(objective -> objective.getName().equals("CHRONOTON"))
                .findFirst()
                .orElse(null);
        if (obj != null) {
            int score = scoreboard.getPlayerScore(playerName, obj).getScore();
            if (displayedValue != score) {
                animationTimer = MAX_TICKS;
                int diff = score - displayedValue;
                // Incrémentation proportionnelle à l'écart, mais au moins 1
                int step = Math.max(1, Math.abs(diff) / 10);
                if (Math.abs(diff) < 20) step = 1; // Pour les petites valeurs, incrémentation fine
                displayedValue += Integer.signum(diff) * step;
                // Clamp pour ne pas dépasser la valeur cible
                if ((diff > 0 && displayedValue > score) || (diff < 0 && displayedValue < score)) {
                    displayedValue = score;
                }
            }

            HudDrawUtils.drawHUDValue(context, displayedValue, animationTimer, MAX_TICKS, 24, Items.GOLD_NUGGET, 0x808080, true);
            if (animationTimer > 0) animationTimer--;
        }
    }
}
