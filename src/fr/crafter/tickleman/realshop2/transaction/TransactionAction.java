package fr.crafter.tickleman.realshop2.transaction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.crafter.tickleman.realplugin.RealEnchantment;
import fr.crafter.tickleman.realplugin.RealItemType;
import fr.crafter.tickleman.realplugin.RealColor;
import fr.crafter.tickleman.realplugin.RealItemStack;
import fr.crafter.tickleman.realshop2.RealShop2Plugin;
import fr.crafter.tickleman.realshop2.price.ItemPriceList;
import fr.crafter.tickleman.realshop2.price.Price;
import fr.crafter.tickleman.realshop2.shop.Shop;

//############################################################################### TransactionAction
public class TransactionAction
{

	private static String fileName;

	private static BufferedWriter transactionsLogFile;

	private RealShop2Plugin plugin;

	//----------------------------------------------------------------------------- TransactionAction
	public TransactionAction(RealShop2Plugin plugin)
	{
		this.plugin = plugin;
		fileName = plugin.getDataFolder().getPath() + "/transactions.log";
	}

	//------------------------------------------------------------------------------------------- buy
	public int buy(Player player, Shop shop, ItemStack itemStack)
	{
		plugin.getLog().debug("buy " + RealItemStack.create(itemStack).toString());
		if (canPay(player, shop, itemStack, null)) {
			Price price = calculatePrice(shop, itemStack);
			if (price != null) {
				plugin.getEconomy().transfer(
					player.getName(), shop.getPlayerName(), price.getBuyPrice(itemStack.getAmount())
				);
				sendMessage(
					player, shop, itemStack,
					price.getBuyPrice(), price.getBuyPrice(itemStack.getAmount()),
					"Purchased", "purchased"
				);
				return itemStack.getAmount();
			}
		}
		return 0;
	}

	//--------------------------------------------------------------------- broadcastPlayersBuyOrSell
	private void broadcastNearbyPlayersBuyOrSell(
		ItemStack itemStack, RealItemType itemType, Player player, Shop shop, String side,
		double amount, double price
	) {
		double radius = plugin.getRealConfig().broadcastNearbyPlayersRadius;
		for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
			if (entity instanceof Player) {
				Player nearbyPlayer = (Player)entity;
				if (
					!shop.getPlayerName().equalsIgnoreCase(nearbyPlayer.getName())
					&& !player.getName().equalsIgnoreCase(nearbyPlayer.getName())
				) {
					nearbyPlayer.sendMessage(
						RealColor.text
						+ plugin.tr("[shop +name] +client " + side + " +item x+quantity (+linePrice) to +owner")
						.replace("+client", RealColor.player + player.getName() + RealColor.text)
						.replace("+item", RealColor.item + plugin.trItemName(itemType) + RealColor.text)
						.replace("+linePrice", "" + RealColor.price + amount + RealColor.text)
						.replace("+name", RealColor.shop + shop.getName() + RealColor.text)
						.replace("+owner", RealColor.player + shop.getPlayerName() + RealColor.text)
						.replace("+price", "" + RealColor.price + price + RealColor.text)
						.replace("+quantity", "" + RealColor.quantity + itemStack.getAmount() + RealColor.text)
						.replace("  ", " ").replace(" ]", "]").replace("[ ", "[")
					);
				}
			}
		}
	}

  //----------------------------------------------------------------------------------------- price
	public Price calculatePrice(Shop shop, ItemStack itemStack)
	{
		return calculatePrice(shop, new RealItemStack(itemStack));
	}

	//----------------------------------------------------------------------------------------- price
	public Price calculatePrice(Shop shop, RealItemStack itemStack)
	{
		RealItemType itemType = itemStack.getItemType();
		ItemPriceList prices = new ItemPriceList(plugin, shop.getPlayerName()).load();
		Price price = prices.getPrice(itemType, itemStack.getDamage(), plugin.getMarketPrices());
		if (price != null) {
			for (Enchantment enchantment : itemStack.getEnchantments()) {
				price.applyRatio(Math.pow(
					plugin.getRealConfig().enchantmentLevelRatio,
					itemStack.getEnchantmentLevel(enchantment)
				));
				price.applyRatio(Math.pow(
					plugin.getRealConfig().enchantmentRandomWeightRatio,
					10 / RealEnchantment.getEnchantmentWeight(enchantment)
				));
			}
		}
		return price;
	}

	//---------------------------------------------------------------------------------------- canPay
	/**
	 * Return true if player can buy buyStack and sell sellStack into the shop
	 */
	public boolean canPay(Player player, Shop shop, ItemStack buyStack, ItemStack sellStack)
	{
		if ((buyStack != null) && !shop.canBuyItem(plugin, new RealItemStack(buyStack))) {
			plugin.getLog().debug("can not buy item");
			return false;
		}
		if ((sellStack != null) && !shop.canSellItem(plugin, new RealItemStack(sellStack))) {
			plugin.getLog().debug("can not sell item");
			return false;
		}
		Price buyPrice = (buyStack == null) ? null : calculatePrice(shop, buyStack);
		Price sellPrice = (sellStack == null) ? null : calculatePrice(shop, sellStack);
		if (
			((buyStack != null) && (buyStack.getAmount() > 0) && (buyPrice == null))
			|| ((sellStack != null) && (sellStack.getAmount() > 0) && (sellPrice == null))
		) {
			if ((buyStack != null) && (buyStack.getAmount() > 0) && (buyPrice == null)) {
				plugin.getLog().debug("canPay price not found for buy " + RealItemStack.create(buyStack));
			}
			if ((buyStack != null) && (buyStack.getAmount() > 0) && (buyPrice == null)) {
				plugin.getLog().debug("canPay sell price not found for sell " + RealItemStack.create(sellStack));
			}
			plugin.getLog().debug("Can not pay as an item has a null price");
			// can't pay if any item has a null price ("price not found")
			return false;
		}
		double diffAmount
			= ((sellPrice == null) ? 0 : sellPrice.getSellPrice(sellStack.getAmount()))
			- ((buyPrice == null) ? 0 : buyPrice.getBuyPrice(buyStack.getAmount()));
		if (diffAmount > 0) {
			plugin.getLog().debug(
				"canPay check if " + plugin.getEconomy().getBalance(shop.getPlayerName())
				+ " >= " + diffAmount
			);
			// sell more than buy : can pay if shop's owner has enough money
			return plugin.getEconomy().getBalance(shop.getPlayerName()) >= diffAmount;
		} else {
			plugin.getLog().debug(
				"canPay check if " + plugin.getEconomy().getBalance(player.getName())
				+ " >= " + (-diffAmount)
			);
			// buy more than sell : can pay if client player has enough money
			return plugin.getEconomy().getBalance(player.getName()) >= -diffAmount;
		}
	}

	//--------------------------------------------------------------------------------------- dispose
	public static void dispose()
	{
		if (transactionsLogFile != null) {
			try {
				transactionsLogFile.close();
			} catch (IOException e) {
			}
			transactionsLogFile = null;
		}
	}

	//-------------------------------------------------------------------------------- logTransaction
	private void logTransaction(
		ItemStack itemStack, RealItemType itemType, Player player, Shop shop, String side,
		double amount, double price
	) {
		try {
			if (transactionsLogFile == null) {
				transactionsLogFile = new BufferedWriter(new FileWriter(fileName));
				transactionsLogFile.append("#player:side;shopName;X;Y;Z;shopOwner;typeId;variant;price;quantity;amount\n");
			}
			transactionsLogFile.append(
				player.getName() + ";"
				+ side + ";"
				+ shop.getName() + ";"
				+ shop.getLocation().getBlockX() + ";"
				+ shop.getLocation().getBlockY() + ";"
				+ shop.getLocation().getBlockZ() + ";"
				+ shop.getPlayerName() + ";"
				+ itemType.getTypeId() + ";"
				+ itemType.getVariant() + ";"
				+ price + ";"
				+ itemStack.getAmount() + ";"
				+ amount
			);
			transactionsLogFile.flush();
		} catch (Exception e) {
			plugin.getLog().severe("Error writting " + fileName);
			e.printStackTrace();
		}
	}

	//------------------------------------------------------------------------------------------ sell
	public int sell(Player player, Shop shop, ItemStack itemStack)
	{
		plugin.getLog().debug("sell " + RealItemStack.create(itemStack).toString());
		if (canPay(player, shop, null, itemStack)) {
			Price price = calculatePrice(shop, itemStack);
			if (price != null) {
				plugin.getEconomy().transfer(
					shop.getPlayerName(), player.getName(), price.getSellPrice(itemStack.getAmount())
				);
				sendMessage(
					player, shop, itemStack,
					price.getSellPrice(), price.getSellPrice(itemStack.getAmount()),
					"Sold", "sold"
				);
				return itemStack.getAmount();
			}
		}
		return 0;
	}

	//----------------------------------------------------------------------------------- sendMessage
	private void sendMessage(
		Player player, Shop shop, ItemStack itemStack,
		double price, double amount,
		String side, String shopSide
	) {
		RealItemType itemType = new RealItemType(itemStack);
		player.sendMessage(
			RealColor.text
			+ plugin.tr(side + " +item x+quantity (+linePrice)")
			.replace("+client", RealColor.player + player.getName() + RealColor.text)
			.replace("+item", RealColor.item + plugin.trItemName(itemType) + RealColor.text)
			.replace("+linePrice", "" + RealColor.price + amount + RealColor.text)
			.replace("+name", RealColor.shop + shop.getName() + RealColor.text)
			.replace("+owner", RealColor.player + shop.getPlayerName() + RealColor.text)
			.replace("+price", "" + RealColor.price + price + RealColor.text)
			.replace("+quantity", "" + RealColor.quantity + itemStack.getAmount() + RealColor.text)
			.replace("  ", " ").replace(" ]", "]").replace("[ ", "[")
		);
		Player shopPlayer = plugin.getServer().getPlayer(shop.getPlayerName());
		if (shopPlayer != null) {
			shopPlayer.sendMessage(
				RealColor.text
				+ plugin.tr("[shop +name] +client " + shopSide + " +item x+quantity (+linePrice) to you")
				.replace("+client", RealColor.player + player.getName() + RealColor.text)
				.replace("+item", RealColor.item + plugin.trItemName(itemType) + RealColor.text)
				.replace("+linePrice", "" + RealColor.price + amount + RealColor.text)
				.replace("+name", RealColor.shop + shop.getName() + RealColor.text)
				.replace("+owner", RealColor.player + shop.getPlayerName() + RealColor.text)
				.replace("+price", "" + RealColor.price + price + RealColor.text)
				.replace("+quantity", "" + RealColor.quantity + itemStack.getAmount() + RealColor.text)
				.replace("  ", " ").replace(" ]", "]").replace("[ ", "[")
			);
		}
		if (plugin.getRealConfig().broadcastNearbyPlayersRadius > 0d) {
			broadcastNearbyPlayersBuyOrSell(itemStack, itemType, player, shop, shopSide, amount, price);
		}
		if (plugin.getRealConfig().logTransactions) {
			logTransaction(itemStack, itemType, player, shop, shopSide, amount, price);
		}
	}

}
