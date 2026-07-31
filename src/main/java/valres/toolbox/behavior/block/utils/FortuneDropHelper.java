package valres.toolbox.behavior.block.utils;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;

import java.util.concurrent.ThreadLocalRandom;

final public class FortuneDropHelper {
    private FortuneDropHelper() {
    }

    public static int binomial(@NonNull Item usedItem, int minimum) {
        return binomial(usedItem, minimum, 3, 4d / 7d);
    }

    public static int binomial(
        @NonNull Item usedItem,
        int minimum,
        int minimumRolls,
        double chance
    ) {
        if (minimum < 0) {
            throw new IllegalArgumentException(
                "Minimum drop count cannot be negative"
            );
        }
        if (minimumRolls < 0) {
            throw new IllegalArgumentException(
                "Minimum roll count cannot be negative"
            );
        }
        if (chance < 0 || chance > 1) {
            throw new IllegalArgumentException(
                "Drop chance must be between 0 and 1"
            );
        }

        int fortuneLevel = Math.max(
            0,
            usedItem.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING)
        );
        int count = minimum;
        int rolls = minimumRolls + fortuneLevel;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int roll = 0; roll < rolls; roll++) {
            if (random.nextDouble() < chance) {
                count++;
            }
        }

        return count;
    }
}
