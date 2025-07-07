package me.textrue.kubejs.fabric.thirdparty.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.platform.InputConstants;
import me.textrue.kubejs.fabric.thirdparty.extensions.KeyMappingExtension;
import me.textrue.kubejs.fabric.thirdparty.settings.IKeyConflictContext;
import me.textrue.kubejs.fabric.thirdparty.settings.KeyConflictContext;
import me.textrue.kubejs.fabric.thirdparty.settings.KeyMappingLookup;
import me.textrue.kubejs.fabric.thirdparty.settings.KeyModifier;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin implements KeyMappingExtension {
	@Shadow
	private InputConstants.Key key;
	@Mutable
	@Shadow
	@Final
	private String name;
	@Mutable
	@Shadow
	@Final
	private String category;
	@Mutable
	@Shadow
	@Final
	private InputConstants.Key defaultKey;

	@Unique
	private IKeyConflictContext keyConflictContext = KeyConflictContext.UNIVERSAL;
	@Unique
	private KeyModifier keyModifier = KeyModifier.NONE;
	@Unique
	private KeyModifier keyModifierDefault = KeyModifier.NONE;

    @Unique
	private static final KeyMappingLookup KEY_MAPPING_LOOKUP = new KeyMappingLookup();
	@Shadow
	@Final
	private static Map<InputConstants.Key, KeyMapping> MAP;

    @Override
    public InputConstants.Key getKey() {
		return this.key;
    }

    @Override
    public void setKeyConflictContext(IKeyConflictContext keyConflictContext) {
		this.keyConflictContext = keyConflictContext;
    }

    @Override
    public IKeyConflictContext getKeyConflictContext() {
		return keyConflictContext;
	}

    @Override
    public KeyModifier getDefaultKeyModifier() {
		return keyModifierDefault;
    }

    @Override
    public KeyModifier getKeyModifier() {
		return keyModifier;
    }

    @Override
    public void setKeyModifierAndCode(KeyModifier keyModifier, InputConstants.Key keyCode) {
		this.key = keyCode;
		if (keyModifier.matches(keyCode))
			keyModifier = KeyModifier.NONE;
		KEY_MAPPING_LOOKUP.remove((KeyMapping) (Object) this);
		MAP.remove((KeyMapping) (Object) this);
		this.keyModifier = keyModifier;
		MAP.put(keyCode, (KeyMapping) (Object) this);
		KEY_MAPPING_LOOKUP.put(keyCode, (KeyMapping) (Object) this);
	}

	@Inject(method = "same", at = @At("HEAD"), cancellable = true)
	private void kjs$addKeyConflictContextToSimilarityCheck(KeyMapping binding, CallbackInfoReturnable<Boolean> cir) {
		if (getKeyConflictContext().conflicts(binding.getKeyConflictContext()) || binding.getKeyConflictContext().conflicts(getKeyConflictContext())) {
			KeyModifier keyModifier = getKeyModifier();
			KeyModifier otherKeyModifier = binding.getKeyModifier();
			if (keyModifier.matches(binding.getKey()) || otherKeyModifier.matches(getKey())) {
				cir.setReturnValue(true);
			} else if (getKey().equals(binding.getKey())) {
				// IN_GAME key contexts have a conflict when at least one modifier is NONE.
				// For example: If you hold shift to crouch, you can still press E to open your inventory. This means that a Shift+E hotkey is in conflict with E.
				// GUI and other key contexts do not have this limitation.
				cir.setReturnValue(keyModifier == otherKeyModifier ||
					(getKeyConflictContext().conflicts(KeyConflictContext.IN_GAME) &&
						(keyModifier == KeyModifier.NONE || otherKeyModifier == KeyModifier.NONE)));
			}
		}
	}

	@Inject(method = "resetMapping", at = @At("HEAD"))
	private static void kjs$resetForgeMap(CallbackInfo ci) {
		KEY_MAPPING_LOOKUP.clear();
	}

	@Inject(method = "resetMapping", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
	private static void kjs$registerMappingToForgeMap(CallbackInfo ci, @Local KeyMapping mapping) {
		KEY_MAPPING_LOOKUP.put(((KeyMappingAccessor) mapping).kjs$getKey(), mapping);
	}

	@WrapMethod(method = "getTranslatedKeyMessage")
	private Component kjs$addKeyModifierToMessage(Operation<Component> original) {
		return this.getKeyModifier().getCombinedName(this.key, original::call);
	}

	@ModifyReturnValue(method = "isDefault", at = @At("RETURN"))
	private boolean kjs$addModifierToDefaultCheck(boolean original) {
		return original && this.getKeyModifier() == this.getDefaultKeyModifier();
	}

	@ModifyReturnValue(method = "isDown", at = @At("RETURN"))
	private boolean kjs$addConflictContextAndModifierToDownCheck(boolean original) {
		return original && this.isConflictContextAndModifierActive();
	}

	@WrapMethod(method = "click")
	private static void kjs$wrapKeyClick(InputConstants.Key key, Operation<Void> original, @Share("currentMap") LocalRef<KeyMapping> currentMap) {
		for (KeyMapping keyMapping : KEY_MAPPING_LOOKUP.getAll(key)) {
			currentMap.set(keyMapping);
			original.call(key);
		}
	}

	@WrapOperation(method = "click", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private static <K, V> V kjs$useForgeMappingIfPossibleForClick(Map<K, V> instance, K o, Operation<V> original, @Share("currentMap") LocalRef<KeyMapping> currentMap) {
		if (currentMap.get() != null) {
			return (V) currentMap.get();
		}

		//noinspection MixinExtrasOperationParameters
		return original.call(instance, o);
	}

	@WrapMethod(method = "set")
	private static void kjs$wrapKeySet(InputConstants.Key key, boolean held, Operation<Void> original, @Share("currentMap") LocalRef<KeyMapping> currentMap) {
		for (KeyMapping keyMapping : KEY_MAPPING_LOOKUP.getAll(key)) {
			currentMap.set(keyMapping);
			original.call(key, held);
		}
	}

	@WrapOperation(method = "set", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private static <K, V> V kjs$useForgeMappingIfPossible(Map<K, V> instance, K o, Operation<V> original, @Share("currentMap") LocalRef<KeyMapping> currentMap) {
		if (currentMap.get() != null) {
			return (V) currentMap.get();
		}

		//noinspection MixinExtrasOperationParameters
		return original.call(instance, o);
	}

	// Kilt-exclusive injects
	@Inject(method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILjava/lang/String;)V", at = @At("TAIL"))
	private void kjs$addKeyValueInForgeMap(String name, InputConstants.Type type, int keyCode, String category, CallbackInfo ci) {
		KEY_MAPPING_LOOKUP.put(this.key, (KeyMapping) (Object) this);
	}
}
