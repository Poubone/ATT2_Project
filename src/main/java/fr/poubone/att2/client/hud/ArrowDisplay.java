package fr.poubone.att2.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class ArrowDisplay {
    public static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options == null) return;

        int arrowCount = 0;
        for (ItemStack stack : client.player.getInventory().main) {
            if (stack.getItem() == Items.ARROW) {
                arrowCount += stack.getCount();
            }
        }

        if (arrowCount == 0) return;

        // Position à gauche de la hotbar
        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        int hotbarWidth = 182;
        int hotbarHeight = 22;

        int hotbarX = (screenWidth / 2) - (hotbarWidth / 2);
        int hotbarY = screenHeight - hotbarHeight - 4;

        int arrowX = hotbarX - 76; // à gauche de la hotbar
        int arrowY = hotbarY + 6;

        context.drawItem(new ItemStack(Items.ARROW), arrowX, arrowY);
        context.drawText(client.textRenderer, Text.literal("x" + arrowCount), arrowX + 20, arrowY + 4, 0xFFFFFF, true);
    }
}
