package fr.crafter.tickleman.realshop2;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import fr.crafter.tickleman.realeconomy.RealEconomy;
import fr.crafter.tickleman.realeconomy.RealEconomyCommand;
import fr.crafter.tickleman.realplugin.RealPlugin;
import fr.crafter.tickleman.realplugin.RealColor;
import fr.crafter.tickleman.realplugin.RealStats;
import fr.crafter.tickleman.realshop2.price.ItemPriceList;
import fr.crafter.tickleman.realshop2.shop.PlayerChestList;
import fr.crafter.tickleman.realshop2.shop.PlayerShopList;
import fr.crafter.tickleman.realshop2.shop.ShopList;

//########################################################################### class RealShop2Plugin
public class RealShop2Plugin extends RealPlugin
{

	private ItemPriceList   marketPrices = null;
	private RealEconomy     economy = null;
	private PlayerChestList playerChestList = null;
	private PlayerShopList  playerShopList = null;
	private ShopList        shopList = null;

	//------------------------------------------------------------------------------- RealShop2Plugin
	public RealShop2Plugin()
	{
		super();
		playerChestList = new PlayerChestList(this);
		playerShopList  = new PlayerShopList(this);
	}

	//--------------------------------------------------------------------------------- getRealConfig
	@Override
	public RealShopConfig getRealConfig()
	{
		return (RealShopConfig)super.getRealConfig();
	}

	//------------------------------------------------------------------------------------ getEconomy
	public RealEconomy getEconomy()
	{
		return economy;
	}

	//------------------------------------------------------------------------------- getMarketPrices
	public ItemPriceList getMarketPrices()
	{
		return marketPrices;
	}

	//---------------------------------------------------------------------------- getPlayerChestList
	public PlayerChestList getPlayerChestList()
	{
		return playerChestList;
	}

	//----------------------------------------------------------------------------- getPlayerShopList
	public PlayerShopList getPlayerShopList()
	{
		return playerShopList;
	}

	//----------------------------------------------------------------------------------- getShopList
	public ShopList getShopList()
	{
		return shopList;
	}

	//------------------------------------------------------------------------------------ loadConfig
	@Override
	protected void loadConfig()
	{
		config = new RealShopConfig(this).load();
	}

	//------------------------------------------------------------------------------- onPlayerCommand
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		String command = cmd.getName().toLowerCase();
		if (command.equals("rs") || command.equals("rshop") || command.equals("realshop")) {
			return new RealShopCommand(this).executeCommand(sender, args);
		} else if ((command.equals("mny") || command.equals("money")) && (
			getEconomy().getEconomyPlugin().equals("none")
			|| getEconomy().getEconomyPlugin().equals("RealEconomy")
		)) {
			return new RealEconomyCommand(this, getEconomy()).executeCommand(sender, args);
		} else {
			return false;
		}
	}

	//-------------------------------------------------------------------------------------- onEnable
	@Override
	public void onDisable()
	{
		if (marketPrices != null) marketPrices.clear();
		if (shopList     != null) shopList.clear();
		super.onDisable();
	}

	//-------------------------------------------------------------------------------------- onEnable
	@Override
	public void onEnable()
	{
		super.onEnable();
		// load files
		marketPrices = new ItemPriceList(this, "market").load();
		shopList     = new ShopList(this).load();
		this.economy = new RealEconomy(this);
		// register events
		RealShopBlockListener     blockListener     = new RealShopBlockListener(this);
		RealShopEntityListener    entityListener    = new RealShopEntityListener(this);
		RealShopInventoryListener inventoryListener = new RealShopInventoryListener(this);
		RealShopPlayerListener    playerListener    = new RealShopPlayerListener(this);
		RealShopServerListener    serverListener    = new RealShopServerListener(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(blockListener, this);
		pm.registerEvents(blockListener, this);
		pm.registerEvents(blockListener, this);
		pm.registerEvents(blockListener, this);
		pm.registerEvents(blockListener, this);
		pm.registerEvents(blockListener, this);
		pm.registerEvents(blockListener, this);
		pm.registerEvents(inventoryListener, this);
		pm.registerEvents(entityListener, this);
		pm.registerEvents(playerListener, this);
		pm.registerEvents(playerListener, this);
		pm.registerEvents(playerListener, this);
		pm.registerEvents(serverListener, this);
		pm.registerEvents(serverListener, this);
		// initialize links
		getEconomy().initRegister();
		getEconomy().initVault();
		getPermissions().initPermissionsHandler();
		// check this out
		//System.out.println("ALL RECIPES :");
		//RealRecipe.dumpAllRecipes();
		//RealRecipe.getItemRecipes(new RealItemType(Material.POTION, (short)16));
		RealStats.call(this, "use");
	}

	//------------------------------------------------------------------------------- opHasPermission
	/**
	 * Op can do anything if permission system is "none"
	 */
	@Override
	public boolean opHasPermission(String permissionString)
	{
		return true;
	}

	//--------------------------------------------------------------------------- playerHasPermission
	/**
	 * if permission system is "none", player :
	 * - if shopOpOnly == true :  can shop only
	 * - if shopOpOnly == false : can shop and manage shops
	 * @throws Exception 
	 */
	@Override
	public boolean playerHasPermission(String permissionString)
	{
		// RealEconomy "mny" commands
		if (permissionString.substring(0, 11).equals("realeconomy")) {
			return permissionString.equals("realeconomy")
				|| permissionString.equals("realeconomy.burn")
				|| permissionString.equals("realeconomy.display")
				|| permissionString.equals("realeconomy.give")
				|| permissionString.equals("realeconomy.help");
		}
		// realshop "rs" / "rshop" commands
		if (getRealConfig().shopOpOnly) {
			return permissionString.equals("realshop")
				|| permissionString.equals("realshop.shop")
				|| permissionString.equals("realshop.info");
		} else {
			return permissionString.equals("realshop")
				|| permissionString.equals("realshop.shop")
				|| permissionString.equals("realshop.help")
				|| permissionString.equals("realshop.info")
				|| permissionString.equals("realshop.create")
				|| permissionString.equals("realshop.delete")
				|| permissionString.equals("realshop.give")
				|| permissionString.equals("realshop.open")
				|| permissionString.equals("realshop.close")
				|| permissionString.equals("realshop.buy")
				|| permissionString.equals("realshop.sell")
				|| permissionString.equals("realshop.xbuy")
				|| permissionString.equals("realshop.xsell")
				|| permissionString.equals("realshop.marketitemsonly")
				|| permissionString.equals("realshop.damageditems")
				|| permissionString.equals("realshop.addassistant")
				|| permissionString.equals("realshop.removeassistant")
				|| permissionString.equals("realshop.price")
				|| permissionString.equals("realshop.price.info")
				|| permissionString.equals("realshop.price.display")
				|| permissionString.equals("realshop.price.set")
				|| permissionString.equals("realshop.price.del");
		}
	}

	//---------------------------------------------------------------------------------------- reload
	public void reload(Player player)
	{
		super.reload();
		player.sendMessage(
			RealColor.message + tr("Reload RealShop configuration files")
		);
		getMarketPrices().load();
		getShopList().load();
		player.sendMessage(
			RealColor.message + "accounts, config, dataValues, economy, lang, market, shops"
		);
	}

}
