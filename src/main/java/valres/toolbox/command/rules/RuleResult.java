package valres.toolbox.command.rules;

import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record RuleResult(@NonNull List<@NonNull Rule> failed) {
	public RuleResult(@NonNull List<@NonNull Rule> failed) {
		this.failed = List.copyOf(Objects.requireNonNull(failed, "Failed rules cannot be null"));
	}

	public boolean isSuccess() {
		return this.failed.isEmpty();
	}
}
