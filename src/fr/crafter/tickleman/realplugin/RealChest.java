package fr.crafter.tickleman.realplugin;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

//####################################################################################### RealChest
/*
 * This Chest object manages small and big chests
 */
public class RealChest
{

	/**
	 * Main chest object
	 * - always set by constructor 
	 */
	private Chest mainChest;

	/**
	 * Neighbor chest object
	 * - null if block is a small chest
	 * - another Chest object for the secondary chest when two adjacent blocks contain a big Chest
	 */
	private Chest neighborChest;

	//------------------------------------------------------------------------------------- ReadChest
	/**
	 * create chest from an existing block reference
	 * block must be a chest tested with block.getType().equals(Material.CHEST)
	 */
	public RealChest(Block block)
	{
		mainChest = (Chest)block.getState();
		RealLocation neighborLocation = new RealLocation(block.getLocation()).neighbor();
		if (neighborLocation == null) {
			neighborChest = null;
		} else {
			neighborChest = (Chest)neighborLocation.getBlock().getState();
			if (neighborChest.getX() < mainChest.getX()) {
				revertChests();
			} else if (neighborChest.getX() == mainChest.getX()) {
				if (neighborChest.getZ() < mainChest.getZ()) {
					revertChests();
				} else if (neighborChest.getZ() == mainChest.getZ()) {
					if (neighborChest.getY() < mainChest.getY()) {
						revertChests();
					}
				}
			}
		}
	}

	//------------------------------------------------------------------------------------- RealChest
	/**
	 * create chest from an existing location reference
	 * block at location must be a chest tested with block.getType().equals(Material.CHEST)
	 */
	public RealChest(Location location)
	{
		this(location.getBlock());
	}

	//-------------------------------------------------------------------------------- getInventories
	public Inventory[] getInventories()
	{
		if (getNeighborChest() == null) {
			Inventory[] inventories = {
				getMainChest().getInventory()
			};
			return inventories;
		} else {
			Inventory[] inventories = {
				getMainChest().getInventory(),
				getNeighborChest().getInventory()
			};
			return inventories;
		}
	}

	//----------------------------------------------------------------------------------- getLocation
	public Location getLocation()
	{
		return new Location(
			getMainChest().getWorld(), getMainChest().getX(), getMainChest().getY(), getMainChest().getZ()
		);
	}

	//---------------------------------------------------------------------------------- getMainChest
	public Chest getMainChest()
	{
		return mainChest;
	}

	//------------------------------------------------------------------------------ getNeighborChest
	public Chest getNeighborChest()
	{
		return neighborChest;
	}

	//---------------------------------------------------------------------------------- revertChests
	private void revertChests()
	{
		Chest chest = mainChest;
		mainChest = neighborChest;
		neighborChest = chest;
	}

	//-------------------------------------------------------------------------------------- toString
	/**
	 * Return "world;x1;y1;z1;x2;y2;z2"
	 * or "world;x1;y1;z1;;;" if no neighbor chest
	 */
	public String toString()
	{
		String result = mainChest.getWorld().getName() + ";"
			+ mainChest.getX() + ";" + mainChest.getY() + ";" + mainChest.getZ() + ";";
		if (neighborChest != null) {
			result += neighborChest.getX() + ";" + neighborChest.getY() + ";" + neighborChest.getZ();
		} else {
			result += ";;";
		}
		return result;
	}

}
