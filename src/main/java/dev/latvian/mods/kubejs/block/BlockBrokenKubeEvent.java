package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Info(value = """
	Invoked when a block is destroyed by a player.
	""")
public class BlockBrokenKubeEvent implements KubePlayerEvent {
	private final Level level;
	private final Player player;
	private final BlockPos pos;
	private final BlockState state;
	private LevelBlock block;

	public BlockBrokenKubeEvent(Level level, Player player, BlockPos pos, BlockState state) {
		this.level = level;
		this.player = player;
		this.pos = pos;
		this.state = state;
	}

	@Override
	@Info("The player that broke the block.")
	public Player getEntity() {
		return player;
	}

	@Info("The block that was broken.")
	public LevelBlock getBlock() {
		if (block == null) {
			block = level.kjs$getBlock(pos).cache(state);
		}

		return block;
	}
}