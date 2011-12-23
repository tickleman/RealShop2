package fr.crafter.tickleman.realshop2.price;

import org.bukkit.entity.Player;

import fr.crafter.tickleman.realshop2.RealShop2Plugin;

//############################################################################# PlayerPricesActions
public class PlayerPriceAction extends PriceAction
{

	//--------------------------------------------------------------------------- PlayerPricesActions
	public PlayerPriceAction(RealShop2Plugin plugin, Player player)
	{
		super(plugin, new ItemPriceList(plugin, player.getName().toLowerCase()).load());
	}

	//---------------------------------------------------------------------------------- getPriceType
	@Override
	public String getPriceType()
	{
		return "Your";
	}

}
