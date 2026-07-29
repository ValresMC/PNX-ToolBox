package valres.toolbox.rcon;

import org.junit.jupiter.api.Test;
import valres.toolbox.rcon.exception.RconException;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class RconSettingsTest {
    @Test
    void createsDefaultSettings() {
        RconSettings settings = RconSettings.defaults("secret", 19132);

        assertEquals("0.0.0.0", settings.getAddress());
        assertEquals(19132, settings.getPort());
        assertEquals("secret", settings.getPassword());
        assertEquals(50, settings.getMaxClients());
        assertEquals(RconSettings.DEFAULT_AUTHENTICATION_TIMEOUT, settings.getAuthenticationTimeout());
        assertEquals(RconSettings.DEFAULT_COMMAND_TIMEOUT, settings.getCommandTimeout());
    }

    @Test
    void rejectsInvalidPort() {
        assertThrows(RconException.class, () -> RconSettings.defaults("secret", 0));
        assertThrows(RconException.class, () -> RconSettings.defaults("secret", 65_536));
    }

    @Test
    void rejectsBlankPassword() {
        assertThrows(RconException.class, () -> RconSettings.defaults("", 19132));
        assertThrows(RconException.class, () -> RconSettings.defaults("   ", 19132));
    }

    @Test
    void rejectsInvalidTimeouts() {
        assertThrows(RconException.class, () -> settingsWithTimeouts(Duration.ZERO, Duration.ofSeconds(1)));
        assertThrows(RconException.class, () -> settingsWithTimeouts(Duration.ofSeconds(1), Duration.ZERO));
        assertThrows(RconException.class, () -> settingsWithTimeouts(Duration.ofNanos(1), Duration.ofSeconds(1)));
    }

    private static RconSettings settingsWithTimeouts(Duration authentication, Duration command) {
        return new RconSettings("127.0.0.1", 19132, "secret", 1, authentication, 1, 100, command);
    }
}
