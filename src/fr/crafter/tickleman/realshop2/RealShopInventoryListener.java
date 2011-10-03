package fr.crafter.tickleman.realshop2;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;

import fr.crafter.tickleman.realplugin.RealInventoryListener;
import fr.crafter.tickleman.realplugin.RealInventoryMove;
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
		Player player = event.getPlayer();
		Shop shop = plugin.getPlayerShopList().insideShop(player);
		if ((shop != null) && (event.getSlot() > -999)) {
			// do something only if clicked on an inventory slot, player being into a shop
			RealInventoryMove move = whatWillReallyBeDone(event);
			boolean clickIntoChest = event.getInventory().getName().toLowerCase().contains("chest");
			TransactionAction transactionAction = new TransactionAction(plugin);
			if (clickIntoChest) {
				if (event.isShiftClick() && shop.getInfiniteBuy(plugin.getConfig().shopInfiniteBuy)) {
					// infinite buy : you can't shift-click that sorry (too much complicated to code)
					event.setCancelled(true);
				} else if (
					(
						shop.getInfiniteBuy(plugin.getConfig().shopInfiniteBuy)
						|| shop.getInfiniteBuy(plugin.getConfig().shopInfiniteSell)
					) && (event.getCursor() != null) && (event.getItem() != null)
				) {
					// infinite buy : you can't click with something on cursor and item slot (too much complicated to code)
					event.setCancelled(true);
				} else {
					// click into chest : sell moved cursor stack, buy moved item stack
					if (transactionAction.canPay(player, shop, move.getItem(), move.getCursor())) {
						if (move.getCursor() != null) {
							transactionAction.sell(player, shop, move.getCursor());
							// infinite sell : empty cursor and nothing changes into inventory slot
							if (shop.getInfiniteSell(plugin.getConfig().shopInfiniteSell)) {
								event.setResult(Result.ALLOW);
								event.setCursor(null);
							}
						}
						if (move.getItem() != null) {
							transactionAction.buy(player, shop, move.getItem());
							// infinite buy : put inventory slot into cursor and does not empty inventory slot
							if (shop.getInfiniteBuy(plugin.getConfig().shopInfiniteBuy)) {
								event.setResult(Result.ALLOW);
								event.setCursor(move.getItem().clone());
								event.setCancelled(true);
							}
						}
					} else {
						event.setCancelled(true);
					}
				}
			} else if (event.isShiftClick() && (move.getItem() != null)) {
				if (shop.getInfiniteSell(plugin.getConfig().shopInfiniteSell)) {
					// infinite sell : you can't shift-click sorry (too much complicated to code)
					event.setCancelled(true);
				} else if (transactionAction.sell(player, shop, move.getItem()) == 0) {
					// shift-click into player's slot : sell moved item stack
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
