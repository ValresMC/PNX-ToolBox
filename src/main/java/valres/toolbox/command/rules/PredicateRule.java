package valres.toolbox.command.rules;

import org.powernukkitx.command.CommandSender;
import valres.toolbox.command.CommandMessages;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

final public class PredicateRule extends Rule {
    final private Predicate<CommandSender> visible;
    final private Predicate<CommandSender> executable;
    final private String message;

    public PredicateRule(Predicate<CommandSender> executable, String message) {
        this(sender -> true, executable, message);
    }

    public PredicateRule(Predicate<CommandSender> visible, Predicate<CommandSender> executable, String message) {
        this.visible = Objects.requireNonNull(visible, "Visibility predicate cannot be null");
        this.executable = Objects.requireNonNull(executable, "Execution predicate cannot be null");
        this.message = message;
    }

    @Override
    public boolean canSee(CommandSender sender) {
        return this.visible.test(sender);
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        return this.canSee(sender) && this.executable.test(sender);
    }

    @Override
    public void fail(CommandSender sender) {
        if (this.message == null) {
            CommandMessages.send(sender, CommandMessages.RULE_PREDICATE);
        } else {
            CommandMessages.sendRaw(sender, this.message, Map.of());
        }
    }
}
