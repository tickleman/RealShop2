package fr.crafter.tickleman.realshop2.transaction;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

	//------------------------------------------------------------------------------------------- buy
	public int buy(Player player, Shop shop, ItemStack itemStack)
	{
		if (canPay(player, shop, itemStack, null)) {
			Price price = calculatePrice(shop, itemStack);
			if (price != null) {
				plugin.getEconomy().transfer(
					player.getName(),
					shop.getPlayerName(),
					price.getBuyPrice(itemStack.getAmount())
				);
				return itemStack.getAmount();
			}
		}
		return 0;
	}

	//----------------------------------------------------------------------------------------- price
	public Price calculatePrice(Shop shop, ItemStack itemStack)
	{
		return calculatePrice(shop, new RealItemStack(itemStack));
	}

	//----------------------------------------------------------------------------------------- price
	public Price calculatePrice(Shop shop, RealItemStack itemStack)
	{
		ItemType itemType = itemStack.getItemType();
		ItemPriceList prices = new ItemPriceList(plugin, shop.getPlayerName());
		return prices.getPrice(itemType, itemStack.getDamage(), plugin.getMarketPrices());
	}

	//---------------------------------------------------------------------------------------- canPay
	/**
	 * Return true if player can buy buyStack and sell sellStack into the shop
	 */
	public boolean canPay(Player player, Shop shop, ItemStack buyStack, ItemStack sellStack)
	{
		Price buyPrice = (buyStack == null) ? null : calculatePrice(shop, buyStack);
		Price sellPrice = (sellStack == null) ? null : calculatePrice(shop, sellStack);
		double diffAmount = ((sellPrice == null) ? 0 : sellPrice.getSellPrice(sellStack.getAmount()))
			- ((buyPrice == null) ? 0 : buyPrice.getBuyPrice(buyStack.getAmount()));
		if (diffAmount > 0) {
			return plugin.getEconomy().getBalance(shop.getPlayerName()) >= diffAmount;
		} else {
			return plugin.getEconomy().getBalance(player.getName()) >= -diffAmount;
		}
	}

	//------------------------------------------------------------------------------------------ sell
	public int sell(Player player, Shop shop, ItemStack itemStack)
	{
		if (canPay(player, shop, null, itemStack)) {
			Price price = calculatePrice(shop, itemStack);
			if (price != null) {
				plugin.getEconomy().transfer(
					shop.getPlayerName(),
					player.getName(),
					price.getSellPrice(itemStack.getAmount())
				);
				return itemStack.getAmount();
			}
		}
		return 0;
	}

}
