package valres.toolbox.command.argument;

import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import valres.toolbox.command.CommandMessages;
import valres.toolbox.command.exception.ArgumentParseException;

final public class LongArgument extends Argument<Long> {
    public LongArgument(String name) {
        super(name);
    }

    public LongArgument(String name, boolean optional) {
        super(name, optional);
    }

    public LongArgument(String name, long defaultValue) {
        super(name, defaultValue);
    }

    @Override
    protected Long parseValue(CommandSender sender, String value) {
        try {
            if (!value.matches("[+-]?\\d+")) {
                throw new NumberFormatException();
            }
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new ArgumentParseException(
                this.getName(),
                value,
                CommandMessages.format(CommandMessages.ARGUMENT_LONG, "argument", this.getName()),
                exception
            );
        }
    }

    @Override
    public String getTypeName() {
        return "long";
    }

    @Override
    public CommandParameter toCommandParameter() {
        return CommandParameter.newType(this.getName(), this.isOptional(), CommandParamType.INT);
    }
}
