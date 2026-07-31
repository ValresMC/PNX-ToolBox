package valres.toolbox.command.rules;

import java.util.Map;
import java.util.function.Predicate;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.command.CommandSender;
import valres.toolbox.command.CommandMessages;

public final class PredicateRule extends Rule {
	private final Predicate<CommandSender> visible;
	private final Predicate<CommandSender> executable;
	private final String message;

	public PredicateRule(Predicate<CommandSender> executable, String message) {
		this(sender -> true, executable, message);
	}

	public PredicateRule(@NonNull Predicate<CommandSender> visible, @NonNull Predicate<CommandSender> executable, String message) {
		this.visible = visible;
		this.executable = executable;
		this.message = message;
	}

	@Override public boolean canSee(CommandSender sender) {
		return this.visible.test(sender);
	}

	@Override public boolean canExecute(CommandSender sender) {
		return this.canSee(sender) && this.executable.test(sender);
	}

	@Override public void fail(CommandSender sender) {
		if (this.message == null) {
			CommandMessages.send(sender, CommandMessages.RULE_PREDICATE);
		} else {
			CommandMessages.sendRaw(sender, this.message, Map.of());
		}
	}
}
