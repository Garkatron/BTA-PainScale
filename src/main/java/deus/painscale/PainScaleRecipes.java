package deus.painscale;

import deus.painscale.item.PainScaleItems;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.util.RecipeEntrypoint;

import static deus.painscale.PainScale.MOD_ID;
import static deus.painscale.PainScale.PSDF;

public class PainScaleRecipes implements RecipeEntrypoint  {
	@Override
	public void onRecipesReady() {
		RecipeBuilder.Shaped(MOD_ID).setShape("OOO", "OGO", "OOO").addInput('O', Items.OLIVINE).addInput('G', Items.FOOD_APPLE_GOLD).create("painscale:recipe/olivine_heart", PainScaleItems.OLIVINE_HEART.getDefaultStack());
	}

	@Override
	public void initNamespaces() {
		final RecipeGroup<RecipeEntryCrafting<?, ?>> painscale = new RecipeGroup<>(
			new RecipeSymbol(new ItemStack(PainScaleItems.OLIVINE_HEART))
		);


		PSDF.register("painscale", painscale);

		Registries.RECIPES.register(MOD_ID, PSDF);
	}
}
