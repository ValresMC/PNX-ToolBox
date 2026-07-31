package valres.toolbox.command.rules;

import java.util.Map;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.RemoteConsoleCommandSender;
import valres.toolbox.command.CommandMessages;

public final class OnlyRconRule extends Rule {
	private final String message;

	public OnlyRconRule() {
		this(null);
	}

	public OnlyRconRule(String message) {
		this.message = message;
	}

	@Override public boolean canSee(CommandSender sender) {
		return sender instanceof RemoteConsoleCommandSender;
	}

	@Override public void fail(CommandSender sender) {
		if (this.message == null) {
			CommandMessages.send(sender, CommandMessages.RULE_ONLY_RCON);
		} else {
			CommandMessages.sendRaw(sender, this.message, Map.of());
		}
	}
}
