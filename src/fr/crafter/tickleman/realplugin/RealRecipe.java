package fr.crafter.tickleman.realplugin;

import java.lang.reflect.Field;
import java.util.HashSet;
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
		for (Object recipe : CraftingManager.getInstance().b()) {
			RealItemStack resultItemStack = new RealItemStack(((CraftingRecipe)recipe).b());
			if (itemType.isSameItem(resultItemStack)) {
				itemRecipes.add(new RealRecipe((CraftingRecipe)recipe, resultItemStack));
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
		return resultItem.toString() + "=" + result.substring(1);
	}

}
