package dev.hephaestus.glowcase.block.entity;

import com.mojang.serialization.Codec;
import dev.hephaestus.glowcase.Glowcase;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ItemAcceptorBlockEntity extends GlowcaseBlockEntity {
	private Identifier item = Identifier.ofVanilla("air");
	public int count = 1;
	public int pulse = 4;
	public OutputDirection outputDirection = OutputDirection.BACK;
	public boolean isItemTag = false;
	private List<Item> itemTagList = List.of();

	public ItemAcceptorBlockEntity(BlockPos pos, BlockState state) {
		super(Glowcase.ITEM_ACCEPTOR_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);

		view.put("item", Identifier.CODEC, this.item);
		view.putInt("count", this.count);
		view.putInt("pulse", this.pulse);
		view.putBoolean("is_item_tag", this.isItemTag);
		view.put("output_direction", OutputDirection.CODEC, this.outputDirection);
	}

	@Override
	protected void readData(ReadView view) {
		super.readData(view);

		this.setItem(view.read("item", Identifier.CODEC).orElse(Identifier.ofVanilla("air")));
		this.count = view.getInt("count", 1);
		this.pulse = view.getInt("pulse", 4);
		this.isItemTag = view.getBoolean("is_item_tag", false);
		this.outputDirection = view.read("output_direction", OutputDirection.CODEC).orElse(OutputDirection.BACK);
	}

	public Identifier getItem() {
		return item;
	}

	public void setItem(Identifier item) {
		if (item == null) {
			return;
		}

		this.item = item;

		TagKey<Item> itemTag = TagKey.of(RegistryKeys.ITEM, item);
		itemTagList = Registries.ITEM.stream().filter(it -> it.getDefaultStack().isIn(itemTag)).toList();
	}

	public ItemStack getDisplayItemStack() {
		if (isItemTag) {
			if (itemTagList.isEmpty()) {
				return ItemStack.EMPTY;
			}

			return itemTagList.get((int) (Util.getMeasuringTimeMs() / 1000f) % itemTagList.size()).getDefaultStack();
		} else {
			return Registries.ITEM.get(item).getDefaultStack();
		}
	}

	public boolean isItemAccepted(ItemStack stack) {
		boolean isEqual = isItemTag
			? stack.isIn(TagKey.of(RegistryKeys.ITEM, item))
			: stack.isOf(Registries.ITEM.get(item));

		return isEqual && stack.getCount() >= count;
	}

	public int getPulse() {
		return pulse;
	}

	public enum OutputDirection implements StringIdentifiable {
		TOP, BACK, BOTTOM;

		public static final Codec<OutputDirection> CODEC = StringIdentifiable.createCodec(OutputDirection::values);

		@Override
		public String asString() {
			return name().toLowerCase(Locale.ROOT);
		}
	}
}
