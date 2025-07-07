package me.textrue.kubejs.fabric.thirdparty.registries;

import dev.latvian.mods.kubejs.KubeJS;
import me.textrue.kubejs.fabric.thirdparty.fluids.FluidType;
import me.textrue.kubejs.fabric.thirdparty.holdersets.AndHolderSet;
import me.textrue.kubejs.fabric.thirdparty.holdersets.AnyHolderSet;
import me.textrue.kubejs.fabric.thirdparty.holdersets.HolderSetType;
import me.textrue.kubejs.fabric.thirdparty.holdersets.NotHolderSet;
import me.textrue.kubejs.fabric.thirdparty.holdersets.OrHolderSet;
import me.textrue.kubejs.fabric.thirdparty.ingredients.BlockTagIngredient;
import me.textrue.kubejs.fabric.thirdparty.ingredients.CompoundIngredient;
import me.textrue.kubejs.fabric.thirdparty.ingredients.DataComponentIngredient;
import me.textrue.kubejs.fabric.thirdparty.ingredients.DifferenceIngredient;
import me.textrue.kubejs.fabric.thirdparty.ingredients.IngredientType;
import me.textrue.kubejs.fabric.thirdparty.ingredients.IntersectionIngredient;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.CompoundFluidIngredient;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.DataComponentFluidIngredient;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.DifferenceFluidIngredient;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.EmptyFluidIngredient;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.FluidIngredientType;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.IntersectionFluidIngredient;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.SingleFluidIngredient;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.TagFluidIngredient;
import me.textrue.kubejs.fabric.thirdparty.sounds.SoundActions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ThirdPartyRegistry {
	private static final Map<ResourceLocation, HolderSetType> HOLDER_SET_TYPES = new LinkedHashMap<>();
	private static final Map<ResourceLocation, IngredientType<?>> INGREDIENT_TYPES = new LinkedHashMap<>();
	private static final Map<ResourceLocation, FluidIngredientType<?>> FLUID_INGREDIENT_TYPES = new LinkedHashMap<>();
	private static final Map<ResourceLocation, FluidType> FLUID_TYPES = new LinkedHashMap<>();

	/**
	 * Stock holder set type that represents any/all values in a registry. Can be used in a holderset object with {@code { "type": "kubejs_thirdparty:any" }}
	 */
	public static final Holder<HolderSetType> ANY_HOLDER_SET = registerHolderSetType("any", AnyHolderSet.Type::new);

	/**
	 * Stock holder set type that represents an intersection of other holdersets. Can be used in a holderset object with {@code { "type": "kubejs_thirdparty:and", "values": [list of holdersets] }}
	 */
	public static final Holder<HolderSetType> AND_HOLDER_SET = registerHolderSetType("and", AndHolderSet.Type::new);

	/**
	 * Stock holder set type that represents a union of other holdersets. Can be used in a holderset object with {@code { "type": "kubejs_thirdparty:or", "values": [list of holdersets] }}
	 */
	public static final Holder<HolderSetType> OR_HOLDER_SET = registerHolderSetType("or", OrHolderSet.Type::new);

	/**
	 * <p>Stock holder set type that represents all values in a registry except those in another given set.
	 * Can be used in a holderset object with {@code { "type": "kubejs_thirdparty:not", "value": holderset }}</p>
	 */
	public static final Holder<HolderSetType> NOT_HOLDER_SET = registerHolderSetType("not", NotHolderSet.Type::new);

	public static final IngredientType<CompoundIngredient> COMPOUND_INGREDIENT_TYPE = registerIngredientType("compound", () -> new IngredientType<>(CompoundIngredient.CODEC));
	public static final IngredientType<DataComponentIngredient> DATA_COMPONENT_INGREDIENT_TYPE = registerIngredientType("components", () -> new IngredientType<>(DataComponentIngredient.CODEC));
	public static final IngredientType<DifferenceIngredient> DIFFERENCE_INGREDIENT_TYPE = registerIngredientType("difference", () -> new IngredientType<>(DifferenceIngredient.CODEC));
	public static final IngredientType<IntersectionIngredient> INTERSECTION_INGREDIENT_TYPE = registerIngredientType("intersection", () -> new IngredientType<>(IntersectionIngredient.CODEC));
	public static final IngredientType<BlockTagIngredient> BLOCK_TAG_INGREDIENT = registerIngredientType("block_tag", () -> new IngredientType<>(BlockTagIngredient.CODEC));

	public static final FluidIngredientType<SingleFluidIngredient> SINGLE_FLUID_INGREDIENT_TYPE = registerFluidIngredientType("single", () -> new FluidIngredientType<>(SingleFluidIngredient.CODEC));
	public static final FluidIngredientType<TagFluidIngredient> TAG_FLUID_INGREDIENT_TYPE = registerFluidIngredientType("tag", () -> new FluidIngredientType<>(TagFluidIngredient.CODEC));
	public static final FluidIngredientType<EmptyFluidIngredient> EMPTY_FLUID_INGREDIENT_TYPE = registerFluidIngredientType("empty", () -> new FluidIngredientType<>(EmptyFluidIngredient.CODEC));
	public static final FluidIngredientType<CompoundFluidIngredient> COMPOUND_FLUID_INGREDIENT_TYPE = registerFluidIngredientType("compound", () -> new FluidIngredientType<>(CompoundFluidIngredient.CODEC));
	public static final FluidIngredientType<DataComponentFluidIngredient> DATA_COMPONENT_FLUID_INGREDIENT_TYPE = registerFluidIngredientType("components", () -> new FluidIngredientType<>(DataComponentFluidIngredient.CODEC));
	public static final FluidIngredientType<DifferenceFluidIngredient> DIFFERENCE_FLUID_INGREDIENT_TYPE = registerFluidIngredientType("difference", () -> new FluidIngredientType<>(DifferenceFluidIngredient.CODEC));
	public static final FluidIngredientType<IntersectionFluidIngredient> INTERSECTION_FLUID_INGREDIENT_TYPE = registerFluidIngredientType("intersection", () -> new FluidIngredientType<>(IntersectionFluidIngredient.CODEC));

	public static final Holder<FluidType> EMPTY_TYPE = registerFluidType("empty", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.minecraft.air")
		.motionScale(1D)
		.canPushEntity(false)
		.canSwim(false)
		.canDrown(false)
		.fallDistanceModifier(1F)
		.pathType(null)
		.adjacentPathType(null)
		.density(0)
		.temperature(0)
		.viscosity(0)) {
		@Override
		public void setItemMovement(ItemEntity entity) {
			if (!entity.isNoGravity()) entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
		}
	});
	public static final Holder<FluidType> WATER_TYPE = registerFluidType("water", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.minecraft.water")
		.fallDistanceModifier(0F)
		.canExtinguish(true)
		.canConvertToSource(true)
		.supportsBoating(true)
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
		.canHydrate(true)) {

		@Override
		public boolean canConvertToSource(FluidState state, LevelReader reader, BlockPos pos) {
			if (reader instanceof Level level) {
				return level.getGameRules().getBoolean(GameRules.RULE_WATER_SOURCE_CONVERSION);
			}
			//Best guess fallback to default (true)
			return super.canConvertToSource(state, reader, pos);
		}

		@Override
		public @Nullable PathType getBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, boolean canFluidLog) {
			return canFluidLog ? super.getBlockPathType(state, level, pos, mob, true) : null;
		}
	});
	public static final Holder<FluidType> LAVA_TYPE = registerFluidType("lava", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.minecraft.lava")
		.canSwim(false)
		.canDrown(false)
		.pathType(PathType.LAVA)
		.adjacentPathType(null)
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
		.lightLevel(15)
		.density(3000)
		.viscosity(6000)
		.temperature(1300)) {

		@Override
		public boolean canConvertToSource(FluidState state, LevelReader reader, BlockPos pos) {
			if (reader instanceof Level level) {
				return level.getGameRules().getBoolean(GameRules.RULE_LAVA_SOURCE_CONVERSION);
			}
			//Best guess fallback to default (false)
			return super.canConvertToSource(state, reader, pos);
		}

		@Override
		public double motionScale(Entity entity) {
			return entity.level().dimensionType().ultraWarm() ? 0.007D : 0.0023333333333333335D;
		}

		@Override
		public void setItemMovement(ItemEntity entity) {
			Vec3 vec3 = entity.getDeltaMovement();
			entity.setDeltaMovement(vec3.x * (double) 0.95F, vec3.y + (double) (vec3.y < (double) 0.06F ? 5.0E-4F : 0.0F), vec3.z * (double) 0.95F);
		}

		@Override
		public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
			// Prevent water movement logic (which is denoted by returning false) being used for lava
			return true;
		}
	});

	private static <T extends FluidIngredientType<?>> T registerFluidIngredientType(String id, Supplier<T> ingredientType) {
		FLUID_INGREDIENT_TYPES.put(id(id), ingredientType.get());
		return ingredientType.get();
	}

	private static <T extends IngredientType<?>> T registerIngredientType(String id, Supplier<T> ingredientType) {
		INGREDIENT_TYPES.put(id(id), ingredientType.get());
		return ingredientType.get();
	}

	private static <T extends HolderSetType> Holder<T> registerHolderSetType(String id, Supplier<T> holderSetType) {
		HOLDER_SET_TYPES.put(id(id), holderSetType.get());
		return Holder.direct(holderSetType.get());
	}

	private static <T extends FluidType> Holder<T> registerFluidType(String id, Supplier<T> fluidType) {
		FLUID_TYPES.put(id(id), fluidType.get());
		return Holder.direct(fluidType.get());
	}

	public static ResourceLocation id(String id) {
		return ResourceLocation.fromNamespaceAndPath(KubeJS.MOD_ID + "_thirdparty", id);
	}

	static {
		HOLDER_SET_TYPES.forEach((id, holderSetType) -> {
			Registry.register(ThirdPartyRegistries.HOLDER_SET_TYPES, id, holderSetType);
		});
		INGREDIENT_TYPES.forEach((id, ingredientType) -> {
			Registry.register(ThirdPartyRegistries.INGREDIENT_TYPES, id, ingredientType);
		});
		FLUID_INGREDIENT_TYPES.forEach((id, ingredientType) -> {
			Registry.register(ThirdPartyRegistries.FLUID_INGREDIENT_TYPES, id, ingredientType);
		});
		FLUID_TYPES.forEach((id, fluidType) -> {
			Registry.register(ThirdPartyRegistries.FLUID_TYPES, id, fluidType);
		});
	}
}
