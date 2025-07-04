package dev.hephaestus.glowcase.block.entity;

import dev.hephaestus.glowcase.util.DisplayBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3f;

import java.util.Optional;

public abstract class DisplayBlockEntity extends GlowcaseBlockEntity {
	private Vector3f offset = new Vector3f(0.0F);
	private Vector3f scale = new Vector3f(1.0F);
	private float pitch = 0.0F;
	private float yaw = 0.0F;
	private boolean renderAsBlock = false;

	public DisplayBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public DisplayBlockSettings toSettings() {
		return new DisplayBlockSettings(
			new Vector3f(offset.x(), offset.y(), offset.z()),
			new Vector3f(scale.x(), scale.y(), scale.z()),
			pitch,
			yaw,
			renderAsBlock
		);
	}

	public void loadSettings(DisplayBlockSettings settings) {
		this.offset.set(settings.offset().x(), settings.offset().y(), settings.offset().z());
		this.scale.set(settings.scale().x(), settings.scale().y(), settings.scale().z());
		this.pitch = settings.pitch();
		this.yaw = settings.yaw();
		this.renderAsBlock = settings.renderAsBlock();
		markDirty();
	}

	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);
		DisplayBlockSettings settings = toSettings();
		if (!settings.isEmpty()) view.put("display", DisplayBlockSettings.CODEC, settings);
	}

	@Override
	protected void readData(ReadView view) {
		super.readData(view);
		Optional<DisplayBlockSettings> settings = view.read("display", DisplayBlockSettings.CODEC);
		loadSettings(settings.orElseGet(DisplayBlockSettings::new));
	}

	public Vector3f getOffset() {
		return offset;
	}

	public Vector3f getScale() {
		return scale;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setOffset(Vector3f offset) {
		this.offset = offset;
		markDirty();
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
		markDirty();
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
		markDirty();
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
		markDirty();
	}

	public boolean getRenderAsBlock() {
		return renderAsBlock;
	}

	public void setRenderAsBlock(boolean renderAsBlock) {
		this.renderAsBlock = renderAsBlock;
		markDirty();
	}
}
