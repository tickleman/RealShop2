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
		System.out.println(" ");
		System.out.println("onInventoryClick()");
		System.out.println("event.cursor = " + ((event.getCursor() == null) ? "null" : ("#" + event.getCursor().getTypeId() + " x" + event.getCursor().getAmount())));
		System.out.println("event.item = " + ((event.getItem() == null) ? "null" : ("#" + event.getItem().getTypeId() + " x" + event.getItem().getAmount())));
		Player player = event.getPlayer();
		Shop shop = plugin.getPlayerShopList().insideShop(player);
		if ((shop != null) && (event.getSlot() > -999)) {
			// do something only if clicked on an inventory slot, player being into a shop
			RealInventoryMove move = whatWillReallyBeDone(event);
			boolean clickIntoChest = event.getInventory().getName().toLowerCase().contains("chest");
			System.out.println("clickIntoChest = " + (clickIntoChest ? "true" : "false"));
			System.out.println("what.cursor = " + ((move.getCursor() == null) ? "null" : ("#" + move.getCursor().getTypeId() + " x" + move.getCursor().getAmount())));
			System.out.println("what.item = " + ((move.getItem() == null) ? "null" : ("#" + move.getItem().getTypeId() + " x" + move.getItem().getAmount())));
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
					System.out.println("  cancelled !");
					event.setCancelled(true);
				}
			} else if (event.isShiftClick() && (move.getItem() != null)) {
				// shift-click into player's slot : sell moved item stack
				if (transactionAction.sell(player, shop, move.getItem()) == 0) {
					System.out.println("  cancelled !");
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
