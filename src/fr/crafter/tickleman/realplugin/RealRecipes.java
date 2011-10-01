package fr.crafter.tickleman.realplugin;

import java.util.List;

import net.minecraft.server.CraftingManager;

import org.bukkit.Material;

//##################################################################################### RealRecipes
public class RealRecipes
{

	//-------------------------------------------------------------------------------- getItemRecipes
	public static void getItemRecipes(Material itemType)
	{
		@SuppressWarnings("rawtypes")
		List recipes = CraftingManager.getInstance().b();
		//List recipes = CraftingManager.getInstance().getRecipeList();
		System.out.println(recipes.size() + " crafting recipes");
	}

}
