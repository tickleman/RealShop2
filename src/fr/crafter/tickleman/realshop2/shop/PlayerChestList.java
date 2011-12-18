package fr.crafter.tickleman.realshop2.shop;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import fr.crafter.tickleman.realplugin.RealChest;
import fr.crafter.tickleman.realshop2.RealShop2Plugin;

//################################################################################# PlayerChestList
public class PlayerChestList
{

	RealShop2Plugin plugin;

	/**
	 * Players selected chest list : Player => RealChest
	 */
	private Map<Player, RealChest> chests = new HashMap<Player, RealChest>();

	//------------------------------------------------------------------------------- PlayerChestList
	public PlayerChestList(RealShop2Plugin plugin)
	{
		this.plugin = plugin;
	}

	//------------------------------------------------------------------------------------- isInChest
	public boolean hasSelectedChest(Player player)
	{
		return (selectedChest(player) != null);
	}

	//----------------------------------------------------------------------------------- selectChest
	public void selectChest(Player player, RealChest chest)
	{
		chests.put(player, chest);
	}

	//----------------------------------------------------------------------------------- playerChest
	public RealChest selectedChest(Player player)
	{
		return chests.get(player);
	}

	//--------------------------------------------------------------------------------- unselectChest
	public void unselectChest(Player player)
	{
		chests.remove(player);
	}

}
