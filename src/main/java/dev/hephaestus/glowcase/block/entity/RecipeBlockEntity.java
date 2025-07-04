package dev.hephaestus.glowcase.block.entity;

import dev.hephaestus.glowcase.Glowcase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class RecipeBlockEntity extends GlowcaseBlockEntity {
	public String recipe = "diamond_sword";
	public TextBlockEntity.ZOffset zOffset = TextBlockEntity.ZOffset.CENTER;

	//TODO: maybe move XYZ rotation to nbt? or use vec2f
	public float rotationX = 0f;
	public float rotationY = 0f;

	public RecipeBlockEntity(BlockPos pos, BlockState state) {
		super(Glowcase.RECIPE_BLOCK_ENTITY.get(), pos, state);
	}

	@Environment(EnvType.CLIENT)
	public void openRecipe() {
		Identifier rid = Identifier.tryParse(recipe);
		/*if (GlowcaseClient.EMI_LOADED) {
			EmiClientUtils.displayRecipe(rid);
		}*/
	}

	public void setRecipe(String newRecipe) {
		recipe = newRecipe;
		markDirty();
	}

	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);

		view.putString("recipe", this.recipe);
		view.put("z_offset", TextBlockEntity.ZOffset.CODEC, this.zOffset);
		view.putFloat("rotationX", this.rotationX);
		view.putFloat("rotationY", this.rotationY);
	}

	@Override
	protected void readComponents(ComponentsAccess components) {
		super.readComponents(components);
	}

	@Override
	protected void readData(ReadView view) {
		super.readData(view);

		this.recipe = view.getString("recipe", "diamond_sword");
		this.zOffset = view.read("z_offset", TextBlockEntity.ZOffset.CODEC).orElse(TextBlockEntity.ZOffset.CENTER);
		this.rotationX = view.getFloat("rotationX", 0);
		this.rotationY = view.getFloat("rotationY", 0);
	}
}
