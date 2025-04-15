package dev.hephaestus.glowcase.util;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.widget.RecipeBackground;
import dev.hephaestus.glowcase.Glowcase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector2i;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class EmiClientUtils {
	private static final BufferBuilderStorage SORRY = new BufferBuilderStorage(1);
	private static final Map<Vector2i, CachedBuffer> BACKGROUND_CACHE = new ConcurrentHashMap<>();
	private static final Map<EmiRecipe, CachedBuffer> FRAMEBUFFER_CACHE = new ConcurrentHashMap<>();

	private static final Map<EmiRecipe, GlowcaseWidgetHolder> HOLDER_CACHE = new ConcurrentHashMap<>();
	private static final Map<Identifier, EmiRecipe> RECIPE_CACHE = new ConcurrentHashMap<>();
	private static final boolean GET_ERROR = MinecraftClient.IS_SYSTEM_MAC;

	// a container to hold the buffer
	private static class CachedBuffer {
        public final Framebuffer framebuffer;
        private volatile boolean dirty;
		public volatile long expire = System.currentTimeMillis() + 33;

        public CachedBuffer(Framebuffer framebuffer, boolean dirty) {
            this.framebuffer = framebuffer;
            this.dirty = dirty;
        }

		public boolean isDirty() {
			return dirty || System.currentTimeMillis() >= expire;
		}

		public void setDirty(boolean dirty) {
			this.dirty = dirty;

			if (!dirty) {
				expire = System.currentTimeMillis() + 33;
			}
		}
	}

	public static void disposeCache() {
        for (CachedBuffer cached : FRAMEBUFFER_CACHE.values()) {
            try {
                cached.framebuffer.delete();
            } catch (Exception e) {
                Glowcase.LOGGER.error("Error disposing: " + e.getMessage());
            }
        }

        FRAMEBUFFER_CACHE.clear();
    }

	public static void markAllDirty() {
        for (CachedBuffer cached : FRAMEBUFFER_CACHE.values()) {
            cached.setDirty(true);
        }
    }

	public static void displayRecipe(Identifier recipeId) {
		if (recipeId == null) {
			return;
		}
		EmiRecipe recipe = EmiApi.getRecipeManager().getRecipe(recipeId);
		if (recipe == null) {
			return;
		}
		EmiApi.displayRecipe(recipe);
	}

	public static boolean renderRecipe(MatrixStack matrices, String recipeString, BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		EmiRecipe recipe = getRecipeToDisplay(recipeString, pos);
		if (recipe == null) {
			return false;
		}

		int fullWidth = recipe.getDisplayWidth() + 8;
		int fullHeight = recipe.getDisplayHeight() + 8;

        try {
			// getEffectVertexConsumers doesn't cause random rendering issues like getEntityVertexConsumers
			// getEffectVertexConsumers caused random rendering issues, is the comment above inverted? - Awakened Redstone
			DrawContext context = new DrawContext(client, SORRY.getEntityVertexConsumers());

			Framebuffer background = createBackground(recipe, context);
			renderFramebuffer(background, matrices, fullWidth, fullHeight);

			Framebuffer foreground = createFramebuffer(recipe, context);
			renderFramebuffer(foreground, matrices, fullWidth, fullHeight);
        } catch (Exception e) {
            Glowcase.LOGGER.error("Error rendering framebuffer!", e);
        } finally {
            MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
        }
		
        return true;
	}

	public static EmiRecipe getRecipeToDisplay(String recipeString, BlockPos pos) {
		Identifier rid = Identifier.tryParse(recipeString);

		if (rid != null) {
			EmiRecipe recipe = RECIPE_CACHE.computeIfAbsent(rid, identifier -> EmiApi.getRecipeManager().getRecipe(rid));

			if (recipe != null) return recipe;
		}

		EmiRecipe recipe;

		// TODO: Improve; Replace with list field entry instead of single string
		if (recipeString.startsWith("xyzzy")) {
			String[] parts = recipeString.split(" ");
			List<EmiRecipe> recipes = EmiApi.getRecipeManager().getRecipes();
			if (parts.length == 2) {
				for (EmiRecipeCategory category : EmiApi.getRecipeManager().getCategories()) {
					if (category.getId().toString().equals(parts[1])) {
						recipes = EmiApi.getRecipeManager().getRecipes(category);
						break;
					}
				}
			}
			int c = (int) (pos.hashCode() ^ (System.currentTimeMillis() / 769));
			if (recipes.isEmpty()) {
				return null;
			}
			recipe = recipes.get(new Random(c).nextInt(recipes.size()));
		} else {
			return null;
		}

		return recipe;
	}

	private static void renderFramebuffer(Framebuffer framebuffer, MatrixStack matrixStack, int fullWidth, int fullHeight) {
		framebuffer.beginRead();

		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
		RenderSystem.setShaderTexture(0, framebuffer.getColorAttachment());
		RenderSystem.enableDepthTest();

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder builder = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		MatrixStack.Entry entry = matrixStack.peek();
		float xMin = -0.15f / 16 * fullWidth;
		float xMax = 0.15f / 16 * fullWidth;
		float yMin = -0.15f / 16 * fullHeight;
		float yMax = 0.15f / 16 * fullHeight;

		builder.vertex(entry, xMin, yMax, 0).color(255, 255, 255, 255).texture(1, 1);
		builder.vertex(entry, xMax, yMax, 0).color(255, 255, 255, 255).texture(0, 1);
		builder.vertex(entry, xMax, yMin, 0).color(255, 255, 255, 255).texture(0, 0);
		builder.vertex(entry, xMin, yMin, 0).color(255, 255, 255, 255).texture(1, 0);

		BufferRenderer.drawWithGlobalProgram(builder.end());

		framebuffer.endRead();
	}

	private static Framebuffer createBackground(EmiRecipe recipe, DrawContext context) {
		int width = recipe.getDisplayWidth() + 8;
		int height = recipe.getDisplayHeight() + 8;

		return BACKGROUND_CACHE.computeIfAbsent(new Vector2i(recipe.getDisplayWidth(), recipe.getDisplayHeight()), ignored -> {
			SimpleFramebuffer framebuffer = new SimpleFramebuffer(width, height, true, GET_ERROR);

			Matrix4fStack view = RenderSystem.getModelViewStack();
			view.pushMatrix();
			view.identity();
			view.scale(2f / width, -2f / height, -1f / 1000f);
			view.translate(-width / 2f, -height / 2f, 0.0f);
			view.translate(0.0f, 0.0f, 10.0f);
			RenderSystem.applyModelViewMatrix();

			float originalFogEnd = RenderSystem.getShaderFogEnd();
			RenderSystem.setShaderFogEnd(Float.MAX_VALUE);

			Matrix4f backupProj = RenderSystem.getProjectionMatrix();
			RenderSystem.setProjectionMatrix(new Matrix4f().identity(), VertexSorter.BY_Z);

			RecipeBackground background = new RecipeBackground(0, 0, width, height);
			framebuffer.beginWrite(true);

			background.render(context, -9999, -9999, 0);

			// Magic incantation/desperate prayer
			RenderSystem.enableDepthTest();
			RenderSystem.disableBlend();
			RenderSystem.enableCull();
			RenderSystem.setShaderColor(1, 1, 1, 1);
			DiffuseLighting.enableForLevel();

			RenderSystem.setProjectionMatrix(backupProj, VertexSorter.BY_DISTANCE);
			view.popMatrix();
			RenderSystem.applyModelViewMatrix();
			SORRY.getEntityVertexConsumers().draw();

			framebuffer.endWrite();
			RenderSystem.setShaderFogEnd(originalFogEnd);

			return new CachedBuffer(framebuffer, false);
		}).framebuffer;
	}

	private static Framebuffer createFramebuffer(EmiRecipe recipe, DrawContext context) {
		MinecraftClient client = MinecraftClient.getInstance();

		int width = recipe.getDisplayWidth() + 8;
		int height = recipe.getDisplayHeight() + 8;
		int scale = 4;

		CachedBuffer cached = FRAMEBUFFER_CACHE.get(recipe);
        if (cached == null) {
            Framebuffer fb = new SimpleFramebuffer(width * scale, height * scale, true, MinecraftClient.IS_SYSTEM_MAC);
            fb.setClearColor(0f, 0f, 0f, 0f);

            cached = new CachedBuffer(fb, true);
            FRAMEBUFFER_CACHE.put(recipe, cached);
        }

		// rerender every frame so dont
		 if (!cached.isDirty()) {
             return cached.framebuffer;
         }

		Framebuffer framebuffer = cached.framebuffer;

		try {
			framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
			framebuffer.beginWrite(true);

			Matrix4fStack view = RenderSystem.getModelViewStack();
			view.pushMatrix();
			view.identity();
			view.translate(-1.0f, 1.0f, 0.0f);
			view.scale(2f / width, -2f / height, -1f / 1000f);
			view.translate(0.0f, 0.0f, 10.0f);
			RenderSystem.applyModelViewMatrix();

			float originalFogEnd = RenderSystem.getShaderFogEnd();
			RenderSystem.setShaderFogEnd(Float.MAX_VALUE);

			Matrix4f backupProj = RenderSystem.getProjectionMatrix();
			RenderSystem.setProjectionMatrix(new Matrix4f().identity(), VertexSorter.BY_Z);

			// Cache the holder so it doesn't create several new classes every frame, just for the sake of it
			// TODO: Clear on EMI reload
			GlowcaseWidgetHolder holder = HOLDER_CACHE.computeIfAbsent(recipe, emiRecipe -> {
				GlowcaseWidgetHolder widgetHolder = new GlowcaseWidgetHolder(recipe.getDisplayWidth(), recipe.getDisplayHeight());
				//widgetHolder.widgets.add(new RecipeBackground(-4, -4, recipe.getDisplayWidth() + 8, recipe.getDisplayHeight() + 8));
				recipe.addWidgets(widgetHolder);
				return widgetHolder;
			});

			context.getMatrices().translate(4, 4, 0);

			//Widget widget = holder.widgets.get(Math.min(2, holder.widgets.size()));
			//widget.render(context, -9999, -9999, 0);

			for (Widget widget : holder.widgets) {
				widget.render(context, -9999, -9999, 0);
			}

			// Magic incantation/desperate prayer
			RenderSystem.enableDepthTest();
			RenderSystem.disableBlend();
			RenderSystem.enableCull();
			RenderSystem.setShaderColor(1, 1, 1, 1);
			// Using disableForLevel fixes a severe light flicker issue
			DiffuseLighting.disableForLevel();

			RenderSystem.setProjectionMatrix(backupProj, VertexSorter.BY_DISTANCE);
			view.popMatrix();
			RenderSystem.applyModelViewMatrix();
			SORRY.getEntityVertexConsumers().draw();

			framebuffer.endWrite();
			RenderSystem.setShaderFogEnd(originalFogEnd);
			client.getFramebuffer().beginWrite(true);

			//cached.dirty = true;
			 cached.setDirty(false);
		} catch (Exception e) {
			Glowcase.LOGGER.error("Error during framebuffer creation!", e);

			// if an error occurs during framebuffer creation, mark the cache as dirty to refresh
			cached.setDirty(true);
		}

		return framebuffer;
	}

	private static class GlowcaseWidgetHolder implements WidgetHolder {
		private final int width, height;
		public final List<Widget> widgets = Lists.newArrayList();

		public GlowcaseWidgetHolder(int width, int height) {
			this.width = width;
			this.height = height;
		}

		@Override
		public int getWidth() {
			return width;
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public <T extends Widget> T add(T widget) {
			widgets.add(widget);
			return widget;
		}
	}
}
