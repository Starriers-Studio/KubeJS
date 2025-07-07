package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@Info(value = """
	Invoked when a block is placed.
	""")
public class BlockPlacedKubeEvent implements KubeEntityEvent {
	private final Level level;
	private final BlockPos pos;
	private final BlockState state;
	private final Entity placer;
	private LevelBlock block;

	public BlockPlacedKubeEvent(Level level, BlockPos pos, BlockState state, @Nullable Entity placer) {
		this.level = level;
		this.pos = pos;
		this.state = state;
		this.placer = placer;
	}

	@Override
	@Info("The level of the block that was placed.")
	public Level getLevel() {
		return level;
	}

	@Override
	@Info("The entity that placed the block. Can be `null`, e.g. when a block is placed by a dispenser.")
	public Entity getEntity() {
		return placer;
	}

	@Info("The block that is placed.")
	public LevelBlock getBlock() {
		if (block == null) {
			block = level.kjs$getBlock(pos).cache(state);
		}
		return block;
	}
}