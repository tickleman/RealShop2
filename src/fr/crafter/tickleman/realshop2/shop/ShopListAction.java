package fr.crafter.tickleman.realshop2.shop;

import org.bukkit.entity.Player;

import fr.crafter.tickleman.realplugin.RealItemStack;
import fr.crafter.tickleman.realplugin.RealItemType;
import fr.crafter.tickleman.realplugin.RealLocation;
import fr.crafter.tickleman.realshop2.RealShop2Plugin;
import fr.crafter.tickleman.realshop2.transaction.TransactionAction;

//################################################################################## ShopListAction
public class ShopListAction
{

	public RealShop2Plugin plugin;

	//-------------------------------------------------------------------------------- shopListAction
	public ShopListAction(RealShop2Plugin plugin)
	{
		this.plugin = plugin;
	}

	//------------------------------------------------------------------------------------ searchItem
	public void searchItem(String[] keywords, Player player)
	{
		RealItemType itemType = RealItemType.parseItemTypeKeywords(keywords);
		RealItemStack itemStack = new RealItemStack(itemType.getTypeId());
		TransactionAction transaction = new TransactionAction(plugin);
		player.sendMessage("Nearest shops for " + itemType.toNamedString() + " are :");
		int count = 0;
		for (Shop shop : plugin.getShopList().getSortedByDistance(player.getLocation()).values()) {
			int amount = shop.contains(itemStack);
			if ((amount > 0) && shop.canBuyItem(plugin, itemStack)) {
				player.sendMessage(
					shop.getName()
					+ " " + RealLocation.toString(shop.getLocation()).replace(";", ", ")
					+ " x" + amount
					+ " (" + transaction.calculatePrice(shop, itemStack).getBuyPrice()
					+ " each)"
				);
				count ++;
			}
			if (count >= 10) {
				break;
			}
		}
	}

	//------------------------------------------------------------------------------------ searchItem
	public void searchItem(String item, Player player)
	{
		String[] keyWords = {item};
		searchItem(keyWords, player);
	}

}
