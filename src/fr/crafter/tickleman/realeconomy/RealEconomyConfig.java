package fr.crafter.tickleman.realeconomy;

import fr.crafter.tickleman.realplugin.Config;
import fr.crafter.tickleman.realplugin.RealPlugin;

//################################################################################## RealShopConfig
public class RealEconomyConfig extends Config
{

	public String currency       = "Coin";
	public double initialBalance = 100.0;

	//----------------------------------------------------------------------------- RealEconomyConfig
	public RealEconomyConfig(final RealPlugin plugin)
	{
		super(plugin, "economy", plugin.getConfig());
	}

}
