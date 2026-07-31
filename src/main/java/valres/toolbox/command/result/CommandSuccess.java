package valres.toolbox.command.result;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.command.CommandSender;
import valres.toolbox.command.ArgumentsList;
import valres.toolbox.command.CommandContext;

public record CommandSuccess(CommandContext context, Object returnValue) implements CommandResult {
	public CommandSuccess(@NonNull CommandContext context, Object returnValue) {
		this.context = context;
		this.returnValue = returnValue;
	}

	@Override public CommandSender sender() {
		return this.context.sender();
	}

	@Override public boolean isSuccess() {
		return true;
	}

	public ArgumentsList getArguments() {
		return this.context.arguments();
	}

	public <T> T getReturnValue(Class<T> type) {
		return type.cast(this.returnValue);
	}
}
