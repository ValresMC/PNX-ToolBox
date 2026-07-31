package valres.toolbox.rcon;

import org.jspecify.annotations.NonNull;
import valres.toolbox.rcon.exception.RconProtocolException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public record RconPacket(
    int requestId,
    int type,
    @NonNull String payload
) {
    final public static int MINIMUM_BODY_SIZE = 10;

    public RconPacket {
        payload = Objects.requireNonNull(
            payload,
            "RCON payload cannot be null"
        );

        if (payload.indexOf('\0') >= 0) {
            throw new RconProtocolException(
                "RCON payload cannot contain a null character"
            );
        }
    }

    public RconPacket(int requestId, int type, @NonNull byte[] payload) {
        this(
            requestId,
            type,
            decodeUtf8(Objects.requireNonNull(
                payload,
                "RCON payload cannot be null"
            ))
        );
    }

    public @NonNull byte[] encode() {
        byte[] payloadBytes = this.payload.getBytes(StandardCharsets.UTF_8);
        int bodySize = payloadBytes.length + MINIMUM_BODY_SIZE;
        ByteBuffer buffer = ByteBuffer.allocate(bodySize + Integer.BYTES).order(ByteOrder.LITTLE_ENDIAN);

        buffer.putInt(bodySize);
        buffer.putInt(this.requestId);
        buffer.putInt(this.type);
        buffer.put(payloadBytes);
        buffer.put((byte) 0);
        buffer.put((byte) 0);
        return buffer.array();
    }

    public static @NonNull RconPacket decode(@NonNull byte[] body) {
        Objects.requireNonNull(body, "RCON packet body cannot be null");
        if (body.length < MINIMUM_BODY_SIZE) {
            throw new RconProtocolException(
                "RCON packet body is shorter than 10 bytes"
            );
        }
        if (body[body.length - 2] != 0 || body[body.length - 1] != 0) {
            throw new RconProtocolException(
                "RCON packet is missing its null terminators"
            );
        }

        ByteBuffer buffer = ByteBuffer.wrap(body).order(ByteOrder.LITTLE_ENDIAN);
        int requestId = buffer.getInt();
        int type = buffer.getInt();
        byte[] payload = Arrays.copyOfRange(body, 8, body.length - 2);
        return new RconPacket(requestId, type, decodeUtf8(payload));
    }

    private static @NonNull String decodeUtf8(@NonNull byte[] payload) {
        try {
            CharBuffer decoded = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(payload));
            return decoded.toString();
        } catch (CharacterCodingException exception) {
            throw new RconProtocolException(
                "RCON payload is not valid UTF-8", exception
            );
        }
    }

    public @NonNull ByteBuffer toBuffer() {
        return ByteBuffer.wrap(this.encode()).order(ByteOrder.LITTLE_ENDIAN);
    }

    public int getRequestId() {
        return this.requestId;
    }

    public int getType() {
        return this.type;
    }

    public @NonNull String getPayload() {
        return this.payload;
    }

    @Override public @NonNull String toString() {
        return "RconPacket[requestId=%d, type=%d, payloadBytes=%d]".formatted(
            this.requestId,
            this.type,
            this.payload.getBytes(StandardCharsets.UTF_8).length
        );
    }
}
