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
	{
		System.out.println("REALRECIPE for " + resultItem.toString());
		this.resultItem = resultItem;
		Field recipeField = null;
		for (Field field : recipe.getClass().getDeclaredFields()) {
			if (field.getType().getCanonicalName().contains("ItemStack[]")) {
				recipeField = field;
				break;
			}
		}
		recipeField.setAccessible(true);
		try {
			for (ItemStack itemStack : (ItemStack[])recipeField.get(recipe)) {
				this.recipeItems.add(new RealItemStack(itemStack));
				System.out.println(": add item to recipe " + new RealItemStack(itemStack).toString());
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
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
				itemRecipes.add(new RealRecipe(recipe, resultItemStack));
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
