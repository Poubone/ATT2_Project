package fr.poubone.att2.mixin;

import fr.poubone.att2.client.hud.HUDConfig;
import fr.poubone.att2.client.renderer.LootBeamRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;


// This code is mainly fram LootBeamsFabricUpdated by VanderCat

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void attemptRenderBeams(Entity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider buffer, int packedLight, CallbackInfo ci){
        var config = HUDConfig.get();
        if (entity instanceof ItemEntity) {
            ItemEntity itemEntity = (ItemEntity) entity;
            if (MinecraftClient.getInstance().player.squaredDistanceTo(itemEntity) > config.renderDistance * config.renderDistance) {
                return;
            }

            boolean shouldRender = false;
            if (config.allItems) {
                shouldRender = true;
            } else {
                if (config.onlyEquipment) {
                    List<Class<? extends Item>> equipmentClasses = Arrays.asList(SwordItem.class, MiningToolItem.class, ArmorItem.class, ShieldItem.class, BowItem.class, CrossbowItem.class, TridentItem.class, ArrowItem.class, FishingRodItem.class);
                    for (Class<? extends Item> item : equipmentClasses) {
                        if (item.isAssignableFrom(itemEntity.getStack().getItem().getClass())) {
                            shouldRender = true;
                            break;
                        }
                    }
                }

                if (config.onlyRare) {
                    shouldRender = itemEntity.getStack().getRarity() != Rarity.COMMON;
                }

                if (isItemInRegistryList(config.whitelist, itemEntity.getStack().getItem())) {
                    shouldRender = true;
                }
            }
            if (isItemInRegistryList(config.blacklist, itemEntity.getStack().getItem())) {
                shouldRender = false;
            }

            if (shouldRender) {
                LootBeamRenderer.renderLootBeam(poseStack, buffer, partialTick, itemEntity.getWorld().getTime(), itemEntity);
            }
        }
    }

    /**
     * Checks if the given item is in the given list of registry names.
     */
    private static boolean isItemInRegistryList(List<String> registryNames, Item item) {
        if (registryNames.size() > 0) {
            for (String id : registryNames.stream().filter((s) -> (!s.isEmpty())).toList()) {
                if (!id.contains(":")) {
                    if (Registries.ITEM.getId(item).getNamespace().equals(id)) {
                        return true;
                    }
                }
                Identifier itemResource = Identifier.tryParse(id);
                if (itemResource != null && Registries.ITEM.get(itemResource).asItem() == item.asItem()) {
                    return true;
                }
            }
        }
        return false;
    }
}
