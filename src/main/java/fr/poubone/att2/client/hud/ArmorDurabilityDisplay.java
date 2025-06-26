package fr.poubone.att2.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ArmorDurabilityDisplay {

    private static final int OFFSET = 76;
    private static final int X_ADJUST = -50;

    public static void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.options == null) return;

        int hotbarX = (ctx.getScaledWindowWidth() / 2) - 182 / 2;
        int hotbarY = ctx.getScaledWindowHeight() - 22 - 4;
        float iconYBase = hotbarY ;
        int startX  = hotbarX + 182 + OFFSET + X_ADJUST;

        ItemStack[] stacks = {
                player.getInventory().getArmorStack(3), // Helmet
                player.getInventory().getArmorStack(2), // Chestplate
                player.getInventory().getArmorStack(1), // Leggings
                player.getInventory().getArmorStack(0)  // Boots
        };

        for (int i = 0; i < stacks.length; i++) {
            ItemStack stack = stacks[i];
            if (stack.isEmpty() || !stack.isDamageable()) continue;

            int max = stack.getMaxDamage();
            int current = max - stack.getDamage();
            float ratio = (float) current / max;
            int color = getColor(ratio);

            int spacing = 24;
            float scale = 0.9f;

            float iconX = startX + i * spacing;
            float iconY = iconYBase;


            ctx.getMatrices().push();
            ctx.getMatrices().translate(iconX + 8 * (1 - scale), iconY + 8 * (1 - scale), 0);
            ctx.getMatrices().scale(scale, scale, 1.0f);
            ctx.drawItem(stack, 0, 0);
            ctx.getMatrices().pop();

            String text = String.valueOf(current) ;
            int textWidth = mc.textRenderer.getWidth(text);
            float textX = iconX + 8 - (textWidth * scale / 2f);
            float textY = iconY + 16 * scale + 2;

            ctx.getMatrices().push();
            ctx.getMatrices().translate(textX, textY, 0);
            ctx.getMatrices().scale(scale, scale, 1.0f);
            ctx.drawText(mc.textRenderer, Text.literal(text), 0, 0, color, true);
            ctx.getMatrices().pop();
        }

    }

    private static int getColor(float ratio) {
        if (ratio > 0.70f) return 0x55FF55;
        if (ratio > 0.30f) return 0xFFFF00;
        return 0xFF5555;
    }
}
