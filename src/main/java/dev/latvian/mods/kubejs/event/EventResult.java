package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import me.textrue.kubejs.fabric.thirdparty.util.event.CompoundEventResult;
import me.textrue.kubejs.fabric.thirdparty.util.event.ThirdPartyEventResult;
import net.fabricmc.fabric.api.util.TriState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class EventResult {
	public enum Type {
		ERROR(ThirdPartyEventResult.pass()),
		PASS(ThirdPartyEventResult.pass()),
		INTERRUPT_DEFAULT(ThirdPartyEventResult.interruptDefault()),
		INTERRUPT_FALSE(ThirdPartyEventResult.interruptFalse()),
		INTERRUPT_TRUE(ThirdPartyEventResult.interruptTrue());

		private final EventResult defaultResult;
		public final ThirdPartyEventResult defaultVanillaResult;
		private final EventExit defaultExit;

		Type(ThirdPartyEventResult defaultVanillaResult) {
			this.defaultResult = new EventResult(null, this, null);
			this.defaultVanillaResult = defaultVanillaResult;
			this.defaultExit = new EventExit(this.defaultResult);
		}

		public EventExit exit(@Nullable Context cx, @Nullable Object value) {
			return value == null ? defaultExit : new EventExit(new EventResult(cx, this, value));
		}
	}

	public static final EventResult PASS = Type.PASS.defaultResult;

	private final Context cx;
	private final Type type;
	private final Object value;

	private EventResult(@Nullable Context cx, Type type, @Nullable Object value) {
		this.cx = cx;
		this.type = type;
		this.value = value;
	}

	@Nullable
	public Context cx() {
		return cx;
	}

	public Type type() {
		return type;
	}

	@Nullable
	public Object value() {
		return value;
	}

	public boolean override() {
		return type != Type.PASS;
	}

	public boolean pass() {
		return type == Type.PASS;
	}

	public boolean interruptDefault() {
		return type == Type.INTERRUPT_DEFAULT;
	}

	public boolean interruptFalse() {
		return type == Type.INTERRUPT_FALSE;
	}

	public boolean interruptTrue() {
		return type == Type.INTERRUPT_TRUE;
	}

//	public boolean applyCancel(ICancellableEvent event) {
//		if (interruptFalse()) {
//			event.setCanceled(true);
//			return true;
//		}
//
//		return false;
//	}

	public <T> CompoundEventResult<T> compoundResult() {
		return switch (type) {
			case INTERRUPT_DEFAULT -> CompoundEventResult.interruptDefault(UtilsJS.cast(value));
			case INTERRUPT_FALSE -> CompoundEventResult.interruptFalse(UtilsJS.cast(value));
			case INTERRUPT_TRUE -> CompoundEventResult.interruptTrue(UtilsJS.cast(value));
			default -> CompoundEventResult.pass();
		};
	}

	public void applyTristate(Consumer<TriState> consumer) {
		if (interruptFalse()) {
			consumer.accept(TriState.FALSE);
		} else if (interruptTrue()) {
			consumer.accept(TriState.TRUE);
		}
	}
}
