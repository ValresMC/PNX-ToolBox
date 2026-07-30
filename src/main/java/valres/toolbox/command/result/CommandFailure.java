package valres.toolbox.command.result;

import org.powernukkitx.command.CommandSender;
import valres.toolbox.command.rules.Rule;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record CommandFailure(
    CommandSender sender,
    CommandFailureReason reason,
    String message,
    String usage,
    Map<String, Object> details, List<Rule> failedRules,
    Throwable cause
) implements CommandResult {
    public CommandFailure(
        CommandSender sender,
        CommandFailureReason reason,
        String message,
        String usage,
        Map<String, Object> details,
        List<Rule> failedRules,
        Throwable cause
    ) {
        this.sender = Objects.requireNonNull(sender, "Command sender cannot be null");
        this.reason = Objects.requireNonNull(reason, "Failure reason cannot be null");
        this.message = Objects.requireNonNullElse(message, "Command failed");
        this.usage = usage;
        this.details = Map.copyOf(Objects.requireNonNull(details, "Failure details cannot be null"));
        this.failedRules = List.copyOf(Objects.requireNonNull(failedRules, "Failed rules cannot be null"));
        this.cause = cause;
    }

    public CommandFailure(CommandSender sender, CommandFailureReason reason, String message, String usage) {
        this(sender, reason, message, usage, Map.of(), List.of(), null);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }
}
