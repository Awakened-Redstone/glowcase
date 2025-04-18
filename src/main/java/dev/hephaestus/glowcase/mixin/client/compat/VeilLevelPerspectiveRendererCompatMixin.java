package dev.hephaestus.glowcase.mixin.client.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.hephaestus.glowcase.client.GlowcaseClient;
import foundry.veil.impl.client.render.dynamicbuffer.DynamicBufferShard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo // Maybe replace this with a mixin plugin
@Environment(EnvType.CLIENT)
@Mixin(value = DynamicBufferShard.class, remap = false)
public class VeilLevelPerspectiveRendererCompatMixin {
	@WrapOperation(at = @At(value = "INVOKE", target = "Lfoundry/veil/api/client/render/VeilLevelPerspectiveRenderer;isRenderingPerspective()Z"), method = "lambda$new$0")
	private static boolean stopVeilFromBreakingEmiBlockRendering(Operation<Boolean> original) {
		if (!GlowcaseClient.PREVENT_VEIL_DYNAMIC_BUFFER.empty()) {
			return true;
		}

		return original.call();
	}
}
