package dev.latvian.mods.kubejs.ingredient;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import me.textrue.kubejs.fabric.thirdparty.ingredients.IngredientType;
import me.textrue.kubejs.fabric.thirdparty.ingredients.IntersectionIngredient;
import me.textrue.kubejs.fabric.thirdparty.registries.ThirdPartyRegistry;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WildcardIngredient implements KubeJSIngredient {
	public static WildcardIngredient INSTANCE = new WildcardIngredient();

	public static final MapCodec<WildcardIngredient> CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<ByteBuf, WildcardIngredient> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	public static final CustomIngredientSerializer<WildcardIngredient> SERIALIZER = (CustomIngredientSerializer<WildcardIngredient>) CustomIngredientSerializer.get(ThirdPartyRegistry.id("wildcard"));

	private WildcardIngredient() {
	}

	@Override
	public IngredientType<?> getType() {
		return KubeJSIngredients.WILDCARD.get();
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && !stack.isEmpty();
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
