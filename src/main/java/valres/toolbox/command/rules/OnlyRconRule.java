package valres.toolbox.command.rules;

import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.RemoteConsoleCommandSender;
import valres.toolbox.command.CommandMessages;

import java.util.Map;

final public class OnlyRconRule extends Rule {
    final private String message;

    public OnlyRconRule() {
        this(null);
    }

    public OnlyRconRule(String message) {
        this.message = message;
    }

    @Override
    public boolean canSee(CommandSender sender) {
        return sender instanceof RemoteConsoleCommandSender;
    }

    @Override
    public void fail(CommandSender sender) {
        if (this.message == null) {
            CommandMessages.send(sender, CommandMessages.RULE_ONLY_RCON);
        } else {
            CommandMessages.sendRaw(sender, this.message, Map.of());
        }
    }
}
