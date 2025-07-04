package dev.hephaestus.glowcase.block.entity;

import com.mojang.logging.LogUtils;
import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.util.DeviatedInteger;
import dev.hephaestus.glowcase.util.DeviatedVec3d;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class ParticleDisplayBlockEntity extends GlowcaseBlockEntity {
	private static final Logger LOGGER = LogUtils.getLogger();

	public ParticleEffect particle = ParticleTypes.FLAME;
	public DeviatedVec3d position = DeviatedVec3d.ZERO;
	public DeviatedVec3d velocity = DeviatedVec3d.ZERO;
	public DeviatedInteger count = DeviatedInteger.ZERO;
	public DeviatedInteger tickRate = DeviatedInteger.ZERO;

	private int tickCounter = 0;

	public ParticleDisplayBlockEntity(BlockPos pos, BlockState state) {
		super(Glowcase.PARTICLE_DISPLAY_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);

		view.put("particle", ParticleTypes.TYPE_CODEC, this.particle);
		view.put("position", DeviatedVec3d.CODEC, this.position);
		view.put("velocity", DeviatedVec3d.CODEC, this.velocity);
		view.put("count", DeviatedInteger.CODEC, this.count);
		view.put("tick_rate", DeviatedInteger.CODEC, this.tickRate);
	}

	@Override
	protected void readData(ReadView view) {
		super.readData(view);

		this.particle = view.read("particle", ParticleTypes.TYPE_CODEC).orElse(ParticleTypes.FLAME);
		this.position = view.read("position", DeviatedVec3d.CODEC).orElse(DeviatedVec3d.ZERO);
		this.velocity = view.read("velocity", DeviatedVec3d.CODEC).orElse(DeviatedVec3d.ZERO);
		this.count = view.read("count", DeviatedInteger.CODEC).orElse(DeviatedInteger.ZERO);
		this.tickRate = view.read("tick_rate", DeviatedInteger.CODEC).orElse(DeviatedInteger.ZERO);
	}

	@Environment(EnvType.CLIENT)
	public static void clientTick(World world, BlockPos pos, BlockState state, ParticleDisplayBlockEntity entity) {
		entity.tickCounter--;
		if (entity.tickCounter > 0) return;

		entity.tickCounter = entity.tickRate.get(world.random::nextDouble);
		for (int i = 0; i < entity.count.get(world.random::nextDouble); i++) {
			Vec3d particlePos = entity.position.get(world.random::nextGaussian).add(pos.toCenterPos());
			Vec3d particleVelocity = entity.velocity.get(world.random::nextGaussian);

			world.addParticleClient(
				entity.particle,
				particlePos.x, particlePos.y, particlePos.z,
				particleVelocity.x, particleVelocity.y, particleVelocity.z
			);
		}
	}
}
