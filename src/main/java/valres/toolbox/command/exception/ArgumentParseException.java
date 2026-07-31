package valres.toolbox.command.exception;

public final class ArgumentParseException extends CommandException {
	private final String argument;
	private final String value;

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
