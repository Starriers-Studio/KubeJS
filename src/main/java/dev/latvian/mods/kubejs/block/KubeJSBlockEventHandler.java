package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.plugin.builtin.event.BlockEvents;
import me.textrue.kubejs.fabric.thirdparty.events.FabricBlockEvents;
import me.textrue.kubejs.fabric.thirdparty.util.event.ThirdPartyEventResult;
import me.textrue.kubejs.fabric.thirdparty.util.value.IntValue;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class KubeJSBlockEventHandler {
	public static void init() {
		UseBlockCallback.EVENT.register(KubeJSBlockEventHandler::rightClick);
		AttackBlockCallback.EVENT.register(KubeJSBlockEventHandler::leftClick);
		FabricBlockEvents.BREAK.register(KubeJSBlockEventHandler::blockBreak);
		FabricBlockEvents.DROP.register(KubeJSBlockEventHandler::drops);
		FabricBlockEvents.PLACE.register(KubeJSBlockEventHandler::blockPlace);
		FabricBlockEvents.FARMLAND_TRAMPLE.register(KubeJSBlockEventHandler::farmlandTrample);
	}

	public static InteractionResult rightClick(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
		var state = world.getBlockState(hitResult.getBlockPos());
		var key = state.getBlock().kjs$getKey();

		if (world instanceof Level level && BlockEvents.RIGHT_CLICKED.hasListeners(key) && !player.getCooldowns().isOnCooldown(player.getItemInHand(hand).getItem())) {
			return BlockEvents.RIGHT_CLICKED.post(level, key, new BlockRightClickedKubeEvent(null, player, hand, hitResult.getBlockPos(), hitResult.getDirection(), hitResult)).compoundResult().result().asMinecraft();
		}
		return InteractionResult.PASS;
	}

	public static InteractionResult leftClick(Player player, Level world, InteractionHand hand, BlockPos pos, Direction direction) {
		var state = world.getBlockState(pos);
		var key = state.getBlock().kjs$getKey();

		if (world instanceof Level level && BlockEvents.LEFT_CLICKED.hasListeners(key)) {
			return BlockEvents.LEFT_CLICKED.post(level, key, new BlockLeftClickedKubeEvent(player, level, hand, pos, direction)).compoundResult().result().asMinecraft();
		}
		return InteractionResult.PASS;
	}

	public static ThirdPartyEventResult blockBreak(Level world, BlockPos pos, BlockState state, ServerPlayer player, IntValue xp) {
		var key = state.getBlock().kjs$getKey();

		if (world instanceof Level level && BlockEvents.BROKEN.hasListeners(key)) {
			return BlockEvents.BROKEN.post(level, key, new BlockBrokenKubeEvent(level, player, pos, state)).compoundResult().result();
		}
		return ThirdPartyEventResult.pass();
	}

	public static void drops(ServerLevel world, BlockPos pos, BlockState state, BlockEntity blockEntity, List<ItemEntity> drops, Entity breaker, ItemStack tool, IntValue xp) {
		var key = state.getBlock().kjs$getKey();

		if (world instanceof ServerLevel level && BlockEvents.DROPS.hasListeners(key)) {
			BlockEvents.DROPS.post(level, key, new BlockDropsKubeEvent(level, pos, state, blockEntity, drops, breaker, tool, xp)).compoundResult().result();
		}
	}

	public static ThirdPartyEventResult blockPlace(Level world, BlockPos pos, BlockState state, Entity placer) {
		var key = state.getBlock().kjs$getKey();

		if (world instanceof Level level && BlockEvents.PLACED.hasListeners(key)) {
			return BlockEvents.PLACED.post(level, key, new BlockPlacedKubeEvent(level, pos, state, placer)).compoundResult().result();
		}
		return ThirdPartyEventResult.pass();
	}

	public static ThirdPartyEventResult farmlandTrample(Level world, BlockPos pos, BlockState state, float distance, Entity entity) {
		var key = state.getBlock().kjs$getKey();

		if (world instanceof Level level && BlockEvents.FARMLAND_TRAMPLED.hasListeners(key)) {
			return BlockEvents.FARMLAND_TRAMPLED.post(level, key, new FarmlandTrampledKubeEvent(level, pos, state, distance, entity)).compoundResult().result();
		}
		return ThirdPartyEventResult.pass();
	}
}