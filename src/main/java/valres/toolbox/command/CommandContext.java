package valres.toolbox.command;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;

import java.util.List;
import java.util.Objects;

public record CommandContext(
    CommandSender sender,
    ArgumentsList arguments,
    String label,
    List<String> rawArguments,
    Command command,
    SubCommand subCommand
) {
    public CommandContext(
        @NonNull CommandSender sender,
        @NonNull ArgumentsList arguments,
        @NonNull String label,
        @NonNull List<String> rawArguments,
        @NonNull Command command,
        SubCommand subCommand
    ) {
        this.sender = sender;
        this.arguments = arguments;
        this.label = label;
        this.rawArguments = List.copyOf(rawArguments);
        this.command = command;
        this.subCommand = subCommand;
    }

    public Player getPlayer() {
        Player player = this.sender.asPlayer();
        if (player == null) {
            throw new IllegalStateException(
                "The command sender is not a player"
            );
        }

        return player;
    }

    public boolean isSubCommand() {
        return this.subCommand != null;
    }

    public void reply(String message) {
        this.sender.sendMessage(message);
    }
}
