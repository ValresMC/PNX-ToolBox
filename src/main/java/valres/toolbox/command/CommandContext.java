package valres.toolbox.command;

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
        CommandSender sender,
        ArgumentsList arguments,
        String label,
        List<String> rawArguments,
        Command command,
        SubCommand subCommand
    ) {
        this.sender = Objects.requireNonNull(sender, "Command sender cannot be null");
        this.arguments = Objects.requireNonNull(arguments, "Arguments cannot be null");
        this.label = Objects.requireNonNull(label, "Command label cannot be null");
        this.rawArguments = List.copyOf(Objects.requireNonNull(rawArguments, "Raw arguments cannot be null"));
        this.command = Objects.requireNonNull(command, "Command cannot be null");
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
