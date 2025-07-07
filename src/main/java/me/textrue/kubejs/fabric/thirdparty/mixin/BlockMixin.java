package me.textrue.kubejs.fabric.thirdparty.mixin;

import me.textrue.kubejs.fabric.thirdparty.events.FabricBlockEvents;
import me.textrue.kubejs.fabric.thirdparty.util.value.IntValue;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviour {
	@Unique
	@Nullable
	private static List<ItemEntity> capturedDrops;
	@Unique
	private static IntValue experienceToDrop = null;
	@Unique
	private static BlockEntity droppedBlockEntity;
	@Unique
	private static Entity droppedBlockBreaker;

	public BlockMixin(Properties properties) {
		super(properties);
	}

	@Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)Ljava/util/List;"))
	private static void injectDropResources$1(BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
		beginCapturingDrops();
		droppedBlockEntity = null;
		droppedBlockBreaker = null;
	}

	@Redirect(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;spawnAfterBreak(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;Z)V"))
	private static void redirectDropResources$1(BlockState state, ServerLevel serverLevel, BlockPos pos, ItemStack itemStack, boolean b) {
		List<ItemEntity> captured = stopCapturingDrops();
		experienceToDrop.accept(EnchantmentHelper.processBlockExperience(serverLevel, itemStack, state.getExpDrop(serverLevel, pos, null, null, itemStack)));
		var droppedXp = experienceToDrop.getAsInt();
		FabricBlockEvents.DROP.invoker().onDrop(serverLevel, pos, state, null, captured, null, itemStack, experienceToDrop);
		for (ItemEntity entity : captured) {
			serverLevel.addFreshEntity(entity);
		}
		// Always pass false for the dropXP (last) param to spawnAfterBreak since we handle XP.
		state.spawnAfterBreak(serverLevel, pos, itemStack, false);
		if (droppedXp > 0) {
			state.getBlock().popExperience(serverLevel, pos, droppedXp);
		}
	}

	@Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)Ljava/util/List;"))
	private static void injectDropResources$2(BlockState state, LevelAccessor level, BlockPos pos, BlockEntity blockEntity, CallbackInfo ci) {
		beginCapturingDrops();
		droppedBlockEntity = blockEntity;
		droppedBlockBreaker = null;
	}

	@Redirect(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;spawnAfterBreak(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;Z)V"))
	private static void redirectDropResources$2(BlockState state, ServerLevel serverLevel, BlockPos pos, ItemStack itemStack, boolean b) {
		var dropBlockEntity = droppedBlockEntity;
		List<ItemEntity> captured = stopCapturingDrops();
		experienceToDrop.accept(EnchantmentHelper.processBlockExperience(serverLevel, itemStack, state.getExpDrop(serverLevel, pos, dropBlockEntity, null, itemStack)));
		var droppedXp = experienceToDrop.getAsInt();
		FabricBlockEvents.DROP.invoker().onDrop(serverLevel, pos, state, dropBlockEntity, captured, null, itemStack, experienceToDrop);
		for (ItemEntity entity : captured) {
			serverLevel.addFreshEntity(entity);
		}
		// Always pass false for the dropXP (last) param to spawnAfterBreak since we handle XP.
		state.spawnAfterBreak(serverLevel, pos, itemStack, false);
		if (droppedXp > 0) {
			state.getBlock().popExperience(serverLevel, pos, droppedXp);
		}
	}

	@Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"))
	private static void injectDropResources$3(BlockState state, Level level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool, CallbackInfo ci) {
		beginCapturingDrops();
		droppedBlockEntity = blockEntity;
		droppedBlockBreaker = entity;
	}

	@Redirect(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;spawnAfterBreak(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;Z)V"))
	private static void redirectDropResources$3(BlockState state, ServerLevel serverLevel, BlockPos pos, ItemStack itemStack, boolean b) {
		var dropBlockEntity = droppedBlockEntity;
		var breaker = droppedBlockBreaker;
		List<ItemEntity> captured = stopCapturingDrops();
		experienceToDrop.accept(EnchantmentHelper.processBlockExperience(serverLevel, itemStack, state.getExpDrop(serverLevel, pos, dropBlockEntity, breaker, itemStack)));
		var droppedXp = experienceToDrop.getAsInt();
		FabricBlockEvents.DROP.invoker().onDrop(serverLevel, pos, state, dropBlockEntity, captured, breaker, itemStack, experienceToDrop);
		for (ItemEntity entity : captured) {
			serverLevel.addFreshEntity(entity);
		}
		// Always pass false for the dropXP (last) param to spawnAfterBreak since we handle XP.
		state.spawnAfterBreak(serverLevel, pos, itemStack, false);
		if (droppedXp > 0) {
			state.getBlock().popExperience(serverLevel, pos, droppedXp);
		}
	}

	@Unique
	private static void beginCapturingDrops() {
        capturedDrops = new ArrayList<>();
    }

	@Unique
	private static List<ItemEntity> stopCapturingDrops() {
        List<ItemEntity> drops = capturedDrops;
        capturedDrops = null;
        return drops;
    }
}
