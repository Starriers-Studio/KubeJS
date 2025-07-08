package me.textrue.kubejs.fabric.thirdparty.mixin.extensions;

import me.textrue.kubejs.fabric.thirdparty.extensions.BlockStateExtension;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockState.class)
public class BlockStateMixin implements BlockStateExtension {
}
