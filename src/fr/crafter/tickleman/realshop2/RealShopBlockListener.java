package fr.crafter.tickleman.realshop2;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;

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
			Shop shop = plugin.getShopList().shopAt(block.getLocation());
			if (shop != null) {
				// break a chest that is a shop : select shop and cancel break
				if (player instanceof Player) {
					new ShopAction(plugin).selectShop(player, shop);
				}
				if (plugin.getRealConfig().shopProtection) {
					event.setCancelled(true);
				} else {
					plugin.getLog().debug("removed shop on block break " + shop.toString());
					plugin.getShopList().remove(shop);
					plugin.getShopList().save();
				}
			} else if (player instanceof Player) {
				// break a chest that is not a shop : does nothing, only unselect
				plugin.getPlayerChestList().unselectChest(player);
				plugin.getPlayerShopList().unselectShop(player);
			}
		}
	}

	//----------------------------------------------------------------------------------- onBlockBurn
	@Override
	public void onBlockBurn(BlockBurnEvent event)
	{
		Block block = event.getBlock();
		if (block.getType().equals(Material.CHEST)) {
			Shop shop = plugin.getShopList().shopAt(block.getLocation());
			if (shop != null) {
				if (plugin.getRealConfig().shopProtection) {
					event.setCancelled(true);
				} else {
					plugin.getLog().debug("removed shop on block burn " + shop.toString());
					plugin.getShopList().remove(shop);
					plugin.getShopList().save();
				}
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
			Shop shop = plugin.getShopList().shopAt(block.getLocation());
			if (shop != null) {
				// damage a chest that is a shop : select shop and cancel damage
				if (player instanceof Player) {
					new ShopAction(plugin).selectShop(player, shop);
				}
				if (plugin.getRealConfig().shopProtection) {
					event.setCancelled(true);
				}
			} else if (player instanceof Player) {
				// damage a chest that is not a shop : does nothing, only unselect shop and select chest
				plugin.getPlayerShopList().unselectShop(player);
				plugin.getPlayerChestList().selectChest(player, new RealChest(block));
			}
		}
	}

	//----------------------------------------------------------------------------------- onBlockFade
	@Override
	public void onBlockFade(BlockFadeEvent event)
	{
		Block block = event.getBlock();
		if (block.getType().equals(Material.CHEST)) {
			Shop shop = plugin.getShopList().shopAt(block.getLocation());
			if (shop != null) {
				if (plugin.getRealConfig().shopProtection) {
					event.setCancelled(true);
				} else {
					plugin.getLog().debug("removed shop on block fade " + shop.toString());
					plugin.getShopList().remove(shop);
					plugin.getShopList().save();
				}
			}
		}
	}

	//--------------------------------------------------------------------------------- onBlockIgnite
	@Override
	public void onBlockIgnite(BlockIgniteEvent event)
	{
		Block block = event.getBlock();
		if (block.getType().equals(Material.CHEST)) {
			Shop shop = plugin.getShopList().shopAt(block.getLocation());
			if (shop != null) {
				if (plugin.getRealConfig().shopProtection) {
					event.setCancelled(true);
				} else {
					plugin.getLog().debug("removed shop on block ignite " + shop.toString());
					plugin.getShopList().remove(shop);
					plugin.getShopList().save();
				}
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
			Shop shop = plugin.getShopList().shopAt(block.getLocation());
			plugin.getLog().debug(
				"shop at " + new RealLocation(block.getLocation()).toString() + " = "
				+ (shop == null ? "null" : shop.toString())
			);
			if (shop != null) {
				if (shop.containsLocation(block.getLocation())) {
					// place chest on a location where it was an old "ghost shop" : delete the shop
					plugin.getLog().debug(
						"There is a ghost shop " + shop.toString()
						+ " at location " + new RealLocation(block.getLocation()).toString()
					);
					plugin.getLog().debug("shop will be deleted");
					plugin.getShopList().remove(shop);
					plugin.getShopList().save();
				} else {
					// place chest near a shop-chest : make the shop bigger
					plugin.getLog().debug(
						"There is a shop on the chest block next to "
						+ new RealLocation(block.getLocation()).toString()
					);
					plugin.getLog().debug("Make the shop bigger");
					shop.forceLocations(shop.getLocation(), block.getLocation());
					plugin.getShopList().save();
					plugin.getPlayerShopList().selectShop(player, shop);
				}
			} else {
				// auto-select chest
				plugin.getPlayerChestList().selectChest(player, new RealChest(block));
			}
		}
	}

	//--------------------------------------------------------------------------------- onBlockSpread
	@Override
	public void onBlockSpread(BlockSpreadEvent event)
	{
		Block block = event.getBlock();
		if (block.getType().equals(Material.CHEST)) {
			Shop shop = plugin.getShopList().shopAt(block.getLocation());
			if (shop != null) {
				if (plugin.getRealConfig().shopProtection) {
					event.setCancelled(true);
				} else {
					plugin.getLog().debug("removed shop on block spread " + shop.toString());
					plugin.getShopList().remove(shop);
					plugin.getShopList().save();
				}
			}
		}
	}

}
