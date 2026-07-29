package valres.toolbox.rcon;

import org.junit.jupiter.api.Test;
import valres.toolbox.rcon.exception.RconProtocolException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class RconPacketTest {
    @Test
    void encodesAndDecodesPacket() {
        RconPacket original = new RconPacket(42, 2, "say Héllo");
        byte[] encoded = original.encode();

        int declaredSize = ByteBuffer.wrap(encoded, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        assertEquals(encoded.length - 4, declaredSize);

        RconPacket decoded = RconPacket.decode(Arrays.copyOfRange(encoded, 4, encoded.length));
        assertEquals(original.getRequestId(), decoded.getRequestId());
        assertEquals(original.getType(), decoded.getType());
        assertEquals(original.getPayload(), decoded.getPayload());
    }

    @Test
    void usesLittleEndianIntegers() {
        byte[] encoded = new RconPacket(0x01020304, 3, "").encode();
        assertArrayEquals(new byte[]{4, 3, 2, 1}, Arrays.copyOfRange(encoded, 4, 8));
    }

    @Test
    void rejectsPacketsThatAreTooSmall() {
        assertThrows(RconProtocolException.class, () -> RconPacket.decode(new byte[9]));
    }

    @Test
    void rejectsMissingNullTerminators() {
        byte[] body = new byte[10];
        body[body.length - 1] = 1;
        assertThrows(RconProtocolException.class, () -> RconPacket.decode(body));
    }

    @Test
    void rejectsMalformedUtf8() {
        byte[] body = ByteBuffer.allocate(11)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(1)
                .putInt(2)
                .put((byte) 0xC3)
                .put((byte) 0)
                .put((byte) 0)
                .array();
        assertThrows(RconProtocolException.class, () -> RconPacket.decode(body));
    }
}
