package valres.toolbox.command.rules;

import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import valres.toolbox.command.CommandMessages;
import valres.toolbox.command.exception.CommandConfigurationException;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final public class CooldownRule extends Rule {
    final private long cooldownNanos;
    final private String message;
    final private Map<String, Long> expirations = new ConcurrentHashMap<>();

    public CooldownRule(Duration cooldown) {
        this(cooldown, null);
    }

    public CooldownRule(Duration cooldown, String message) {
        try {
            this.cooldownNanos = cooldown.toNanos();
        } catch (ArithmeticException exception) {
            throw new CommandConfigurationException(
                "Command cooldown is too large", exception
            );
        }
        if (this.cooldownNanos < 0) {
            throw new CommandConfigurationException(
                "Command cooldown cannot be negative"
            );
        }

        this.message = message;
    }

    @Override public boolean canExecute(CommandSender sender) {
        return this.getRemaining(sender).isZero();
    }

    @Override public void fail(CommandSender sender) {
        long seconds = Math.max(1, (long) Math.ceil(this.getRemaining(sender).toNanos() / 1_000_000_000.0));
        Map<String, Object> placeholders = Map.of("time", seconds);
        if (this.message == null) {
            CommandMessages.send(sender, CommandMessages.RULE_COOLDOWN, placeholders);
        } else {
            CommandMessages.sendRaw(sender, this.message, placeholders);
        }
    }

    @Override public void onExecuted(CommandSender sender) {
        if (this.cooldownNanos == 0) {
            return;
        }

        this.expirations.put(this.getSenderKey(sender), System.nanoTime() + this.cooldownNanos);
    }

    public Duration getRemaining(CommandSender sender) {
        String key = this.getSenderKey(sender);
        Long expiration = this.expirations.get(key);
        if (expiration == null) {
            return Duration.ZERO;
        }

        long remaining = expiration - System.nanoTime();
        if (remaining <= 0) {
            this.expirations.remove(key, expiration);
            return Duration.ZERO;
        }

        return Duration.ofNanos(remaining);
    }

    public void reset(CommandSender sender) {
        this.expirations.remove(this.getSenderKey(sender));
    }

    public void resetAll() {
        this.expirations.clear();
    }

    private String getSenderKey(CommandSender sender) {
        Player player = sender.asPlayer();
        if (player != null) {
            return "player:" + player.getUniqueId();
        }

        return sender.getClass().getName() + ":" + sender.getName().toLowerCase(Locale.ROOT);
    }
}
