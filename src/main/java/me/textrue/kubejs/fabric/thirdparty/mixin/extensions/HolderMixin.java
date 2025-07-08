package me.textrue.kubejs.fabric.thirdparty.mixin.extensions;

import me.textrue.kubejs.fabric.thirdparty.extensions.HolderExtension;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Holder.class)
public class HolderMixin<T> implements HolderExtension<T> {
}
