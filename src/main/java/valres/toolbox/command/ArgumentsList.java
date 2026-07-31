package valres.toolbox.command;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

final public class ArgumentsList implements Iterable<Map.Entry<String, Object>> {
    final private CommandSender sender;
    final private Map<String, Object> values;

    public ArgumentsList(
        @NonNull CommandSender sender,
        @NonNull Map<String, Object> values
    ) {
        this.sender = sender;
        this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public boolean has(String name) {
        return this.values.containsKey(name);
    }

    public Object get(String name) {
        return this.values.get(name);
    }

    public Object get(String name, Object defaultValue) {
        return this.values.getOrDefault(name, defaultValue);
    }

    public <T> T get(String name, Class<T> type) {
        Object value = this.values.get(name);

        return value == null ? null : type.cast(value);
    }

    public <T> T require(String name, Class<T> type) {
        T value = this.get(name, type);
        if (value == null) {
            throw new IllegalArgumentException(
                "Required command argument '" + name + "' is missing"
            );
        }

        return value;
    }

    public <T> Optional<T> optional(String name, Class<T> type) {
        return Optional.ofNullable(this.get(name, type));
    }

    public String string(String name) {
        return this.get(name, String.class);
    }

    public int integer(String name) {
        return this.require(name, Integer.class);
    }

    public long longInteger(String name) {
        return this.require(name, Long.class);
    }

    public double decimal(String name) {
        return this.require(name, Double.class);
    }

    public boolean bool(String name) {
        return this.require(name, Boolean.class);
    }

    public Player player(String name) {
        return this.get(name, Player.class);
    }

    public Vector3 vector3(String name) {
        return this.get(name, Vector3.class);
    }

    public BlockVector3 blockPosition(String name) {
        return this.get(name, BlockVector3.class);
    }

    public Level level(String name) {
        return this.get(name, Level.class);
    }

    public UUID uuid(String name) {
        return this.get(name, UUID.class);
    }

    @SuppressWarnings("unchecked")
    public List<Entity> targets(String name) {
        Object value = this.values.get(name);
        if (value == null) {
            return List.of();
        }
        if (!(value instanceof List<?> list) || list.stream().anyMatch(element -> !(element instanceof Entity))) {
            throw new ClassCastException(
                "Command argument '" + name + "' is not an entity list"
            );
        }

        return (List<Entity>) list;
    }

    public int size() {
        return this.values.size();
    }

    public Map<String, Object> toMap() {
        return this.values;
    }

    @Override @NonNull public Iterator<Map.Entry<String, Object>> iterator() {
        return this.values.entrySet().iterator();
    }
}
