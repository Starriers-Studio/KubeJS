package me.textrue.kubejs.fabric.thirdparty.mixin.extensions;

import me.textrue.kubejs.fabric.thirdparty.extensions.HolderSetExtension;
import net.minecraft.core.HolderSet;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HolderSet.class)
public class HolderSetMixin<T> implements HolderSetExtension<T> {
}
