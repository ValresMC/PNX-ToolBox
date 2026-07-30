package valres.toolbox.command.exception;

final public class ArgumentParseException extends CommandException {
    final private String argument;
    final private String value;

    public ArgumentParseException(String argument, String value, String message) {
        super(message);

        this.argument = argument;
        this.value = value;
    }

    public ArgumentParseException(String argument, String value, String message, Throwable cause) {
        super(message, cause);

        this.argument = argument;
        this.value = value;
    }

    public String getArgument() {
        return this.argument;
    }

    public String getValue() {
        return this.value;
    }
}
