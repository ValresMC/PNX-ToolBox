package valres.toolbox.command.rules;

import org.powernukkitx.command.CommandSender;
import valres.toolbox.command.CommandMessages;

import java.util.Map;
import java.util.Objects;

final public class PermissionRule extends Rule {
    final private String permission;
    final private String message;

    public PermissionRule(String permission) {
        this(permission, null);
    }

    public PermissionRule(String permission, String message) {
        this.permission = Objects.requireNonNull(permission, "Permission cannot be null");
        this.message = message;
    }

    @Override
    public boolean canSee(CommandSender sender) {
        return sender.hasPermission(this.permission);
    }

    @Override
    public void fail(CommandSender sender) {
        Map<String, Object> placeholders = Map.of("permission", this.permission);
        if (this.message == null) {
            CommandMessages.send(sender, CommandMessages.RULE_PERMISSION, placeholders);
        } else {
            CommandMessages.sendRaw(sender, this.message, placeholders);
        }
    }

    public String getPermission() {
        return this.permission;
    }
}
