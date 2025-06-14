package fr.poubone.att2.client.data;

import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;

import java.util.HashMap;
import java.util.Map;

public class StatManager {
    public static final Map<String, Integer> stats = new HashMap<>();

    private static final String[] STAT_KEYS = {
        "STR_TOT", "RES_TOT", "SPD_TOT", "HAS_TOT",
        "DAR_TOT", "HER_TOT", "HUN_TOT", "LUC_TOT"
    };


    public static void updateAll() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        Scoreboard scoreboard = client.world.getScoreboard();
        String playerName = client.player.getEntityName();

        for (String key : STAT_KEYS) {

            ScoreboardObjective objective = scoreboard.getObjectives().stream()
                    .filter(obj -> obj.getName().equals(key))
                    .findFirst()
                    .orElse(null);

            if (objective != null) {
                ScoreboardPlayerScore score = scoreboard.getPlayerScore(client.player.getEntityName(), objective);
                stats.put(key, score.getScore());
            }
        }
    }

    public static String[] getKeys() {
        return STAT_KEYS;
    }
}
