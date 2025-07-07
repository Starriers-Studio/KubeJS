package me.textrue.kubejs.fabric.thirdparty.mixin;

import com.google.common.base.Throwables;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ContextChain;

import me.textrue.kubejs.fabric.thirdparty.events.CommandPerformEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Commands.class)
public class CommandsMixin {
	@ModifyVariable(method = "finishParsing",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;validateParseResults(Lcom/mojang/brigadier/ParseResults;)V"), argsOnly = true)
	private static ParseResults<CommandSourceStack> commandPerformParseResults(ParseResults<CommandSourceStack> results) {
		var event = new CommandPerformEvent(results, null);
		if (CommandPerformEvent.EVENT.invoker().act(event).isPresent()) {
			if (event.getThrowable() != null) {
				Throwables.throwIfUnchecked(event.getThrowable());
			}
			return null;
		}
		return event.getResults();
	}

	@Inject(method = "finishParsing",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;validateParseResults(Lcom/mojang/brigadier/ParseResults;)V"), cancellable = true)
	private static void nullParseResult(ParseResults<CommandSourceStack> results, String command, CommandSourceStack stack, CallbackInfoReturnable<ContextChain<CommandSourceStack>> cir) {
		if (results == null) cir.setReturnValue(null);
	}
}
