package fr.crafter.tickleman.realshop2;

import org.bukkit.entity.Player;
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
				// click into chest : sell moved cursor stack, buy moved item stack
				if (transactionAction.canPay(player, shop, move.getItem(), move.getCursor())) {
					if (move.getCursor() != null) {
						transactionAction.sell(player, shop, move.getCursor());
					}
					if (move.getItem() != null) {
						transactionAction.buy(player, shop, move.getItem());
					}
				} else {
					event.setCancelled(true);
				}
			} else if (event.isShiftClick() && (move.getItem() != null)) {
				// shift-click into player's slot : sell moved item stack
				if (transactionAction.sell(player, shop, move.getItem()) == 0) {
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
