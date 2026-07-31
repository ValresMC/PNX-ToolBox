package valres.toolbox.command.rules;

import java.util.Map;
import org.powernukkitx.command.CommandSender;
import valres.toolbox.command.CommandMessages;

public final class OnlyPlayerRule extends Rule {
	private final String message;

	public OnlyPlayerRule() {
		this(null);
	}

	public OnlyPlayerRule(String message) {
		this.message = message;
	}

	@Override public boolean canSee(CommandSender sender) {
		return sender.isPlayer();
	}

	@Override public void fail(CommandSender sender) {
		if (this.message == null) {
			CommandMessages.send(sender, CommandMessages.RULE_ONLY_PLAYER);
		} else {
			CommandMessages.sendRaw(sender, this.message, Map.of());
		}
	}
}
