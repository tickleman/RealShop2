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
				if (player instanceof Player) {
					new ShopAction(plugin).selectShop(player, shop);
				}
				event.setCancelled(true);
			} else if (player instanceof Player) {
				plugin.getPlayerShopList().unselectShop(player);
				plugin.getPlayerChestList().unselectChest(player);
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
				if (player instanceof Player) {
					new ShopAction(plugin).selectShop(player, shop);
				}
				event.setCancelled(true);
			} else if (event.getPlayer() != null) {
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
			RealLocation location = new RealLocation(block.getLocation()).neighbor();
			if (location != null) {
				Shop shop = plugin.getShopList().shopAt(location);
				if (shop != null) {
					shop.setLocation(location);
					plugin.getShopList().save();
				} else {
					plugin.getPlayerChestList().selectChest(player, new RealChest(block));
				}
			}
		}
	}

}
