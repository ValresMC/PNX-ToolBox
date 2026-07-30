package valres.toolbox.command.argument;

import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import valres.toolbox.command.CommandMessages;
import valres.toolbox.command.exception.ArgumentParseException;

final public class PlayerArgument extends Argument<Player> {
    final private boolean exact;

    public PlayerArgument(String name) {
        this(name, false, false, null, true);
    }

    public PlayerArgument(String name, boolean optional) {
        this(name, optional, false, null, true);
    }

    public PlayerArgument(String name, boolean optional, boolean exact) {
        this(name, optional, false, null, exact);
    }

    public PlayerArgument(String name, Player defaultValue, boolean exact) {
        this(name, true, true, defaultValue, exact);
    }

    private PlayerArgument(String name, boolean optional, boolean hasDefault, Player defaultValue, boolean exact) {
        super(name, optional, hasDefault, defaultValue);

        this.exact = exact;
    }

    @Override
    protected Player parseValue(CommandSender sender, String value) {
        Player player = this.exact
            ? sender.getServer().getPlayerExact(value)
            : sender.getServer().getPlayer(value);

        if (player == null) {
            throw new ArgumentParseException(
                this.getName(),
                value,
                CommandMessages.format(CommandMessages.ARGUMENT_PLAYER_NOT_ONLINE, "player", value)
            );
        }

        return player;
    }

    @Override
    public String getTypeName() {
        return "player";
    }

    @Override
    public CommandParameter toCommandParameter() {
        return CommandParameter.newType(this.getName(), this.isOptional(), CommandParamType.SELECTION);
    }
}
