package fr.crafter.tickleman.realshop2;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;

import fr.crafter.tickleman.realplugin.RealInventoryListener;
import fr.crafter.tickleman.realplugin.RealItemStack;
import fr.crafter.tickleman.realshop2.shop.Shop;
import fr.crafter.tickleman.realshop2.shop.ShopAction;
import fr.crafter.tickleman.realshop2.transaction.TransactionAction;

public class RealShopInventoryListener extends RealInventoryListener
{

	private final RealShop2Plugin plugin;

	//--------------------------------------------------------------------- RealShopInventoryListener
	public RealShopInventoryListener(RealShop2Plugin plugin)
	{
		super();
		this.plugin = plugin;
	}

	//------------------------------------------------------------------------------ onInventoryClick
	@Override
	public void onInventoryClick(InventoryClickEvent event)
	{
		Shop shop = plugin.getPlayerShopList().insideShop(event.getPlayer());
		if ((shop != null) && (event.getSlot() > -999)) {
			boolean isInChest = event.getInventory().getName().toLowerCase().contains("chest");
			if (isInChest) {
				rightClickOnlyOne(event);
			}
			ItemStack[] what = whatWillReallyBeDone(event);
			TransactionAction transactionAction = new TransactionAction(plugin);
			if (event.isShiftClick() && (what[1] != null)) {
				int doneQuantity;
				if (isInChest) {
					doneQuantity = transactionAction.buy(
						event.getPlayer(), shop, new RealItemStack(what[1]), true
					);
				} else {
					doneQuantity = transactionAction.sell(
						event.getPlayer(), shop, new RealItemStack(what[1]), true
					);
				}
				if (doneQuantity == 0) {
					event.setCancelled(true);
				}
			} else if (isInChest) {
				boolean canDo = true;
				if (what[0] != null) {
					// shop's owner can buy ?
					canDo = canDo && transactionAction.canBuy(
						shop.getPlayerName(), shop, new RealItemStack(what[0])
					);
				}
				if (what[1] != null) {
					// player can buy ?
					canDo = canDo && transactionAction.canBuy(
						event.getPlayer().getName(), shop, new RealItemStack(what[1])
					);
				}
				if (canDo) {
					// execute the transaction only if can be fully done
					if (what[0] != null) {
						transactionAction.sell(
							event.getPlayer(), shop, new RealItemStack(what[0]), true
						);
					}
					if (what[1] != null) {
						transactionAction.buy(
							event.getPlayer(), shop, new RealItemStack(what[1]), true
						);
					}
				} else {
					event.setCancelled(true);
				}
			}
		}
	}

	//------------------------------------------------------------------------------ onInventoryClose
	@Override
  public void onInventoryClose(InventoryCloseEvent event)
  {
		super.onInventoryClose(event);
		if (plugin.getPlayerShopList().isInShop(event.getPlayer())) {
			new ShopAction(plugin).exitShop(event.getPlayer());
		}
  }

}
