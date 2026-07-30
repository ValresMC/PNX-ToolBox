package valres.toolbox.command.rules;

import org.powernukkitx.command.CommandSender;

import java.time.Duration;
import java.util.function.Predicate;

final public class Rules {
    private Rules() {
    }

    public static PermissionRule permission(String permission) {
        return new PermissionRule(permission);
    }

    public static OnlyPlayerRule onlyPlayer() {
        return new OnlyPlayerRule();
    }

    public static OnlyConsoleRule onlyConsole() {
        return new OnlyConsoleRule();
    }

    public static OnlyRconRule onlyRcon() {
        return new OnlyRconRule();
    }

    public static CooldownRule cooldown(Duration duration) {
        return new CooldownRule(duration);
    }

    public static PredicateRule predicate(Predicate<CommandSender> predicate, String failureMessage) {
        return new PredicateRule(predicate, failureMessage);
    }
}
