package dev.hephaestus.glowcase.packet;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.ConfigLinkBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record C2SEditConfigLinkBlock(BlockPos pos, String title, String url) implements C2SEditBlockEntity {
	public static final Id<C2SEditConfigLinkBlock> ID = new Id<>(Glowcase.id("channel.configlink.save"));
	public static final PacketCodec<RegistryByteBuf, C2SEditConfigLinkBlock> PACKET_CODEC = PacketCodec.tuple(
		BlockPos.PACKET_CODEC, C2SEditConfigLinkBlock::pos,
		PacketCodecs.STRING, C2SEditConfigLinkBlock::title,
		PacketCodecs.STRING, C2SEditConfigLinkBlock::url,
		C2SEditConfigLinkBlock::new
	);

	public static C2SEditConfigLinkBlock of(ConfigLinkBlockEntity be) {
		return new C2SEditConfigLinkBlock(be.getPos(), be.getTitle(), be.getUrl());
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	@Override
	public void receive(ServerWorld world, BlockEntity blockEntity) {
		if (!(blockEntity instanceof ConfigLinkBlockEntity be)) return;
		if (this.title().length() <= ConfigLinkBlockEntity.TITLE_MAX_LENGTH) {
			be.setTitle(this.title());
		}
		if (this.url().length() <= ConfigLinkBlockEntity.URL_MAX_LENGTH) {
			be.setUrl(this.url());
		}
	}
}
