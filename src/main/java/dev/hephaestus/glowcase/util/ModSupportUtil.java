package dev.hephaestus.glowcase.util;

/**
 * @author Ampflower
 **/
public final class ModSupportUtil {
	public static final String MOD_NS = "glowcase:mod/";

	private static final int MOD_ID_START = MOD_NS.length();

	public static String getModId(String link) {
		if (link.startsWith(MOD_NS)) {
			return link.substring(MOD_ID_START);
		}

		return null;
	}
}
