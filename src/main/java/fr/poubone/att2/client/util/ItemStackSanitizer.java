package fr.poubone.att2.client.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class ItemStackSanitizer {

    public static ItemStack sanitize(ItemStack original) {
        ItemStack sanitized = original.copy();

        if (!sanitized.hasNbt()) return sanitized;

        NbtCompound tag = sanitized.getOrCreateNbt();

        if (!tag.contains("display")) return sanitized;

        NbtCompound display = tag.getCompound("display");

        // Nettoyage du nom
        if (display.contains("Name")) {
            try {
                String rawJson = display.getString("Name");
                Text parsed = Text.Serializer.fromJson(rawJson);

                String clean = Formatting.strip(parsed.getString());

                // Convertir TextColor → Formatting si possible
                TextColor textColor = parsed.getStyle().getColor();
                Formatting color = Formatting.GRAY;

                if (textColor != null) {
                    for (Formatting f : Formatting.values()) {
                        if (f.isColor() && f.getColorValue() == textColor.getRgb()) {
                            color = f;
                            break;
                        }
                    }
                }

                Formatting finalColor = color;
                Text safeName = Text.literal(clean)
                        .styled(s -> s.withColor(finalColor).withItalic(false));

                display.putString("Name", Text.Serializer.toJson(safeName));
            } catch (Exception e) {
                display.remove("Name");
            }
        }


        // Nettoyage du lore
        if (display.contains("Lore")) {
            try {
                NbtList originalLore = display.getList("Lore", NbtString.STRING_TYPE);
                NbtList sanitizedLore = new NbtList();

                for (int i = 0; i < originalLore.size(); i++) {
                    String raw = originalLore.getString(i);
                    String cleanText = Formatting.strip(Text.Serializer.fromJson(raw).getString());
                    Text safeLine = Text.literal(cleanText).styled(s -> s.withColor(Formatting.GRAY).withItalic(false));
                    sanitizedLore.add(NbtString.of(Text.Serializer.toJson(safeLine)));
                }

                display.put("Lore", sanitizedLore);
            } catch (Exception e) {
                display.remove("Lore");
            }
        }

        tag.put("display", display);
        sanitized.setNbt(tag);

        return sanitized;
    }
}
