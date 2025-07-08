package me.textrue.kubejs.fabric.thirdparty.extensions;

import me.textrue.kubejs.fabric.thirdparty.util.ChunkRenderTypeSet;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public interface BakedModelExtension {
	private BakedModel self() {
		return (BakedModel) this;
	}

	default ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand) {
		return new ItemBlockRenderTypes().getRenderLayers(state);
	}
}
