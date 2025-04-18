package dev.hephaestus.glowcase.client;

import dev.hephaestus.glowcase.GlowcaseCommonProxy;
import dev.hephaestus.glowcase.block.entity.ConfigLinkBlockEntity;
import dev.hephaestus.glowcase.block.entity.EntityDisplayBlockEntity;
import dev.hephaestus.glowcase.block.entity.HyperlinkBlockEntity;
import dev.hephaestus.glowcase.block.entity.ItemAcceptorBlockEntity;
import dev.hephaestus.glowcase.block.entity.ItemDisplayBlockEntity;
import dev.hephaestus.glowcase.block.entity.ItemProviderBlockEntity;
import dev.hephaestus.glowcase.block.entity.OutlineBlockEntity;
import dev.hephaestus.glowcase.block.entity.ParticleDisplayBlockEntity;
import dev.hephaestus.glowcase.block.entity.PopupBlockEntity;
import dev.hephaestus.glowcase.block.entity.RecipeBlockEntity;
import dev.hephaestus.glowcase.block.entity.ScreenBlockEntity;
import dev.hephaestus.glowcase.block.entity.SoundPlayerBlockEntity;
import dev.hephaestus.glowcase.block.entity.SpriteBlockEntity;
import dev.hephaestus.glowcase.block.entity.TextBlockEntity;
import dev.hephaestus.glowcase.client.gui.screen.ingame.ConfigLinkBlockEditScreen;
import dev.hephaestus.glowcase.client.gui.screen.ingame.EntityDisplayEditScreen;
import dev.hephaestus.glowcase.client.gui.screen.ingame.HyperlinkBlockEditScreen;
import dev.hephaestus.glowcase.client.gui.screen.ingame.ItemAcceptorBlockEditScreen;
import dev.hephaestus.glowcase.client.gui.screen.ingame.ItemDisplayEditScreen;
import dev.hephaestus.glowcase.client.gui.screen.ingame.ItemProviderBlockEditScreen;
import dev.hephaestus.glowcase.client.gui.screen.ingame.NoteEditScreen;
import dev.hephaestus.glowcase.client.gui.screen.ingame.OutlineBlockEditScreen;
import dev.hephaestus.glowcase.client.gui.screen.ingame.ParticleDisplayEditScreen;
import dev.hephaestus.glowcase.client.gui.screen.ingame.PopupBlockEditScreen;
import dev.hephaestus.glowcase.client.gui.screen.ingame.PopupBlockViewScreen;
import dev.hephaestus.glowcase.client.gui.screen.ingame.RecipeBlockEditScreen;
import dev.hephaestus.glowcase.client.gui.screen.ingame.ScreenBlockEditScreen;
import dev.hephaestus.glowcase.client.gui.screen.ingame.SoundPlayerBlockEditScreen;
import dev.hephaestus.glowcase.client.gui.screen.ingame.SpriteBlockEditScreen;
import dev.hephaestus.glowcase.client.gui.screen.ingame.TabletEditScreen;
import dev.hephaestus.glowcase.client.gui.screen.ingame.TextBlockEditScreen;
import dev.hephaestus.glowcase.client.util.ConfigLinkClientUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class GlowcaseClientProxy extends GlowcaseCommonProxy {

	@Override
	public void openConfigLinkBlockEditScreen(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null && client.world.getBlockEntity(pos) instanceof ConfigLinkBlockEntity be) {
			MinecraftClient.getInstance().setScreen(new ConfigLinkBlockEditScreen(be));
		}
	}

	@Override
	public void openConfigScreen(String link) {
		MinecraftClient client = MinecraftClient.getInstance();
		client.setScreen(ConfigLinkClientUtil.getConfigScreen(client, link));
	}

	@Override
	public void openHyperlinkBlockEditScreen(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null && client.world.getBlockEntity(pos) instanceof HyperlinkBlockEntity be) {
			MinecraftClient.getInstance().setScreen(new HyperlinkBlockEditScreen(be));
		}
	}

	@Override
	public void openUrlWithConfirmation(String url) {
		ConfirmLinkScreen.open(MinecraftClient.getInstance().currentScreen, url);
	}

	@Override
	public void openItemDisplayBlockEditScreen(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null && client.world.getBlockEntity(pos) instanceof ItemDisplayBlockEntity be) {
			MinecraftClient.getInstance().setScreen(new ItemDisplayEditScreen(be));
		}
	}

	@Override
	public void openItemProviderBlockEditScreen(BlockPos pos){
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null && client.world.getBlockEntity(pos) instanceof ItemProviderBlockEntity be) {
			MinecraftClient.getInstance().setScreen(new ItemProviderBlockEditScreen(be));
		}
	}

	@Override
	public void openTextBlockEditScreen(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null && client.world.getBlockEntity(pos) instanceof TextBlockEntity be) {
			MinecraftClient.getInstance().setScreen(new TextBlockEditScreen(be));
		}
	}

	@Override
	public void openPopupBlockEditScreen(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null && client.world.getBlockEntity(pos) instanceof PopupBlockEntity be) {
			MinecraftClient.getInstance().setScreen(new PopupBlockEditScreen(be));
		}
	}

	@Override
	public void openPopupBlockViewScreen(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null && client.world.getBlockEntity(pos) instanceof PopupBlockEntity be) {
			MinecraftClient.getInstance().setScreen(new PopupBlockViewScreen(be));
		}
	}

	@Override
	public void openScreenBlockEditScreen(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null && client.world.getBlockEntity(pos) instanceof ScreenBlockEntity be) {
			MinecraftClient.getInstance().setScreen(new ScreenBlockEditScreen(be));
		}
	}

	@Override
	public void openRecipeBlockEditScreen(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null && client.world.getBlockEntity(pos) instanceof RecipeBlockEntity be) {
			MinecraftClient.getInstance().setScreen(new RecipeBlockEditScreen(be));
		}
	}

	@Override
	public void openSpriteBlockEditScreen(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null && client.world.getBlockEntity(pos) instanceof SpriteBlockEntity be) {
			MinecraftClient.getInstance().setScreen(new SpriteBlockEditScreen(be));
		}
	}

	@Override
	public void openOutlineBlockEditScreen(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null && client.world.getBlockEntity(pos) instanceof OutlineBlockEntity be) {
			MinecraftClient.getInstance().setScreen(new OutlineBlockEditScreen(be));
		}
	}

	@Override
	public void openParticleDisplayBlockEditScreen(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null && client.world.getBlockEntity(pos) instanceof ParticleDisplayBlockEntity be) {
			MinecraftClient.getInstance().setScreen(new ParticleDisplayEditScreen(be));
		}
	}

	@Override
	public void openSoundBlockEditScreen(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null && client.world.getBlockEntity(pos) instanceof SoundPlayerBlockEntity be) {
			MinecraftClient.getInstance().setScreen(new SoundPlayerBlockEditScreen(be));
		}
	}

	@Override
	public void openItemAcceptorBlockEditScreen(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null && client.world.getBlockEntity(pos) instanceof ItemAcceptorBlockEntity be) {
			MinecraftClient.getInstance().setScreen(new ItemAcceptorBlockEditScreen(be));
		}
	}

	@Override
	public void openTabletEditScreen(ItemStack stack) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null) {
			MinecraftClient.getInstance().setScreen(new TabletEditScreen(stack));
		}
	}

	@Override
	public void openNoteEditScreen(ItemStack stack) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null) {
			MinecraftClient.getInstance().setScreen(new NoteEditScreen(stack));
		}
	}

	@Override
	public void openEntityDisplayBlockEditScreen(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null && client.world.getBlockEntity(pos) instanceof EntityDisplayBlockEntity be) {
			MinecraftClient.getInstance().setScreen(new EntityDisplayEditScreen(be));
		}
	}
}
