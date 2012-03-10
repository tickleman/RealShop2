package fr.crafter.tickleman.realshop2;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import com.nijikokun.register.payment.Methods;

//########################################################################## RealShopServerListener
public class RealShopServerListener implements Listener
{

	private RealShop2Plugin	plugin;

	// ----------------------------------------------------------------------- RealShopServerListener
	public RealShopServerListener(RealShop2Plugin plugin)
	{
		this.plugin = plugin;
	}

	// ------------------------------------------------------------------------------ OnPluginDisable
	@EventHandler
	public void onPluginDisable(PluginDisableEvent event)
	{
		if (plugin.getPermissions().getPermissionsPluginName().equals(event.getPlugin().getName())) {
			plugin.getPermissions().disablePermissionsHandler();
		}
		if (plugin.getEconomy().getEconomyPlugin().equalsIgnoreCase("Register")) {
			// remove payment method
			try {
				if (Methods.hasMethod()) {
					Boolean check = Methods.checkDisabled(event.getPlugin());
					if (check) {
						plugin.getEconomy().disableRegister();
						plugin.getLog().info(
							"Payment method was disabled. No longer accepting payments"
						);
					}
				}
			} catch (Exception e) {
				plugin.getLog().info("Could not link to Register");
			}
		}
		if (event.getPlugin().getName().equalsIgnoreCase("Vault")) {
			plugin.getEconomy().disableVault();
		}
	}

	//-------------------------------------------------------------------------------- onPluginEnable
	@EventHandler
	public void onPluginEnable(PluginEnableEvent event)
	{
		plugin.getEconomy().initRegister();
		plugin.getEconomy().initVault();
		plugin.getPermissions().initPermissionsHandler();
	}

}
