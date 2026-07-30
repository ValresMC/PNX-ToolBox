package valres.toolbox.command.exception;

final public class CommandConfigurationException extends CommandException {
    public CommandConfigurationException(String message) {
        super(message);
    }

    public CommandConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
