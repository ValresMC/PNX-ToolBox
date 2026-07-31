package valres.toolbox.rcon;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import valres.toolbox.rcon.exception.RconException;

public record RconSettings(@NonNull String address, int port, @NonNull String password, int maxClients, @NonNull Duration authenticationTimeout, int listenBacklog, int maxPacketSize, @NonNull Duration commandTimeout) {
	public static final String DEFAULT_ADDRESS = "0.0.0.0";
	public static final int DEFAULT_MAX_CLIENTS = 50;
	public static final Duration DEFAULT_AUTHENTICATION_TIMEOUT = Duration.ofSeconds(5);
	public static final int DEFAULT_LISTEN_BACKLOG = 50;
	public static final int DEFAULT_MAX_PACKET_SIZE = 65535;
	public static final Duration DEFAULT_COMMAND_TIMEOUT = Duration.ofSeconds(10);
	private static final int MAX_CONFIGURED_PACKET_SIZE = 16 * 1024 * 1024;

	public RconSettings {
		address = requireText(address, "RCON bind address");
		if (port < 1 || port > 65535) {
			throw new RconException("RCON port must be between 1 and 65535");
		}

		password = requireText(password, "RCON password");

		if (maxClients < 1) {
			throw new RconException("RCON maxClients must be at least 1");
		}

		requireSocketTimeout(authenticationTimeout, "RCON authentication timeout");

		if (listenBacklog < 1) {
			throw new RconException("RCON listen backlog must be at least 1");
		}

		if (maxPacketSize < RconPacket.MINIMUM_BODY_SIZE || maxPacketSize > MAX_CONFIGURED_PACKET_SIZE) {
			throw new RconException("RCON packet limit must be between 10 and 16777216 bytes");
		}

		if (password.getBytes(StandardCharsets.UTF_8).length > maxPacketSize - RconPacket.MINIMUM_BODY_SIZE) {
			throw new RconException("RCON password is larger than the configured packet limit");
		}

		requireSocketTimeout(commandTimeout, "RCON command timeout");
	}

	public static @NonNull RconSettings defaults(@NonNull String password, int port) {
		return defaults(password, port, DEFAULT_ADDRESS);
	}

	public static @NonNull RconSettings defaults(@NonNull String password, int port, @NonNull String address) {
		return new RconSettings(address, port, password, DEFAULT_MAX_CLIENTS, DEFAULT_AUTHENTICATION_TIMEOUT, DEFAULT_LISTEN_BACKLOG, DEFAULT_MAX_PACKET_SIZE, DEFAULT_COMMAND_TIMEOUT);
	}

	private static @NonNull String requireText(@NonNull String value, @NonNull String name) {
		Objects.requireNonNull(value, name + " cannot be null");
		if (value.isBlank()) {
			throw new RconException(name + " cannot be blank");
		}
		return value;
	}

	private static void requireSocketTimeout(@NonNull Duration value, @NonNull String name) {
		Objects.requireNonNull(value, name + " cannot be null");
		long milliseconds;
		try {
			milliseconds = value.toMillis();
		} catch (ArithmeticException exception) {
			throw new RconException(name + " is too large", exception);
		}
		if (milliseconds < 1 || milliseconds > Integer.MAX_VALUE) {
			throw new RconException(name + " must be between 1 ms and " + Integer.MAX_VALUE + " ms");
		}
	}

	public @NonNull String getAddress() {
		return this.address;
	}

	public int getPort() {
		return this.port;
	}

	public @NonNull String getPassword() {
		return this.password;
	}

	public int getMaxClients() {
		return this.maxClients;
	}

	public @NonNull Duration getAuthenticationTimeout() {
		return this.authenticationTimeout;
	}

	public int getListenBacklog() {
		return this.listenBacklog;
	}

	public int getMaxPacketSize() {
		return this.maxPacketSize;
	}

	public @NonNull Duration getCommandTimeout() {
		return this.commandTimeout;
	}

	@Override public @NonNull String toString() {
		return "RconSettings[address=%s, port=%d, password=<redacted>, maxClients=%d, authenticationTimeout=%s, listenBacklog=%d, maxPacketSize=%d, commandTimeout=%s]".formatted(this.address, this.port, this.maxClients, this.authenticationTimeout, this.listenBacklog, this.maxPacketSize, this.commandTimeout);
	}
}
