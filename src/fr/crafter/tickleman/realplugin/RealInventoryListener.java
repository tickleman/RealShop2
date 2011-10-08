package fr.crafter.tickleman.realplugin;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;

//########################################################################### RealInventoryListener
public class RealInventoryListener extends InventoryListener
{

	/** Which inventory object has clicked the player ? himself, a chest, a furnace, ... ? */
	private HashMap <Player, Inventory> playerInventory = new HashMap <Player, Inventory>();

	//--------------------------------------------------------------------------------- availableRoom
	/**
	 * Return available quantity for the item stack into the inventory
	 *
	 * @param inventory the inventory to put the item stack into
	 * @param itemStack the item stack we want to put into the inventory
	 * @return integer available quantity, 0 if no room, itemStack.getAmount() if there is enough room
	 */
	public int availableRoom(Inventory inventory, ItemStack itemStack)
	{
		int toStore = itemStack.getAmount();
		if (inventory.firstEmpty() > -1) {
			return itemStack.getAmount();
		} else {
			for (ItemStack slot : inventory.getContents()) {
				if (	
					(slot.getTypeId() == itemStack.getTypeId())
					&& (slot.getType().getMaxStackSize() > slot.getAmount())
				) {
					toStore -= (slot.getType().getMaxStackSize() - slot.getAmount());
					if (toStore <= 0) {
						return itemStack.getAmount();
					}
				}
			}
			return itemStack.getAmount() - toStore;
		}
	}

	//------------------------------------------------------------------------ doWhatWillReallyBeDone
	/**
	 * Change the event : do only the calculated action
	 * (not tested, is this really usefull ?)
	 *
	 * @param event
	 */
	public RealInventoryMove doWhatWillReallyBeDone(InventoryClickEvent event)
	{
		RealInventoryMove inventoryMove = whatWillReallyBeDone(event);
		event.setResult(Result.ALLOW);
		event.setCursor(inventoryMove.getCursor());
		event.setItem(inventoryMove.getItem());
		event.setCancelled(true);
		return inventoryMove;
	}

	//------------------------------------------------------------------------------- insideInventory
	/**
	 * Which inventory is opened on the player's view ?
	 *
	 * @param Player player
	 * @return Inventory
	 */
	public Inventory insideInventory(Player player)
	{
		return playerInventory.get(player);
	}

	//------------------------------------------------------------------------------ onInventoryClose
	@Override
  public void onInventoryClose(InventoryCloseEvent event)
  {
		super.onInventoryClose(event);
		if (event.getInventory() != event.getPlayer().getInventory()) {
			playerInventory.remove(event.getPlayer());
		}
  }

	//------------------------------------------------------------------------------- onInventoryOpen
	@Override
	public void onInventoryOpen(InventoryOpenEvent event)
	{
		super.onInventoryOpen(event);
		if (event.getInventory() != event.getPlayer().getInventory()) {
			playerInventory.put(event.getPlayer(), event.getInventory());
		}
	}

	//----------------------------------------------------------------------------- rightClickOnlyOne
	/**
	 * If right-click on a stack containing items and cursor is empty :
	 * take only one item on inventory, instead of 50%
	 * All other configurations : standard behavior
	 *
	 * @param event
	 */
	public void rightClickOnlyOne(InventoryClickEvent event)
	{
		if (
			(event.getCursor() == null) && (event.getItem() != null)
			&& !event.isLeftClick() && !event.isShiftClick()
		) {
			event.setResult(Result.ALLOW);
			event.setCursor(event.getItem()/*.clone()*/);
			event.getCursor().setAmount(1);
			if (event.getItem().getAmount() == 1) {
				event.setItem(null);
			} else {
				event.getItem().setAmount(event.getItem().getAmount() - 1);
			}
			event.setCancelled(true);
		}
	}

	//-------------------------------------------------------------------------- whatWillReallyBeDone
	/**
	 * Return the real quantities that will be moved when the event will be done (item exchanges)
	 * 
	 * Calculates the quantity into the cursor that will be released (null if none)
	 * and the quantity from the slot (item) that will be taken
	 * 
	 * This tries to respect Minecraft game's rules to tell the developer the really moving quantities
	 *
	 * @param InventoryClickEvent event
	 * @return ItemStack[2] {cursor, item}
	 */
	public RealInventoryMove whatWillReallyBeDone(InventoryClickEvent event)
	{
		ItemStack cursor = ((event.getCursor() == null) ? null : event.getCursor().clone());
		ItemStack item   = ((event.getItem() == null) ? null : event.getItem().clone());
		if (event.getSlot() > -999) {
			if (event.isShiftClick()) {
				// shift click : check if there is enough room into the destination inventory
				if (item != null) {
					Inventory checkInventory = event.getInventory().getName().equals("Inventory")
						? insideInventory(event.getPlayer())
						: event.getPlayer().getInventory();
					int room = availableRoom(checkInventory, item);
					if (room < item.getAmount()) {
						if (room > 0) {
							item.setAmount(room);
						} else {
							item = null;
						}
					}
					cursor = null;
				}
			} else if (event.isLeftClick()) {
				// left click into the same item : check if there is enough room into the destination slot
				if ((item != null) && (cursor != null) && (item.getTypeId() == cursor.getTypeId())) {
					int room = Math.min(cursor.getAmount(), item.getType().getMaxStackSize() - item.getAmount());
					if (room > 0) {
						cursor.setAmount(room);
					} else {
						cursor = null;
					}
					item = null;
				}
			} else if ((item == null) && (cursor != null)) {
				// right click on an empty slot : cursor 1
				cursor.setAmount(1);
			} else if ((item != null) && (cursor == null)) {
				// right click to item from slot : item 50%
				item.setAmount((int)Math.ceil(item.getAmount() / 2.0));
			} else if ((item != null) && (cursor != null) && (item.getTypeId() == cursor.getTypeId())) {
				// right click into the same item : check if there is enough room into the destination slot to cursor 1
				if (item.getType().getMaxStackSize() > item.getAmount()) {
					cursor.setAmount(1);
				} else {
					cursor = null;
				}
				item = null;
			}
		}
		return new RealInventoryMove(cursor, item);
	}

}
