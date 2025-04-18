package dev.hephaestus.glowcase.client.util;

import com.terraformersmc.modmenu.ModMenu;
import dev.hephaestus.glowcase.util.ModSupportUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author Ampflower
 **/
@Environment(EnvType.CLIENT)
public final class ConfigLinkClientUtil {
	private static final boolean modmenuAvailable = FabricLoader.getInstance().isModLoaded("modmenu");

	private static final Text glowcase = Text.translatable("block.glowcase.config_link_block");
	private static final Text missingModmenu = Text.translatable("gui.glowcase.config_link.missing.modmenu");

	public static Screen getConfigScreen(MinecraftClient client, String link) {
		Screen screen = getModScreen(client, link);

		if (screen != null) {
			return screen;
		}

		return notImplemented(client, link);
	}

	@Nullable
	public static Screen getModScreen(MinecraftClient client, String link) {
		String id = ModSupportUtil.getModId(link);

		if (id == null) {
			return null;
		}

		if (!modmenuAvailable) {
			return modmenuUnavailable(client);
		}

		Screen screen = ModMenu.getConfigScreen(id, null);

		if (screen != null) {
			return screen;
		}

		Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer(id);

		return mod
			.map(modContainer -> modScreenUnavailable(client, modContainer.getMetadata().getName()))
			.orElseGet(() -> modUnavailable(client, id));
	}

	private static Screen modScreenUnavailable(MinecraftClient client, String modName) {
		return notice(client, glowcase, Text.translatable("gui.glowcase.config_link.missing.mod_screen", modName));
	}

	private static Screen modUnavailable(MinecraftClient client, String modId) {
		return notice(client, glowcase, Text.translatable("gui.glowcase.config_link.missing.mod", modId));
	}

	private static Screen modmenuUnavailable(MinecraftClient client) {
		return notice(client, glowcase, missingModmenu);
	}

	private static Screen notImplemented(MinecraftClient client, String link) {
		return notice(client, glowcase, Text.translatable("gui.glowcase.config_link.missing.link", link));
	}

	private static Screen notice(MinecraftClient client, Text title, Text notice) {
		return new NoticeScreen(() -> client.setScreen(null), title, notice, ScreenTexts.OK, true);
	}
}
