package fr.poubone.att2.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

public class ChatItemUtils {

    public static void sendItemInChat(ItemStack originalStack, String label) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || originalStack.isEmpty()) return;

        // Clone le stack pour ne pas altérer l'original
        ItemStack safeStack = originalStack.copy();
        NbtCompound tag = safeStack.getOrCreateNbt();

        // 🔒 Nettoie Name/Lore pour éviter les §
        TextComponentSanitizer.sanitizeDisplayTag(tag);

        // Remet à jour le NBT
        safeStack.setNbt(tag);

        // 📦 Crée le texte avec HoverEvent
        Text text = Text.literal(label)
                .setStyle(Style.EMPTY.withHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(safeStack))
                ));


        System.out.println(text.toString());

        player.sendMessage(text, false);
    }

    // Overload pratique si pas de label fourni
    public static void sendItemInChat(ItemStack stack) {
        String label = stack.getName().getString();
        sendItemInChat(stack, label);
    }
}