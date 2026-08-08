package valres.toolbox.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.Server;
import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.Recipe;
import org.powernukkitx.recipe.ShapedRecipe;
import org.powernukkitx.recipe.descriptor.DefaultDescriptor;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.registry.RecipeRegistry;
import org.powernukkitx.registry.Registries;

public final class CraftUtils {
	private CraftUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static int replaceOutput(@NonNull String currentIdentifier, @NonNull String replacementIdentifier) {
		String normalizedCurrentIdentifier = normalizeIdentifier(currentIdentifier);
		Item replacement = getItemOrThrow(replacementIdentifier);
		RecipeRegistry recipes = Registries.RECIPE;
		int replacedRecipes = 0;

		synchronized (recipes) {
			for (Recipe recipe : getRegisteredRecipes(recipes)) {
				if (replaceOutputs(recipe, normalizedCurrentIdentifier, replacement)) {
					replacedRecipes++;
				}
			}

			if (replacedRecipes > 0) {
				recipes.rebuildPacket();
			}
		}

		if (replacedRecipes > 0) {
			syncRecipes(recipes);
		}

		return replacedRecipes;
	}

	public static int removeByOutput(@NonNull String @NonNull... outputIdentifiers) {
		Set<String> normalizedIdentifiers = normalizeIdentifiers(outputIdentifiers);
		if (normalizedIdentifiers.isEmpty()) {
			return 0;
		}

		RecipeRegistry recipes = Registries.RECIPE;
		List<Recipe> removedRecipes = new ArrayList<>();

		synchronized (recipes) {
			List<Recipe> registeredRecipes = new ArrayList<>(getRegisteredRecipes(recipes));
			List<Recipe> retainedRecipes = new ArrayList<>(registeredRecipes.size());

			for (Recipe recipe : registeredRecipes) {
				if (hasMatchingOutput(recipe, normalizedIdentifiers)) {
					removedRecipes.add(recipe);
				} else {
					retainedRecipes.add(recipe);
				}
			}

			if (removedRecipes.isEmpty()) {
				return 0;
			}

			Map<Recipe, Double> experience = new HashMap<>(recipes.getRecipeXpMap());
			try {
				rebuildRegistry(recipes, retainedRecipes, experience);
			} catch (RuntimeException | Error exception) {
				try {
					rebuildRegistry(recipes, registeredRecipes, experience);
				} catch (RuntimeException | Error restoreException) {
					exception.addSuppressed(restoreException);
				}
				throw exception;
			}
		}

		syncRemovedRecipes(recipes, removedRecipes);
		return removedRecipes.size();
	}

	public static int replaceAllInput(@NonNull String currentIdentifier, @NonNull String replacementIdentifier) {
		String normalizedCurrentIdentifier = normalizeIdentifier(currentIdentifier);
		Item replacement = getItemOrThrow(replacementIdentifier);
		RecipeRegistry recipes = Registries.RECIPE;
		int replacedRecipes = 0;

		synchronized (recipes) {
			for (Recipe recipe : getRegisteredRecipes(recipes)) {
				if (replaceInputs(recipe, normalizedCurrentIdentifier, replacement)) {
					replacedRecipes++;
				}
			}

			if (replacedRecipes > 0) {
				recipes.rebuildPacket();
			}
		}

		if (replacedRecipes > 0) {
			syncRecipes(recipes);
		}

		return replacedRecipes;
	}

	private static boolean replaceOutputs(Recipe recipe, String currentIdentifier, Item replacement) {
		boolean replaced = false;
		List<Item> results = recipe.getResults();

		for (int index = 0; index < results.size(); index++) {
			Item currentResult = results.get(index);
			if (!currentResult.getId().equals(currentIdentifier)) {
				continue;
			}

			Item newResult = replacement.clone();
			newResult.setCount(currentResult.getCount());
			results.set(index, newResult);
			replaced = true;
		}

		return replaced;
	}

	private static boolean replaceInputs(Recipe recipe, String currentIdentifier, Item replacement) {
		if (recipe instanceof ShapedRecipe shapedRecipe) {
			return replaceShapedInputs(shapedRecipe, currentIdentifier, replacement);
		}

		boolean replaced = false;
		List<ItemDescriptor> ingredients = recipe.getIngredients();

		for (int index = 0; index < ingredients.size(); index++) {
			ItemDescriptor descriptor = ingredients.get(index);
			if (!(descriptor instanceof DefaultDescriptor defaultDescriptor)) {
				continue;
			}

			Item currentInput = defaultDescriptor.getItem();
			if (!currentInput.getId().equals(currentIdentifier)) {
				continue;
			}

			ingredients.set(index, createReplacementDescriptor(replacement, currentInput));
			replaced = true;
		}

		return replaced;
	}

	private static boolean replaceShapedInputs(ShapedRecipe recipe, String currentIdentifier, Item replacement) {
		boolean replaced = false;
		Set<Character> visitedKeys = new HashSet<>();
		String[] shape = recipe.getShape();

		for (int row = 0; row < shape.length; row++) {
			for (int column = 0; column < shape[row].length(); column++) {
				char key = shape[row].charAt(column);
				if (key == ' ' || !visitedKeys.add(key)) {
					continue;
				}

				ItemDescriptor descriptor = recipe.getIngredient(column, row);
				if (!(descriptor instanceof DefaultDescriptor defaultDescriptor)) {
					continue;
				}

				Item currentInput = defaultDescriptor.getItem();
				if (!currentInput.getId().equals(currentIdentifier)) {
					continue;
				}

				recipe.setIngredient(key, createReplacementDescriptor(replacement, currentInput));
				replaced = true;
			}
		}

		return replaced;
	}

	private static DefaultDescriptor createReplacementDescriptor(Item replacement, Item currentInput) {
		Item replacementItem = replacement.clone();
		replacementItem.setCount(currentInput.getCount());

		if (!currentInput.hasMeta()) {
			replacementItem.disableMeta();
		}

		if (currentInput.hasNbt()) {
			replacementItem.setNbt(Objects.requireNonNull(currentInput.getNbt()).copy());
		}

		return new DefaultDescriptor(replacementItem);
	}

	private static boolean hasMatchingOutput(Recipe recipe, Set<String> identifiers) {
		for (Item output : recipe.getResults()) {
			if (identifiers.contains(output.getId())) {
				return true;
			}
		}
		return false;
	}

	private static Set<Recipe> getRegisteredRecipes(RecipeRegistry recipes) {
		Set<Recipe> registeredRecipes = new LinkedHashSet<>(recipes.getNetworkIdRecipeMap().values());
		registeredRecipes.addAll(recipes.getFurnaceRecipeMap());
		registeredRecipes.addAll(recipes.getBlastFurnaceRecipeMap());
		registeredRecipes.addAll(recipes.getSmokerRecipeMap());
		registeredRecipes.addAll(recipes.getCampfireRecipeMap());
		registeredRecipes.addAll(recipes.getBrewingRecipeMap());
		registeredRecipes.addAll(recipes.getContainerRecipeMap());
		registeredRecipes.addAll(recipes.getModProcessRecipeMap());
		return registeredRecipes;
	}

	private static void rebuildRegistry(RecipeRegistry recipes, List<Recipe> registeredRecipes,
			Map<Recipe, Double> experience) {
		recipes.cleanAllRecipes();

		for (Recipe recipe : registeredRecipes) {
			recipes.register(recipe);
			Double recipeExperience = experience.get(recipe);
			if (recipeExperience != null) {
				recipes.setRecipeXp(recipe, recipeExperience);
			}
		}

		recipes.rebuildPacket();
	}

	private static void syncRemovedRecipes(RecipeRegistry recipes, List<Recipe> removedRecipes) {
		Server.getInstance().getOnlinePlayers().values().forEach(player -> {
			removedRecipes.forEach(player.getRecipeBook()::lock);
			player.sendPacketImmediately(recipes.getCraftingPacket());
		});
	}

	private static void syncRecipes(RecipeRegistry recipes) {
		Server.getInstance().getOnlinePlayers().values()
				.forEach(player -> player.sendPacketImmediately(recipes.getCraftingPacket()));
	}

	private static Item getItemOrThrow(String identifier) {
		String normalizedIdentifier = normalizeIdentifier(identifier);
		Item item = Item.get(normalizedIdentifier);
		if (item.isNull()) {
			throw new IllegalArgumentException("Unknown item '" + normalizedIdentifier + "'");
		}
		return item;
	}

	private static Set<String> normalizeIdentifiers(String[] identifiers) {
		Objects.requireNonNull(identifiers, "Output identifiers cannot be null");
		Set<String> normalizedIdentifiers = new HashSet<>(identifiers.length);
		for (String identifier : identifiers) {
			normalizedIdentifiers.add(normalizeIdentifier(identifier));
		}
		return normalizedIdentifiers;
	}

	private static @NonNull String normalizeIdentifier(@NonNull String identifier) {
		String normalizedIdentifier = Objects.requireNonNull(identifier, "Identifier cannot be null").trim()
				.toLowerCase(Locale.ROOT);
		if (normalizedIdentifier.isEmpty()) {
			throw new IllegalArgumentException("Identifier cannot be empty");
		}
		return normalizedIdentifier.contains(":") ? normalizedIdentifier : "minecraft:" + normalizedIdentifier;
	}
}
