package fr.crafter.tickleman.realeconomy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.crafter.tickleman.realplugin.RealPlugin;
import fr.crafter.tickleman.realplugin.VarTools;

//############################################################################## RealEconomyCommand
public class RealEconomyCommand
{

	private RealPlugin  plugin;
	private RealEconomy economy;

	//---------------------------------------------------------------------------- RealEconomyCommand
	public RealEconomyCommand(RealPlugin plugin, RealEconomy economy)
	{
		this.plugin = plugin;
		this.economy = economy;
	}

	//-------------------------------------------------------------------------------- executeCommand
	public boolean executeCommand(CommandSender sender, String[] args)
	{
		if (sender instanceof Player) {
			Player player = (Player)sender;
			if (plugin.hasPermission(player)) {
				String[] params = {
					(args.length > 0) ? solveAlias1(args[0]) : "",
					(args.length > 1) ? args[1] : "",
					(args.length > 2) ? args[2] : ""
				};
				RealEconomyAction action = new RealEconomyAction(economy);
				if (plugin.hasPermission(player, params[0])) {
					if (params[0].equals("burn")) {
						action.burn(player, VarTools.parseDouble(params[1], 0.0));
					} else if (params[0].equals("dec")) {
						action.dec(player, params[1], VarTools.parseDouble(params[2], 0.0));
					} else if (params[0].equals("display")) {
						action.display(player);
					} else if (params[0].equals("give")) {
						action.give(player, params[1], VarTools.parseDouble(params[2], 0.0));
					} else if (params[0].equals("help")) {
						action.help(player);
					} else if (params[0].equals("inc")) {
						action.inc(player, params[1], VarTools.parseDouble(params[2], 0.0));
					} else if (params[0].equals("set")) {
						action.set(player, params[1], VarTools.parseDouble(params[2], 0.0));
					} else if (params[0].equals("tell")) {
						action.tell(player, params[1]);
					}
				} else if (
					!params[0].isEmpty() && !params[1].isEmpty() && plugin.hasPermission(player, "give")
				) {
					action.give(player, params[0], VarTools.parseDouble(params[1], 0.0));
				} else if (!params[0].isEmpty() && plugin.hasPermission(player, "tell")) {
					action.tell(player, params[1]);
				} else if (params[0].isEmpty() && plugin.hasPermission(player, "display")) {
					action.display(player);
				}
			}
		}
		return true;
	}

	//----------------------------------------------------------------------------------- solveAlias1
	private String solveAlias1(String param)
	{
		if (param.equals("b")) return "burn";
		if (param.equals("d")) return "dec";
		if (param.equals("g")) return "give";
		if (param.equals("h")) return "help";
		if (param.equals("i")) return "inc";
		if (param.equals("s")) return "set";
		return param;
	}

}
