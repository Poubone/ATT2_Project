package fr.poubone.att2.client.util;

import net.minecraft.util.Identifier;

import java.util.Map;

public class ModTextures {
    public static final Identifier XP_ORB = new Identifier("minecraft", "textures/entity/experience_orb.png");
    public static final Identifier MANA_FRAME = new Identifier("att2", "textures/cadre_vide.png");
    public static final Identifier MANA_FILL = new Identifier("att2", "textures/orbe.png");
    public static final Identifier DAR_SPRITE = new Identifier("att2", "textures/dahal.png");

    public static final Map<String, Identifier> STAT_ICONS = Map.of(
        "HAS", new Identifier("minecraft", "textures/mob_effect/haste.png"),
        "HER", new Identifier("minecraft", "textures/mob_effect/regeneration.png"),
        "HUN", new Identifier("minecraft", "textures/mob_effect/saturation.png"),
        "LUC", new Identifier("minecraft", "textures/mob_effect/luck.png"),
        "RES", new Identifier("minecraft", "textures/mob_effect/resistance.png"),
        "SPD", new Identifier("minecraft", "textures/mob_effect/speed.png"),
        "STR", new Identifier("minecraft", "textures/mob_effect/strength.png")
    );
}
