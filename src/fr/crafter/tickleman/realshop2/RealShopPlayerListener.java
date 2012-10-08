package fr.crafter.tickleman.realshop2;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.crafter.tickleman.realshop2.price.PlayerPriceAction;
import fr.crafter.tickleman.realshop2.shop.ShopAction;

//########################################################################## RealShopPlayerListener
/**
 * Handle events for all Player related events
 * @author tickleman
 */
public class RealShopPlayerListener implements Listener
{

	private final RealShop2Plugin plugin;

	//------------------------------------------------------------------------ RealShopPlayerListener
	public RealShopPlayerListener(RealShop2Plugin instance)
	{
		super();
		plugin = instance;
	}

	//---------------------------------------------------------------------------------- onPlayerChat
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		if (PlayerPriceAction.isChangingPrice(player)) {
			if (new PlayerPriceAction(plugin, player).chatChangePriceChat(
				event.getPlayer(), event.getMessage()
			)) {
				event.setCancelled(true);
			}
		}
	}

	//------------------------------------------------------------------------------ onPlayerInteract
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		// check if player right-clicked a chest
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
    	Block block = event.getClickedBlock();
			if (block.getType().equals(Material.CHEST)) {
				if (!new ShopAction(plugin).enterChestBlock(event.getPlayer(), block)) {
					event.setCancelled(true);
				}
			}
		}
	}

	//--------------------------------------------------------------------------------- onPlayerLogin
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		plugin.getPlayerChestList().unselectChest(event.getPlayer());
		plugin.getPlayerShopList().exitShop(event.getPlayer());
	}

	//---------------------------------------------------------------------------------- onPlayerQuit
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		plugin.getPlayerChestList().unselectChest(event.getPlayer());
		plugin.getPlayerShopList().exitShop(event.getPlayer());
	}

}
