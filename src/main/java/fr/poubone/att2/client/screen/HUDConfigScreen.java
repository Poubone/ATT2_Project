package fr.poubone.att2.client.screen;

import fr.poubone.att2.client.hud.HUDConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class HUDConfigScreen extends Screen {
    private final List<CheckboxWidget> checkboxes = new ArrayList<>();

    public HUDConfigScreen() {
        super(Text.literal("Configuration HUD"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
    @Override
    protected void init() {
        HUDConfig config = HUDConfig.get();
        int y = height / 4;

        checkboxes.clear();

        checkboxes.add(addDrawableChild(new CheckboxWidget(width / 2 - 100, y, 200, 20, Text.literal("Afficher Chronoton"), config.showChronoton)));
        checkboxes.add(addDrawableChild(new CheckboxWidget(width / 2 - 100, y + 25, 200, 20, Text.literal("Afficher XP"), config.showXP)));
        checkboxes.add(addDrawableChild(new CheckboxWidget(width / 2 - 100, y + 50, 200, 20, Text.literal("Afficher Mana"), config.showMana)));
        checkboxes.add(addDrawableChild(new CheckboxWidget(width / 2 - 100, y + 75, 200, 20, Text.literal("Afficher Stats"), config.showStats)));
        checkboxes.add(addDrawableChild(new CheckboxWidget(width / 2 - 100, y + 100, 200, 20, Text.literal("Afficher Flèches"), config.showArrows)));

        addDrawableChild(ButtonWidget.builder(Text.literal("Sauvegarder et Fermer"), b -> {
            config.showChronoton = checkboxes.get(0).isChecked();
            config.showXP = checkboxes.get(1).isChecked();
            config.showMana = checkboxes.get(2).isChecked();
            config.showStats = checkboxes.get(3).isChecked();
            config.showArrows = checkboxes.get(4).isChecked();
            HUDConfig.save();
            MinecraftClient.getInstance().setScreen(null);
        }).dimensions(width / 2 - 100, y + 140, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context,mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Configuration du HUD"), width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}
