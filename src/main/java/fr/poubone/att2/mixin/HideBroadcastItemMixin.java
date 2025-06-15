package fr.poubone.att2.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemFeatureRenderer.class)
public abstract class HideBroadcastItemMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            T entity,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float customAngle,
            float headYaw,
            float headPitch,
            CallbackInfo ci
    ) {
        if (entity instanceof ArmorStandEntity stand
                && stand.getCustomName() != null
                && "[BROADCAST_TAG]".equals(stand.getCustomName().getString())) {
            ci.cancel();
        }
    }
}

