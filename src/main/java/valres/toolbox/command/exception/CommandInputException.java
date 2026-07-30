package valres.toolbox.command.exception;

import valres.toolbox.command.result.CommandFailureReason;

import java.util.Map;
import java.util.Objects;

final public class CommandInputException extends CommandException {
    final private CommandFailureReason reason;
    final private Map<String, Object> details;

    public CommandInputException(CommandFailureReason reason, String message) {
        this(reason, message, Map.of());
    }

    public CommandInputException(CommandFailureReason reason, String message, Map<String, Object> details) {
        super(message);

        this.reason = Objects.requireNonNull(reason, "Failure reason cannot be null");
        this.details = Map.copyOf(Objects.requireNonNull(details, "Failure details cannot be null"));
    }

    public CommandFailureReason getReason() {
        return this.reason;
    }

    public Map<String, Object> getDetails() {
        return this.details;
    }
}
