package me.textrue.kubejs.fabric.thirdparty.extensions;

import me.textrue.kubejs.fabric.thirdparty.util.ChunkRenderTypeSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public interface ItemBlockRenderTypesExtension {
	ChunkRenderTypeSet getRenderLayers(BlockState state);

	void setRenderLayer(Block block, RenderType type);

	void setRenderLayer(Block block, Predicate<RenderType> predicate);

	void setRenderLayer(Block block, ChunkRenderTypeSet layers);
}
