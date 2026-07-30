package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.ByteTag;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.LongTag;
import org.powernukkitx.nbt.tag.ShortTag;
import org.powernukkitx.nbt.tag.StringTag;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.components.type.ItemComponentValue;

import java.lang.reflect.Array;
import java.util.*;

final public class ComponentNbtHelper {
    private ComponentNbtHelper() {
    }

    public static @NonNull CompoundTag compound(@NonNull Map<?, ?> values) {
        CompoundTag result = new CompoundTag();

        for (Map.Entry<?, ?> entry : values.entrySet()) {
            if (entry.getKey() == null) {
                throw new IllegalArgumentException(
                    "An NBT compound key cannot be null"
                );
            }

            Object value = normalizeValue(entry.getValue());
            if (value != null) {
                result.put(String.valueOf(entry.getKey()), tag(value));
            }
        }

        return result;
    }

    public static @NonNull CompoundTag compound(Object... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException(
                "Compound values must be provided as key/value pairs"
            );
        }

        Map<String, Object> values = new LinkedHashMap<>();
        for (int index = 0; index < keyValuePairs.length; index += 2) {
            Object key = keyValuePairs[index];
            if (key == null) {
                throw new IllegalArgumentException(
                    "An NBT compound key cannot be null"
                );
            }
            values.put(String.valueOf(key), keyValuePairs[index + 1]);
        }

        return compound(values);
    }

    public static @NonNull ListTag<StringTag> stringList(@NonNull Collection<String> values) {
        ListTag<StringTag> result = new ListTag<>(Tag.TAG_String);

        for (String value : values) {
            if (value == null) {
                throw new IllegalArgumentException(
                    "An NBT string list cannot contain null"
                );
            }
            result.add(new StringTag(value));
        }

        return result;
    }

    public static @NonNull ListTag<Tag> numericList(@NonNull Collection<? extends Number> values) {
        NumericListType type = numericListType(values);
        ListTag<Tag> result = new ListTag<>(type.tagId);

        for (Number value : values) {
            result.add(type.createTag(value));
        }

        return result;
    }

    public static @NonNull ListTag<CompoundTag> compoundList(@NonNull Collection<?> values) {
        ListTag<CompoundTag> result = new ListTag<>(Tag.TAG_Compound);

        for (Object entry : values) {
            Object value = normalizeValue(entry);
            if (value instanceof CompoundTag compound) {
                result.add(compound);
            } else if (value instanceof Map<?, ?> map) {
                result.add(compound(map));
            } else {
                throw new IllegalArgumentException(
                    "A compound list only accepts Map, CompoundTag or ItemComponentValue entries"
                );
            }
        }

        return result;
    }

    public static @NonNull Tag tag(Object value) {
        value = normalizeValue(value);

        switch (value) {
            case null -> throw new IllegalArgumentException(
                "NBT has no null tag; put null in a compound to omit the entry"
            );
            case Tag existingTag -> {
                return existingTag;
            }
            case Boolean bool -> {
                return new ByteTag((byte) (bool ? 1 : 0));
            }
            case Byte number -> {
                return new ByteTag(number);
            }
            case Short number -> {
                return new ShortTag(number);
            }
            case Integer number -> {
                return new IntTag(number);
            }
            case Long number -> {
                return new LongTag(number);
            }
            case Float number -> {
                return new FloatTag(number);
            }
            case Double number -> {
                return new DoubleTag(number);
            }
            default -> {
            }
        }
        if (value instanceof CharSequence || value instanceof Character) {
            return new StringTag(value.toString());
        }
        if (value instanceof Map<?, ?> map) {
            return compound(map);
        }
        if (value instanceof Collection<?> collection) {
            return collectionTag(collection);
        }
        if (value.getClass().isArray()) {
            return collectionTag(arrayValues(value));
        }

        return new StringTag(String.valueOf(value));
    }

    private static @NonNull Tag collectionTag(@NonNull Collection<?> values) {
        List<Object> normalized = values.stream()
            .map(ComponentNbtHelper::normalizeValue)
            .toList();

        if (normalized.isEmpty()) {
            return new ListTag<>(Tag.TAG_End);
        }
        if (normalized.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("An NBT list cannot contain null");
        }
        if (normalized.stream().allMatch(String.class::isInstance)) {
            return stringList(normalized.stream().map(String.class::cast).toList());
        }
        if (normalized.stream().allMatch(Number.class::isInstance)) {
            return numericList(normalized.stream().map(Number.class::cast).toList());
        }
        if (normalized.stream().allMatch(Boolean.class::isInstance)) {
            return booleanList(normalized.stream().map(Boolean.class::cast).toList());
        }
        if (normalized.stream().allMatch(ComponentNbtHelper::isCompoundValue)) {
            return compoundList(normalized);
        }
        if (normalized.stream().allMatch(Tag.class::isInstance)) {
            return tagList(normalized.stream().map(Tag.class::cast).toList());
        }

        throw new IllegalArgumentException(
            "NBT lists must contain values of one compatible type"
        );
    }

    private static @NonNull ListTag<ByteTag> booleanList(@NonNull Collection<Boolean> values) {
        ListTag<ByteTag> result = new ListTag<>(Tag.TAG_Byte);
        for (Boolean value : values) {
            result.add(new ByteTag((byte) (value ? 1 : 0)));
        }
        return result;
    }

    private static @NonNull ListTag<Tag> tagList(@NonNull Collection<Tag> values) {
        byte tagId = values.iterator().next().getId();
        ListTag<Tag> result = new ListTag<>(tagId);

        for (Tag value : values) {
            if (value.getId() != tagId) {
                throw new IllegalArgumentException(
                    "An NBT tag list cannot mix " + Tag.getTagName(tagId) + " and " + Tag.getTagName(value.getId())
                );
            }
            result.add(value);
        }

        return result;
    }

    private static boolean isCompoundValue(Object value) {
        return value instanceof Map<?, ?> || value instanceof CompoundTag;
    }

    private static Object normalizeValue(Object value) {
        if (value instanceof ItemComponentValue componentValue) {
            return componentValue.toMap();
        }
        if (value instanceof Enum<?> enumValue) {
            return enumValue.toString();
        }
        return value;
    }

    private static @NonNull List<Object> arrayValues(@NonNull Object array) {
        int length = Array.getLength(array);
        List<Object> values = new ArrayList<>(length);
        for (int index = 0; index < length; index++) {
            values.add(Array.get(array, index));
        }
        return values;
    }

    private static @NonNull NumericListType numericListType(@NonNull Collection<? extends Number> values) {
        boolean containsLong = false;
        boolean containsFloat = false;
        boolean containsDouble = false;

        for (Number value : values) {
            if (value == null) {
                throw new IllegalArgumentException(
                    "An NBT numeric list cannot contain null"
                );
            }
            if (value instanceof Double) {
                containsDouble = true;
            } else if (value instanceof Float) {
                containsFloat = true;
            } else if (value instanceof Long) {
                containsLong = true;
            } else if (!(value instanceof Byte || value instanceof Short || value instanceof Integer)) {
                throw new IllegalArgumentException(
                    "Unsupported NBT number type: " + value.getClass().getName()
                );
            }
        }

        if (containsDouble || containsFloat && containsLong) {
            return NumericListType.DOUBLE;
        }
        if (containsFloat) {
            return NumericListType.FLOAT;
        }
        if (containsLong) {
            return NumericListType.LONG;
        }
        return NumericListType.INT;
    }

    private enum NumericListType {
        INT(Tag.TAG_Int) {
            @Override Tag createTag(Number value) {
                return new IntTag(value.intValue());
            }
        },
        LONG(Tag.TAG_Long) {
            @Override Tag createTag(Number value) {
                return new LongTag(value.longValue());
            }
        },
        FLOAT(Tag.TAG_Float) {
            @Override Tag createTag(Number value) {
                return new FloatTag(value.floatValue());
            }
        },
        DOUBLE(Tag.TAG_Double) {
            @Override Tag createTag(Number value) {
                return new DoubleTag(value.doubleValue());
            }
        };

        final private byte tagId;

        NumericListType(byte tagId) {
            this.tagId = tagId;
        }

        abstract Tag createTag(Number value);
    }
}
