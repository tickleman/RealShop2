package fr.crafter.tickleman.realeconomy;

import org.bukkit.entity.Player;

import fr.crafter.tickleman.realplugin.RealColor;

//############################################################################### RealEconomyAction
public class RealEconomyAction
{

	private RealEconomy economy;

	//----------------------------------------------------------------------------- RealEconomyAction
	public RealEconomyAction(RealEconomy economy)
	{
		this.economy = economy;
	}

	//------------------------------------------------------------------------------------------ burn
	public void burn(Player player, double amount)
	{
		Double playerAmount = economy.getBalance(player.getName());
		economy.setBalance(player.getName(), Math.max(0.0, playerAmount - amount));
		player.sendMessage(
			RealColor.message + "You burn "
			+ RealColor.price + economy.format(Math.min(playerAmount, amount))
		);
	}

	//------------------------------------------------------------------------------------------- dec
	public void dec(Player player, String playerName, double amount)
	{
		Double playerAmount = economy.getBalance(playerName);
		economy.setBalance(playerName, Math.max(0.0, playerAmount - amount));
		player.sendMessage(
			RealColor.message + "You decrease "
			+ RealColor.player + playerName
			+ RealColor.message + "'s balance of "
			+ RealColor.price + economy.format(Math.min(playerAmount, amount))
		);
		Player toPlayer = player.getServer().getPlayer(playerName);
		if (toPlayer != null) {
			toPlayer.sendMessage(
				RealColor.player + playerName
				+ RealColor.message + " decreased your balance of "
				+ RealColor.price + economy.format(Math.min(playerAmount, amount))
			);
		}
	}

	//--------------------------------------------------------------------------------------- display
	public void display(Player player)
	{
		player.sendMessage(
			RealColor.message + "You've got "
			+ RealColor.price + economy.getBalance(player.getName(), true)
			+ RealColor.message + " in your pocket"
		);
	}

	//------------------------------------------------------------------------------------------ give
	public void give(Player player, String playerName, double amount)
	{
		if (economy.hasAccount(playerName)) {
			Double playerAmount = economy.getBalance(player.getName());
			amount = Math.min(playerAmount, amount);
			economy.setBalance(player.getName(), playerAmount - amount);
			economy.setBalance(playerName, economy.getBalance(playerName) + amount);
			player.sendMessage(
				RealColor.message + "You give " + RealColor.price + economy.format(amount)
				+ RealColor.message + " to " + RealColor.player + playerName
			);
			Player toPlayer = player.getServer().getPlayer(playerName);
			if (toPlayer != null) {
				toPlayer.sendMessage(
					RealColor.player + playerName
					+ RealColor.message + " gives you "
					+ RealColor.price + economy.format(amount)
				);
			}
		} else {
			player.sendMessage(RealColor.cancel + playerName + " has no bank account");
		}
	}

	//------------------------------------------------------------------------------------------ help
	public void help(Player player)
	{
		player.sendMessage(RealColor.doc + "RealEconomy help : [isoptional], <replaceitbyvalue>");
		player.sendMessage(RealColor.command + "/mny [display]" + RealColor.doc + " : tell me how many money I have in my pocket");
		player.sendMessage(RealColor.command + "/mny [give] <player> <amount>" + RealColor.doc + " : give money to another player");
		player.sendMessage(RealColor.command + "/mny burn <amount>" + RealColor.doc + " : burn your money");
		if (player.isOp()) {
			player.sendMessage("RealEconomy operator help");
			player.sendMessage(RealColor.command + "/mny [tell] <player>" + RealColor.doc + " : tell me how many money the player has");
			player.sendMessage(RealColor.command + "/mny dec <player> <amount>" + RealColor.doc + " : decrease the balance of a player");
			player.sendMessage(RealColor.command + "/mny inc <player> <amount>" + RealColor.doc + " : increase balance of a player");
			player.sendMessage(RealColor.command + "/mny set <player> <balance>" + RealColor.doc + " : sets the balance of a player");
		}
	}

	//------------------------------------------------------------------------------------------- inc
	public void inc(Player player, String playerName, double amount)
	{
		economy.setBalance(playerName, economy.getBalance(playerName) + amount);
		player.sendMessage(
			RealColor.message + "You increase "
			+ RealColor.player + playerName
			+ RealColor.message + "'s balance of " 
			+ RealColor.price + economy.format(amount)
		);
		Player toPlayer = player.getServer().getPlayer(playerName);
		if (toPlayer != null) {
			toPlayer.sendMessage(
				RealColor.player + playerName
				+ RealColor.message + " increased your balance of "
				+ RealColor.price + economy.format(amount)
			);
		}
	}

	//------------------------------------------------------------------------------------------- set
	public void set(Player player, String playerName, double amount)
	{
		economy.setBalance(playerName, amount);
		player.sendMessage(
			RealColor.player + playerName
			+ RealColor.message + " balance set to "
			+ RealColor.price + economy.format(amount)
		);
		Player toPlayer = player.getServer().getPlayer(playerName);
		if (toPlayer != null) {
			toPlayer.sendMessage(
				RealColor.player + playerName
				+ RealColor.message + " sets your balance to "
				+ RealColor.price + economy.format(amount)
			);
		}
	}

	//------------------------------------------------------------------------------------------ tell
	public void tell(Player player, String playerName)
	{
		player.sendMessage(
			RealColor.player + playerName + RealColor.message + " has got "
			+ RealColor.price + economy.getBalance(playerName, true)
			+ RealColor.message + " in his pocket"
		);
	}

}
