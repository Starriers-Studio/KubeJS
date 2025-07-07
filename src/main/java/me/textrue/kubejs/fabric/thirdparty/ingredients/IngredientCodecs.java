package me.textrue.kubejs.fabric.thirdparty.ingredients;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.textrue.kubejs.fabric.thirdparty.util.ThirdPartyExtraCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class IngredientCodecs {
	/**
     * This codec allows both the {@code {...}} and {@code [{...}, {...}, ...]} syntax.
     * {@code []} is allowed for empty ingredients, and will only match empty stacks.
     */
    public static final Codec<Ingredient> CODEC = CraftingHelper.makeIngredientCodec(true);
    /**
     * Same as {@link #CODEC} except that empty ingredients ({@code []}) are not allowed.
     */
    public static final Codec<Ingredient> CODEC_NONEMPTY = CraftingHelper.makeIngredientCodec(false);
    /**
     * This is a codec that only allows the {@code {...}} syntax.
     * Array ingredients are serialized using the CompoundIngredient custom ingredient type:
    * {@code { "type": "neoforge:compound", "ingredients": [{...}, {...}, ...] }}.
     */
    public static final MapCodec<Ingredient> MAP_CODEC_NONEMPTY = CraftingHelper.makeIngredientMapCodec();
    public static final Codec<List<Ingredient>> LIST_CODEC = MAP_CODEC_NONEMPTY.codec().listOf();
    public static final Codec<List<Ingredient>> LIST_CODEC_NONEMPTY = LIST_CODEC.validate(list -> list.isEmpty() ? DataResult.error(() -> "Item array cannot be empty, at least one item must be defined") : DataResult.success(list));

	public static final MapCodec<Ingredient.ItemValue> ITEM_VALUE_MAP_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(ItemStack.SIMPLE_ITEM_CODEC.fieldOf("item")
		.forGetter((itemValue) -> itemValue.item())).apply(instance, Ingredient.ItemValue::new));
	public static final Codec<Ingredient.ItemValue> ITEM_VALUE_CODEC = ITEM_VALUE_MAP_CODEC.codec();

	public static final MapCodec<Ingredient.TagValue> TAG_VALUE_MAP_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(TagKey.codec(Registries.ITEM)
		.fieldOf("tag").forGetter((tagValue) -> tagValue.tag())).apply(instance, Ingredient.TagValue::new));
	public static final Codec<Ingredient.TagValue> TAG_VALUE_CODEC = TAG_VALUE_MAP_CODEC.codec();

	public static final MapCodec<Ingredient.Value> VALUE_MAP_CODEC = ThirdPartyExtraCodecs.xor(ITEM_VALUE_MAP_CODEC, TAG_VALUE_MAP_CODEC)
		.xmap((either) -> either.map((itemValue) -> itemValue, (tagValue) -> tagValue), (value) -> {
		if (value instanceof Ingredient.TagValue tagValue) {
			return Either.right(tagValue);
		} else if (value instanceof Ingredient.ItemValue itemValue) {
			return Either.left(itemValue);
		} else {
			throw new UnsupportedOperationException("This is neither an item value nor a tag value.");
		}
	});
	public static final Codec<Ingredient.Value> VALUE_CODEC = VALUE_MAP_CODEC.codec();
}
