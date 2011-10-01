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
		this.resultItem = resultItem;
		Field recipeField = null;
		for (Field field : recipe.getClass().getDeclaredFields()) {
			if (
				field.getType().getCanonicalName().contains(".ItemStack[]")
				|| field.getType().getCanonicalName().contains(".List")
			) {
				recipeField = field;
				break;
			}
		}
		recipeField.setAccessible(true);
		try {
			if (recipeField.getType().getCanonicalName().contains(".ItemStack[]")) {
				// ItemStack[]
				for (ItemStack itemStack : (ItemStack[])recipeField.get(recipe)) {
					if (itemStack != null) {
						this.recipeItems.add(new RealItemStack(itemStack));
					}
				}
			} else {
				// List
				@SuppressWarnings("unchecked")
				List<ItemStack> itemStackList = (List<ItemStack>)recipeField.get(recipe);
				for (int i = 0; i < itemStackList.size(); i ++) {
					ItemStack itemStack = itemStackList.get(i);
					if (itemStack != null) {
						this.recipeItems.add(new RealItemStack(itemStack));
					}
				}
			}
		} catch (Exception e) {
			System.out.println("[ERROR] on " + resultItem.toString() + " recipe " + recipe.getClass() + " field " + recipeField.getType().getCanonicalName());
			e.printStackTrace();
		}
		System.out.println("recipe " + toString());
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
