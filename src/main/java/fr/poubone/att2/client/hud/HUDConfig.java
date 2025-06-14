package fr.poubone.att2.client.hud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class HUDConfig {
    public boolean showChronoton = true;
    public boolean showXP = true;
    public boolean showMana = true;
    public boolean showStats = true;
    public boolean showArrows = true;

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
}
