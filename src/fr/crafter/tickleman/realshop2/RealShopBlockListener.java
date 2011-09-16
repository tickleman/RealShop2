package fr.crafter.tickleman.realshop2;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;

import fr.crafter.tickleman.realplugin.RealChest;
import fr.crafter.tickleman.realshop2.shop.Shop;
import fr.crafter.tickleman.realshop2.shop.ShopAction;

//########################################################################### RealShopBlockListener
public class RealShopBlockListener extends BlockListener
{

	private final RealShop2Plugin plugin;

	//------------------------------------------------------------------------- RealShopBlockListener
	public RealShopBlockListener(final RealShop2Plugin plugin)
	{
		this.plugin = plugin;
	}

	//---------------------------------------------------------------------------------- onBlockBreak
	@Override
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (event.getBlock().getType().equals(Material.CHEST)) {
			Shop shop = plugin.getShopList().shopAt(event.getBlock());
			if (shop != null) {
				if (event.getPlayer() != null) {
					new ShopAction(plugin).selectShop(event.getPlayer(), shop);
				}
				event.setCancelled(true);
			} else if (event.getPlayer() != null) {
				plugin.getPlayerShopList().unselectShop(event.getPlayer());
				plugin.getPlayerChestList().unselectChest(event.getPlayer());
			}
		}
	}

	//--------------------------------------------------------------------------------- onBlockDamage
	@Override
	public void onBlockDamage(BlockDamageEvent event)
	{
		if (event.getBlock().getType().equals(Material.CHEST)) {
			Shop shop = plugin.getShopList().shopAt(event.getBlock());
			if (shop != null) {
				if (event.getPlayer() != null) {
					new ShopAction(plugin).selectShop(event.getPlayer(), shop);
				}
				event.setCancelled(true);
			} else if (event.getPlayer() != null) {
				plugin.getPlayerShopList().unselectShop(event.getPlayer());
				plugin.getPlayerChestList().selectChest(event.getPlayer(), new RealChest(event.getBlock()));
			}
		}
	}

}
