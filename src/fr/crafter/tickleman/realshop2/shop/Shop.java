package fr.crafter.tickleman.realshop2.shop;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

import fr.crafter.tickleman.realplugin.RealChest;
import fr.crafter.tickleman.realplugin.ItemType;
import fr.crafter.tickleman.realplugin.ItemTypeList;
import fr.crafter.tickleman.realplugin.RealLocation;
import fr.crafter.tickleman.realplugin.VarTools;

//############################################################################################ Shop
public class Shop
{

	/**
	 * Players will not be able to sell these items into this shop
	 */
	private ItemTypeList buyExclude = new ItemTypeList();

	/**
	 * Players will be able to buy only these items into this shop
	 */
	private ItemTypeList buyOnly = new ItemTypeList();

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
	private ItemTypeList sellExclude = new ItemTypeList();

	/** Players will be able to sell only these items into this shop */
	private ItemTypeList sellOnly = new ItemTypeList();

	//------------------------------------------------------------------------------------------ Shop
	public Shop(Location location, String playerName)
	{
		setLocation(location);
		setPlayerName(playerName);
	}

	//------------------------------------------------------------------------------------- setOpened
	public void close()
	{
		this.opened = false;
	}

	//-------------------------------------------------------------------------------- getBuyOnlyList
	public ItemTypeList getBuyOnlyList()
	{
		return buyOnly;
	}

	//----------------------------------------------------------------------------- getBuyExcludeList
	public ItemTypeList getBuyExcludeList()
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
		return infiniteBuy;
	}

	//-------------------------------------------------------------------------------- getInfiniteBuy
	public boolean getInfiniteBuy(boolean global)
	{
		return (infiniteBuy == null) ? global : infiniteBuy;
	}

	//------------------------------------------------------------------------------- getInfiniteSell
	public Boolean getInfiniteSell()
	{
		return infiniteSell;
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
	public ItemTypeList getSellOnlyList()
	{
		return sellOnly;
	}

	//------------------------------------------------------------------------------- getSellOnlyList
	public ItemTypeList getSellExcludeList()
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
	public boolean isItemBuyAllowed(ItemType itemType)
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
	public boolean isItemSellAllowed(ItemType itemType)
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
	public void itemBuyExclude(ItemType itemType)
	{
		buyExclude.put(itemType);
	}

	//---------------------------------------------------------------------------------- allowItemBuy
	public void itemBuyOnly(ItemType itemType)
	{
		buyOnly.put(itemType);
	}

	//---------------------------------------------------------------------------------- allowItemBuy
	public void itemSellExclude(ItemType itemType)
	{
		sellExclude.put(itemType);
	}

	//---------------------------------------------------------------------------------- allowItemBuy
	public void itemSellOnly(ItemType itemType)
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
			if (line[4] != "" && line[5] != "" && line[6] != "") {
				shop.location2 = new Location(
					shop.location1.getWorld(),
					Double.parseDouble(line[4]),
					Double.parseDouble(line[5]),
					Double.parseDouble(line[6])
				);
			}
			shop.setName(line[8]);
			shop.setBuyOnlyList(ItemTypeList.parseItemTypeList(line[9]));
			shop.setSellOnlyList(ItemTypeList.parseItemTypeList(line[10]));
			shop.setBuyExcludeList(ItemTypeList.parseItemTypeList(line[11]));
			shop.setSellExcludeList(ItemTypeList.parseItemTypeList(line[12]));
			shop.setOpened(VarTools.parseBoolean(line[13]));
			shop.setInfiniteBuy(VarTools.parseBoolean(line[14]));
			shop.setInfiniteSell(VarTools.parseBoolean(line[15]));
			shop.setMarketItemsOnly(VarTools.parseBoolean(line[16]));
			shop.setDamagedItems(VarTools.parseBoolean(line[17]));
			return shop;
		} catch (Exception e) {
			return null;
		}
	}

	//------------------------------------------------------------------------------- revertLocations
	private void revertLocations()
	{
		Location location = location2;
		location2 = location1;
		location1 = location;
	}

	//----------------------------------------------------------------------------- setBuyExcludeList
	public void setBuyExcludeList(ItemTypeList itemTypeSet)
	{
		buyExclude = itemTypeSet;
	}

	//-------------------------------------------------------------------------------- setBuyOnlyList
	public void setBuyOnlyList(ItemTypeList itemTypeSet)
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
	public void setSellExcludeList(ItemTypeList itemTypeSet)
	{
		sellExclude = itemTypeSet;
	}

	//----------------------------------------------------------------------------------- setSellOnly
	public void setSellOnlyList(ItemTypeList itemTypeSet)
	{
		sellOnly = itemTypeSet;
	}

	//--------------------------------------------------------------------------------- setPlayerName
	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}

	//-------------------------------------------------------------------------------------- toString
	public String toString()
	{
		return getLocation().getWorld().getName() + ";"
		+ VarTools.floor(getLocation().getX()) + ";"
		+ VarTools.floor(getLocation().getY()) + ";"
		+ VarTools.floor(getLocation().getZ()) + ";"
		+ ((getLocation2() == null) ? ";" : VarTools.floor(getLocation2().getX()) + ";")
		+ ((getLocation2() == null) ? ";" : VarTools.floor(getLocation2().getY()) + ";")
		+ ((getLocation2() == null) ? ";" : VarTools.floor(getLocation2().getZ()) + ";")
		+ getPlayerName() + ";"
		+ getName() + ";"
		+ buyOnly.toString() + ";"
		+ sellOnly.toString() + ";"
		+ buyExclude.toString() + ";"
		+ sellExclude.toString() + ";"
		+ VarTools.toString(isOpened()) + ";"
		+ VarTools.toString(getInfiniteBuy()) + ";"
		+ VarTools.toString(getInfiniteSell()) + ";"
		+ VarTools.toString(getMarketItemsOnly()) + ";"
		+ VarTools.toString(getDamagedItems());
	}

}
