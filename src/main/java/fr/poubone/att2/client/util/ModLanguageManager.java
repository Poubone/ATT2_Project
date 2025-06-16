package fr.poubone.att2.client.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ModLanguageManager {
    private static Map<String, String> translations = new HashMap<>();
    private static String currentLanguage = "fr"; // valeur par défaut

    public static void loadLanguage(MinecraftClient client, String langCode) {
        currentLanguage = langCode;
        Identifier langFile = new Identifier("att2", "lang_mod/" + langCode + ".json");
        translations.clear();
        try (InputStream stream = client.getResourceManager().getResource(langFile).get().getInputStream()) {
            JsonObject json = JsonParser.parseReader(new InputStreamReader(stream, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                translations.put(entry.getKey(), entry.getValue().getAsString());
            }
        } catch (IOException e) {
            System.err.println("Erreur de chargement du fichier de langue : " + e.getMessage());
        }
    }

    public static Text get(String key) {
        return Text.literal(translations.getOrDefault(key, "§c?" + key));
    }
}
