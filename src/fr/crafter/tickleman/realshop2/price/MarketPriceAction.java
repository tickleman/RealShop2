package fr.crafter.tickleman.realshop2.price;

import fr.crafter.tickleman.realshop2.RealShop2Plugin;

//############################################################################### MarketPriceAction
public class MarketPriceAction extends PriceAction
{

	//----------------------------------------------------------------------------- MarketPriceAction
	public MarketPriceAction(RealShop2Plugin plugin)
	{
		super(plugin, plugin.getMarketPrices());
	}

	//---------------------------------------------------------------------------------- getPriceType
	@Override
	public String getPriceType()
	{
		return "Market";
	}

}
