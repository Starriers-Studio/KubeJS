package me.textrue.kubejs.fabric.thirdparty.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface BlockStateExtension {
	private BlockState self() {
		return (BlockState) this;
	}

	default int getExpDrop(LevelAccessor level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity breaker, ItemStack tool) {
		return self().getBlock().getExpDrop(self(), level, pos, blockEntity, breaker, tool);
	}
}
