package fr.crafter.tickleman.realshop2;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

//########################################################################## RealShopEntityListener
public class RealShopEntityListener extends EntityListener
{

	private RealShop2Plugin plugin;

	public final static int BLAST_RADIUS = 4;

	//------------------------------------------------------------------------ RealShopEntityListener
	public RealShopEntityListener(RealShop2Plugin plugin)
	{
		this.plugin = plugin;
	}

	//------------------------------------------------------------------------------- onEntityExplode
	@Override
	public void onEntityExplode(EntityExplodeEvent event)
	{
		List<Block> dontExplodeBlocks = new ArrayList<Block>();
		for (Block block : event.blockList()) {
			if (block.getType().equals(Material.CHEST)) {
				if (plugin.getShopList().shopAt(block) != null) {
					dontExplodeBlocks.add(block);
				}
			}
		}
		for (Block block : dontExplodeBlocks) {
			event.blockList().remove(block);
		}
	}

}
