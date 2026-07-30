package valres.toolbox.command.argument;

import org.powernukkitx.Server;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.level.Level;
import valres.toolbox.command.CommandMessages;
import valres.toolbox.command.exception.ArgumentParseException;

import java.util.Collection;

final public class LevelArgument extends Argument<Level> {
    final private CommandEnum commandEnum;

    public LevelArgument(String name) {
        this(name, false);
    }

    public LevelArgument(String name, boolean optional) {
        super(name, optional);

        this.commandEnum = new CommandEnum(name + "Levels", this::getLevelNames);
    }

    @Override
    protected Level parseValue(CommandSender sender, String value) {
        Level level = sender.getServer().getLevelByName(value);
        if (level == null) {
            throw new ArgumentParseException(
                this.getName(),
                value,
                CommandMessages.format(CommandMessages.ARGUMENT_LEVEL_NOT_LOADED, "level", value)
            );
        }

        return level;
    }

    @Override
    public String getTypeName() {
        return "level";
    }

    @Override
    public CommandParameter toCommandParameter() {
        return CommandParameter.newEnum(this.getName(), this.isOptional(), this.commandEnum);
    }

    public void refresh() {
        this.commandEnum.updateSoftEnum();
    }

    private Collection<String> getLevelNames() {
        return Server.getInstance().getLevels().values().stream()
            .map(Level::getName)
            .distinct()
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .toList();
    }
}
