package fr.poubone.att2.client.screen;

import fr.poubone.att2.client.input.KeybindManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class RadialMenuScreen extends Screen {
    private static final int OPTION_COUNT = 6;
    private int selectedOption = -1;

    private static final String[] OPTION_LABELS = {
            "Stock 1", "Stock 2", "Stock 3", "Stock 4", "Stock 5", "Ranger"
    };

    public RadialMenuScreen() {
        super(Text.literal("Stock Menu"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int cx = width / 2;
        int cy = height / 2;
        int radius = 70;

        double dx = mouseX - cx;
        double dy = mouseY - cy;
        double distance = Math.sqrt(dx * dx + dy * dy);

        double angle = Math.atan2(dy, dx);
        angle = (angle + 2 * Math.PI) % (2 * Math.PI);

        final double OFFSET = 1.7;
        selectedOption = (distance < radius / 2.5)
                ? -1
                : (int) (((angle / (2 * Math.PI)) * OPTION_COUNT + OFFSET) % OPTION_COUNT);

        for (int i = 0; i < OPTION_COUNT; i++) {
            double theta = (2 * Math.PI / OPTION_COUNT) * i - Math.PI / 3;
            int tx = (int) (cx + Math.cos(theta) * radius);
            int ty = (int) (cy + Math.sin(theta) * radius);

            float scale = (i == selectedOption) ? 1.2f : 1.0f;
            context.getMatrices().push();
            context.getMatrices().translate(tx - 8 * scale, ty - 8 * scale, 0);
            context.getMatrices().scale(scale, scale, 1.0f);

            if (i < 5) {
                context.drawItem(Items.CHEST.getDefaultStack(), 0, 0);
            } else {
                context.drawItem(Items.CHEST_MINECART.getDefaultStack(), 0, 0);
            }

            context.getMatrices().pop();

            if (i < 5) {
                context.drawText(textRenderer, Text.literal("" + (i + 1)), tx - 3, ty - 18, 0xFFFFFF, true);
            }

            if (i == selectedOption) {
                context.drawTooltip(textRenderer, Text.literal(OPTION_LABELS[i]), mouseX, mouseY);
            }
        }

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Stockage"), cx, cy - 8, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            executeOption(selectedOption);
            MinecraftClient.getInstance().setScreen(null);
            KeybindManager.blockRadialUntilRelease();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void tick() {
        if (!isRadialKeyHeld()) {
            close();
            executeOption(selectedOption);
        }
    }

    private boolean isRadialKeyHeld() {
        long window = MinecraftClient.getInstance().getWindow().getHandle();
        int keyCode = KeybindManager.getRadialMenuKey().boundKey.getCode();
        return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
    }

    private void executeOption(int index) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if (index >= 0 && index < 5) {
            int spellLevel = index + 1;
            client.player.networkHandler.sendCommand("function att2:gameplay/dahal/action/spell20/selectlvl" + spellLevel);
        } else if (index == 5) {
            client.player.networkHandler.sendCommand("function att2:gameplay/dahal/action/spell20/stock_in");
        }
    }
}
