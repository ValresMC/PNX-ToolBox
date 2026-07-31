package valres.toolbox.command.result;

import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.command.CommandSender;
import valres.toolbox.command.rules.Rule;

public record CommandFailure(CommandSender sender, CommandFailureReason reason, String message, String usage, Map<String, Object> details, List<Rule> failedRules, Throwable cause) implements CommandResult {
	public CommandFailure(@NonNull CommandSender sender, @NonNull CommandFailureReason reason, @NonNull String message, String usage, @NonNull Map<String, Object> details, @NonNull List<Rule> failedRules, Throwable cause) {
		this.sender = sender;
		this.reason = reason;
		this.message = message;
		this.usage = usage;
		this.details = Map.copyOf(details);
		this.failedRules = List.copyOf(failedRules);
		this.cause = cause;
	}

	public CommandFailure(CommandSender sender, CommandFailureReason reason, String message, String usage) {
		this(sender, reason, message, usage, Map.of(), List.of(), null);
	}

	@Override public boolean isSuccess() {
		return false;
	}
}
