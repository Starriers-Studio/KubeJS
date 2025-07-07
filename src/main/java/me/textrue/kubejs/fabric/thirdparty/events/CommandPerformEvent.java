package me.textrue.kubejs.fabric.thirdparty.events;

import com.mojang.brigadier.ParseResults;
import me.textrue.kubejs.fabric.thirdparty.util.event.ThirdPartyEventActor;
import me.textrue.kubejs.fabric.thirdparty.util.event.ThirdPartyEventResult;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

public class CommandPerformEvent {
	public static final Event<ThirdPartyEventActor<CommandPerformEvent>> EVENT = EventFactory.createArrayBacked(ThirdPartyEventActor.class, (listeners) -> (event) -> {
		for (ThirdPartyEventActor<CommandPerformEvent> listener : listeners) {
			ThirdPartyEventResult result = listener.act(event);
			if (result != ThirdPartyEventResult.pass()) {
				return result;
			}
		}
		return ThirdPartyEventResult.pass();
	});

	private ParseResults<CommandSourceStack> results;
	@Nullable
	private Throwable throwable;

	public CommandPerformEvent(ParseResults<CommandSourceStack> results, @Nullable Throwable throwable) {
		this.results = results;
		this.throwable = throwable;
	}

	public ParseResults<CommandSourceStack> getResults() {
		return results;
	}

	public void setResults(ParseResults<CommandSourceStack> results) {
		this.results = results;
	}

	@Nullable
	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(@Nullable Throwable throwable) {
		this.throwable = throwable;
	}
}
