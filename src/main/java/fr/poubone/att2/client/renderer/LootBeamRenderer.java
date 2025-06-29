package fr.poubone.att2.client.renderer;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import fr.poubone.att2.client.hud.HUDConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix3f;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

// This code is mainly fram LootBeamsFabricUpdated by VanderCat

public abstract class LootBeamRenderer extends RenderLayer {

	private static final Identifier LOOT_BEAM_TEXTURE = new Identifier("att2", "textures/loot_beam.png");
	private static final RenderLayer LOOT_BEAM_RENDERTYPE = createRenderType();

	public LootBeamRenderer(String string, VertexFormat vertexFormat, VertexFormat.DrawMode mode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
		super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
	}

	public static void renderLootBeam(MatrixStack stack, VertexConsumerProvider buffer, float pticks, long worldtime, ItemEntity item) {
		var config = HUDConfig.get();

		String nbtRarity = null;
		if (item.getStack().hasNbt() && item.getStack().getNbt().contains("Rarity")) {
			nbtRarity = item.getStack().getNbt().getString("Rarity");
		}

		if (nbtRarity == null || !config.renderRarities.contains(nbtRarity)) {
			return;
		}

		float beamAlpha = config.beamAlpha;
		float fadeDistance = config.fadeDistance;
		var player = MinecraftClient.getInstance().player;
		var distance = player.squaredDistanceTo(item);
		if (distance < fadeDistance) {
			beamAlpha *= Math.max(0, distance - fadeDistance + 1);
		}
		if (beamAlpha <= 0.1f) {
			return;
		}

		float glowAlpha = beamAlpha * 0.4f;
		float beamRadius = 0.05f * config.beamRadius;
		float glowRadius = beamRadius + (beamRadius * 0.2f);
		float beamHeight = config.beamHeight;
		float yOffset = config.beamYOffset;

		TextColor color = getItemColor(item);
		float R = ((color.getRgb() >> 16) & 0xff) / 255f;
		float G = ((color.getRgb() >> 8) & 0xff) / 255f;
		float B = (color.getRgb() & 0xff) / 255f;

		stack.push();

		stack.push();
		float rotation = (float) Math.floorMod(worldtime, 40L) + pticks;
		stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation * 2.25F - 45.0F));
		stack.translate(0, yOffset, 0);
		stack.translate(0, 1, 0);
		stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
		renderPart(stack, buffer.getBuffer(LOOT_BEAM_RENDERTYPE), R, G, B, beamAlpha, beamHeight, 0.0F, beamRadius, beamRadius, 0.0F, -beamRadius, 0.0F, 0.0F, -beamRadius);
		stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-180));
		renderPart(stack, buffer.getBuffer(LOOT_BEAM_RENDERTYPE), R, G, B, beamAlpha, beamHeight, 0.0F, beamRadius, beamRadius, 0.0F, -beamRadius, 0.0F, 0.0F, -beamRadius);
		stack.pop();

		stack.translate(0, yOffset, 0);
		stack.translate(0, 1, 0);
		stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
		renderPart(stack, buffer.getBuffer(LOOT_BEAM_RENDERTYPE), R, G, B, glowAlpha, beamHeight, -glowRadius, -glowRadius, glowRadius, -glowRadius, -beamRadius, glowRadius, glowRadius, glowRadius);
		stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-180));
		renderPart(stack, buffer.getBuffer(LOOT_BEAM_RENDERTYPE), R, G, B, glowAlpha, beamHeight, -glowRadius, -glowRadius, glowRadius, -glowRadius, -beamRadius, glowRadius, glowRadius, glowRadius);

		stack.pop();

		if (config.renderNametags) {
			renderNameTag(stack, buffer, item, color);
		}
	}


	private static void renderNameTag(MatrixStack stack, VertexConsumerProvider buffer, ItemEntity item, TextColor color) {
		var config = HUDConfig.get();

		if (MinecraftClient.getInstance().player.isInSneakingPose() || (config.renderNametagsOnlook && isLookingAt(MinecraftClient.getInstance().player, item, config.nametagLookSensitivity))) {

			float foregroundAlpha = config.nametagTextAlpha;
			float backgroundAlpha = config.nametagBackgroundAlpha;
			double yOffset = config.nametagYOffset;
			int foregroundColor = (color.getRgb() & 0xffffff) | ((int) (255 * foregroundAlpha) << 24);
			int backgroundColor = (color.getRgb() & 0xffffff) | ((int) (255 * backgroundAlpha) << 24);

			stack.push();

			stack.translate(0.0D, Math.min(1D, MinecraftClient.getInstance().player.squaredDistanceTo(item) * 0.025D) + yOffset, 0.0D);
			stack.multiply(MinecraftClient.getInstance().getEntityRenderDispatcher().getRotation());

			float nametagScale = config.nametagScale;
			stack.scale(-0.02F * nametagScale, -0.02F * nametagScale, 0.02F * nametagScale);

			//Render stack counts on nametag
			TextRenderer fontrenderer = MinecraftClient.getInstance().textRenderer;
			String itemName = StringHelper.stripTextFormat(item.getStack().getName().getString());
			if (config.renderStackcount) {
				int count = item.getStack().getCount();
				if (count > 1) {
					itemName = itemName + " x" + count;
				}
			}

			stack.translate(0, 0, -10);
			RenderText(fontrenderer, stack, buffer, itemName, foregroundColor, backgroundColor, backgroundAlpha);


			//DrawRarity(item, foregroundAlpha, backgroundAlpha, fontrenderer, stack, buffer, nbtRarity);

			stack.pop();
		}
	}

	private static void DrawRarity(ItemEntity item,
								   float foregroundAlpha,
								   float backgroundAlpha,
								   TextRenderer fontRenderer,
								   MatrixStack stack,
								   VertexConsumerProvider buffer,
								   String nbtRarity
	) {
		var config = HUDConfig.get();

		if (nbtRarity == null || !config.renderRarities.contains(nbtRarity)) {
			return;
		}

		stack.translate(0.0D, 10, 0.0D);
		stack.scale(0.75f, 0.75f, 0.75f);

		// Tu choisis la couleur : blanche si demandé, sinon une couleur de ton choix
		TextColor rarityColor = config.whiteRarities ? TextColor.fromFormatting(Formatting.WHITE) : TextColor.fromFormatting(Formatting.GOLD);
		int foregroundColor = (rarityColor.getRgb() & 0xffffff) | ((int) (255 * foregroundAlpha) << 24);
		int backgroundColor = (rarityColor.getRgb() & 0xffffff) | ((int) (255 * backgroundAlpha) << 24);
		RenderText(fontRenderer, stack, buffer, nbtRarity.toUpperCase(), foregroundColor, backgroundColor, backgroundAlpha);
	}


	private static boolean AlwaysHasRarity(ItemStack item) {
		var config = HUDConfig.get();

		List<String> overrides = config.alwaysDrawRaritiesOn;
		if (overrides.isEmpty())
			return false;

		for (String name : overrides.stream().filter((s) -> (!s.isEmpty())).toList()) {
			Identifier registry = Identifier.tryParse(name.replace("#", ""));

			if (!name.contains(":"))
				if (Registries.ITEM.getId(item.getItem()).getNamespace().equals(name))
					return true;

			if (registry == null)
				continue;

			if (name.startsWith("#")) {
				Optional<RegistryEntryList.Named<Item>> tag = Registries.ITEM.streamTagsAndEntries().filter(pair -> pair.getFirst().id().equals(registry))
						.findFirst().map(Pair::getSecond);
				//					Optional<HolderSet.Named<Item>> tag = Registry.ITEM.getTag(TagKey.create(Registry.ITEM_REGISTRY, registry));
				if (tag.isPresent() && tag.get().contains(Registries.ITEM.getEntry(Registries.ITEM.getKey(item.getItem()).get()).get())) {
					return true;
				}
			}

			Optional<Item> registryItem = Registries.ITEM.getOrEmpty(registry);

			if (registryItem.isPresent() && registryItem.get().asItem() == item.getItem())
				return true;
		}
		return false;
	}

	private static void RenderText(
			TextRenderer fontRenderer,
			MatrixStack stack,
			VertexConsumerProvider buffer,
			String text,
			int foregroundColor,
			int backgroundColor,
			float backgroundAlpha
	) {
		var config = HUDConfig.get();

		if (config.borders) {
			float w = -fontRenderer.getWidth(text) / 2f;
			int bg = new Color(0, 0, 0, (int) (255 * backgroundAlpha)).getRGB();

			var matrix = stack.peek().getPositionMatrix();
			fontRenderer.draw(text, w + 1f, 0, bg, false, matrix, buffer, TextRenderer.TextLayerType.NORMAL, 0, 0);
			fontRenderer.draw(text, w - 1f, 0, bg, false, matrix, buffer, TextRenderer.TextLayerType.NORMAL, 0, 0);
			fontRenderer.draw(text, w, 1f, bg, false, matrix, buffer, TextRenderer.TextLayerType.NORMAL, 0, 0);
			fontRenderer.draw(text, w ,-1f, bg, false, matrix, buffer, TextRenderer.TextLayerType.NORMAL, 0, 0);

			stack.translate(0.0D, 0.0D, -0.01D);
			matrix = stack.peek().getPositionMatrix();
			fontRenderer.draw(text, w, 0, foregroundColor, false, matrix, buffer, TextRenderer.TextLayerType.NORMAL, 0, 15728864);
			stack.translate(0.0D, 0.0D, 0.01D);
		} else {
			fontRenderer.draw(text, (float) (-fontRenderer.getWidth(text) / 2), 0f, foregroundColor, false, stack.peek().getPositionMatrix(), buffer, TextRenderer.TextLayerType.NORMAL, backgroundColor, 15728864);
		}
	}


	private static TextColor getItemColor(ItemEntity item) {
		var config = HUDConfig.get();

		try {

			TextColor override = config.getColorFromItemOverrides(item.getStack().getItem());
			if (override != null) {
				return override;
			}

			if (item.getStack().hasNbt() && item.getStack().getNbt().contains("lootbeams.color")) {
				return TextColor.parse(item.getStack().getNbt().getString("lootbeams.color"));
			}

			if (config.renderNameColor) {
				TextColor nameColor = getRawColor(item.getStack().getName());
				if (!nameColor.equals(TextColor.fromFormatting(Formatting.WHITE))) {
					return nameColor;
				}
			}


			if (config.renderRarityColor && item.getStack().getRarity().formatting != null) {
				return TextColor.fromFormatting(item.getStack().getRarity().formatting);
			} else {
				return TextColor.fromFormatting(Formatting.WHITE);
			}
		} catch (Exception e) {
			return TextColor.fromFormatting(Formatting.WHITE);
		}
	}

	private static TextColor getRawColor(Text text) {
		List<Style> list = Lists.newArrayList();
		text.visit((acceptor, styleIn) -> {
			TextVisitFactory.visitFormatted(styleIn, acceptor, (string, style, consumer) -> {
				list.add(style);
				return true;
			});
			return Optional.empty();
		}, Style.EMPTY);
		if (list.get(0).getColor() != null) {
			return list.get(0).getColor();
		}
		return TextColor.fromFormatting(Formatting.WHITE);
	}

	private static void renderPart(MatrixStack stack, VertexConsumer builder, float red, float green, float blue, float alpha, float height, float radius_1, float radius_2, float radius_3, float radius_4, float radius_5, float radius_6, float radius_7, float radius_8) {
		MatrixStack.Entry matrixentry = stack.peek();
		Matrix4f matrixpose = matrixentry.getPositionMatrix();
		Matrix3f matrixnormal = matrixentry.getNormalMatrix();
		renderQuad(matrixpose, matrixnormal, builder, red, green, blue, alpha, height, radius_1, radius_2, radius_3, radius_4);
		renderQuad(matrixpose, matrixnormal, builder, red, green, blue, alpha, height, radius_7, radius_8, radius_5, radius_6);
		renderQuad(matrixpose, matrixnormal, builder, red, green, blue, alpha, height, radius_3, radius_4, radius_7, radius_8);
		renderQuad(matrixpose, matrixnormal, builder, red, green, blue, alpha, height, radius_5, radius_6, radius_1, radius_2);
	}

	private static void renderQuad(Matrix4f pose, Matrix3f normal, VertexConsumer builder, float red, float green, float blue, float alpha, float y, float z1, float texu1, float z, float texu) {
		addVertex(pose, normal, builder, red, green, blue, alpha, y, z1, texu1, 1f, 0f);
		addVertex(pose, normal, builder, red, green, blue, alpha, 0f, z1, texu1, 1f, 1f);
		addVertex(pose, normal, builder, red, green, blue, alpha, 0f, z, texu, 0f, 1f);
		addVertex(pose, normal, builder, red, green, blue, alpha, y, z, texu, 0f, 0f);
	}

	private static void addVertex(Matrix4f pose, Matrix3f normal, VertexConsumer builder, float red, float green, float blue, float alpha, float y, float x, float z, float texu, float texv) {
		builder.vertex(pose, x, y, z).color(red, green, blue, alpha).texture(texu, texv).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(normal, 0.0F, 1.0F, 0.0F).next();
	}

	private static String toBinaryName(String mapName){
		return "L" + mapName.replace('.', '/') + ";";
	}

	private static RenderLayer createRenderType() {
		RenderLayer.MultiPhaseParameters state = RenderLayer.MultiPhaseParameters.builder()
				.texture(new RenderPhase.Texture(LOOT_BEAM_TEXTURE, false, false))
				.lightmap(ENABLE_LIGHTMAP)
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.program(RenderLayer.TRANSLUCENT_PROGRAM)
				.overlay(RenderPhase.DISABLE_OVERLAY_COLOR)
				.depthTest(RenderPhase.LEQUAL_DEPTH_TEST)
				.writeMaskState(RenderLayer.COLOR_MASK).build(false);
		try {
			Method method = RenderLayer.class.getDeclaredMethod(
					FabricLoader.getInstance().getMappingResolver().mapMethodName("intermediary", "net.minecraft.class_1921", "method_24049",
							"(Ljava/lang/String;" + toBinaryName("net.minecraft.class_293")
									+ toBinaryName("net.minecraft.class_293$class_5596")
									+ "IZZ"
									+ toBinaryName("net.minecraft.class_1921$class_4688") + ")"
									+ toBinaryName("net.minecraft.class_1921$class_4687")),
					String.class, VertexFormat.class, VertexFormat.DrawMode.class, int.class, boolean.class, boolean.class, MultiPhaseParameters.class);
			method.setAccessible(true);
			return (RenderLayer) method.invoke(null, "loot_beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, true, state);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return RenderLayer.getEntityTranslucent(LOOT_BEAM_TEXTURE, false);
	}

	private static boolean isLookingAt(ClientPlayerEntity player, Entity target, double accuracy) {
		Vec3d difference = new Vec3d(target.getX() - player.getX(), target.getEyeY() - player.getEyeY(), target.getZ() - player.getZ());
		double length = difference.length();
		double dot = player.getRotationVec(1.0F).normalize().dotProduct(difference.normalize());
		return dot > 1.0D - accuracy / length && player.canSee(target);
	}

}