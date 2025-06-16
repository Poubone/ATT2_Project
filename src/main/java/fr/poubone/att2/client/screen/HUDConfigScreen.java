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

        int y = height / 4;
        checkboxes.clear();

        checkboxes.add(addDrawableChild(new CheckboxWidget(width / 2 - 100, y, 200, 20, ModLanguageManager.get("screen.hud_config.show_chronoton"), config.showChronoton)));
        checkboxes.add(addDrawableChild(new CheckboxWidget(width / 2 - 100, y + 25, 200, 20, ModLanguageManager.get("screen.hud_config.show_xp"), config.showXP)));
        checkboxes.add(addDrawableChild(new CheckboxWidget(width / 2 - 100, y + 50, 200, 20, ModLanguageManager.get("screen.hud_config.show_mana"), config.showMana)));
        checkboxes.add(addDrawableChild(new CheckboxWidget(width / 2 - 100, y + 75, 200, 20, ModLanguageManager.get("screen.hud_config.show_stats"), config.showStats)));
        checkboxes.add(addDrawableChild(new CheckboxWidget(width / 2 - 100, y + 100, 200, 20, ModLanguageManager.get("screen.hud_config.show_arrows"), config.showArrows)));

        languageButton = addDrawableChild(ButtonWidget.builder(
                Text.literal(ModLanguageManager.get("screen.hud_config.language_button").getString() + " : " + config.modLanguage.toUpperCase()),
                b -> cycleLanguage()
        ).dimensions(width / 2 - 100, y + 130, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(ModLanguageManager.get("screen.hud_config.save_and_close"), b -> {
            config.showChronoton = checkboxes.get(0).isChecked();
            config.showXP = checkboxes.get(1).isChecked();
            config.showMana = checkboxes.get(2).isChecked();
            config.showStats = checkboxes.get(3).isChecked();
            config.showArrows = checkboxes.get(4).isChecked();
            HUDConfig.save();
            MinecraftClient.getInstance().setScreen(null);
        }).dimensions(width / 2 - 100, y + 160, 200, 20).build());
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
}
