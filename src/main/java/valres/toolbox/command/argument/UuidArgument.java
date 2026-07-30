package valres.toolbox.command.argument;

import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import valres.toolbox.command.CommandMessages;
import valres.toolbox.command.exception.ArgumentParseException;

import java.util.UUID;

final public class UuidArgument extends Argument<UUID> {
    public UuidArgument(String name) {
        super(name);
    }

    public UuidArgument(String name, boolean optional) {
        super(name, optional);
    }

    @Override
    protected UUID parseValue(CommandSender sender, String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            throw new ArgumentParseException(
                this.getName(),
                value,
                CommandMessages.format(CommandMessages.ARGUMENT_UUID, "argument", this.getName()),
                exception
            );
        }
    }

    @Override
    public String getTypeName() {
        return "uuid";
    }

    @Override
    public CommandParameter toCommandParameter() {
        return CommandParameter.newType(this.getName(), this.isOptional(), CommandParamType.ID);
    }
}
