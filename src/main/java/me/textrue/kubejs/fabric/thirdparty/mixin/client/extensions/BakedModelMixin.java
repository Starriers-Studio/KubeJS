package me.textrue.kubejs.fabric.thirdparty.mixin.client.extensions;

import me.textrue.kubejs.fabric.thirdparty.extensions.BakedModelExtension;
import net.minecraft.client.resources.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BakedModel.class)
public class BakedModelMixin implements BakedModelExtension {
}
