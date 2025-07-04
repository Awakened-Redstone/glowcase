package dev.hephaestus.glowcase.block.entity;

import com.mojang.serialization.Codec;
import dev.hephaestus.glowcase.Glowcase;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class ItemProviderBlockEntity extends GlowcaseBlockEntity implements InfiniteInventory, StackInteractable {
	protected ItemStack stack = ItemStack.EMPTY;
	protected GivesItem givesItem = GivesItem.ALWAYS;
	protected boolean invisible = false;
	public long cooldown = 0;
	protected Map<UUID, Long> givenTimes = new HashMap<>();

	public ItemProviderBlockEntity(BlockPos pos, BlockState state) {
		super(Glowcase.ITEM_PROVIDER_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public boolean matchesStack(ItemStack stack) {
		return ItemStack.areItemsEqual(this.stack, stack);
	}

	@Override
	public void setFromStack(ItemStack stack) {
		this.stack = stack.copy();
		this.givenTimes.clear();
		this.markDirty();
	}

	@Override
	public void unsetFromStack() {
		this.stack = ItemStack.EMPTY;
		this.markDirty();
	}

	@Override
	public ItemStack getStack() {
		return stack;
	}

	public GivesItem getGivesItem() {
		return givesItem;
	}

	public boolean isInvisible() {
		return this.invisible;
	}

	public void setGivesItem(GivesItem givesItem) {
		this.givesItem = givesItem;
		markDirty();
	}

	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);

		if (!this.stack.isEmpty()) view.put("item", ItemStack.CODEC, this.stack);
		view.put("gives_item", GivesItem.CODEC, this.givesItem);
		view.putLong("cooldown", this.cooldown);
		view.put("given_times", Codec.unboundedMap(Uuids.CODEC, Codec.LONG), givenTimes);

		view.putBoolean("invisible", this.invisible);
	}

	@Override
	protected void readData(ReadView view) {
		super.readData(view);

		this.stack = view.read("item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
		this.givesItem = view.read("gives_item", GivesItem.CODEC).orElse(GivesItem.ALWAYS);
		this.cooldown = view.getLong("cooldown", 0);
		this.givenTimes = new HashMap<>(view.read("given_times", Codec.unboundedMap(Uuids.CODEC, Codec.LONG)).orElseGet(() -> Map.of()));
		this.invisible = view.getBoolean("invisible", false);
	}

	public void cycleGiveType() {
		this.givesItem = GivesItem.values()[(this.givesItem.ordinal() + 1) % GivesItem.values().length];
		givenTimes.clear();
		markDirty();
	}

	public long getCooldownTicks(PlayerEntity player) {
		return givenTimes.containsKey(player.getUuid()) ? givenTimes.get(player.getUuid()) + this.cooldown * 20 - world.getTime() : 0;
	}

	public boolean canGiveTo(PlayerEntity player) {
		if (!hasItem()) return false;
		else return switch (this.givesItem) {
			case ALWAYS -> true;
			case TIMED -> player.isCreative() || getCooldownTicks(player) <= 0;
			case ONE -> player.isCreative() || !player.getInventory().containsAny(Set.of(stack.getItem()));
		};
	}

	public void giveTo(PlayerEntity player) {
		ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);
		boolean holdingSameAsDisplay = ItemStack.areItemsAndComponentsEqual(getStack(), itemStack);

		if (itemStack.isEmpty()) {
			player.setStackInHand(Hand.MAIN_HAND, getStack().copy());
		} else if (holdingSameAsDisplay) {
			itemStack.increment(getStack().getCount());
			itemStack.capCount(itemStack.getMaxCount());
			player.setStackInHand(Hand.MAIN_HAND, itemStack);
		} else {
			return;
		}

		if (!player.isCreative()) {
			givenTimes.put(player.getUuid(), world.getTime());
			markDirty();
		}
	}

	public enum GivesItem implements StringIdentifiable {
		ALWAYS, TIMED, ONE;

		public static final Codec<GivesItem> CODEC = StringIdentifiable.createCodec(GivesItem::values);

		@Override
		public String asString() {
			return name().toLowerCase(Locale.ROOT);
		}
	}
}
