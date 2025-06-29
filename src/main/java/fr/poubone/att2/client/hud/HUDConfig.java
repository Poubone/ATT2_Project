package fr.poubone.att2.client.hud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HUDConfig {
    // HUD options
    public boolean showChronoton = true;
    public boolean showXP = true;
    public boolean showMana = true;
    public boolean showStats = true;
    public boolean showArrows = true;
    public boolean showArmorDurability = true;
    public String modLanguage = "fr";

    public List<String> renderRarities = new ArrayList<>(List.of(
            "com", "cur", "epi", "epi_set", "leg", "leg_armset",
            "misc", "myt", "que", "rar", "spe", "ult", "unc", "unk"
    ));

    public boolean allItems = true;
    public boolean onlyEquipment = false;
    public boolean onlyRare = false;
    public List<String> whitelist = new ArrayList<>();
    public List<String> blacklist = new ArrayList<>();
    public List<String> colorOverrides = new ArrayList<>();

    public boolean renderNameColor = true;
    public boolean renderRarityColor = true;
    public float beamRadius = 1;
    public float beamHeight = 1;
    public float beamYOffset = 0;
    public float beamAlpha = 0.85f;
    public float renderDistance = 24.0f;
    public float fadeDistance = 2.0f;

    public boolean borders = true;
    public boolean renderNametags = true;
    public boolean renderNametagsOnlook = true;
    public boolean renderStackcount = true;
    public float nametagLookSensitivity = 0.018f;
    public float nametagTextAlpha = 1;
    public float nametagBackgroundAlpha = 0.5f;
    public float nametagScale = 1.0f;
    public float nametagYOffset = 0.75f;
    public List<String> alwaysDrawRaritiesOn = List.of("#minecraft:music_discs");
    public boolean whiteRarities = false;

    private static final File FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "att2_hud.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static HUDConfig INSTANCE;

    public static HUDConfig get() {
        if (INSTANCE == null) load();
        return INSTANCE;
    }

    public static void load() {
        try {
            if (FILE.exists()) {
                try (FileReader reader = new FileReader(FILE)) {
                    INSTANCE = GSON.fromJson(reader, HUDConfig.class);
                }
            } else {
                INSTANCE = new HUDConfig();
            }
        } catch (Exception e) {
            e.printStackTrace();
            INSTANCE = new HUDConfig();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setModLanguage(String langCode) {
        get().modLanguage = langCode;
        save();
    }

    public static String getModLanguage() {
        return get().modLanguage;
    }

    // 🟢 Méthode utilitaire pour obtenir une couleur override
    public static TextColor getColorFromItemOverrides(Item i) {
        List<String> overrides = get().colorOverrides;
        if (overrides.isEmpty()) return null;

        for (String unparsed : overrides.stream().filter((s) -> (!s.isEmpty())).toList()) {
            String[] configValue = unparsed.split("=");
            if (configValue.length != 2) continue;

            String nameIn = configValue[0];
            Identifier registry = Identifier.tryParse(nameIn.replace("#", ""));
            TextColor colorIn;
            try {
                colorIn = TextColor.parse(configValue[1]);
            } catch (Exception e) {
                return null;
            }

            if (!nameIn.contains(":")) {
                if (Registries.ITEM.getId(i).getNamespace().equals(nameIn))
                    return colorIn;
            }

            if (registry == null) continue;

            if (nameIn.startsWith("#")) {
                Optional<RegistryEntryList.Named<Item>> tag = Registries.ITEM.streamTagsAndEntries()
                        .filter(pair -> pair.getFirst().id().equals(registry))
                        .findFirst().map(Pair::getSecond);
                if (tag.isPresent() && tag.get().contains(Registries.ITEM.getEntry(Registries.ITEM.getKey(i).get()).get())) {
                    return colorIn;
                }
            }

            Optional<Item> registryItem = Registries.ITEM.getOrEmpty(registry);
            if (registryItem.isPresent() && registryItem.get().asItem() == i.asItem())
                return colorIn;
        }

        return null;
    }
}
