package valres.toolbox.command.rules;

import java.util.List;
import java.util.Objects;

public record RuleResult(
    List<Rule> failed
) {
    public RuleResult(List<Rule> failed) {
        this.failed = List.copyOf(Objects.requireNonNull(failed, "Failed rules cannot be null"));
    }

    public boolean isSuccess() {
        return this.failed.isEmpty();
    }
}
