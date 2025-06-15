package fr.poubone.att2.client.util;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.Text.Serializer;

public class ChatUtils {

    public static void displayHeldItem(MinecraftClient client) {
        if (client.player == null) return;

        ItemStack item = client.player.getMainHandStack();
        if (item.isEmpty()) {
            client.player.sendMessage(net.minecraft.text.Text.literal("§cAucun item en main !"), false);
            return;
        }

        // Clone et nettoyer les NBTs
        NbtCompound tag = item.getNbt();
        if (tag != null) {
            tag = tag.copy();
            TextComponentSanitizer.sanitizeDisplayTag(tag);
        }

        ItemStack sanitizedStack = item.copy();
        sanitizedStack.setNbt(tag);

        // Créer le texte JSON hover complet
        Text hoverText = sanitizedStack.toHoverableText();
        String hoverJson = Text.Serializer.toJson(hoverText); // JSON déjà formaté

        String command = "/tellraw @a " + hoverJson;
        System.out.println(command);
        client.player.networkHandler.sendChatCommand(command.substring(1)); // sans le /
    }

}
