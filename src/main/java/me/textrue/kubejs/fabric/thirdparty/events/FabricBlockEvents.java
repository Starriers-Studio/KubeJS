package me.textrue.kubejs.fabric.thirdparty.events;

import me.textrue.kubejs.fabric.thirdparty.util.event.ThirdPartyEventResult;
import me.textrue.kubejs.fabric.thirdparty.util.value.IntValue;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FabricBlockEvents {
	public static final Event<Break> BREAK = EventFactory.createArrayBacked(Break.class, callbacks -> (level, pos, state, player, xp) -> {
		for (Break callback : callbacks) {
			return callback.breakBlock(level, pos, state, player, xp);
		}
		return ThirdPartyEventResult.pass();
	});

	public static final Event<Place> PLACE = EventFactory.createArrayBacked(Place.class, callbacks -> (level, pos, state, placer) -> {
		for (Place callback : callbacks) {
			return callback.placeBlock(level, pos, state, placer);
		}
		return ThirdPartyEventResult.pass();
	});

	public static final Event<FarmlandTrample> FARMLAND_TRAMPLE = EventFactory.createArrayBacked(FarmlandTrample.class, callbacks -> (world, pos, state, distance, entity) -> {
		for (FarmlandTrample callback : callbacks) {
			return callback.trample(world, pos, state, distance, entity);
		}
		return ThirdPartyEventResult.pass();
	});

	public static final Event<Drop> DROP = EventFactory.createArrayBacked(Drop.class, callbacks -> (level, pos, state, blockEntity, drops, breaker, tool, xp) -> {
		for (Drop callback : callbacks) {
			callback.onDrop(level, pos, state, blockEntity, drops, breaker, tool, xp);
		}
	});

	public interface Place {
		ThirdPartyEventResult placeBlock(Level level, BlockPos pos, BlockState state, @Nullable Entity placer);
	}

	public interface Break {
		ThirdPartyEventResult breakBlock(Level level, BlockPos pos, BlockState state, ServerPlayer player, @Nullable IntValue xp);
	}

	public interface FarmlandTrample {
		ThirdPartyEventResult trample(Level level, BlockPos pos, BlockState state, float distance, Entity entity);
	}

	@FunctionalInterface
	public interface Drop {
		void onDrop(ServerLevel level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, List<ItemEntity> drops, @Nullable Entity breaker, ItemStack tool, @Nullable IntValue xp);
	}
}
