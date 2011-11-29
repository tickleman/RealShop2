package fr.crafter.tickleman.realshop2;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;

import fr.crafter.tickleman.realplugin.RealInventoryListener;
import fr.crafter.tickleman.realplugin.RealInventoryMove;
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
		plugin.getLog().debug(
			"onInventoryClick = "
			+ " cursor [ "+ RealItemStack.create(event.getCursor()) + "]"
			+ " item [ "+ RealItemStack.create(event.getItem()) + "]"
		);
		Player player = event.getPlayer();
		Shop shop = plugin.getPlayerShopList().insideShop(player);
		if (
			(shop != null) && (event.getSlot() > -999)
			&& !shop.getPlayerName().equals(player.getName())
		) {
			// do something only if clicked on an inventory slot,
			// and if the player is into another player's shop
			RealInventoryMove move = whatWillReallyBeDone(event);
			plugin.getLog().debug(
				"whatWillReallyBeDone = "
				+ " cursor [ "+ RealItemStack.create(move.getCursor()) + "]"
				+ " item [ "+ RealItemStack.create(move.getItem()) + "]"
			);
			boolean clickIntoChest = event.getInventory().getName().toLowerCase().contains("chest");
			TransactionAction transactionAction = new TransactionAction(plugin);
			if (clickIntoChest) {
				if (event.isShiftClick() && shop.getInfiniteBuy()) {
					// infinite buy : you can't shift-click that sorry (too much complicated to code)
					plugin.getLog().debug("infinite buy not allowed with shift-click : cancel");
					event.setCancelled(true);
				} else if (
					(
						shop.getInfiniteBuy()
						|| shop.getInfiniteBuy()
					) && (event.getCursor() != null) && (event.getItem() != null)
				) {
					// infinite buy : you can't click with something on cursor and item slot (too much complicated to code)
					plugin.getLog().debug("infinite buy not allowed with cursor + item slots filled : cancel");
					event.setCancelled(true);
				} else {
					// click into chest : sell moved cursor stack, buy moved item stack
					if (transactionAction.canPay(player, shop, move.getItem(), move.getCursor())) {
						if (move.getCursor() != null) {
							if (transactionAction.sell(player, shop, move.getCursor()) > 0) {
								// infinite sell : empty cursor and nothing changes into inventory slot
								if (shop.getInfiniteSell()) {
									plugin.getLog().debug("infinite sell action : null cursor");
									event.setResult(Result.ALLOW);
									event.setCursor(null);
								}
							}
						}
						if (move.getItem() != null) {
							if (transactionAction.buy(player, shop, move.getItem()) > 0) {
								// infinite buy : put inventory slot into cursor and does not empty inventory slot
								if (shop.getInfiniteBuy()) {
									plugin.getLog().debug("infinite buy action : clone item and cancel");
									event.setResult(Result.ALLOW);
									event.setCursor(move.getItem().clone());
									event.setCancelled(true);
								}
							}
						}
					} else {
						// can't pay (can't sell + buy)
						plugin.getLog().debug("Can't pay : cancel");
						event.setCancelled(true);
					}
				}
			} else if (event.isShiftClick() && (move.getItem() != null)) {
				if (shop.getInfiniteSell()) {
					// infinite sell : you can't shift-click sorry (too much complicated to code)
					plugin.getLog().debug("infinite-sell is not allowed with shift-click : cancel");
					event.setCancelled(true);
				} else if (transactionAction.sell(player, shop, move.getItem()) == 0) {
					// shift-click into player's slot : sell moved item stack
					plugin.getLog().debug("shift-click on player's slot is not allowed : cancel");
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
