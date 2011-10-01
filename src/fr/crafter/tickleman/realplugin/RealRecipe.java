package fr.crafter.tickleman.realplugin;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.server.CraftingManager;
import net.minecraft.server.CraftingRecipe;
import net.minecraft.server.ItemStack;

//##################################################################################### RealRecipes
public class RealRecipe
{

	private Set<RealItemStack> recipeItems = new HashSet<RealItemStack>();
	private RealItemStack resultItem;

	//--------------------------------------------------------------------------------- getResultItem
	public Set<RealItemStack> getRecipeItems()
	{
		return recipeItems;
	}

	//--------------------------------------------------------------------------------- getResultItem
	public RealItemStack getResultItem()
	{
		return resultItem;
	}

	//------------------------------------------------------------------------------------ RealRecipe
	/**
	 * Generate a easily usable recipe, based on Minecraft's server recipe
	 */
	public RealRecipe(CraftingRecipe recipe, RealItemStack resultItem)
		throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException
	{
		Field recipeItems = recipe.getClass().getDeclaredField("b");
		recipeItems.setAccessible(true);
		@SuppressWarnings("unchecked")
		List<ItemStack> sourceRecipeItems = (List<ItemStack>)recipeItems.get(recipe);
		for (int i = 0; i < sourceRecipeItems.size(); i ++) {
			this.recipeItems.add(new RealItemStack(sourceRecipeItems.get(i)));
		}
		this.resultItem = resultItem;
	}

	//-------------------------------------------------------------------------------- getItemRecipes
	/**
	 * Return a set of possible recipes for given item type
	 */
	public static Set<RealRecipe> getItemRecipes(ItemType itemType)
	{
		Set<RealRecipe> itemRecipes = new HashSet<RealRecipe>();
		@SuppressWarnings("unchecked")
		List<CraftingRecipe> recipes = CraftingManager.getInstance().b();
		for (int i = 0; i < recipes.size(); i++) {
			CraftingRecipe recipe = recipes.get(i);
			RealItemStack resultItemStack = new RealItemStack(recipe.b());
			if (itemType.isSameItem(resultItemStack)) {
				try {
					itemRecipes.add(new RealRecipe(recipe, resultItemStack));
				} catch (Exception e) {
					System.out.println("Exception on itemRecipes.add() into RealRecipe::Set()");
				}
				System.out.println("found recipe for " + resultItemStack.toString());
			}
		}
		return itemRecipes;
	}

	//-------------------------------------------------------------------------------------- toString
	@Override
	public String toString()
	{
		String result = "";
		for (RealItemStack itemStack : recipeItems) {
			result += "+" + itemStack.toString();
		}
		return result.substring(1) + "=" + resultItem.toString();
	}

}
