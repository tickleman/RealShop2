package fr.crafter.tickleman.realplugin;

import org.bukkit.inventory.ItemStack;

//############################################################################### RealInventoryMove
public class RealInventoryMove
{

	private ItemStack cursor;
	private ItemStack item;

	//----------------------------------------------------------------------------- RealInventoryMove
	public RealInventoryMove(ItemStack cursor, ItemStack item)
	{
		setCursor(cursor);
		setItem(item);
	}

	//------------------------------------------------------------------------------------- getCursor
	public ItemStack getCursor()
	{
		return cursor;
	}

	//--------------------------------------------------------------------------------------- getItem
	public ItemStack getItem()
	{
		return item;
	}

	//------------------------------------------------------------------------------------- setCursor
	public void setCursor(ItemStack cursor)
	{
		this.cursor = cursor;
	}

	//--------------------------------------------------------------------------------------- setItem
	public void setItem(ItemStack item)
	{
		this.item = item;
	}

}
