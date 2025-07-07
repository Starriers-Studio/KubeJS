package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Info(value = """
	Invoked when an entity attempts to trample farmland.
	""")
public class FarmlandTrampledKubeEvent implements KubeEntityEvent {
	private final Level level;
	private final float distance;
	private final Entity entity;
	private final LevelBlock block;

	public FarmlandTrampledKubeEvent(Level level, BlockPos pos, BlockState state, float distance, Entity entity) {
		this.level = level;
		this.distance = distance;
		this.entity = entity;
		this.block = level.kjs$getBlock(pos).cache(state);
	}

	@Info("The distance of the entity from the block.")
	public float getDistance() {
		return distance;
	}

	@Override
	@Info("The entity that is attempting to trample the farmland.")
	public Entity getEntity() {
		return entity;
	}

	@Override
	@Info("The level that the farmland and the entity are in.")
	public Level getLevel() {
		return level;
	}

	@Info("The farmland block.")
	public LevelBlock getBlock() {
		return block;
	}
}
