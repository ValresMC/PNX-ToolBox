package valres.toolbox.command.result;

import org.powernukkitx.command.CommandSender;

public interface CommandResult {
    CommandSender sender();
    boolean isSuccess();
}
