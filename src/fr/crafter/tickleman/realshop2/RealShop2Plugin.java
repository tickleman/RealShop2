package fr.crafter.tickleman.realshop2;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;

import fr.crafter.tickleman.realeconomy.RealEconomy;
import fr.crafter.tickleman.realeconomy.RealEconomyCommand;
import fr.crafter.tickleman.realplugin.RealPlugin;
import fr.crafter.tickleman.realplugin.RealColor;
import fr.crafter.tickleman.realshop2.price.ItemPriceList;
import fr.crafter.tickleman.realshop2.shop.PlayerChestList;
import fr.crafter.tickleman.realshop2.shop.PlayerShopList;
import fr.crafter.tickleman.realshop2.shop.ShopList;

//########################################################################### class RealShop2Plugin
public class RealShop2Plugin extends RealPlugin
{

	private ItemPriceList   marketPrices;
	private RealEconomy     economy;
	private PlayerChestList playerChestList;
	private PlayerShopList  playerShopList;
	private ShopList        shopList;

	//------------------------------------------------------------------------------- RealShop2Plugin
	public RealShop2Plugin()
	{
		super();
		playerChestList = new PlayerChestList(this);
		playerShopList  = new PlayerShopList(this);
	}

	//------------------------------------------------------------------------------------- getConfig
	@Override
	public RealShopConfig getConfig()
	{
		return (RealShopConfig)super.getConfig();
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
		if (command.equals("rs") || command.equals("rshop")) {
			return new RealShopCommand(this).executeCommand(sender, args);
		} else if (
			command.equals("mny")
			&& (getConfig().economyPlugin.equals("none") || getConfig().economyPlugin.equals("RealEconomy"))
		) {
			return new RealEconomyCommand(this, getEconomy()).executeCommand(sender, args);
		} else {
			return false;
		}
	}

	//-------------------------------------------------------------------------------------- onEnable
	@Override
	public void onDisable()
	{
		marketPrices.clear();
		shopList.clear();
		super.onDisable();
	}

	//-------------------------------------------------------------------------------------- onEnable
	@Override
	public void onEnable()
	{
		super.onEnable();
		// register events
		RealShopBlockListener     blockListener     = new RealShopBlockListener(this);
		RealShopInventoryListener inventoryListener = new RealShopInventoryListener(this);
		RealShopPlayerListener    playerListener    = new RealShopPlayerListener(this);
		RealShopServerListener    serverListener    = new RealShopServerListener(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_BREAK,     blockListener,     Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGE,    blockListener,     Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT,    inventoryListener, Event.Priority.Normal,  this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener,    Priority.Normal,        this);
		pm.registerEvent(Event.Type.PLAYER_LOGIN,    playerListener,    Priority.Normal,        this);
		pm.registerEvent(Event.Type.PLAYER_QUIT,     playerListener,    Priority.Normal,        this);
		pm.registerEvent(Event.Type.PLUGIN_DISABLE,  serverListener,    Priority.Normal,        this);
		pm.registerEvent(Event.Type.PLUGIN_ENABLE,   serverListener,    Priority.Normal,        this);
		// load files
		marketPrices = new ItemPriceList(this, "market").load();
		shopList     = new ShopList(this).load();
		this.economy = new RealEconomy(this);
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
		if (permissionString.contains("mny.")) {
			return permissionString.equals("mny")
				|| permissionString.equals("mny.burn")
				|| permissionString.equals("mny.display")
				|| permissionString.equals("mny.give")
				|| permissionString.equals("mny.help");
		}
		// realshop "rs" / "rshop" commands
		if (getConfig().shopOpOnly) {
			return permissionString.equals("")
				|| permissionString.equals("shop")
				|| permissionString.equals("info");
		} else {
			return permissionString.equals("")
				|| permissionString.equals("shop")
				|| permissionString.equals("help")
				|| permissionString.equals("info")
				|| permissionString.equals("create")
				|| permissionString.equals("delete")
				|| permissionString.equals("give")
				|| permissionString.equals("open")
				|| permissionString.equals("close")
				|| permissionString.equals("buy")
				|| permissionString.equals("sell")
				|| permissionString.equals("xbuy")
				|| permissionString.equals("xsell")
				|| permissionString.equals("marketitemsonly")
				|| permissionString.equals("damageditems")
				|| permissionString.equals("price")
				|| permissionString.equals("price.info")
				|| permissionString.equals("price.display")
				|| permissionString.equals("price.set")
				|| permissionString.equals("price.del");
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
