package valres.toolbox.behavior.block.permutation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import org.jspecify.annotations.NonNull;
import valres.toolbox.behavior.block.builder.BlockBuilder;
import valres.toolbox.behavior.block.permutation.resolver.CropsPermutationResolver;

/**
 * Extensible resolver that generates client permutations from a block's PNX
 * type and state properties.
 */
public abstract class PermutationsResolver {
	private static final List<PermutationsResolver> resolvers;

	static {
		resolvers = new ArrayList<>();
		register(new CropsPermutationResolver());

		ServiceLoader.load(PermutationsResolver.class).forEach(PermutationsResolver::register);
	}

	public abstract boolean supports(@NonNull BlockBuilder builder);

	public abstract void resolve(@NonNull BlockBuilder builder);

	public int getPriority() {
		return 0;
	}

	public static synchronized void register(@NonNull PermutationsResolver resolver) {
		boolean alreadyRegistered = resolvers.stream().anyMatch(existing -> existing.getClass() == resolver.getClass());
		if (alreadyRegistered) {
			return;
		}

		resolvers.add(resolver);
		resolvers.sort(Comparator.comparingInt(PermutationsResolver::getPriority));
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
