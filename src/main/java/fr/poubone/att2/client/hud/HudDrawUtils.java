package fr.poubone.att2.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.poubone.att2.client.util.ModTextures;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
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

    public static void drawLine2D(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        if (x1 == x2) {
            // Ligne verticale
            int yMin = Math.min(y1, y2);
            int yMax = Math.max(y1, y2);
            context.fill(x1, yMin, x1 + 1, yMax, color);
        } else if (y1 == y2) {
            // Ligne horizontale
            int xMin = Math.min(x1, x2);
            int xMax = Math.max(x1, x2);
            context.fill(xMin, y1, xMax, y1 + 1, color);
        } else {
            // Ligne diagonale (approximation simple)
            int steps = 100;
            for (int i = 0; i < steps; i++) {
                float t = i / (float) steps;
                float t2 = (i + 1) / (float) steps;
                int xStart = (int) (x1 + (x2 - x1) * t);
                int yStart = (int) (y1 + (y2 - y1) * t);
                int xEnd = (int) (x1 + (x2 - x1) * t2);
                int yEnd = (int) (y1 + (y2 - y1) * t2);
                context.fill(xStart, yStart, xEnd + 1, yEnd + 1, color);
            }
        }
    }
    public static void drawThickLine(DrawContext context, float x1, float y1, float x2, float y2, float thickness, int color) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len == 0) return;

        float nx = -dy / len;
        float ny = dx / len;
        float px = nx * thickness / 2;
        float py = ny * thickness / 2;

        float[] vertices = {
                x1 + px, y1 + py,
                x1 - px, y1 - py,
                x2 - px, y2 - py,
                x2 + px, y2 + py
        };

        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < 4; i++) {
            float vx = vertices[i * 2];
            float vy = vertices[i * 2 + 1];
            buffer.vertex(matrix, vx, vy, 0).color(r, g, b, a).next();
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.disableBlend();
    }



}
