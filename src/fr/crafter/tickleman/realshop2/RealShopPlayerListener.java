package fr.crafter.tickleman.realshop2;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.crafter.tickleman.realshop2.shop.ShopAction;

//########################################################################## RealShopPlayerListener
/**
 * Handle events for all Player related events
 * @author tickleman
 */
public class RealShopPlayerListener extends PlayerListener
{

	private final RealShop2Plugin plugin;

	//------------------------------------------------------------------------ RealShopPlayerListener
	public RealShopPlayerListener(RealShop2Plugin instance)
	{
		super();
		plugin = instance;
	}

	//------------------------------------------------------------------------------ onPlayerInteract
	@Override
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
	@Override
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		plugin.getPlayerChestList().unselectChest(event.getPlayer());
		plugin.getPlayerShopList().exitShop(event.getPlayer());
	}

	//---------------------------------------------------------------------------------- onPlayerQuit
	@Override
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		plugin.getPlayerChestList().unselectChest(event.getPlayer());
		plugin.getPlayerShopList().exitShop(event.getPlayer());
	}

}
