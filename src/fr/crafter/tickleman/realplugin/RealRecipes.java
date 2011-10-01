package fr.crafter.tickleman.realplugin;

import java.util.List;

import net.minecraft.server.CraftingManager;
import net.minecraft.server.CraftingRecipe;

//##################################################################################### RealRecipes
public class RealRecipes
{

	//-------------------------------------------------------------------------------- getItemRecipes
	public static void getItemRecipes(ItemType itemType)
	{
		@SuppressWarnings("unchecked")
		List<CraftingRecipe> recipes = CraftingManager.getInstance().b();
		//List recipes = CraftingManager.getInstance().getRecipeList();
		System.out.println(recipes.size() + " crafting recipes");
		for (int i = 0; i < recipes.size(); i++) {
			CraftingRecipe recipe = recipes.get(i);
			RealItemStack resultItemStack = new RealItemStack(recipe.b());
			if (itemType.isSameItem(resultItemStack)) {
				System.out.println("found recipe for " + resultItemStack.toString());
			}
		}
	}

}
