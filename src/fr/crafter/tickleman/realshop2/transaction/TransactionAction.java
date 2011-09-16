package fr.crafter.tickleman.realshop2.transaction;

import org.bukkit.entity.Player;

import fr.crafter.tickleman.realplugin.ItemType;
import fr.crafter.tickleman.realplugin.RealItemStack;
import fr.crafter.tickleman.realshop2.RealShop2Plugin;
import fr.crafter.tickleman.realshop2.price.ItemPriceList;
import fr.crafter.tickleman.realshop2.price.Price;
import fr.crafter.tickleman.realshop2.shop.Shop;

//############################################################################### TransactionAction
public class TransactionAction
{

	private RealShop2Plugin plugin;

	//----------------------------------------------------------------------------- TransactionAction
	public TransactionAction(RealShop2Plugin plugin)
	{
		this.plugin = plugin;
	}

	//---------------------------------------------------------------------------------------- canBuy
	/*
	 * Return true if player can buy all the itemStack quantity with the shop's pricing policy
	 */
	public boolean canBuy(String playerName, Shop shop, RealItemStack itemStack)
	{
		ItemType itemType = itemStack.getItemType();
		ItemPriceList prices = new ItemPriceList(plugin, shop.getPlayerName());
		Price price = prices.getPrice(itemType, itemStack.getDamage(), plugin.getMarketPrices());
		if (price != null) {
			double balance = plugin.getEconomy().getBalance(playerName);
			int canBuy = Math.min(
				itemStack.getAmount(),
				(int)Math.floor(balance / price.getBuyPrice(itemStack.getAmount()))
			);
			return canBuy == itemStack.getAmount();
		} else {
			return false;
		}
	}

	//------------------------------------------------------------------------------------------- buy
	public int buy(Player player, Shop shop, RealItemStack itemStack, boolean allOrNone)
	{
		ItemType itemType = itemStack.getItemType();
		ItemPriceList prices = new ItemPriceList(plugin, shop.getPlayerName());
		Price price = prices.getPrice(itemType, itemStack.getDamage(), plugin.getMarketPrices());
		if (price != null) {
			System.out.println(
				"buy " + itemStack.toString() + " = " + price.getBuyPrice(itemStack.getAmount())
			);
			double balance = plugin.getEconomy().getBalance(player.getName());
			int canBuy = Math.min(
				itemStack.getAmount(),
				(int)Math.floor(balance / price.getBuyPrice(itemStack.getAmount()))
			);
			if ((canBuy == itemStack.getAmount()) || !allOrNone) {
				plugin.getEconomy().setBalance(
					player.getName(),
					balance - price.getBuyPrice(canBuy)
				);
				plugin.getEconomy().setBalance(
					shop.getPlayerName(),
					plugin.getEconomy().getBalance(shop.getPlayerName()) + price.getBuyPrice(canBuy)
				);
				return canBuy;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	//------------------------------------------------------------------------------------------ sell
	public int sell(Player player, Shop shop, RealItemStack itemStack, boolean allOrNone)
	{
		ItemType itemType = itemStack.getItemType();
		ItemPriceList prices = new ItemPriceList(plugin, shop.getPlayerName());
		Price price = prices.getPrice(itemType, itemStack.getDamage(), plugin.getMarketPrices());
		if (price != null) {
			System.out.println(
				"sell " + itemStack.toString() + " = " + price.getSellPrice(itemStack.getAmount())
			);
			double balance = plugin.getEconomy().getBalance(shop.getPlayerName());
			int canSell = Math.min(
				itemStack.getAmount(),
				(int)Math.floor(balance / price.getSellPrice(itemStack.getAmount()))
			);
			if ((canSell == itemStack.getAmount()) || !allOrNone) {
				plugin.getEconomy().setBalance(
					shop.getPlayerName(),
					balance - price.getSellPrice(canSell)
				);
				plugin.getEconomy().setBalance(
					player.getName(),
					plugin.getEconomy().getBalance(player.getName()) + price.getSellPrice(canSell)
				);
				return canSell;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

}
