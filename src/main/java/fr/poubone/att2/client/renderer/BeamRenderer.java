package fr.poubone.att2.client.renderer;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class BeamRenderer {

    private static final Identifier BEAM_TEXTURE = new Identifier("att2", "textures/lootbeam.png");

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> renderBeams(context.matrixStack(), context.camera(), context.tickDelta()));
    }

    private static void renderBeams(MatrixStack matrices, Camera camera, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        if (world == null) return;

        Vec3d camPos = camera.getPos();
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

        for (var entity : world.getEntities()) {
            if (entity instanceof ItemEntity itemEntity) {
                ItemStack stack = itemEntity.getStack();
                TextColor textColor = stack.getName().getStyle().getColor();

                float r = 1.0f, g = 1.0f, b = 1.0f;
                if (textColor != null) {
                    int rgb = textColor.getRgb();
                    r = ((rgb >> 16) & 0xFF) / 255f;
                    g = ((rgb >> 8) & 0xFF) / 255f;
                    b = (rgb & 0xFF) / 255f;
                }

                matrices.push();
                try {
                    matrices.translate(
                            itemEntity.getX() - camPos.x - 0.5,
                            itemEntity.getY() - camPos.y + 0.1,
                            itemEntity.getZ() - camPos.z - 0.5
                    );

                    BeaconBlockEntityRenderer.renderBeam(
                            matrices,
                            immediate,
                            BEAM_TEXTURE,
                            tickDelta,
                            1.0f,
                            world.getTime(),
                            0,
                            2,
                            new float[]{r, g, b},
                            0.05f,
                            0.1f
                    );
                } finally {
                    matrices.pop();
                }

            }
        }

        immediate.draw();
    }
}
