package dev.hephaestus.glowcase.client.render.block.entity;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.OutlineBlockEntity;
import dev.hephaestus.glowcase.client.util.BlockEntityRenderUtil;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShapes;

public record OutlineBlockEntityRenderer(BlockEntityRendererFactory.Context context) implements BlockEntityRenderer<OutlineBlockEntity> {
	public static Identifier ITEM_TEXTURE = Glowcase.id("textures/item/outline_block.png");

	public void render(OutlineBlockEntity entity, float f, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
		if (entity.getWorld() == null || entity.getWorld().getBlockState(entity.getPos()).isAir()) return;
		double x = entity.offset.getX();
		double y = entity.offset.getY();
		double z = entity.offset.getZ();
		double width = entity.scale.getX();
		double height = entity.scale.getY();
		double depth = entity.scale.getZ();

		VertexRendering.drawOutline(
			matrices, vertexConsumers.getBuffer(RenderLayer.getLines()),
			VoxelShapes.cuboid(x, y, z, x + width, y + height, z + depth),
			0, 0, 0, entity.color | 0xFF000000
		);

		if (entity.scale.equals(Vec3i.ZERO) || BlockEntityRenderUtil.shouldRenderPlaceholder(entity.getPos())) BlockEntityRenderUtil.renderBillboardPlaceholder(entity, ITEM_TEXTURE, 1.0F, matrices, vertexConsumers, context.getRenderDispatcher().camera);
	}
}
