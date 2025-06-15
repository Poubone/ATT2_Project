package fr.poubone.att2.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class HooverItem {

    public static String convertNbtToStrictJson(NbtCompound tag) {
        String rawNbt = tag.asString();  // toujours bien formé côté Mojang

        // ASTUCE : entoure avec "" et passe par JsonParser
        // (Minecraft accepte le tag comme un objet JSON pur)
        String fixed = rawNbt.replace("\"", "\\\"");

        // Le tag doit rester objet JSON, pas string
        return rawNbt;
    }

    public static void sendHoverItemToChat(MinecraftClient client) {
        if (client.player == null) return;

        ItemStack rawStack = client.player.getMainHandStack();
        ItemStack stack = ItemStackSanitizer.sanitize(rawStack);
        Text displayName = getSafeDisplayNameText(stack);
        String plainName = Formatting.strip(displayName.getString());

        Text hoverText = Text.literal("<")
                .append(Text.literal(plainName)
                        .setStyle(Style.EMPTY
                                .withColor(displayName.getStyle().getColor())
                                .withHoverEvent(new HoverEvent(
                                        HoverEvent.Action.SHOW_ITEM,
                                        new HoverEvent.ItemStackContent(stack)
                                ))
                        )
                )
                .append(">");

        String command = "tellraw @a " + Text.Serializer.toJson(hoverText);
        System.out.println("[DEBUG tellraw safe] " + command);
        client.player.networkHandler.sendChatCommand(command);

    }
    private static String getColorString(Text text) {
        TextColor color = text.getStyle().getColor();
        return color != null ? color.getName() : "gray";
    }


    // Récupère un Text avec nom stylé, sans §
    private static Text getSafeDisplayNameText(ItemStack stack) {
        if (stack.hasNbt()) {
            NbtCompound tag = stack.getNbt();
            if (tag != null && tag.contains("display", NbtElement.COMPOUND_TYPE)) {
                NbtCompound display = tag.getCompound("display");
                if (display.contains("Name", NbtElement.STRING_TYPE)) {
                    String rawJson = display.getString("Name");
                    try {
                        Text parsed = Text.Serializer.fromJson(rawJson);

                        if (parsed.getString().contains("§")) {
                            String clean = Formatting.strip(parsed.getString());

                            TextColor textColor = parsed.getStyle().getColor();
                            Formatting formattingColor = Formatting.GRAY;

                            if (textColor != null) {
                                for (Formatting f : Formatting.values()) {
                                    if (f.isColor() && f.getColorValue() == textColor.getRgb()) {
                                        formattingColor = f;
                                        break;
                                    }
                                }
                            }

                            return Text.literal(clean)
                                    .setStyle(Style.EMPTY.withColor(formattingColor));
                        }

                        return parsed;
                    } catch (Exception ignored) {}
                }
            }
        }

        // fallback : nom brut sans style
        return Text.literal(Formatting.strip(stack.getName().getString()))
                .setStyle(Style.EMPTY.withColor(Formatting.GRAY));
    }

}
