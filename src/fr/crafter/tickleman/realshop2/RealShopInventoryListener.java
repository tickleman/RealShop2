package fr.crafter.tickleman.realshop2;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import fr.crafter.tickleman.realplugin.RealColor;
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

	//---------------------------------------------------------------------------- anotherCancelEvent
	public void anotherCancelEvent(InventoryClickEvent event, RealInventoryMove originItems)
	{
		if (originItems != null) {
			plugin.getLog().debug(
				"anotherCancelEvent : back to "
				+ originItems.getItem().getAmount() + ", " + originItems.getCursor().getAmount()
			);
			event.setCurrentItem(originItems.getItem());
			event.setCursor(originItems.getCursor());
		}
		event.setCancelled(true);
	}

	//------------------------------------------------------------------------------ anotherWayToMove
	private RealInventoryMove anotherWayToMove(RealInventoryMove move, InventoryClickEvent event)
	{
		if (event.isRightClick() && !plugin.getRealConfig().rightClickBuyMode.equals("chest")) {
			if (plugin.getRealConfig().rightClickBuyMode.equals("one")) {
				if (
					(move.getCursor().getAmount() == 0)
					&& (event.getCursor().getAmount() == 0)
					&& (move.getItem().getAmount() > 1)
					&& (event.getCurrentItem().getAmount() > 1)
				) {
					RealInventoryMove originItems = new RealInventoryMove(
						RealItemStack.cloneItem(event.getCursor()),
						RealItemStack.cloneItem(event.getCurrentItem())
					);
					event.setResult(Result.ALLOW);
					event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
					event.setCursor(event.getCurrentItem().clone());
					event.getCursor().setAmount(1);
					move.getItem().setAmount(1);
					plugin.getLog().debug("anotherWayToMove => 1");
					return originItems;
				}
			}
		}
		return null;
	}

	//------------------------------------------------------------------------------ onInventoryClick
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		plugin.getLog().debug(
			"onInventoryClick = "
			+ " cursor [ "+ RealItemStack.create(event.getCursor()).toString() + "]"
			+ " item [ "+ RealItemStack.create(event.getCurrentItem()).toString() + "]"
		);
		HumanEntity humanEntity = event.getWhoClicked();
		if (humanEntity instanceof Player) {
			Player player = (Player)humanEntity;
			Shop shop = plugin.getPlayerShopList().insideShop(player);
			if (
				(shop != null) && (event.getSlot() > -999)
				&& !shop.getPlayerName().equalsIgnoreCase(player.getName())
				&& !shop.playerIsAnAssistant(player.getName())
			) {
				// do something only if clicked on an inventory slot,
				// and if the player is into another player's shop
				RealInventoryMove move = whatWillReallyBeDone(event);
				plugin.getLog().debug(
					"whatWillReallyBeDone = "
					+ " cursor [ "+ RealItemStack.create(move.getCursor()) + "]"
					+ " item [ "+ RealItemStack.create(move.getItem()) + "]"
				);
				//System.out.println("raw slot is " + event.getRawSlot());
				boolean clickIntoChest = clickedInventory(event).equals(event.getInventory());
				TransactionAction transactionAction = new TransactionAction(plugin);
				if (clickIntoChest) {
					if (event.isShiftClick() && shop.getInfiniteBuy(plugin.getRealConfig().shopInfiniteBuy)) {
						// infinite buy : you can't shift-click that sorry (too much complicated to code)
						plugin.getLog().debug("infinite buy not allowed with shift-click : cancel");
						event.setCancelled(true);
					} else if (
						shop.getInfiniteBuy(plugin.getRealConfig().shopInfiniteBuy)
						&& !event.getCursor().getType().equals(Material.AIR)
						&& !event.getCurrentItem().getType().equals(Material.AIR)
					) {
						// infinite buy : you can't click with something on cursor and item slot (too much complicated to code)
						plugin.getLog().debug("infinite buy not allowed with cursor + item slots filled : cancel");
						event.setCancelled(true);
					} else if (
						(
							shop.getInfiniteBuy(plugin.getRealConfig().shopInfiniteBuy)
							|| shop.getInfiniteSell(plugin.getRealConfig().shopInfiniteSell)
						)
						&& !event.getCursor().getType().equals(Material.AIR)
						&& !event.getCurrentItem().getType().equals(Material.AIR)
						&& !event.getCursor().getType().equals(event.getCurrentItem().getType())
					) {
						// infinite buy or infinite sell shop : can't exchange items (too much complicated)
						plugin.getLog().debug("shop is infinite buy or infinite sell : can't do that");
						event.setCancelled(true);
					} else {
						// click into chest : sell moved cursor stack, buy moved item stack
						RealInventoryMove originItems = anotherWayToMove(move, event);
						if (transactionAction.canPay(player, shop, move.getItem(), move.getCursor())) {
							if (!move.getCursor().getType().equals(Material.AIR)) {
								if (transactionAction.sell(player, shop, move.getCursor()) > 0) {
									// infinite sell : empty cursor and nothing changes into inventory slot
									if (shop.getInfiniteSell(plugin.getRealConfig().shopInfiniteSell)) {
										plugin.getLog().debug("infinite sell action : null item");
										event.setResult(Result.ALLOW);
										if ((event.getCursor().getAmount() - move.getCursor().getAmount()) == 0) {
											event.setCursor(new ItemStack(Material.AIR, 0, (short)-1));
										} else {
											event.getCursor().setAmount(
												event.getCursor().getAmount() - move.getCursor().getAmount()
											);
										}
										anotherCancelEvent(event, originItems);
									}
								}
							}
							if (!move.getItem().getType().equals(Material.AIR)) {
								if (transactionAction.buy(player, shop, move.getItem()) > 0) {
									// infinite buy : put inventory slot into cursor and does not empty inventory slot
									if (shop.getInfiniteBuy(plugin.getRealConfig().shopInfiniteBuy)) {
										plugin.getLog().debug("infinite buy action : clone item and cancel");
										event.setResult(Result.ALLOW);
										event.setCursor(move.getItem().clone());
										anotherCancelEvent(event, originItems);
									}
								}
							}
						} else {
							// can't pay (can't sell + buy)
							plugin.getLog().debug("Can't pay : cancel");
							anotherCancelEvent(event, originItems);
						}
					}
				} else if (event.isShiftClick() && !move.getItem().getType().equals(Material.AIR)) {
					if (availableRoom(event.getInventory(), move.getItem()) < move.getItem().getAmount()) {
						player.sendMessage(
							RealColor.cancel
							+ plugin.tr("Not enough room for +quantity1 (+quantity2 available)")
							.replace("+quantity1", RealColor.quantity + new Integer(move.getItem().getAmount()).toString() + RealColor.cancel)
							.replace("+quantity2", RealColor.quantity + new Integer(availableRoom(event.getInventory(), move.getItem())).toString() + RealColor.cancel)
						);
						event.setCancelled(true);
					} else {
						if (shop.getInfiniteSell(plugin.getRealConfig().shopInfiniteSell)) {
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
		} else {
			// only players can click on inventories slots
			event.setCancelled(true);
		}
	}

	//------------------------------------------------------------------------------ onInventoryClose
	@EventHandler
  public void onInventoryClose(InventoryCloseEvent event)
  {
  	HumanEntity humanEntity = event.getPlayer();
  	if (humanEntity instanceof Player) {
  		Player player = (Player)humanEntity;
  		super.onInventoryClose(event);
			if (plugin.getPlayerShopList().isInShop(player)) {
				new ShopAction(plugin).exitShop(player);
			}
  	}
  }

	//------------------------------------------------------------------------------- onInventoryOpen
	@EventHandler
  public void onInventoryOpen(InventoryOpenEvent event)
  {
		plugin.getLog().debug(event.getPlayer().getName() + " entered shop ");
		super.onInventoryOpen(event);
  }

}
