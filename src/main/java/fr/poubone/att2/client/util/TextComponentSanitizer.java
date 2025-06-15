package fr.poubone.att2.client.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class TextComponentSanitizer {

    public static void sanitizeDisplayTag(NbtCompound itemTag) {
        if (!itemTag.contains("display", 10)) return;

        NbtCompound display = itemTag.getCompound("display");

        if (display.contains("Name", 8)) {
            String rawName = display.getString("Name");
            JsonObject cleanName = convertName(rawName);
            display.putString("Name", cleanName.toString());
        }

        if (display.contains("Lore", 9)) {
            NbtList loreList = display.getList("Lore", 8);
            List<String> rawLore = new ArrayList<>();
            for (int i = 0; i < loreList.size(); i++) {
                rawLore.add(loreList.getString(i));
            }

            JsonArray cleanLore = convertLore(rawLore);
            NbtList newLoreList = new NbtList();
            for (int i = 0; i < cleanLore.size(); i++) {
                newLoreList.add(NbtString.of(cleanLore.get(i).toString()));
            }

            display.put("Lore", newLoreList);
        }
    }

    public static JsonArray convertSectionColorToJsonComponents(String input) {
        JsonArray components = new JsonArray();
        StringBuilder currentText = new StringBuilder();
        String currentColor = null;
        boolean bold = false, italic = false, underlined = false, strikethrough = false, obfuscated = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '§' && i + 1 < input.length()) {
                if (currentText.length() > 0) {
                    components.add(createComponent(currentText.toString(), currentColor, bold, italic, underlined, strikethrough, obfuscated));
                    currentText.setLength(0);
                }

                char code = input.charAt(++i);
                Formatting format = Formatting.byCode(code);
                if (format != null) {
                    if (format.isColor()) {
                        currentColor = format.getName();
                        bold = italic = underlined = strikethrough = obfuscated = false;
                    } else {
                        switch (format) {
                            case BOLD -> bold = true;
                            case ITALIC -> italic = true;
                            case UNDERLINE -> underlined = true;
                            case STRIKETHROUGH -> strikethrough = true;
                            case OBFUSCATED -> obfuscated = true;
                            case RESET -> {
                                currentColor = null;
                                bold = italic = underlined = strikethrough = obfuscated = false;
                            }
                        }
                    }
                }
            } else {
                currentText.append(c);
            }
        }

        if (currentText.length() > 0) {
            components.add(createComponent(currentText.toString(), currentColor, bold, italic, underlined, strikethrough, obfuscated));
        }

        return components;
    }

    private static JsonObject createComponent(String text, String color, boolean bold, boolean italic, boolean underlined, boolean strikethrough, boolean obfuscated) {
        JsonObject comp = new JsonObject();
        comp.addProperty("text", text);
        comp.addProperty("italic", italic); // ✅ applique italic seulement si §o était présent

        if (color != null) comp.addProperty("color", color);
        if (bold) comp.addProperty("bold", true);
        if (underlined) comp.addProperty("underlined", true);
        if (strikethrough) comp.addProperty("strikethrough", true);
        if (obfuscated) comp.addProperty("obfuscated", true);

        return comp;
    }

    public static JsonObject convertName(String raw) {
        String cleaned = raw;
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }

        JsonArray parts = convertSectionColorToJsonComponents(cleaned);
        if (parts.size() == 1 && parts.get(0).isJsonObject()) {
            return parts.get(0).getAsJsonObject();
        }

        JsonObject root = new JsonObject();
        root.addProperty("text", "");
        root.add("extra", parts);
        root.addProperty("italic", false); // 🔒 sécurité au niveau racine

        return root;
    }

    public static JsonArray convertLore(List<String> loreLines) {
        JsonArray newLore = new JsonArray();
        for (String line : loreLines) {
            String rawText = extractTextFromJsonLine(line);
            JsonArray components = convertSectionColorToJsonComponents(rawText);
            newLore.add(components);
        }
        return newLore;
    }

    private static String extractTextFromJsonLine(String jsonLine) {
        try {
            JsonObject obj = JsonParser.parseString(jsonLine).getAsJsonObject();
            return obj.get("text").getAsString();
        } catch (Exception e) {
            return jsonLine;
        }
    }
}
