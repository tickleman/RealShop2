package fr.crafter.tickleman.realshop2.shop;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.crafter.tickleman.realplugin.RealItemType;
import fr.crafter.tickleman.realplugin.RealItemTypeList;
import fr.crafter.tickleman.realplugin.RealChest;
import fr.crafter.tickleman.realplugin.RealColor;
import fr.crafter.tickleman.realshop2.RealShop2Plugin;
import fr.crafter.tickleman.realshop2.price.ItemPriceList;
import fr.crafter.tickleman.realshop2.price.Price;

//###################################################################################### shopAction
public class ShopAction
{

	public RealShop2Plugin plugin;

	//------------------------------------------------------------------------------------ shopAction
	public ShopAction(RealShop2Plugin plugin)
	{
		this.plugin = plugin;
	}

	//---------------------------------------------------------------------------------- addAssistant
	public void addAssistant(Player player, Shop shop, String playerName)
	{
		shop.addAssistantPlayerName(playerName);
		plugin.getShopList().save();
		player.sendMessage(
			RealColor.message
			+ plugin.tr("Your shop now has assistants +assistants")
			.replace("+assistants", RealColor.player + shop.getAssistantsString().replace(",", ", ") + RealColor.message)
		);
	}

	//------------------------------------------------------------------------------------ buyExclude
	public void buyExclude(Player player, Shop shop, String chain)
	{
		itemTypeListChain(player, chain, shop.getBuyExcludeList(), "not buy");
	}

	//--------------------------------------------------------------------------------------- buyOnly
	public void buyOnly(Player player, Shop shop, String chain)
	{
		itemTypeListChain(player, chain, shop.getBuyOnlyList(), "buy");
	}

	//----------------------------------------------------------------------------------------- close
	public void close(Player player, Shop shop)
	{
		shop.close();
		plugin.getShopList().save();
		player.sendMessage(
			RealColor.message
			+ plugin.tr("The shop +name is now closed")
			.replace("+name", RealColor.shop + shop.getName() + RealColor.message)
		);
	}

	//------------------------------------------------------------------------------------ createShop
	public Shop createShop(Location location, Player player, String shopName)
	{
		Shop shop = new Shop(location, player.getName());
		shop.setName(shopName);
		shop.itemSellOnly(new RealItemType(0));
		plugin.getShopList().put(shop);
		plugin.getShopList().save();
		player.sendMessage(
			RealColor.message + plugin.tr("The shop +name has been created")
			.replace("+name", RealColor.shop + shop.getName() + RealColor.message)
		);
		selectShop(player, shop);
		return shop;
	}

	//------------------------------------------------------------------------------------ deleteShop
	public void deleteShop(Player player, Shop shop)
	{
		plugin.getPlayerShopList().exitShop(player);
		plugin.getPlayerShopList().unselectShop(player);
		plugin.getShopList().remove(shop);
		plugin.getShopList().save();
		player.sendMessage(
			RealColor.message + plugin.tr("The shop +name has been deleted")
			.replace("+name", RealColor.shop + shop.getName() + RealColor.message)
		);
		enterChestBlock(player, shop.getLocation().getBlock());
	}

	//------------------------------------------------------------------------------- enterChestBlock
	/**
	 * player enters chest / shop block
	 *
	 * @param player
	 * @param block
	 * @return true if chest opening is allowed (simple chest or allowed shop), else false
	 */
	public boolean enterChestBlock(Player player, Block block)
	{
		Shop shop = plugin.getShopList().shopAt(block.getLocation());
		plugin.getPlayerChestList().selectChest(player, new RealChest(block));
		if (shop != null) {
			plugin.getLog().debug("enterShop(" + player.getName() + ")");
			return enterShop(player, shop);
		} else {
			plugin.getLog().debug("selectChest(" + player.getName() + ")");
			plugin.getPlayerShopList().unselectShop(player);
			return true;
		}
	}

	//------------------------------------------------------------------------------------- enterShop
	public boolean enterShop(Player player, Shop shop)
	{
		if (!plugin.hasPermission(player, "realshop.shop")) {
			// players must have "realshop.shop" permission to enter a shop
			player.sendMessage(
				RealColor.cancel
				+ plugin.tr("You don't have the permission to shop")
			);
			return false;
		} else {
			if (!player.getName().equalsIgnoreCase(shop.getPlayerName()) && !shop.isOpened()) {
				// shop is owned by someone else, and is closed
				player.sendMessage(
					RealColor.cancel
					+ plugin.tr("+owner's shop +name is closed, please come later")
					.replace("+name", RealColor.shop + shop.getName() + RealColor.cancel)
					.replace("+owner", RealColor.player + shop.getPlayerName() + RealColor.cancel)
				);
				return false;
			} else {
				if (shop != plugin.getPlayerShopList().insideShop(player)) {
					// player enters the shop
					plugin.getPlayerShopList().enterShop(player, shop);
					if (player.getName().equalsIgnoreCase(shop.getPlayerName())) {
						// player enters its own shop
						player.sendMessage(
							RealColor.message
							+ plugin.tr("Welcome into your shop +name")
							.replace("+name", RealColor.shop + shop.getName() + RealColor.message)
						);
					} else {
						// player enters another player's shop
						player.sendMessage(
							RealColor.message
							+ plugin.tr("Welcome into +owner's shop +name. You've got +money in your pocket")
							.replace("+money", RealColor.price + plugin.getEconomy().getBalance(player.getName(), true) + RealColor.message)
							.replace("+name", RealColor.shop + shop.getName() + RealColor.message)
							.replace("+owner", RealColor.player + shop.getPlayerName() + RealColor.message)
						);
					}
				}
				return true;
			}
		}
	}

	//-------------------------------------------------------------------------------------- exitShop
	public void exitShop(Player player)
	{
		Shop shop = plugin.getPlayerShopList().insideShop(player);
		if (shop != null) {
			if (player.getName().equalsIgnoreCase(shop.getPlayerName())) {
				// player exits its own shop
				player.sendMessage(
					RealColor.message
					+ plugin.tr("You leaved your shop +name")
					.replace("+name", RealColor.shop + shop.getName() + RealColor.message)
				);
			} else {
				// player enters another player's shop
				player.sendMessage(
					RealColor.message
					+ plugin.tr("You leaved +owner's shop +name. Have a nice day")
					.replace("+name", RealColor.shop + shop.getName() + RealColor.message)
					.replace("+owner", RealColor.player + shop.getPlayerName() + RealColor.message)
				);
			}
			plugin.getPlayerShopList().exitShop(player);
		}
	}

	//-------------------------------------------------------------------------------------- giveShop
	public void giveShop(Player player, Shop shop, String playerName)
	{
		if (playerName.equals("")) {
			player.sendMessage(
				RealColor.cancel + "/rshop help player"
				+ RealColor.message + " : " + plugin.tr("player name is missing")
			);
		} else {
			shop.setPlayerName(playerName);
			plugin.getShopList().save();
			player.sendMessage(
				RealColor.message
				+ plugin.tr("The shop +name was given to +player")
				.replace("+name", RealColor.shop + shop.getName() + RealColor.message)
				.replace("+owner", RealColor.player + player.getName() + RealColor.message)
				.replace("+player", RealColor.player + playerName + RealColor.message)
			);
		}
	}

	//----------------------------------------------------------------------------- itemTypeListChain
	private void itemTypeListChain(
		Player player, String chain, RealItemTypeList itemTypeList, String what
	) {
		try {
			itemTypeList.addRemoveChain(chain);
		} catch (NumberFormatException e) {
		}
		plugin.getShopList().save();
		player.sendMessage(
			RealColor.message + plugin.tr("Now clients can " + what + " +items")
			.replace("+items", RealColor.item + itemTypeList.toNamedString(plugin) + RealColor.message)
		);
	}

	//------------------------------------------------------------------------------------------ open
	public void open(Player player, Shop shop)
	{
		shop.open();
		plugin.getShopList().save();
		player.sendMessage(
			RealColor.message
			+ plugin.tr("The shop +name is now opened")
			.replace("+name", RealColor.shop + shop.getName() + RealColor.message)
		);
	}

	//------------------------------------------------------------------------------- removeAssistant
	public void removeAssistant(Player player, Shop shop, String playerName)
	{
		shop.removeAssistantPlayerName(playerName);
		plugin.getShopList().save();
		player.sendMessage(
			RealColor.message
			+ plugin.tr("Your shop now has assistants +assistants")
			.replace("+assistants", RealColor.player + shop.getAssistantsString().replace(",", ", ") + RealColor.message)
		);
	}

	//------------------------------------------------------------------------------------ selectShop
	public boolean selectShop(Player player, Shop shop)
	{
		plugin.getPlayerShopList().selectShop(player, shop);
		if (player.getName().equalsIgnoreCase(shop.getPlayerName())) {
			// player enters its own shop
			player.sendMessage(
				RealColor.message
				+ plugin.tr("You selected your shop +name")
				.replace("+name", RealColor.shop + shop.getName() + RealColor.message)
			);
		} else {
			// player enters another player's shop
			player.sendMessage(
				RealColor.message
				+ plugin.tr("You selected +owner's shop +name")
				.replace("+name", RealColor.shop + shop.getName() + RealColor.message)
				.replace("+owner", RealColor.player + shop.getPlayerName() + RealColor.message)
			);
		}
		shopPricesInfos(player, shop);
		return true;
	}

	//----------------------------------------------------------------------------------- sellExclude
	public void sellExclude(Player player, Shop shop, String chain)
	{
		itemTypeListChain(player, chain, shop.getSellExcludeList(), "not sell");
	}

	//-------------------------------------------------------------------------------------- sellOnly
	public void sellOnly(Player player, Shop shop, String chain)
	{
		itemTypeListChain(player, chain, shop.getSellOnlyList(), "sell");
	}

	//------------------------------------------------------------------------------- setDamagedItems
	public void setDamagedItems(Player player, Shop shop, Boolean flag)
	{
		shop.setDamagedItems(flag);
		plugin.getShopList().save();
		player.sendMessage(
			RealColor.message
			+ plugin.tr("Damaged item buy/sell flag is")
			+ " " + RealColor.command
			+ plugin.tr(shop.getDamagedItems(plugin.getRealConfig().shopDamagedItems) ? "on" : "off")
		);
	}

	//-------------------------------------------------------------------------------- setInfiniteBuy
	public void setInfiniteBuy(Player player, Shop shop, Boolean flag)
	{
		shop.setInfiniteBuy(flag);
		plugin.getShopList().save();
		player.sendMessage(
			RealColor.message
			+ plugin.tr("Infinite buy flag is")
			+ " " + RealColor.command
			+ plugin.tr(shop.getInfiniteBuy(plugin.getRealConfig().shopInfiniteBuy) ? "on" : "off")
		);
	}

	//------------------------------------------------------------------------------- setInfiniteSell
	public void setInfiniteSell(Player player, Shop shop, Boolean flag)
	{
		shop.setInfiniteSell(flag);
		plugin.getShopList().save();
		player.sendMessage(
			RealColor.message
			+ plugin.tr("Infinite sell flag is")
			+ " " + RealColor.command
			+ plugin.tr(shop.getInfiniteSell(plugin.getRealConfig().shopInfiniteSell) ? "on" : "off")
		);
	}

	//---------------------------------------------------------------------------- setMarketItemsOnly
	public void setMarketItemsOnly(Player player, Shop shop, Boolean flag)
	{
		shop.setMarketItemsOnly(flag);
		plugin.getShopList().save();
		player.sendMessage(
			RealColor.message
			+ plugin.tr("Trade market items only flag is")
			+ " " + RealColor.command
			+ plugin.tr(
				shop.getMarketItemsOnly(plugin.getRealConfig().shopMarketItemsOnly) ? "on" : "off"
			)
		);
	}

	//-------------------------------------------------------------------------------------- shopInfo
	public void shopInfo(Player player, Shop shop)
	{
		player.sendMessage(
			RealColor.message
			+ plugin.tr("+owner's shop +name : +opened")
			.replace("+name", RealColor.shop + shop.getName() + RealColor.message)
			.replace("+opened", plugin.tr(shop.isOpened() ? "opened" : "closed"))
			.replace("+owner", RealColor.player + shop.getPlayerName() + RealColor.message)
		);
		if (!shop.getAssistantsString().isEmpty()) {
			player.sendMessage(
				RealColor.message
				+ plugin.tr("assistants : +assistants")
				.replace("+assistants", RealColor.player + shop.getAssistantsString().replace(",", ", ") + RealColor.message)
			);
		}
		player.sendMessage(
			RealColor.message
			+ (shop.getInfiniteBuy() ? "+" : "-") + plugin.tr("infinite buy") + " "
			+ (shop.getInfiniteSell() ? "+" : "-") + plugin.tr("infinite sell") + " "
		);
		player.sendMessage(
			RealColor.message
			+ (shop.getMarketItemsOnly(plugin.getRealConfig().shopMarketItemsOnly) ? "+" : "-")
			+ plugin.tr("market items only") + " "
			+ (shop.getDamagedItems(plugin.getRealConfig().shopDamagedItems) ? "+" : "-")
			+ plugin.tr("accepts damaged items")
		);
	}

	//------------------------------------------------------------------------------- shopPricesInfos
	public void shopPricesInfos(Player player, Shop shop)
	{
		ItemPriceList ownerPrices = new ItemPriceList(plugin, shop.getPlayerName()).load();
		plugin.getLog().debug("marketPrices = " + plugin.getMarketPrices().toString());
		plugin.getLog().debug("ownerPrices = " + ownerPrices.toString());
		// sell (may be as long as number of filled slots on player's inventory)
		RealItemTypeList itemTypeList = new RealItemTypeList();
		StringBuffer list = new StringBuffer();
		int count = 20;
		boolean already = false;
		for (ItemStack itemStack : player.getInventory().getContents()) if (itemStack != null) {
			RealItemType itemType = new RealItemType(itemStack);
			if ((itemTypeList.get(itemType) == null) && shop.isItemSellAllowed(itemType)) {
				itemTypeList.put(itemType);
				Price price = ownerPrices.getPrice(itemType, plugin.getMarketPrices());
				if (price != null) {
					plugin.getLog().debug("price of " + itemType.toString() + " = " + price.toString());
					if (already) {
						list.append(RealColor.message).append(", ");
					} else {
						already = true;
					}
					list.append(RealColor.item).append(plugin.trItemName(itemType))
						.append(RealColor.message).append(": ").append(RealColor.price)
						.append(price.getSellPrice());
					if (count-- < 0) {
						if (already) {
							list.append(RealColor.message).append(", ...");
						}
						break;
					}
				}
			}
		}
		if (!already) {
			player.sendMessage(RealColor.cancel + plugin.tr("Nothing can be sold here"));
		} else {
			player.sendMessage(
				RealColor.message + plugin.tr("You can sell +items")
				.replace("+items", list + RealColor.message)
			);
		}
		// buy (may be as long as the number of filled slots on the chest's inventory) 
		itemTypeList.clear();
		list = new StringBuffer();
		count = 20;
		already = false;
		for (Inventory inventory : shop.getChest().getInventories()) {
			for (ItemStack itemStack : inventory.getContents()) if (itemStack != null ) {
				RealItemType itemType = new RealItemType(itemStack);
				
				if ((itemTypeList.get(itemType) == null) && shop.isItemBuyAllowed(itemType)) {
					itemTypeList.put(itemType);
					Price price = ownerPrices.getPrice(itemType, plugin.getMarketPrices());
					if (price != null) {
						plugin.getLog().debug("price of " + itemType.toString() + " = " + price.toString());
						if (already) {
							list.append(RealColor.message).append(", ");
						} else {
							already = true;
						}
						list.append(RealColor.item).append(plugin.trItemName(itemType))
							.append(RealColor.message).append(": ").append(RealColor.price)
							.append(price.getBuyPrice());
						if (count-- < 0) {
							if (already) {
								list.append(RealColor.message).append(", ...");
							}
							break;
						}
					}
				}
			}
			if (count < 0) {
				break;
			}
		}
		if (!already) {
			player.sendMessage(RealColor.cancel + plugin.tr("Nothing to buy here"));
		} else {
			player.sendMessage(
				RealColor.message + plugin.tr("You can buy +items")
				.replace("+items", list + RealColor.message)
			);
		}
	}

}
