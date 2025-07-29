package fr.poubone.att2.client.screen;

import fr.poubone.att2.client.input.KeybindManager;
import fr.poubone.att2.client.util.ModLanguageManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class RepairMenuScreen extends Screen {
    private static final int OPTION_COUNT = 7;
    private int selectedOption = -1;

    public RepairMenuScreen() {
        super(ModLanguageManager.get("screen.repair.title"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int cx = width / 2;
        int cy = height / 2;
        int radius = 70;

        double dx = mouseX - cx;
        double dy = mouseY - cy;
        double distance = Math.sqrt(dx * dx + dy * dy);

        double angle = Math.atan2(dy, dx);
        angle = (angle + 2 * Math.PI) % (2 * Math.PI);

        final double OFFSET = 1.7;
        selectedOption = (distance < radius / 2.5)
                ? -1
                : (int) (((angle / (2 * Math.PI)) * OPTION_COUNT + OFFSET) % OPTION_COUNT);

        ItemStack[] itemIcons = getRepairOptionIcons();

        for (int i = 0; i < OPTION_COUNT; i++) {
            double theta = (2 * Math.PI / OPTION_COUNT) * i - Math.PI / 3;
            int tx = (int) (cx + Math.cos(theta) * radius);
            int ty = (int) (cy + Math.sin(theta) * radius);

            ItemStack stack = itemIcons[i];
            ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();

            context.getMatrices().push();
            context.getMatrices().translate(tx - 8, ty - 8, 0);
            float scale = (i == selectedOption) ? 1.2f : 1.0f;
            context.getMatrices().scale(scale, scale, 1.0f);
            
            if (i == 6) {
                // Dessiner l'icône composite centrée dans la case
                drawCompositeArmorIcon(context);
            } else {
                context.drawItem(stack, 0, 0);
            }
            
            context.getMatrices().pop();

            if (i == selectedOption) {
                context.drawTooltip(textRenderer, ModLanguageManager.get("screen.repair.option." + i), mouseX, mouseY);
            }
        }

        context.drawCenteredTextWithShadow(textRenderer, ModLanguageManager.get("screen.repair.title"), cx, cy - 8, 0xFFFFFF);
    }

    private void drawCompositeArmorIcon(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        // Récupérer les vraies armures du joueur
        ItemStack helmet = getItemOrBarrier(client.player.getInventory().getArmorStack(3));
        ItemStack chestplate = getItemOrBarrier(client.player.getInventory().getArmorStack(2));
        ItemStack leggings = getItemOrBarrier(client.player.getInventory().getArmorStack(1));
        ItemStack boots = getItemOrBarrier(client.player.getInventory().getArmorStack(0));
        // Dessiner les 4 pièces dans un carré 2x2 centré sur (0,0)
        context.drawItem(helmet, -6, -6);      // Haut gauche
        context.drawItem(chestplate, 2, -6);   // Haut droite
        context.drawItem(leggings, -6, 2);     // Bas gauche
        context.drawItem(boots, 2, 2);         // Bas droite
    }

    private ItemStack[] getRepairOptionIcons() {
        MinecraftClient client = MinecraftClient.getInstance();
        ItemStack[] icons = new ItemStack[7];

        if (client.player != null) {
            icons[0] = getItemOrBarrier(client.player.getInventory().getArmorStack(3)); // Helmet
            icons[1] = getItemOrBarrier(client.player.getInventory().getArmorStack(2)); // Chestplate
            icons[2] = getItemOrBarrier(client.player.getInventory().getArmorStack(1)); // Leggings
            icons[3] = getItemOrBarrier(client.player.getInventory().getArmorStack(0)); // Boots
            icons[4] = getItemOrBarrier(client.player.getStackInHand(Hand.OFF_HAND));   // Offhand
        }

        icons[5] = new ItemStack(Items.ANVIL); // Voir les objets
        icons[6] = new ItemStack(Items.DIAMOND_HELMET); // Réparer toutes les armures (placeholder - sera remplacé par l'icône composite)
        return icons;
    }

    private ItemStack getItemOrBarrier(ItemStack stack) {
        return (stack != null && !stack.isEmpty()) ? new ItemStack(stack.getItem()) : new ItemStack(Items.BARRIER);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            executeOption(selectedOption);
            MinecraftClient.getInstance().setScreen(null);
            KeybindManager.blockRepairUntilRelease();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void tick() {
        if (!isRepairKeyHeld()) {
            close();
            executeOption(selectedOption);
        }
    }

    private boolean isRepairKeyHeld() {
        long window = MinecraftClient.getInstance().getWindow().getHandle();
        int keyCode = KeybindManager.getRepairItemMenuKey().boundKey.getCode();
        return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
    }

    private void executeOption(int index) {
        if (index < 0) return;

        if (index == 6) {
            // Réparer toutes les armures - exécuter les 4 commandes d'affilée
            executeAllArmorRepair();
            return;
        }

        String cmd = switch (index) {
            case 0 -> "function att2:gameplay/shop/mending/tools/trigger_helmet";
            case 1 -> "function att2:gameplay/shop/mending/tools/trigger_chestplate";
            case 2 -> "function att2:gameplay/shop/mending/tools/trigger_leggings";
            case 3 -> "function att2:gameplay/shop/mending/tools/trigger_boots";
            case 4 -> "function att2:gameplay/shop/mending/tools/trigger_offhand";
            case 5 -> "function att2:dialogs/gameplay/shop/tools_number_info";
            default -> null;
        };

        if (cmd != null && MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.networkHandler.sendCommand(cmd);
        }
    }

    private void executeAllArmorRepair() {
        if (MinecraftClient.getInstance().player == null) return;
        
        // Exécuter les 4 commandes de réparation d'affilée
        String[] commands = {
            "function att2:gameplay/shop/mending/tools/trigger_helmet",
            "function att2:gameplay/shop/mending/tools/trigger_chestplate",
            "function att2:gameplay/shop/mending/tools/trigger_leggings",
            "function att2:gameplay/shop/mending/tools/trigger_boots"
        };
        
        for (String command : commands) {
            MinecraftClient.getInstance().player.networkHandler.sendCommand(command);
        }
    }
}
