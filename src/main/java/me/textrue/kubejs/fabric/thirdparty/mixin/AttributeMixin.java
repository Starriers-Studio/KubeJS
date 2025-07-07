package me.textrue.kubejs.fabric.thirdparty.mixin;

import me.textrue.kubejs.fabric.thirdparty.extensions.AttributeExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Attribute.class)
public abstract class AttributeMixin implements AttributeExtension {
	@Shadow
	public abstract ChatFormatting getStyle(boolean isPositive);

	@Override
	public TextColor getMergedStyle(boolean isPositive) {
		return TextColor.fromLegacyFormat(getStyle(isPositive));
	}
}
