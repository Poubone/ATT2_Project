package fr.poubone.att2.client;

import fr.poubone.att2.client.hud.HudRenderer;
import fr.poubone.att2.client.input.KeybindManager;
import fr.poubone.att2.client.renderer.BeamRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

@Environment(EnvType.CLIENT)
public class ATT2_ProjectClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeybindManager.register();
        BeamRenderer.register();

        HudRenderCallback.EVENT.register(HudRenderer::render);

    }
}
