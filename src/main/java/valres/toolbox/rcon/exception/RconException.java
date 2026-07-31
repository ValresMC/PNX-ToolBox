package valres.toolbox.rcon.exception;

public class RconException extends RuntimeException {
	public RconException(String message) {
		super(message);
	}

	public RconException(String message, Throwable cause) {
		super(message, cause);
	}
}
