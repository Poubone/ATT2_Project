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
                displayedValue += Math.signum(score - displayedValue);
            }

            HudDrawUtils.drawHUDValue(context, displayedValue, animationTimer, MAX_TICKS, 24, Items.GOLD_NUGGET, 0x808080, true);
            if (animationTimer > 0) animationTimer--;
        }
    }
}
