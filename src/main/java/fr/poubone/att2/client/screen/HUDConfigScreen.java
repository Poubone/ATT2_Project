package fr.poubone.att2.client.screen;

import fr.poubone.att2.client.hud.HUDConfig;
import fr.poubone.att2.client.util.ModLanguageManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HUDConfigScreen extends Screen {
    private final List<CheckboxWidget> checkboxes = new ArrayList<>();
    private final List<String> rarityKeys = List.of("com", "cur", "epi", "epi_set", "leg", "leg_armset",
            "misc", "myt", "que", "rar", "spe", "ult", "unc", "unk");
    private final List<String> availableLanguages = Arrays.asList("en", "fr");
    private int currentLangIndex = 0;
    private ButtonWidget languageButton;

    public HUDConfigScreen() {
        super(ModLanguageManager.get("screen.hud_config.title"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void init() {
        this.clearChildren();
        HUDConfig config = HUDConfig.get();
        currentLangIndex = availableLanguages.indexOf(config.modLanguage);
        if (currentLangIndex < 0) currentLangIndex = 0;

        checkboxes.clear();
        List<OptionEntry> options = new ArrayList<>();

        // HUD options
        options.add(new OptionEntry(ModLanguageManager.get("screen.hud_config.show_chronoton").getString(), config.showChronoton));
        options.add(new OptionEntry(ModLanguageManager.get("screen.hud_config.show_xp").getString(), config.showXP));
        options.add(new OptionEntry(ModLanguageManager.get("screen.hud_config.show_mana").getString(), config.showMana));
        options.add(new OptionEntry(ModLanguageManager.get("screen.hud_config.show_stats").getString(), config.showStats));
        options.add(new OptionEntry(ModLanguageManager.get("screen.hud_config.show_arrows").getString(), config.showArrows));
        options.add(new OptionEntry(ModLanguageManager.get("screen.hud_config.show_armor_durability").getString(), config.showArmorDurability));

        // Loot beam options
        options.add(new OptionEntry(ModLanguageManager.get("screen.hud_config.render_allItems").getString(), config.allItems));
        options.add(new OptionEntry(ModLanguageManager.get("screen.hud_config.render_nametags").getString(), config.renderNametags));
        options.add(new OptionEntry(ModLanguageManager.get("screen.hud_config.render_stackcount").getString(), config.renderStackcount));

        // Rarities
        for (String rarity : rarityKeys) {
            String key = "screen.hud_config.render_rarity." + rarity;
            boolean enabled = config.renderRarities.contains(rarity);
            options.add(new OptionEntry(ModLanguageManager.get(key).getString(), enabled));
        }

        int spacing = 25;
        int buttonWidth = 200;
        int margin = 20;
        int availableWidth = width - margin * 2;
        int availableHeight = height - margin * 4 - 50; // espace boutons
        int maxRows = Math.max(1, availableHeight / spacing);
        int columns = (int) Math.ceil(options.size() / (float) maxRows);

        int startX = (width - (columns * (buttonWidth + 20) - 20)) / 2;
        int baseY = margin * 2;

        for (int i = 0; i < options.size(); i++) {
            int col = i / maxRows;
            int row = i % maxRows;
            int x = startX + col * (buttonWidth + 20);
            int y = baseY + row * spacing;

            OptionEntry entry = options.get(i);
            checkboxes.add(addDrawableChild(new CheckboxWidget(x, y, buttonWidth, 20, Text.literal(entry.label()), entry.value())));
        }

        int yButtons = baseY + maxRows * spacing + 10;

        languageButton = addDrawableChild(ButtonWidget.builder(
                Text.literal(ModLanguageManager.get("screen.hud_config.language_button").getString() + " : " + config.modLanguage.toUpperCase()),
                b -> cycleLanguage()
        ).dimensions(width / 2 - buttonWidth / 2, yButtons, buttonWidth, 20).build());

        addDrawableChild(ButtonWidget.builder(
                ModLanguageManager.get("screen.hud_config.save_and_close"),
                b -> {
                    int i = 0;
                    config.showChronoton = checkboxes.get(i++).isChecked();
                    config.showXP = checkboxes.get(i++).isChecked();
                    config.showMana = checkboxes.get(i++).isChecked();
                    config.showStats = checkboxes.get(i++).isChecked();
                    config.showArrows = checkboxes.get(i++).isChecked();
                    config.showArmorDurability = checkboxes.get(i++).isChecked();

                    config.allItems = checkboxes.get(i++).isChecked();
                    config.renderNametags = checkboxes.get(i++).isChecked();
                    config.renderStackcount = checkboxes.get(i++).isChecked();

                    config.renderRarities.clear();
                    for (String rarity : rarityKeys) {
                        if (checkboxes.get(i++).isChecked()) {
                            config.renderRarities.add(rarity);
                        }
                    }

                    HUDConfig.save();
                    MinecraftClient.getInstance().setScreen(null);
                }
        ).dimensions(width / 2 - buttonWidth / 2, yButtons + spacing, buttonWidth, 20).build());
    }

    private void cycleLanguage() {
        currentLangIndex = (currentLangIndex + 1) % availableLanguages.size();
        String newLang = availableLanguages.get(currentLangIndex);
        HUDConfig.setModLanguage(newLang);
        ModLanguageManager.loadLanguage(MinecraftClient.getInstance(), newLang);
        this.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, ModLanguageManager.get("screen.hud_config.title"), width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    private record OptionEntry(String label, boolean value) {}
}
