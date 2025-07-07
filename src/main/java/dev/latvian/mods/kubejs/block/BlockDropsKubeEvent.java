package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.typings.Info;
import me.textrue.kubejs.fabric.thirdparty.util.value.IntValue;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Info(value = """
	Modify dropped items and xp from block.
	""")
public class BlockDropsKubeEvent implements KubeEntityEvent {
	private final ServerLevel level;
	private final BlockPos pos;
	private final BlockState state;
	private final BlockEntity blockEntity;
	private final List<ItemEntity> drops;
	private final Entity breaker;
	private final ItemStack tool;
	private LevelBlock block;
	private final IntValue experience;

	public BlockDropsKubeEvent(ServerLevel level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, List<ItemEntity> drops, @Nullable Entity breaker, ItemStack tool, @Nullable IntValue xp) {
		this.level = level;
		this.pos = pos;
		this.state = state;
		this.blockEntity = blockEntity;
		this.drops = drops;
		this.breaker = breaker;
		this.tool = tool;
		this.experience = xp;
	}

	@Override
	public ServerLevel getLevel() {
		return level;
	}

	@Override
	@Nullable
	public Entity getEntity() {
		return breaker;
	}

	@Info("The block that was broken.")
	public LevelBlock getBlock() {
		if (block == null) {
			block = level.kjs$getBlock(pos).cache(state).cache(blockEntity);
		}

		return block;
	}

	@Info("The experience dropped by the block.")
	public int getXp() {
		return experience.getAsInt();
	}

	@Info("Sets the experience dropped by the block.")
	public void setXp(int xp) {
		this.experience.accept(xp);
	}

	@Info("Dropped item entities.")
	public List<ItemEntity> getItemEntities() {
		return drops;
	}

	@Info("Dropped items. Immutable.")
	public List<ItemStack> getItems() {
		return drops.stream().map(ItemEntity::getItem).toList();
	}

	public boolean containsItem(ItemPredicate item) {
		for (var drop : drops) {
			if (item.test(drop.getItem())) {
				return true;
			}
		}

		return false;
	}

	public ItemEntity addItem(ItemStack item) {
		double x = pos.getX() + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25);
		double y = pos.getY() + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25) - EntityType.ITEM.getHeight() / 2.0;
		double z = pos.getZ() + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25);
		var entity = new ItemEntity(level, x, y, z, item);
		drops.add(entity);
		return entity;
	}

	public void removeItem(ItemPredicate item) {
		drops.removeIf(drop -> item.test(drop.getItem()));
	}

	@Nullable
	@Info("The tool used when breaking this block. May be null.")
	public ItemStack getTool() {
		return tool.isEmpty() ? null : tool;
	}
}