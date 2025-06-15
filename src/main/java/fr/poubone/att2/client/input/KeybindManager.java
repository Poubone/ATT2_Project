package fr.poubone.att2.client.input;

import fr.poubone.att2.client.screen.HUDConfigScreen;
import fr.poubone.att2.client.screen.RadialMenuScreen;
import fr.poubone.att2.client.screen.RepairMenuScreen;
import fr.poubone.att2.client.screen.StatUpgradeScreen;
import fr.poubone.att2.client.util.BroadcastScanner;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class KeybindManager {
    public static boolean showCustomHUD = true;

    private static KeyBinding toggleHUDKey;
    private static KeyBinding radialMenuKey;
    private static boolean radialReleased = true;


    private static KeyBinding statUpgradeMenuKey;

    private static KeyBinding playerGlow;

    private static KeyBinding collectItems;
    private static KeyBinding quest;

    private static KeyBinding repairItemMenuKey;
    private static boolean repairReleased = true;

    private static KeyBinding whistle;
    private static KeyBinding openHUDConfig;



    private static KeyBinding broadcastKey;
    private static long lastUsedTime = 0;
    private static final long COOLDOWN_MS = 5000;



    public static void register() {
        toggleHUDKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Toggle HUD",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "ATT2"
        ));

        radialMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Menu de stockage",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "ATT2"
        ));


        statUpgradeMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Menu des statistiques du joueur",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_U,
                "ATT2"
        ));

        playerGlow = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Faire briller les joueurs",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "ATT2"
        ));
        collectItems = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Collecter les items",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "ATT2"
        ));
        quest = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Quête principale",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "ATT2"
        ));

        repairItemMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Menu de réparation",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                "ATT2"
        ));

        whistle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Siffler son cheval",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_W,
                "ATT2"
        ));


        openHUDConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Configurer HUD",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "ATT2"
        ));

        broadcastKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Hoover Item",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "ATT2"
        ));



        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (toggleHUDKey.wasPressed()) {
                showCustomHUD = !showCustomHUD;
                client.player.sendMessage(Text.literal("HUD: " + (showCustomHUD ? "Activé" : "Désactivé")), true);
                client.player.networkHandler.sendCommand("scoreboard objectives setdisplay sidebar.team.gold GAMELEVEL");
                client.player.networkHandler.sendCommand("scoreboard objectives setdisplay sidebar.team.black DAHAL");
                client.player.networkHandler.sendCommand("scoreboard objectives setdisplay sidebar.team.aqua DAHALMAX");
                client.player.networkHandler.sendCommand("scoreboard objectives setdisplay sidebar.team.blue LVL_UPGRADE_REQ");
                client.player.networkHandler.sendCommand("scoreboard objectives setdisplay sidebar.team.dark_aqua DAR_TOT");
                client.player.networkHandler.sendCommand("scoreboard objectives setdisplay sidebar.team.dark_blue HAS_TOT");
                client.player.networkHandler.sendCommand("scoreboard objectives setdisplay sidebar.team.dark_gray HER_TOT");
                client.player.networkHandler.sendCommand("scoreboard objectives setdisplay sidebar.team.dark_green HUN_TOT");
                client.player.networkHandler.sendCommand("scoreboard objectives setdisplay sidebar.team.dark_purple LUC_TOT");
                client.player.networkHandler.sendCommand("scoreboard objectives setdisplay sidebar.team.yellow RES_TOT");
                client.player.networkHandler.sendCommand("scoreboard objectives setdisplay sidebar.team.gray SPD_TOT");
                client.player.networkHandler.sendCommand("scoreboard objectives setdisplay sidebar.team.green STR_TOT");
                client.player.networkHandler.sendCommand("scoreboard objectives setdisplay sidebar.team.white SKILLPOINT");

            }
            while (statUpgradeMenuKey.wasPressed()) {
                client.setScreen(new StatUpgradeScreen());
            }
            while (playerGlow.wasPressed()) {
                client.player.networkHandler.sendCommand("effect give @a minecraft:glowing 15 0 true");
            }
            while (collectItems.wasPressed()) {
                client.player.networkHandler.sendCommand("function att2:gameplay/misc/tp_item/run");
            }
            while (quest.wasPressed()) {
                client.player.networkHandler.sendCommand("function att2:gameplay/quest/mainquest/go");
            }

            while (whistle.wasPressed()) {
                client.player.networkHandler.sendCommand("function att2:gameplay/misc/horse/whistle");
            }

            while (openHUDConfig.wasPressed()) {
                client.setScreen(new HUDConfigScreen());
            }

            while (broadcastKey.wasPressed()) {
                handleBroadcastKey(client);
            }

            if (radialMenuKey.isPressed()) {
                if (radialReleased && client.currentScreen == null) {
                    client.setScreen(new RadialMenuScreen());
                    radialReleased = false;
                }
            } else {
                radialReleased = true;
            }

            if (repairItemMenuKey.isPressed()) {
                if (repairReleased && client.currentScreen == null) {
                    client.setScreen(new RepairMenuScreen());
                    repairReleased = false;
                }
            } else {
                repairReleased = true;
            }

            BroadcastScanner.tick(client);

        });


    }

    public static KeyBinding getRadialMenuKey() {
        return radialMenuKey;
    }
    public static void blockRadialUntilRelease() {
        radialReleased = false;
    }

    public static KeyBinding getRepairItemMenuKey() {
        return repairItemMenuKey;
    }
    public static void blockRepairUntilRelease() {
        repairReleased = false;
    }


    private static void handleBroadcastKey(MinecraftClient client) {
        if (client.player == null) return;

        long now = System.currentTimeMillis();
        if (now - lastUsedTime < COOLDOWN_MS) {
            long secondsLeft = (COOLDOWN_MS - (now - lastUsedTime)) / 1000;
            client.player.sendMessage(Text.literal("§cAttends encore " + secondsLeft + "s avant de renvoyer un item."), true);
            return;
        }

        lastUsedTime = now;

        String playerName = client.player.getName().getString();

        // ✅ Envoie une commande tellraw visible par tous
        String tellraw = String.format(
                "tellraw @a [{\"text\":\"%s\",\"color\":\"aqua\"},{\"text\":\" a partagé un objet !\",\"color\":\"gray\"}]",
                playerName
        );

        client.player.networkHandler.sendChatCommand(tellraw);

        // ✅ Ensuite lance le broadcast
        client.player.networkHandler.sendChatCommand("function mymod:broadcast");
    }


}
