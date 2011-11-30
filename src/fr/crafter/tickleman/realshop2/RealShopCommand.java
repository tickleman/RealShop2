package fr.crafter.tickleman.realshop2;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.crafter.tickleman.realplugin.RealItemType;
import fr.crafter.tickleman.realplugin.RealChest;
import fr.crafter.tickleman.realplugin.RealColor;
import fr.crafter.tickleman.realplugin.RealVarTools;
import fr.crafter.tickleman.realshop2.price.MarketPriceAction;
import fr.crafter.tickleman.realshop2.price.PlayerPriceAction;
import fr.crafter.tickleman.realshop2.price.PriceAction;
import fr.crafter.tickleman.realshop2.shop.Shop;
import fr.crafter.tickleman.realshop2.shop.ShopAction;

//################################################################################# RealShopCommand
public class RealShopCommand
{
	
	RealShop2Plugin plugin;

	//------------------------------------------------------------------------------- RealShopCommand
	public RealShopCommand(RealShop2Plugin plugin)
	{
		this.plugin = plugin;
	}

	//-------------------------------------------------------------------------------- executeCommand
	public boolean executeCommand(CommandSender sender, String[] args)
	{
		if (sender instanceof Player) {
			Player player = (Player)sender;
			String[] params = {
				(args.length > 0) ? solveAlias1(args[0]) : "",
				(args.length > 1) ? solveAlias2(args[1]) : "",
				(args.length > 2) ? args[2] : "",
				(args.length > 3) ? args[3] : "",
				(args.length > 4) ? args[4] : "",
			};
			if (plugin.hasPermission(player, "realshop." + params[0])) {
				Boolean  ok = generalCommand(player, params);
				if (!ok) ok = marketPriceCommand(player, params);
				if (!ok) ok = playerPriceCommand(player, params);
				if (!ok) shopCommand(player, params);
			}
		}
		return true;
	}

	//-------------------------------------------------------------------------------- generalCommand
	private boolean generalCommand(Player player, String[] params)
	{
		if (params[0].equals("help")) {
			new RealShopHelp(plugin).showHelp(player, params[1]);
			return true;
		} else if (params[0].equals("reload")) {
			plugin.reload(player);
			return true;
		}
		return false;
	}

	//---------------------------------------------------------------------------- marketPriceCommand
	private boolean marketPriceCommand(Player player, String[] params)
	{
		return priceCommand(player, params, "market", "market", new MarketPriceAction(plugin));
	}

	//---------------------------------------------------------------------------- playerPriceCommand
	private boolean playerPriceCommand(Player player, String[] params)
	{
		return priceCommand(player, params, "price", "your own", new PlayerPriceAction(plugin, player));
	}

	//---------------------------------------------------------------------------------- priceCommand
	private boolean priceCommand(
		Player player, String[] params, String permissionString,
		String priceType, PriceAction priceAction
	) {
		if (params[0].equals(permissionString)) {
			if (params[1].equals("")) params[1] = "info";
			if (
				params[1].equals("info") || params[1].equals("del")
				|| plugin.hasPermission(player, "realshop." + permissionString + "." + params[1])
			) {
				if (params[1].equals("info")) {
					priceAction.info(player, RealVarTools.parseInt(params[2], 1));
				} else if (params[1].equals("del")) {
					priceAction.del(player, RealItemType.parseItemType(params[2]));
				} else if (!params[2].isEmpty()) {
					priceAction.set(
						player, RealItemType.parseItemType(params[1]),
						RealVarTools.parseDouble(params[2], 0.0), RealVarTools.parseDouble(params[3], null)
					);
				} else {
					priceAction.display(player, RealItemType.parseItemType(params[1]));
				}
			} else {
				player.sendMessage(
					RealColor.cancel + plugin.tr("You are not allowed to access " + priceType + " prices")
				);
			}
			return true;
		} else {
			return false;
		}
	}

	//----------------------------------------------------------------------------------- shopCommand
	private void shopCommand(Player player, String[] params)
	{
		ShopAction shopAction = new ShopAction(plugin);
		RealChest chest       = plugin.getPlayerChestList().selectedChest(player);
		Shop shop             = plugin.getPlayerShopList().selectedShop(player);
		if (params[0].equals("create")) {
			if (shop != null) {
				player.sendMessage(RealColor.cancel + plugin.tr("This chest is already a shop"));
			} else if (chest == null) {
				player.sendMessage(RealColor.cancel + plugin.tr("You must first select a chest"));
			} else {
				shop = shopAction.createShop(chest.getLocation(), player, params[1]);
			}
		} else if (shop == null) {
			player.sendMessage(
				RealColor.cancel + plugin.tr("The chest you selected is not a shop")
			);
		} else if (
			!player.getName().equals(shop.getPlayerName()) && !plugin.hasPermission(player, "realshop.op")
		) {
			player.sendMessage(
				RealColor.cancel
				+ plugin.tr("The chest-shop you selected belongs to +owner")
				.replace("+name", RealColor.shop + shop.getName() + RealColor.cancel)
				.replace("+owner", RealColor.player + shop.getPlayerName() + RealColor.cancel)
			);
		} else if (params[0].equals("info")) {
			shopAction.shopInfo(player, shop);
		} else if (params[0].equals("delete")) {
			shopAction.deleteShop(player, shop);
		} else if (params[0].equals("open")) {
			shopAction.open(player, shop);
		} else if (params[0].equals("close")) {
			shopAction.close(player, shop);
		} else if (params[0].equals("give")) {
			shopAction.giveShop(player, shop, params[1]);
		} else if (params[0].equals("buy")) {
			shopAction.buyOnly(player, shop, params[1]);
		} else if (params[0].equals("sell")) {
			shopAction.sellOnly(player, shop, params[1]);
		} else if (params[0].equals("xbuy")) {
			shopAction.buyExclude(player, shop, params[1]);
		} else if (params[0].equals("xsell")) {
			shopAction.sellExclude(player, shop, params[1]);
		} else if (params[0].equals("infiniteBuy")) {
			shopAction.setInfiniteBuy(player, shop, RealVarTools.parseBoolean(params[1]));
		} else if (params[0].equals("infiniteSell")) {
			shopAction.setInfiniteSell(player, shop, RealVarTools.parseBoolean(params[1]));
		} else if (params[0].equals("marketItemsOnly")) {
			shopAction.setMarketItemsOnly(player, shop, RealVarTools.parseBoolean(params[1]));
		} else if (params[0].equals("damagedItems")) {
			shopAction.setDamagedItems(player, shop, RealVarTools.parseBoolean(params[1]));
		}
	}

	//----------------------------------------------------------------------------------- solveAlias1
	private String solveAlias1(String param)
	{
		if (param.equals(""))    return "info";
		if (param.equals("?"))   return "help";
		if (param.equals("b"))   return "buy";
		if (param.equals("c"))   return "create";
		if (param.equals("chk")) return "check";
		if (param.equals("cl"))  return "close";
		if (param.equals("day")) return "daily";
		if (param.equals("del")) return "delete";
		if (param.equals("di"))  return "damagedItems";
		if (param.equals("g"))   return "give";
		if (param.equals("h"))   return "help";
		if (param.equals("i"))   return "info";
		if (param.equals("ib"))  return "infiniteBuy";
		if (param.equals("is"))  return "infiniteSell";
		if (param.equals("m"))   return "market";
		if (param.equals("mi"))  return "marketItemsOnly";
		if (param.equals("op"))  return "open";
		if (param.equals("p"))   return "price";
		if (param.equals("r"))   return "reload";
		if (param.equals("s"))   return "sell";
		if (param.equals("sim")) return "simul";
		if (param.equals("xb"))  return "xbuy";
		if (param.equals("xs"))  return "xsell";
		return param;
	}

	//----------------------------------------------------------------------------------- solveAlias2
	private String solveAlias2(String param)
	{
		if (param.equals("a")) return "add";
		if (param.equals("d")) return "del";
		if (param.equals("i")) return "info";
		return param;
	}

}
