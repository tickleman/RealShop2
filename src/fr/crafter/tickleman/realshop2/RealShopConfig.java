package fr.crafter.tickleman.realshop2;

import fr.crafter.tickleman.realplugin.Config;

//################################################################################## RealShopConfig
public class RealShopConfig extends Config
{

	/** Default configuration values (if not in file) */
	public double  amountRatio = 5000.0;
	public double  buySellRatio = .95;
	public boolean dailyPricesCalculation = false;
	public String  economyPlugin = "none";
	public double  maxDailyRatio = 1.95;
	public double  maxItemPrice = 99999.0;
	public double  minDailyRatio = .05;
	public double  minItemPrice = .1;
	public boolean shopDamagedItems = false;
	public boolean shopInfiniteBuy = false;
	public boolean shopInfiniteSell = false;
	public boolean shopMarketItemsOnly = false;
	public boolean shopOpOnly = false;
	public double  workForceRatio = 1.1;

	//---------------------------------------------------------------------------------------- Config
	public RealShopConfig(final RealShop2Plugin plugin)
	{
		super(plugin);
	}

	//------------------------------------------------------------------------------------- getPlugin
	@Override
	protected RealShop2Plugin getPlugin()
	{
		return (RealShop2Plugin)super.getPlugin();
	}

}
