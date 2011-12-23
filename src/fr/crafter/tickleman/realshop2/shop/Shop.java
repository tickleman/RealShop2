package fr.crafter.tickleman.realshop2.shop;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.crafter.tickleman.realplugin.RealChest;
import fr.crafter.tickleman.realplugin.RealItemStack;
import fr.crafter.tickleman.realplugin.RealItemType;
import fr.crafter.tickleman.realplugin.RealItemTypeList;
import fr.crafter.tickleman.realplugin.RealLocation;
import fr.crafter.tickleman.realplugin.RealVarTools;
import fr.crafter.tickleman.realshop2.RealShop2Plugin;

//############################################################################################ Shop
public class Shop
{

	/**
	 * Shop assistant player names
	 */
	private Set<String> assistantPlayerNames = new HashSet<String>();

	/**
	 * Players will not be able to sell these items into this shop
	 */
	private RealItemTypeList buyExclude = new RealItemTypeList();

	/**
	 * Players will be able to buy only these items into this shop
	 */
	private RealItemTypeList buyOnly = new RealItemTypeList();

	/**
	 * If true : allow damaged items to be shopped 
	 * If null : no specific way to work ( = use default)
	 */
	private Boolean damagedItems = null;

	/**
	 * If true : each time a player buy an item, the chest's slot will be automatically reloaded
	 * If null : no specific way to work ( = use default)
	 */
	private Boolean infiniteBuy = null;

	/**
	 * If true : each time a player sells an item, the chest's slot will get back to original state
	 * If null : no specific way to work ( = use default)
	 */
	private Boolean infiniteSell = null;
	
	/** Shop's position(s) */
	private Location location1;
	private Location location2;

	/**
	 * If true : allow only shopping of items that are into the market prices file
	 * If null : no specific way to work ( = use default)
	 */
	private Boolean marketItemsOnly = null;

	/** Shop's optional name */
	private String name;

	/**
	 * Shop is opened if true, closed if false
	 */
	private boolean opened = true;

	/**
	 * Shop's owner
	 */
	private String playerName;

	/** Players will not be able to buy these items into this shop */
	private RealItemTypeList sellExclude = new RealItemTypeList();

	/** Players will be able to sell only these items into this shop */
	private RealItemTypeList sellOnly = new RealItemTypeList();

	//------------------------------------------------------------------------------------------ Shop
	public Shop(Location location, String playerName)
	{
		setLocation(location);
		setPlayerName(playerName);
	}

	//------------------------------------------------------------------------ addAssistantPlayerName
	public void addAssistantPlayerName(String playerName)
	{
		assistantPlayerNames.add(playerName.toLowerCase());
	}

	//------------------------------------------------------------------------------------ canBuyItem
	public boolean canBuyItem(RealShop2Plugin plugin, RealItemStack itemStack)
	{
		if (
			((itemStack.getDamage() > 0) && !getDamagedItems(plugin.getRealConfig().shopDamagedItems))
			|| (!getBuyOnlyList().isEmpty() && getBuyOnlyList().get(itemStack) == null)
			|| (getBuyExcludeList().get(itemStack) != null)
		) {
			if ((itemStack.getDamage() > 0) && !getDamagedItems(plugin.getRealConfig().shopDamagedItems)) {
				plugin.getLog().debug("can not buy damaged item");
			}
			if (!getBuyOnlyList().isEmpty() && getBuyOnlyList().get(itemStack) == null) {
				plugin.getLog().debug("can not buy not in only list item");
			}
			if (getBuyExcludeList().get(itemStack) != null) {
				plugin.getLog().debug("can not buy item in exclude list");
			}
			return false;
		} else {
			return true;
		}
	}

	//----------------------------------------------------------------------------------- canSellItem
	public boolean canSellItem(RealShop2Plugin plugin, RealItemStack itemStack)
	{
		if (
			((itemStack.getDamage() > 0) && !getDamagedItems(plugin.getRealConfig().shopDamagedItems))
			|| (!getSellOnlyList().isEmpty() && getSellOnlyList().get(itemStack) == null)
			|| (getSellExcludeList().get(itemStack) != null)
		) {
			if ((itemStack.getDamage() > 0) && !getDamagedItems(plugin.getRealConfig().shopDamagedItems)) {
				plugin.getLog().debug("can not sell damaged item");
			}
			if (!getSellOnlyList().isEmpty() && getSellOnlyList().get(itemStack) == null) {
				plugin.getLog().debug("can not sell not in only list item");
			}
			if (getSellExcludeList().get(itemStack) != null) {
				plugin.getLog().debug("can not sell item in exclude list");
			}
			return false;
		} else {
			return true;
		}
	}

	//------------------------------------------------------------------------------------- setOpened
	public void close()
	{
		this.opened = false;
	}

	//-------------------------------------------------------------------------------------- contains
	public int contains(RealItemStack itemStack)
	{
		int amount = 0;
		if (getLocation().getBlock().getType().equals(Material.CHEST)) {
			RealChest chest = new RealChest(getLocation());
			for (Inventory inventory : chest.getInventories()) {
				for (ItemStack stack : inventory.all(itemStack.getTypeId()).values()) {
					amount += stack.getAmount();
				}
			}
		}
		return amount;
	}

	//------------------------------------------------------------------------------ containsLocation
	public boolean containsLocation(Location location)
	{
		return location1.equals(location) || (location2 != null && location2.equals(location));
	}

	//-------------------------------------------------------------------------------- forceLocations
	public void forceLocations(Location location1, Location location2)
	{
		this.location1 = location1;
		this.location2 = location2;
		sortLocations();
	}

	//--------------------------------------------------------------------------- getAssistantsString
	public String getAssistantsString()
	{
		String assistants = "";
		for (String assistant : assistantPlayerNames) {
			assistants += "," + assistant;
		}
		if (!assistants.isEmpty()) {
			assistants = assistants.substring(1);
		}
		return assistants;
	}

	//-------------------------------------------------------------------------------- getBuyOnlyList
	public RealItemTypeList getBuyOnlyList()
	{
		return buyOnly;
	}

	//----------------------------------------------------------------------------- getBuyExcludeList
	public RealItemTypeList getBuyExcludeList()
	{
		return buyExclude;
	}

	//-------------------------------------------------------------------------------------- getChest
	public RealChest getChest()
	{
		return new RealChest(getLocation());
	}

	//------------------------------------------------------------------------------- getDamagedItems
	public Boolean getDamagedItems()
	{
		return damagedItems;
	}

	//------------------------------------------------------------------------------- getDamagedItems
	public boolean getDamagedItems(boolean global)
	{
		return (damagedItems == null) ? global : damagedItems;
	}

	//----------------------------------------------------------------------------------------- getId
	public String getId()
	{
		return RealLocation.getId(getLocation());
	}

	//-------------------------------------------------------------------------------- getInfiniteBuy
	public Boolean getInfiniteBuy()
	{
		return (infiniteBuy == null) ? false : infiniteBuy;
	}

	//-------------------------------------------------------------------------------- getInfiniteBuy
	public boolean getInfiniteBuy(boolean global)
	{
		return (infiniteBuy == null) ? global : infiniteBuy;
	}

	//------------------------------------------------------------------------------- getInfiniteSell
	public Boolean getInfiniteSell()
	{
		return (infiniteSell == null) ? false : infiniteSell;
	}

	//------------------------------------------------------------------------------- getInfiniteSell
	public boolean getInfiniteSell(boolean global)
	{
		return (infiniteSell == null) ? global : infiniteSell;
	}

	//----------------------------------------------------------------------------------- getLocation
	public Location getLocation()
	{
		return getLocation1();
	}

	//---------------------------------------------------------------------------------- getLocation1
	public Location getLocation1()
	{
		return location1;
	}

	//---------------------------------------------------------------------------------- getLocation1
	public Location getLocation2()
	{
		return location2;
	}

	//---------------------------------------------------------------------------- getMarketItemsOnly
	public Boolean getMarketItemsOnly()
	{
		return marketItemsOnly;
	}

	//---------------------------------------------------------------------------- getMarketItemsOnly
	public boolean getMarketItemsOnly(boolean global)
	{
		return (marketItemsOnly == null) ? global : marketItemsOnly;
	}

	//--------------------------------------------------------------------------------------- getName
	public String getName()
	{
		return name;
	}

	//--------------------------------------------------------------------------------- getPlayerName
	public String getPlayerName()
	{
		return playerName;
	}

	//------------------------------------------------------------------------------- getSellOnlyList
	public RealItemTypeList getSellOnlyList()
	{
		return sellOnly;
	}

	//------------------------------------------------------------------------------- getSellOnlyList
	public RealItemTypeList getSellExcludeList()
	{
		return sellExclude;
	}

	//-------------------------------------------------------------------------------------- getWorld
	public World getWorld()
	{
		return getLocation().getWorld();
	}

	//------------------------------------------------------------------------------ isItemBuyAllowed
	/**
	 * Returns true if the player can buy an item into this shop
	 */
	public boolean isItemBuyAllowed(RealItemType itemType)
	{
		boolean result = (
			(buyOnly.isEmpty() || (buyOnly.get(itemType) != null))
			&& (buyExclude.get(itemType) == null)
		);
		return result;
	}

	//----------------------------------------------------------------------------- isItemSellAllowed
	/**
	 * Returns true if the player can sell an item into this shop
	 */
	public boolean isItemSellAllowed(RealItemType itemType)
	{
		boolean result = (
			(sellOnly.isEmpty() || (sellOnly.get(itemType) != null))
			&& (sellExclude.get(itemType) == null)
		);
		return result;
	}

	//-------------------------------------------------------------------------------------- isOpened
	public boolean isOpened()
	{
		return opened;
	}

	//---------------------------------------------------------------------------------- allowItemBuy
	public void itemBuyExclude(RealItemType itemType)
	{
		buyExclude.put(itemType);
	}

	//---------------------------------------------------------------------------------- allowItemBuy
	public void itemBuyOnly(RealItemType itemType)
	{
		buyOnly.put(itemType);
	}

	//---------------------------------------------------------------------------------- allowItemBuy
	public void itemSellExclude(RealItemType itemType)
	{
		sellExclude.put(itemType);
	}

	//---------------------------------------------------------------------------------- allowItemBuy
	public void itemSellOnly(RealItemType itemType)
	{
		sellOnly.put(itemType);
	}

	//------------------------------------------------------------------------------------- setOpened
	public void open()
	{
		this.opened = true;
	}

	//------------------------------------------------------------------------------------- parseShop
	public static Shop parseShop(Server server, String buffer)
	{
		try {
			String[] line = buffer.split(";");
			Shop shop = new Shop(
				new Location(
					server.getWorld(line[0]),
					Double.parseDouble(line[1]),
					Double.parseDouble(line[2]),
					Double.parseDouble(line[3])
				),
				line[7]
			);
			if (!line[4].equals("") && !line[5].equals("") && !line[6].equals("")) {
				shop.location2 = new Location(
					shop.location1.getWorld(),
					Double.parseDouble(line[4]),
					Double.parseDouble(line[5]),
					Double.parseDouble(line[6])
				);
			}
			try {
				shop.setName(line[8]);
				shop.setBuyOnlyList(RealItemTypeList.parseItemTypeList(line[9]));
				shop.setSellOnlyList(RealItemTypeList.parseItemTypeList(line[10]));
				shop.setBuyExcludeList(RealItemTypeList.parseItemTypeList(line[11]));
				shop.setSellExcludeList(RealItemTypeList.parseItemTypeList(line[12]));
				shop.setOpened(RealVarTools.parseBoolean(line[13]));
				shop.setInfiniteBuy(RealVarTools.parseBoolean(line[14]));
				shop.setInfiniteSell(RealVarTools.parseBoolean(line[15]));
				shop.setMarketItemsOnly(RealVarTools.parseBoolean(line[16]));
				shop.setDamagedItems(RealVarTools.parseBoolean(line[17]));
				shop.setAssistants(line[18]);
			} catch (Exception e) {
			}
			return shop;
		} catch (Exception e) {
			System.out.println("[SEVERE] [RealShop2] parseShop error " + buffer);
			System.out.println("[SEVERE] [RealShop2] " + e.getMessage());
			e.printStackTrace(System.out);
			return null;
		}
	}

	//----------------------------------------------------------------------------------- parseShopV0
	public static Shop parseShopV0(Server server, String buffer)
	{
		try {
			String[] line = buffer.split(";");
			Shop shop = new Shop(
				new Location(
					server.getWorld(line[0]),
					Double.parseDouble(line[1]),
					Double.parseDouble(line[2]),
					Double.parseDouble(line[3])
				),
				line[4]
			);
			try {
				shop.setBuyOnlyList(RealItemTypeList.parseItemTypeList(line[5]));
				shop.setSellOnlyList(RealItemTypeList.parseItemTypeList(line[6]));
				shop.setBuyExcludeList(RealItemTypeList.parseItemTypeList(line[7]));
				shop.setSellExcludeList(RealItemTypeList.parseItemTypeList(line[8]));
				shop.setName(line[9]);
				shop.setOpened(RealVarTools.parseBoolean(line[10]));
				shop.setInfiniteBuy(line[11].indexOf("infiniteBuy") > -1);
				shop.setInfiniteSell(line[11].indexOf("infiniteSell") > -1);
				shop.setMarketItemsOnly(line[11].indexOf("marketItemsOnly") > -1);
				shop.setDamagedItems(line[11].indexOf("damagedItems") > -1);
			} catch (Exception e) {
				// "index out of bound" exception is ignored
			}
			return shop;
		} catch (Exception e) {
			System.out.println("[SEVERE] [RealShop2] parseShopV0 error " + buffer);
			System.out.println("[SEVERE] [RealShop2] " + e.getMessage());
			e.printStackTrace(System.out);
			return null;
		}
	}

	//--------------------------------------------------------------------------- playerIsAnAssistant
	public boolean playerIsAnAssistant(String playerName)
	{
		return assistantPlayerNames.contains(playerName.toLowerCase());
	}

	//--------------------------------------------------------------------- removeAssistantPlayerName
	public void removeAssistantPlayerName(String playerName)
	{
		assistantPlayerNames.remove(playerName.toLowerCase());
	}

	//------------------------------------------------------------------------------- revertLocations
	private void revertLocations()
	{
		Location location = location2;
		location2 = location1;
		location1 = location;
	}

	//--------------------------------------------------------------------------------- setAssistants
	public void setAssistants(String assistants)
	{
		for (String assistant : assistants.split(",")) {
			addAssistantPlayerName(assistant);
		}
	}

	//----------------------------------------------------------------------------- setBuyExcludeList
	public void setBuyExcludeList(RealItemTypeList itemTypeSet)
	{
		buyExclude = itemTypeSet;
	}

	//-------------------------------------------------------------------------------- setBuyOnlyList
	public void setBuyOnlyList(RealItemTypeList itemTypeSet)
	{
		buyOnly = itemTypeSet;
	}

	//------------------------------------------------------------------------------- setDamagedItems
	public void setDamagedItems(Boolean damagedItems)
	{
		this.damagedItems = damagedItems;
	}

	//-------------------------------------------------------------------------------- setInfiniteBuy
	public void setInfiniteBuy(Boolean infiniteBuy)
	{
		this.infiniteBuy = infiniteBuy;
	}

	//------------------------------------------------------------------------------- setInfiniteSell
	public void setInfiniteSell(Boolean infiniteSell)
	{
		this.infiniteSell = infiniteSell;
	}

	//----------------------------------------------------------------------------------- setLocation
	public void setLocation(Location location)
	{
		location1 = location;
		location2 = new RealLocation(location).neighbor();
		sortLocations();
	}

	//---------------------------------------------------------------------------- setMarketItemsOnly
	public void setMarketItemsOnly(Boolean marketItemsOnly)
	{
		this.marketItemsOnly = marketItemsOnly;
	}

	//--------------------------------------------------------------------------------------- setName
	public void setName(String name)
	{
		this.name = name;
	}

	//------------------------------------------------------------------------------------- setOpened
	public void setOpened(boolean opened)
	{
		this.opened = opened;
	}

	//-------------------------------------------------------------------------------- setSellExclude
	public void setSellExcludeList(RealItemTypeList itemTypeSet)
	{
		sellExclude = itemTypeSet;
	}

	//----------------------------------------------------------------------------------- setSellOnly
	public void setSellOnlyList(RealItemTypeList itemTypeSet)
	{
		sellOnly = itemTypeSet;
	}

	//--------------------------------------------------------------------------------- setPlayerName
	public void setPlayerName(String playerName)
	{
		this.playerName = playerName.toLowerCase();
	}

	//--------------------------------------------------------------------------------- sortLocations
	private void sortLocations()
	{
		if (location2 != null) {
			if (location2.getX() < location1.getX()) {
				revertLocations();
			} else if (location2.getX() == location1.getX()) {
				if (location2.getZ() < location1.getZ()) {
					revertLocations();
				} else if (location2.getZ() == location1.getZ()) {
					if (location2.getY() < location1.getY()) {
						revertLocations();
					}
				}
			}
		}
	}

	//-------------------------------------------------------------------------------------- toString
	public String toString()
	{
		return getLocation().getWorld().getName() + ";"
		+ getLocation().getBlockX() + ";"
		+ getLocation().getBlockY() + ";"
		+ getLocation().getBlockZ() + ";"
		+ ((getLocation2() == null) ? ";" : getLocation2().getBlockX() + ";")
		+ ((getLocation2() == null) ? ";" : getLocation2().getBlockY() + ";")
		+ ((getLocation2() == null) ? ";" : getLocation2().getBlockZ() + ";")
		+ getPlayerName() + ";"
		+ getName() + ";"
		+ buyOnly.toString() + ";"
		+ sellOnly.toString() + ";"
		+ buyExclude.toString() + ";"
		+ sellExclude.toString() + ";"
		+ RealVarTools.toString(isOpened()) + ";"
		+ RealVarTools.toString(getInfiniteBuy()) + ";"
		+ RealVarTools.toString(getInfiniteSell()) + ";"
		+ RealVarTools.toString(getMarketItemsOnly()) + ";"
		+ RealVarTools.toString(getDamagedItems()) + ";"
		+ getAssistantsString();
	}

}
