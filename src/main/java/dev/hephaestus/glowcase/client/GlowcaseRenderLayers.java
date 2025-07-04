package dev.hephaestus.glowcase.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.hephaestus.glowcase.Glowcase;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class GlowcaseRenderLayers extends RenderLayer {
	public static RenderPhase.Layering GLOWCASE_POLYGON_OFFSET_LAYERING = new Layering("glowcase_polygon_offset_layering", () -> {
		/*RenderSystem.polygonOffset(-1, -1.0F);
		RenderSystem.enablePolygonOffset();*/
	}, () -> {
		/*RenderSystem.polygonOffset(0.0F, 0.0F);
		RenderSystem.disablePolygonOffset();*/
	});

	public static final Function<Boolean, RenderPipeline> SCREEN_PROGRAM = Util.memoize((culling) -> RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.TEXT_SNIPPET)
			.withLocation(Glowcase.id("pipeline/screen"))
			.withVertexShader("core/rendertype_text")
			.withFragmentShader("core/rendertype_text")
			.withCull(culling)
			.withSampler("Sampler0")
			.withSampler("Sampler2")
			.build()
	));

	public static final RenderPipeline TEXT_PLATE_PROGRAM = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET).withLocation(Glowcase.id("pipeline/text_plate")).withCull(false).build()
	);

	// Use a custom render layer to render the text plate - mimics DrawableHelper's RenderSystem call
	public static final RenderLayer TEXT_PLATE = RenderLayer.of("glowcase_text_plate",
		256,
		true,
		true,
		TEXT_PLATE_PROGRAM,
		RenderLayer.MultiPhaseParameters.builder().texture(NO_TEXTURE).build(false));


	private static final BiFunction<Identifier, Boolean, RenderLayer> SCREEN = Util.memoize((texture, culling) -> {
		return RenderLayer.of(
			"glowcase_screen",
			786432,
			false,
			true,
			SCREEN_PROGRAM.apply(culling),
			MultiPhaseParameters.builder()
			.texture(new Texture(texture, false))
			.lightmap(ENABLE_LIGHTMAP)
			.layering(GLOWCASE_POLYGON_OFFSET_LAYERING)
			.build(false));
	});

	public GlowcaseRenderLayers(String name, int size, boolean hasCrumbling, boolean translucent, Runnable begin, Runnable end) {
		super(name, size, hasCrumbling, translucent, begin, end);
	}

	public static RenderLayer getScreen(Identifier texture, boolean culling) {
		return SCREEN.apply(texture, culling);
	}
}
