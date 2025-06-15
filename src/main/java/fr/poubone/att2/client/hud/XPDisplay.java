package fr.poubone.att2.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;

public class XPDisplay {
    public static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        Scoreboard scoreboard = client.world.getScoreboard();
        String name = client.player.getEntityName();

        ScoreboardObjective required = scoreboard.getObjectives()
                .stream()
                .filter(objective -> objective.getName().equals("LVL_UPGRADE_REQ"))
                .findFirst()
                .orElse(null);

        ScoreboardObjective current = scoreboard.getObjectives()
                .stream()
                .filter(objective -> objective.getName().equals("GAMELEVEL"))
                .findFirst()
                .orElse(null);

        if (required != null && current != null) {
            int requiredXp = scoreboard.getPlayerScore(name, required).getScore();
            int currentLvl = scoreboard.getPlayerScore(name, current).getScore();
            if (currentLvl >= 50){
                ScoreboardObjective master = scoreboard.getObjectives()
                        .stream()
                        .filter(objective -> objective.getName().equals("LEVELMASTER"))
                        .findFirst()
                        .orElse(null);
                if (master != null) {
                    int currentMaster = scoreboard.getPlayerScore(name, master).getScore();
                    currentLvl = currentLvl + currentMaster;

                }
            }
            int currentXp = client.player.experienceLevel;

            String display = "Niveau " + currentLvl + " : " + currentXp + "/" + requiredXp;
            HudDrawUtils.drawXPHUDValue(context, display, 8, 0x00FF00);
        }
    }
}
