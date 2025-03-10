package dev.hephaestus.glowcase.packet;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.ItemAcceptorBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record C2SEditItemAcceptorBlock(BlockPos pos, String item, int count, ItemAcceptorBlockEntity.InputType inputType, ItemAcceptorBlockEntity.OutputDirection outputDirection) implements C2SEditBlockEntity {
	public static final Id<C2SEditItemAcceptorBlock> ID = new Id<>(Glowcase.id("channel.item_acceptor.save"));
	public static final PacketCodec<RegistryByteBuf, C2SEditItemAcceptorBlock> PACKET_CODEC = PacketCodec.tuple(
		BlockPos.PACKET_CODEC, C2SEditItemAcceptorBlock::pos,
		PacketCodecs.STRING, C2SEditItemAcceptorBlock::item,
		PacketCodecs.INTEGER, C2SEditItemAcceptorBlock::count,
		PacketCodecs.indexed(i -> ItemAcceptorBlockEntity.InputType.values()[i], ItemAcceptorBlockEntity.InputType::ordinal), C2SEditItemAcceptorBlock::inputType,
		PacketCodecs.BYTE.xmap(index -> ItemAcceptorBlockEntity.OutputDirection.values()[index], outputDirection -> (byte) outputDirection.ordinal()), C2SEditItemAcceptorBlock::outputDirection,
		C2SEditItemAcceptorBlock::new
	);

	public static C2SEditItemAcceptorBlock of(ItemAcceptorBlockEntity be) {
		return new C2SEditItemAcceptorBlock(be.getPos(), be.getItem(), be.count, be.inputType, be.outputDirection);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	@Override
	public void receive(ServerWorld world, BlockEntity blockEntity) {
		if (!(blockEntity instanceof ItemAcceptorBlockEntity be)) return;

		be.setItem(this.item());
		be.count = this.count();
		be.inputType = this.inputType();
		be.outputDirection = this.outputDirection();

		be.markDirty();
	}

	@Override
	public boolean canEdit(ServerPlayerEntity player) {
		return C2SEditBlockEntity.super.canEdit(player) && (inputType != ItemAcceptorBlockEntity.InputType.COMMAND || player.hasPermissionLevel(2));
	}
}
