package me.textrue.kubejs.fabric.thirdparty.mixin.extensions.block;

import me.textrue.kubejs.fabric.thirdparty.extensions.BlockExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DropExperienceBlock.class)
public class DropExperienceBlockMixin implements BlockExtension {
	@Shadow
	@Final
	private IntProvider xpRange;

	@Override
    public int getExpDrop(BlockState state, LevelAccessor level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity breaker, ItemStack tool) {
        return this.xpRange.sample(level.getRandom());
	}
}
