package fr.crafter.tickleman.realshop2;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.crafter.tickleman.realshop2.shop.Shop;

//########################################################################## RealShopEntityListener
public class RealShopEntityListener implements Listener
{

	private RealShop2Plugin plugin;

	public final static int BLAST_RADIUS = 4;

	//------------------------------------------------------------------------ RealShopEntityListener
	public RealShopEntityListener(RealShop2Plugin plugin)
	{
		this.plugin = plugin;
	}

	//------------------------------------------------------------------------------- onEntityExplode
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event)
	{
		List<Block> dontExplodeBlocks = new ArrayList<Block>();
		for (Block block : event.blockList()) {
			if (block.getType().equals(Material.CHEST)) {
				Shop shop = plugin.getShopList().shopAt(block.getLocation());
				if (shop != null) {
					if (plugin.getRealConfig().shopProtection) {
						dontExplodeBlocks.add(block);
					} else {
						plugin.getLog().debug("removed shop on creeper explosion " + shop.toString());
						plugin.getShopList().remove(shop);
						plugin.getShopList().save();
					}
				}
			}
		}
		for (Block block : dontExplodeBlocks) {
			event.blockList().remove(block);
		}
	}

}
