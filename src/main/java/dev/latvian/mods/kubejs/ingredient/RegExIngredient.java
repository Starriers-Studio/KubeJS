package dev.latvian.mods.kubejs.ingredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import io.netty.buffer.ByteBuf;
import me.textrue.kubejs.fabric.thirdparty.ingredients.IngredientType;
import me.textrue.kubejs.fabric.thirdparty.registries.ThirdPartyRegistry;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;

public record RegExIngredient(Pattern pattern, String patternString) implements KubeJSIngredient {
	public static final MapCodec<RegExIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		RegExpKJS.CODEC.fieldOf("pattern").forGetter(RegExIngredient::pattern)
	).apply(instance, RegExIngredient::new));

	public static final StreamCodec<ByteBuf, RegExIngredient> STREAM_CODEC = RegExpKJS.STREAM_CODEC.map(RegExIngredient::new, RegExIngredient::pattern);
	public static final CustomIngredientSerializer<RegExIngredient> SERIALIZER = (CustomIngredientSerializer<RegExIngredient>) CustomIngredientSerializer.get(ThirdPartyRegistry.id("regex"));

	public RegExIngredient(Pattern pattern) {
		this(pattern, RegExpKJS.toRegExpString(pattern));
	}

	@Override
	public IngredientType<?> getType() {
		return KubeJSIngredients.REGEX.get();
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && pattern.matcher(stack.kjs$getId()).find();
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		return getItems().toList();
	}

	@Override
	public boolean requiresTesting() {
		return false;
	}

	@Override
	public CustomIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof RegExIngredient i && patternString.equals(i.patternString);
	}

	@Override
	public int hashCode() {
		return patternString.hashCode();
	}

	@Override
	public String toString() {
		return "KubeJSItemRegExIngredient[" + patternString + "]";
	}
}
