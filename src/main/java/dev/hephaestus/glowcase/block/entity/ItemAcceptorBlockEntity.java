package dev.hephaestus.glowcase.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.client.render.block.entity.BakedBlockEntityRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class ItemAcceptorBlockEntity extends GlowcaseBlockEntity {
	private String item = "";
	public int count = 1;
	public OutputDirection outputDirection = OutputDirection.BACK;
	public InputType inputType = InputType.ITEM;
	private List<Item> itemTagList = List.of();

	public ItemAcceptorBlockEntity(BlockPos pos, BlockState state) {
		super(Glowcase.ITEM_ACCEPTOR_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(tag, registryLookup);

		tag.putString("item", this.item);
		tag.putInt("count", this.count);
		tag.putString("input_type", this.inputType.name());
		tag.putString("output_direction", this.outputDirection.name());
	}

	@Override
	public void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(tag, registryLookup);

		setItem(tag.getString("item"));
		this.count = tag.getInt("count");
		this.inputType = InputType.valueOf(tag.getString("input_type"));
		this.outputDirection = OutputDirection.valueOf(tag.getString("output_direction"));
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;

		Identifier id = Identifier.tryParse(item);
		if (inputType == InputType.ITEM_TAG && id != null) {
			TagKey<Item> itemTag = TagKey.of(RegistryKeys.ITEM, id);
			itemTagList = Registries.ITEM.stream().filter(it -> it.getDefaultStack().isIn(itemTag)).toList();
		}
	}

	public ItemStack getDisplayItemStack() {
		if (inputType == InputType.ITEM_TAG) {
			if (itemTagList.isEmpty()) {
				return ItemStack.EMPTY;
			}

			return itemTagList.get((int) (Util.getMeasuringTimeMs() / 1000f) % itemTagList.size()).getDefaultStack();
		} else if (inputType == InputType.COMMAND) {
			return Items.COMMAND_BLOCK.getDefaultStack();
		} else {
			return Registries.ITEM.get(Identifier.tryParse(item)).getDefaultStack();
		}
	}

	public boolean isAccepted(PlayerEntity player, ItemStack stack) {
		if (inputType == InputType.COMMAND) {
			if (player.getServer() != null) {
				try {
					int result = player.getServer().getCommandManager().getDispatcher().execute(getItem(), player.getCommandSource().withSilent().withLevel(2));
					if (result == count) return true;
				} catch (CommandSyntaxException ignored) {}
			}
			return false;
		}

		boolean isEqual = inputType == InputType.ITEM_TAG
			? stack.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.tryParse(item)))
			: stack.isOf(Registries.ITEM.get(Identifier.tryParse(item)));

		return isEqual && stack.getCount() >= count;
	}

	@SuppressWarnings({"MethodCallSideOnly", "VariableUseSideOnly"})
	@Override
	public void markRemoved() {
		if (world != null && world.isClient) {
			BakedBlockEntityRenderer.Manager.markForRebuild(getPos());
		}
		super.markRemoved();
	}

	public enum OutputDirection {
		TOP, BACK, BOTTOM
	}

	public enum InputType {
		ITEM, ITEM_TAG, COMMAND
	}
}
