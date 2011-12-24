package fr.crafter.tickleman.realshop2.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.crafter.tickleman.realplugin.RealColor;
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

	//---------------------------------------------------------------------------------- cleanupShops
	public void cleanupShops(Player player)
	{
		player.sendMessage(RealColor.message + plugin.tr("Cleanup shops list in action..."));
		Integer removed = 0;
		Integer resized = 0;
		for (Object shopObject : plugin.getShopList().getShops().toArray()) {
			Shop shop = (Shop)shopObject;
			boolean chest1 = Material.CHEST.equals(shop.getLocation1().getBlock().getType());
			boolean chest2 = (shop.getLocation2() == null) ? chest1
				: Material.CHEST.equals(shop.getLocation2().getBlock().getType());
			if (!chest1 || !chest2) {
				if (!chest1 && !chest2) {
					plugin.getShopList().remove(shop);
					removed ++;
				} else if (!chest1 && chest2) {
					shop.setLocation(shop.getLocation2());
					plugin.getShopList().remove(shop);
					plugin.getShopList().put(shop);
					resized ++;
				} else if (chest1 && !chest2) {
					shop.setLocation(shop.getLocation1());
					resized ++;
				}
			}
		}
		if (removed > 0 || resized > 0) {
			plugin.getShopList().save();
		}
		player.sendMessage(
			RealColor.message + plugin.tr("- +removed shops removed")
			.replace("+removed", removed.toString())
		);
		player.sendMessage(
			RealColor.message + plugin.tr("- +resized shops resized")
			.replace("+resized", removed.toString())
		);
	}

	//------------------------------------------------------------------------------------ searchItem
	public void searchItem(String[] keywords, Player player, String gpsCallString)
	{
		short gpsCall;
		try {
			gpsCall = Short.parseShort(gpsCallString);
		} catch (Exception e) {
			gpsCall = 1;
		}
		RealItemType itemType = RealItemType.parseItemTypeKeywords(keywords);
		RealItemStack itemStack = new RealItemStack(itemType.getTypeId());
		TransactionAction transaction = new TransactionAction(plugin);
		player.sendMessage("Nearest shops for " + itemType.toNamedString() + " are :");
		short count = 0;
		for (Shop shop : plugin.getShopList().getSortedByDistance(player.getLocation()).values()) {
			int amount = shop.contains(itemStack);
			if ((amount > 0) && shop.canBuyItem(plugin, itemStack)) {
				count ++;
				player.sendMessage(
					((count == gpsCall) ? RealColor.price : RealColor.message)
					+ count + ". " + shop.getName()
					+ " " + RealLocation.toString(shop.getLocation()).replace(";", ", ")
					+ " x" + amount
					+ " (" + transaction.calculatePrice(shop, itemStack).getBuyPrice()
					+ " each)"
				);
				if (count == gpsCall) {
					player.setCompassTarget(shop.getLocation());
				}
			}
			if (count >= 9) {
				break;
			}
		}
		if (gpsCallString.equals("off") || gpsCallString.equals("nogps")) {
			player.setCompassTarget(player.getLocation().getWorld().getSpawnLocation());
		}
	}

	//------------------------------------------------------------------------------------ searchItem
	public void searchItem(String item, Player player, String gpsCallString)
	{
		String[] keyWords = {item};
		searchItem(keyWords, player, gpsCallString);
	}

}
