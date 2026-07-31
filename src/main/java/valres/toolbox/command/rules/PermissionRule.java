package valres.toolbox.command.rules;

import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.command.CommandSender;
import valres.toolbox.command.CommandMessages;

public final class PermissionRule extends Rule {
	private final String permission;
	private final String message;

	public PermissionRule(@NonNull String permission) {
		this(permission, null);
	}

	public PermissionRule(@NonNull String permission, String message) {
		this.permission = Objects.requireNonNull(permission, "Permission cannot be null");
		this.message = message;
	}

	@Override public boolean canSee(CommandSender sender) {
		return sender.hasPermission(this.permission);
	}

	@Override public void fail(CommandSender sender) {
		Map<String, Object> placeholders = Map.of("permission", this.permission);
		if (this.message == null) {
			CommandMessages.send(sender, CommandMessages.RULE_PERMISSION, placeholders);
		} else {
			CommandMessages.sendRaw(sender, this.message, placeholders);
		}
	}

	public @NonNull String getPermission() {
		return this.permission;
	}
}
