package dev.latvian.mods.kubejs.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import me.textrue.kubejs.fabric.thirdparty.ingredients.IngredientType;
import me.textrue.kubejs.fabric.thirdparty.registries.ThirdPartyRegistry;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record NamespaceIngredient(String namespace) implements KubeJSIngredient {
	public static final MapCodec<NamespaceIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("namespace").forGetter(NamespaceIngredient::namespace)
	).apply(instance, NamespaceIngredient::new));

	public static final StreamCodec<ByteBuf, NamespaceIngredient> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(NamespaceIngredient::new, NamespaceIngredient::namespace);
	public static final CustomIngredientSerializer<NamespaceIngredient> SERIALIZER = (CustomIngredientSerializer<NamespaceIngredient>) CustomIngredientSerializer.get(ThirdPartyRegistry.id("namespace"));

	@Override
	public IngredientType<?> getType() {
		return KubeJSIngredients.NAMESPACE.get();
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && stack.kjs$getMod().equals(namespace);
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
}
