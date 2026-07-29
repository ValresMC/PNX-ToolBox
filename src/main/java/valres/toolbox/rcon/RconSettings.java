package valres.toolbox.rcon;

import valres.toolbox.rcon.exception.RconException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

public final class RconSettings {
    final public static String DEFAULT_ADDRESS = "0.0.0.0";
    final public static int DEFAULT_MAX_CLIENTS = 50;
    final public static Duration DEFAULT_AUTHENTICATION_TIMEOUT = Duration.ofSeconds(5);
    final public static int DEFAULT_LISTEN_BACKLOG = 50;
    final public static int DEFAULT_MAX_PACKET_SIZE = 65_535;
    final public static Duration DEFAULT_COMMAND_TIMEOUT = Duration.ofSeconds(10);
    final private static int MAX_CONFIGURED_PACKET_SIZE = 16 * 1024 * 1024;

    final private String address;
    final private int port;
    final private String password;
    final private int maxClients;
    final private Duration authenticationTimeout;
    final private int listenBacklog;
    final private int maxPacketSize;
    final private Duration commandTimeout;

    public RconSettings(String address, int port, String password, int maxClients, Duration authenticationTimeout, int listenBacklog, int maxPacketSize, Duration commandTimeout) {
        this.address = requireText(address, "RCON bind address");
        if (port < 1 || port > 65_535) {
            throw new RconException(
                "RCON port must be between 1 and 65535"
            );
        }

        this.port = port;
        this.password = requireText(password, "RCON password");

        if (this.password.getBytes(StandardCharsets.UTF_8).length > maxPacketSize - RconPacket.MINIMUM_BODY_SIZE) {
            throw new RconException(
                "RCON password is larger than the configured packet limit"
            );
        }

        if (maxClients < 1) {
            throw new RconException(
                "RCON maxClients must be at least 1"
            );
        }

        this.maxClients = maxClients;
        this.authenticationTimeout = requireSocketTimeout(authenticationTimeout, "RCON authentication timeout");

        if (listenBacklog < 1) {
            throw new RconException(
                "RCON listen backlog must be at least 1"
            );
        }

        this.listenBacklog = listenBacklog;

        if (maxPacketSize < RconPacket.MINIMUM_BODY_SIZE || maxPacketSize > MAX_CONFIGURED_PACKET_SIZE) {
            throw new RconException(
                "RCON packet limit must be between 10 and 16777216 bytes"
            );
        }

        this.maxPacketSize = maxPacketSize;
        this.commandTimeout = requireSocketTimeout(commandTimeout, "RCON command timeout");
    }

    public static RconSettings defaults(String password, int port) {
        return defaults(password, port, DEFAULT_ADDRESS);
    }

    public static RconSettings defaults(String password, int port, String address) {
        return new RconSettings(address, port, password, DEFAULT_MAX_CLIENTS, DEFAULT_AUTHENTICATION_TIMEOUT, DEFAULT_LISTEN_BACKLOG, DEFAULT_MAX_PACKET_SIZE, DEFAULT_COMMAND_TIMEOUT);
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name + " cannot be null");
        if (value.isBlank()) {
            throw new RconException(
                name + " cannot be blank"
            );
        }
        return value;
    }

    private static Duration requireSocketTimeout(Duration value, String name) {
        Objects.requireNonNull(value, name + " cannot be null");
        long milliseconds;
        try {
            milliseconds = value.toMillis();
        } catch (ArithmeticException exception) {
            throw new RconException(
                name + " is too large", exception
            );
        }
        if (milliseconds < 1 || milliseconds > Integer.MAX_VALUE) {
            throw new RconException(
                name + " must be between 1 ms and " + Integer.MAX_VALUE + " ms"
            );
        }
        return value;
    }

    public String getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    public String getPassword() {
        return this.password;
    }

    public int getMaxClients() {
        return this.maxClients;
    }

    public Duration getAuthenticationTimeout() {
        return this.authenticationTimeout;
    }

    public int getListenBacklog() {
        return this.listenBacklog;
    }

    public int getMaxPacketSize() {
        return this.maxPacketSize;
    }

    public Duration getCommandTimeout() {
        return this.commandTimeout;
    }
}
