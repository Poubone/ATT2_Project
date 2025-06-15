package fr.poubone.att2.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.poubone.att2.client.util.ModTextures;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.Item;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

public class HudDrawUtils {

    public static void drawHUDValue(DrawContext context, int value, int animationTimer, int maxTicks, int yOffset, Item icon, int color, boolean animate) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        String display = String.valueOf(value);
        OrderedText text = Text.literal(display).asOrderedText();

        int screenWidth = client.getWindow().getScaledWidth();
        int padding = 8;
        int iconSize = 16;
        int textWidth = textRenderer.getWidth(text);

        float x = screenWidth - textWidth - iconSize - padding * 0.5f;
        float y = padding + yOffset;

        int outlineColor = 0x000000;
        VertexConsumerProvider.Immediate vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        float scale = 1.0f;
        if (animate && animationTimer > 0) {
            float progress = (float) (maxTicks - animationTimer) / maxTicks;
            scale = 1.0f + (float) Math.sin(progress * Math.PI) * 0.3f;
        }

        context.getMatrices().push();
        context.getMatrices().translate(x - iconSize - 2 + 8, y + 8, 0);
        context.getMatrices().scale(scale, scale, 1.0f);
        context.getMatrices().translate(-8, -8, 0);
        context.drawItem(icon.getDefaultStack(), 0, 0);
        context.getMatrices().pop();

        textRenderer.drawWithOutline(
            text,
            x,
            y + 4,
            color,
            outlineColor,
            matrix,
            vertexConsumers,
            0xF000F0
        );

        vertexConsumers.draw();
    }

    public static void drawXPHUDValue(DrawContext context, String display, int yOffset, int color) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        OrderedText text = Text.literal(display).asOrderedText();

        int screenWidth = client.getWindow().getScaledWidth();
        int padding = 8;
        int iconSize = 12;
        int textWidth = textRenderer.getWidth(text);

        float x = screenWidth - textWidth - iconSize - padding * 0.5f;
        float y = padding + yOffset;

        int outlineColor = 0x000000;
        VertexConsumerProvider.Immediate vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        int ticks = (int) (System.currentTimeMillis() / 100) % 16;
        int frameX = (ticks % 4) * 16;
        int frameY = (ticks / 4) * 16;
        int textHeight = textRenderer.fontHeight;
        int iconDrawY = (int) (y + (textHeight - 16) / 2);

        RenderSystem.setShaderColor(0.3f, 1.0f, 0.3f, 1.0f);

        context.drawTexture(
            ModTextures.XP_ORB,
            (int) (x - iconSize - 4), iconDrawY,
            frameX, frameY,
            16, 16,
            64, 64
        );

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        textRenderer.drawWithOutline(
            text,
            x + 2,
            y + 2,
            color,
            outlineColor,
            matrix,
            vertexConsumers,
            0xF000F0
        );

        vertexConsumers.draw();
    }
}
