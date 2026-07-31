package valres.toolbox.command.exception;

import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import valres.toolbox.command.result.CommandFailureReason;

public final class CommandInputException extends CommandException {
	private final CommandFailureReason reason;
	private final Map<String, Object> details;

	public CommandInputException(@NonNull CommandFailureReason reason, @NonNull String message) {
		this(reason, message, Map.of());
	}

	public CommandInputException(@NonNull CommandFailureReason reason, @NonNull String message, @NonNull Map<String, Object> details) {
		super(message);

		this.reason = Objects.requireNonNull(reason, "Failure reason cannot be null");
		this.details = Map.copyOf(Objects.requireNonNull(details, "Failure details cannot be null"));
	}

	public @NonNull CommandFailureReason getReason() {
		return this.reason;
	}

	public @NonNull Map<String, Object> getDetails() {
		return this.details;
	}
}
