package valres.toolbox.behavior.block.permutation;

import org.jspecify.annotations.NonNull;
import valres.toolbox.behavior.block.builder.BlockBuilder;
import valres.toolbox.behavior.block.permutation.resolver.CropsPermutationResolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Extensible resolver that generates client permutations from a block's PNX
 * type and state properties.
 */
abstract public class PermutationsResolver {
    final private static List<PermutationsResolver> resolvers;

    static {
        resolvers = new ArrayList<>();
        register(new CropsPermutationResolver());

        ServiceLoader.load(PermutationsResolver.class).forEach(
            PermutationsResolver::register
        );
    }

    abstract public boolean supports(@NonNull BlockBuilder builder);

    abstract public void resolve(@NonNull BlockBuilder builder);

    public int getPriority() {
        return 0;
    }

    public static synchronized void register(
        @NonNull PermutationsResolver resolver
    ) {
        boolean alreadyRegistered = resolvers.stream().anyMatch(
            existing -> existing.getClass() == resolver.getClass()
        );
        if (alreadyRegistered) {
            return;
        }

        resolvers.add(resolver);
        resolvers.sort(Comparator.comparingInt(
            PermutationsResolver::getPriority
        ));
    }

    public static void resolveAll(@NonNull BlockBuilder builder) {
        for (PermutationsResolver resolver : getResolvers()) {
            if (resolver.supports(builder)) {
                resolver.resolve(builder);
            }
        }
    }

    public static synchronized @NonNull List<PermutationsResolver> getResolvers() {
        return Collections.unmodifiableList(new ArrayList<>(resolvers));
    }
}
