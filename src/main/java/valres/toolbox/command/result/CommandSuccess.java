package valres.toolbox.command.result;

import org.powernukkitx.command.CommandSender;
import valres.toolbox.command.ArgumentsList;
import valres.toolbox.command.CommandContext;

import java.util.Objects;

public record CommandSuccess(
    CommandContext context,
    Object returnValue
) implements CommandResult {
    public CommandSuccess(CommandContext context, Object returnValue) {
        this.context = Objects.requireNonNull(context, "Command context cannot be null");
        this.returnValue = returnValue;
    }

    @Override
    public CommandSender sender() {
        return this.context.sender();
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    public ArgumentsList getArguments() {
        return this.context.arguments();
    }

    public <T> T getReturnValue(Class<T> type) {
        return type.cast(this.returnValue);
    }
}
