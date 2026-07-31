package valres.toolbox.command.rules;

import org.powernukkitx.command.CommandSender;

public abstract class Rule {
	public boolean canSee(CommandSender sender) {
		return true;
	}

	public boolean canExecute(CommandSender sender) {
		return this.canSee(sender);
	}

	public void onPassed(CommandSender sender) {
	}

	public void onExecuted(CommandSender sender) {
	}

	public abstract void fail(CommandSender sender);
}
