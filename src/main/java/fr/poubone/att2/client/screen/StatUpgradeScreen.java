package fr.poubone.att2.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.poubone.att2.client.data.StatManager;
import fr.poubone.att2.client.util.ModLanguageManager;
import fr.poubone.att2.client.util.ModTextures;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

public class StatUpgradeScreen extends Screen {

    private static final int ICON_SIZE = 16;
    private static final int LINE_HEIGHT = 25;
    private static final int COLUMN_SPACING = 180;
    private int skillPoints = 0;

    private final List<String> STAT_KEYS = List.of("STR", "RES", "SPD", "HAS", "DAR", "HER", "LUC", "HUN");

    private final Map<String, String> STAT_COMMAND_KEYS = Map.of(
            "STR", "strength",
            "RES", "resistance",
            "SPD", "speed",
            "HAS", "haste",
            "DAR", "dahalregen",
            "HER", "healthregen",
            "LUC", "luck",
            "HUN", "hunger"
    );

    private final Map<String, Integer> upgradeRequirements = new HashMap<>();
    private final Map<ButtonWidget, String> buttonToStatKey = new HashMap<>();

    private final Queue<String> pendingStatTasks = new ArrayDeque<>();
    private int tickCooldown = 0;
    private boolean statTickActive = false;

    public StatUpgradeScreen() {
        super(ModLanguageManager.get("screen.stat_upgrade.title"));
    }

    @Override
    protected void init() {
        super.init();
        this.clearChildren();
        StatManager.updateAll();
        startUpgradeRequirementFetch();

        int centerX = this.width / 2;
        int startY = 40;

        int i = 0;
        for (String key : STAT_KEYS) {
            int col = i % 2;
            int row = i / 2;

            int baseX = centerX + (col == 0 ? -COLUMN_SPACING / 2 : COLUMN_SPACING / 2);
            int y = startY + row * LINE_HEIGHT;

            int value = StatManager.stats.getOrDefault(key + "_TOT", 0);
            String text = key + " : " + value;
            int textWidth = textRenderer.getWidth(text);

            int iconX = baseX - textWidth / 2 - ICON_SIZE - 10;
            int textX = baseX - textWidth / 2;
            int buttonX = textX + textWidth + 5;

            ButtonWidget plusButton = ButtonWidget.builder(Text.literal("+"), btn -> {
                String cmdKey = STAT_COMMAND_KEYS.getOrDefault(key, key.toLowerCase());
                MinecraftClient.getInstance().player.networkHandler.sendCommand("function att2:gameplay/stat/" + cmdKey + "/upgrade");
                startUpgradeRequirementFetch();
            }).dimensions(buttonX, y, 20, 20).build();

            this.addDrawableChild(plusButton);
            buttonToStatKey.put(plusButton, key);
            i++;
        }

        // 🔻 Bouton Fermer
        int closeY = startY + 5 * LINE_HEIGHT;
        this.addDrawableChild(ButtonWidget.builder(ModLanguageManager.get("screen.stat_upgrade.close"), btn -> {
            MinecraftClient.getInstance().setScreen(null);
        }).dimensions(this.width / 2 - 40, closeY, 80, 20).build());
    }

    private void startUpgradeRequirementFetch() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        pendingStatTasks.clear();
        pendingStatTasks.add("SKILLPOINT_SET");
        pendingStatTasks.add("SKILLPOINT_GET");
        for (String stat : STAT_KEYS) {
            pendingStatTasks.add("SET_" + stat);
            pendingStatTasks.add("GET_" + stat);
        }

        tickCooldown = 0;
        statTickActive = true;
        ClientTickEvents.END_CLIENT_TICK.register(this::processNextStatTask);
    }

    private void processNextStatTask(MinecraftClient client) {
        if (!statTickActive || client.player == null || client.world == null) return;

        if (tickCooldown > 0) {
            tickCooldown--;
            return;
        }

        if (pendingStatTasks.isEmpty()) {
            statTickActive = false;
            return;
        }

        String task = pendingStatTasks.poll();

        if (task.startsWith("SET_")) {
            String statKey = task.substring(4);
            String objective = statKey + "_UPGRADE_REQ";
            client.player.networkHandler.sendCommand("scoreboard objectives setdisplay sidebar.team.red " + objective);
            tickCooldown = 2;
        } else if (task.startsWith("GET_")) {
            String statKey = task.substring(4);
            String objectiveName = statKey + "_UPGRADE_REQ";
            String playerName = client.player.getEntityName();
            Scoreboard scoreboard = client.world.getScoreboard();
            ScoreboardObjective objective = scoreboard.getObjectives()
                    .stream()
                    .filter(obj -> obj.getName().equals(objectiveName))
                    .findFirst()
                    .orElse(null);
            if (objective != null) {
                int val = scoreboard.getPlayerScore(playerName, objective).getScore();
                upgradeRequirements.put(statKey, val);
            }
            tickCooldown = 1;
        } else if (task.equals("SKILLPOINT_SET")) {
            client.player.networkHandler.sendCommand("scoreboard objectives setdisplay sidebar.team.red SKILLPOINT");
            tickCooldown = 2;
        } else if (task.equals("SKILLPOINT_GET")) {
            String objectiveName = "SKILLPOINT";
            String playerName = client.player.getEntityName();
            Scoreboard scoreboard = client.world.getScoreboard();

            ScoreboardObjective objective = scoreboard.getObjectives()
                    .stream()
                    .filter(obj -> obj.getName().equals(objectiveName))
                    .findFirst()
                    .orElse(null);

            if (objective != null) {
                skillPoints = scoreboard.getPlayerScore(playerName, objective).getScore();
            }
            tickCooldown = 1;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int startY = 40;

        int i = 0;
        for (String key : STAT_KEYS) {
            int col = i % 2;
            int row = i / 2;

            int baseX = centerX + (col == 0 ? -COLUMN_SPACING / 2 : COLUMN_SPACING / 2);
            int y = startY + row * LINE_HEIGHT;

            int value = StatManager.stats.getOrDefault(key + "_TOT", 0);
            String text = key + " : " + value;
            int textWidth = textRenderer.getWidth(text);

            int iconX = baseX - textWidth / 2 - ICON_SIZE - 10;
            int textX = baseX - textWidth / 2;

            // Icône
            if (key.equals("DAR")) {
                context.getMatrices().push();
                context.getMatrices().translate(iconX, y, 0);
                context.drawItem(Items.BOOK.getDefaultStack(), 0, 0);
                context.getMatrices().pop();
            } else {
                Identifier icon = ModTextures.STAT_ICONS.get(key);
                if (icon != null) {
                    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                    context.drawTexture(icon, iconX, y, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
                }
            }

            context.drawText(textRenderer, text, textX, y + 4, 0xFFFFFF, false);

            if (mouseX >= textX && mouseX <= textX + textWidth && mouseY >= y && mouseY <= y + LINE_HEIGHT) {
                context.drawTooltip(textRenderer, ModLanguageManager.get("stat.label." + key.toLowerCase()), mouseX, mouseY);
            }

            i++;
        }

        for (Map.Entry<ButtonWidget, String> entry : buttonToStatKey.entrySet()) {
            ButtonWidget btn = entry.getKey();
            String statKey = entry.getValue();

            if (btn.isHovered()) {
                Integer req = upgradeRequirements.get(statKey);
                if (req != null) {
                    context.drawTooltip(textRenderer, ModLanguageManager.get("screen.stat_upgrade.req").copy().append(" " + req), mouseX, mouseY);
                }
            }

            Integer req = upgradeRequirements.get(statKey);
            if (req != null) {
                if (skillPoints < req) {
                    btn.setMessage(Text.literal("🔒"));
                    btn.active = false;
                } else {
                    btn.setMessage(Text.literal("+"));
                    btn.active = true;
                }
            }
        }

        String header = ModLanguageManager.get("screen.stat_upgrade.skill_points").getString().replace("{points}", String.valueOf(skillPoints));
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(header), this.width / 2, 15, 0xFFFFFF);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
