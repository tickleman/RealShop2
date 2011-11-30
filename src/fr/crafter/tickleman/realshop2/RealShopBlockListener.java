package fr.crafter.tickleman.realshop2;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import fr.crafter.tickleman.realplugin.RealChest;
import fr.crafter.tickleman.realplugin.RealLocation;
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
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if (block.getType().equals(Material.CHEST)) {
			Shop shop = plugin.getShopList().shopAt(block);
			if (shop != null) {
				// break a chest that is a shop : select shop and cancel break
				if (player instanceof Player) {
					new ShopAction(plugin).selectShop(player, shop);
				}
				event.setCancelled(true);
			} else if (player instanceof Player) {
				// break a chest that is not a shop : does nothing, only unselect
				plugin.getPlayerChestList().unselectChest(player);
				plugin.getPlayerShopList().unselectShop(player);
			}
		}
	}

	//--------------------------------------------------------------------------------- onBlockDamage
	@Override
	public void onBlockDamage(BlockDamageEvent event)
	{
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if (block.getType().equals(Material.CHEST)) {
			Shop shop = plugin.getShopList().shopAt(block);
			if (shop != null) {
				// damage a chest that is a shop : select shop and cancel damage
				if (player instanceof Player) {
					new ShopAction(plugin).selectShop(player, shop);
				}
				event.setCancelled(true);
		} else if (player instanceof Player) {
				// damage a chest that is not a shop : does nothing, only unselect shop and select chest
				plugin.getPlayerShopList().unselectShop(player);
				plugin.getPlayerChestList().selectChest(player, new RealChest(block));
			}
		}
	}

	//---------------------------------------------------------------------------------- onBlockPlace
	@Override
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if (block.getType().equals(Material.CHEST) && (player instanceof Player)) {
			Shop shop = plugin.getShopList().shopAt(block);
			if (shop != null) {
				// place chest on a location where it was an old "ghost shop" : delete the shop
				plugin.getShopList().delete(shop);
				plugin.getShopList().save();
			} else {
				RealLocation location = new RealLocation(block.getLocation()).neighbor();
				if (location != null) {
					shop = plugin.getShopList().shopAt(location);
					if (shop != null) {
						// place chest near a shop-chest : make the shop bigger
						shop.setLocation(location);
						plugin.getShopList().save();
						plugin.getPlayerShopList().selectShop(player, shop);
					} else {
						// auto-select chest
						plugin.getPlayerChestList().selectChest(player, new RealChest(block));
					}
				}
			}
		}
	}

}
