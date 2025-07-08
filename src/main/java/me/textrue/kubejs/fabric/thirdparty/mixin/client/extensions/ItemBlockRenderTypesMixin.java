package me.textrue.kubejs.fabric.thirdparty.mixin.client.extensions;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.textrue.kubejs.fabric.thirdparty.extensions.ItemBlockRenderTypesExtension;
import me.textrue.kubejs.fabric.thirdparty.util.ChunkRenderTypeSet;
import net.minecraft.Util;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(ItemBlockRenderTypes.class)
public class ItemBlockRenderTypesMixin implements ItemBlockRenderTypesExtension {
	@Shadow
	@Final
	private static Map<Block, RenderType> TYPE_BY_BLOCK;

	@Shadow
	private static boolean renderCutout;
	@Unique
	private static final ChunkRenderTypeSet CUTOUT_MIPPED = ChunkRenderTypeSet.of(RenderType.cutoutMipped());
	@Unique
	private static final ChunkRenderTypeSet SOLID = ChunkRenderTypeSet.of(RenderType.solid());
	@Unique
	private static final Map<Block, ChunkRenderTypeSet> BLOCK_RENDER_TYPES = Util.make(new Object2ObjectOpenHashMap<>(TYPE_BY_BLOCK.size(), 0.5F), map -> {
        map.defaultReturnValue(SOLID);
        for(Map.Entry<Block, RenderType> entry : TYPE_BY_BLOCK.entrySet()) {
            map.put(entry.getKey(), ChunkRenderTypeSet.of(entry.getValue()));
        }
    });

	@Override
	public ChunkRenderTypeSet getRenderLayers(BlockState state) {
		Block block = state.getBlock();
        if (block instanceof LeavesBlock) {
            return renderCutout ? CUTOUT_MIPPED : SOLID;
        } else {
            return BLOCK_RENDER_TYPES.get(block);
       }
	}

	@Override
    public void setRenderLayer(Block block, RenderType type) {
        com.google.common.base.Preconditions.checkArgument(type.getChunkLayerId() >= 0, "The argument must be a valid chunk render type returned by RenderType#chunkBufferLayers().");
        setRenderLayer(block, ChunkRenderTypeSet.of(type));
	}

	@Override
    public synchronized void setRenderLayer(Block block, java.util.function.Predicate<RenderType> predicate) {
        setRenderLayer(block, createSetFromPredicate(predicate));
    }

	@Override
    public synchronized void setRenderLayer(Block block, ChunkRenderTypeSet layers) {
        BLOCK_RENDER_TYPES.put(block, layers);
    }

	@Unique
	private static ChunkRenderTypeSet createSetFromPredicate(java.util.function.Predicate<RenderType> predicate) {
        return ChunkRenderTypeSet.of(RenderType.chunkBufferLayers().stream().filter(predicate).toArray(RenderType[]::new));
	}
}
