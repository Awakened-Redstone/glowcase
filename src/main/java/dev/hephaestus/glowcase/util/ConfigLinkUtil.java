package dev.hephaestus.glowcase.util;

import net.fabricmc.loader.api.FabricLoader;

/**
 * @author Ampflower
 **/
public final class ConfigLinkUtil {

	public static String getModName(String link) {
		String id = ModSupportUtil.getModId(link);

		if (id == null) {
			return link;
		}

		return FabricLoader.getInstance()
			.getModContainer(id)
			.map(modContainer -> modContainer.getMetadata().getName())
			.orElse(id);
	}
}
