package fr.crafter.tickleman.realeconomy;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.RegisteredServiceProvider;

import com.nijikokun.register.payment.Method;
import com.nijikokun.register.payment.Methods;

import fr.crafter.tickleman.realplugin.RealPlugin;

//##################################################################################### RealEconomy
public class RealEconomy
{

	private RealAccounts      accounts;
	private RealEconomyConfig config;
	private RealPlugin        plugin;
	private String            economyPlugin;
	private Method            paymentMethod = null;
  private Economy           vaultEconomy = null;

	//----------------------------------------------------------------------------------- RealEconomy
	public RealEconomy(RealPlugin plugin)
	{
		this.plugin = plugin;
		this.economyPlugin = "RealEconomy";
		accounts = new RealAccounts(plugin);
		config = new RealEconomyConfig(plugin);
		config.load();
	}

	//---------------------------------------------------------------------------------------- format
	public String format(Double amount)
	{
		String result;
		result = amount.toString() + " " + getCurrency();
		return result.replace(".00 ", "").replace(".0 ", "");
	}

	//------------------------------------------------------------------------------------ getBalance
	public double getBalance(String playerName)
	{
		Double balance = 0.0;
		if (economyPlugin.equals("Vault")) {
			balance = vaultEconomy.getBalance(playerName);
		} else if (economyPlugin.equals("Register")) {
			balance = paymentMethod.getAccount(playerName).balance();
		} else {
			balance = accounts.getBalance(playerName);
			if (balance == null) {
				balance = config.initialBalance;
			}
		}
		plugin.getLog().debug(economyPlugin + " getBalance(" + playerName + ") = " + balance);
		return Math.round(balance * 100.0) / 100.0;
	}

	//------------------------------------------------------------------------------------ getBalance
	public String getBalance(String playerName, boolean withCurrency)
	{
		Double balance = getBalance(playerName);
		if (withCurrency) {
			return format(balance);
		} else {
			return balance.toString();
		}
	}

	//------------------------------------------------------------------------------------ hasAccount
	public boolean hasAccount(String playerName)
	{
		if (economyPlugin.equals("Vault")) {
			return vaultEconomy.getBalance(playerName) > 0;
		} else if (economyPlugin.equals("Register")) {
			return paymentMethod.hasAccount(playerName);
		} else {
			return (accounts.getBalance(playerName) != null);
		}
	}

	//----------------------------------------------------------------------------------- getCurrency
	public String getCurrency()
	{
		return config.currency;
	}

	//------------------------------------------------------------------------------ getEconomyPlugin
	public String getEconomyPlugin()
	{
		return economyPlugin;
	}

	//---------------------------------------------------------------------------------- initRegister
	public void initRegister()
	{
		if (plugin.getServer().getPluginManager().getPlugin("Register") != null) {
			if (!Methods.hasMethod()) {
				if (Methods.setMethod(plugin.getServer().getPluginManager())) {
					Method method = Methods.getMethod();
					if (method != null) {
						setPaymentMethod(method);
						plugin.getLog().info(
							"Payment method " + method.getName() + " version " + method.getVersion()
							+ " enabled (Register)"
						);
					}
				}
			}
		}
	}

	//------------------------------------------------------------------------------------- initVault
	public void initVault()
	{
		if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Economy> economyProvider
				= plugin.getServer().getServicesManager().getRegistration(
					net.milkbowl.vault.economy.Economy.class
				);
			if (economyProvider != null) {
				vaultEconomy = economyProvider.getProvider();
				if (vaultEconomy != null) {
					economyPlugin = "Vault";
					plugin.getLog().info(
						"Economy provider " + vaultEconomy.getName() + " enabled (Vault)"
					);
				}
			}
		}
	}

	//------------------------------------------------------------------------------------ setBalance
	public void setBalance(String playerName, double balance)
	{
		if (economyPlugin.equals("Vault")) {
			if (balance > vaultEconomy.getBalance(playerName)) {
				vaultEconomy.depositPlayer(playerName, balance - vaultEconomy.getBalance(playerName));
			} else {
				vaultEconomy.withdrawPlayer(playerName, vaultEconomy.getBalance(playerName) - balance);
			}
		} else if (economyPlugin.equals("Register")) {
			paymentMethod.getAccount(playerName).set(balance);
		} else {
			accounts.setBalance(playerName, balance);
		}
		plugin.getLog().debug("setBalance(" + playerName + ", " + balance + ")");
	}

	//------------------------------------------------------------------------------ setPaymentMethod
	public void setPaymentMethod(Method paymentMethod)
	{
		economyPlugin = "Register";
		this.paymentMethod = paymentMethod;
	}

	//-------------------------------------------------------------------------------------- transfer
	public void transfer(String playerNameFrom, String playerNameTo, double balance)
	{
		setBalance(playerNameFrom, getBalance(playerNameFrom) - balance);
		setBalance(playerNameTo,   getBalance(playerNameTo)   + balance);
	}

}
