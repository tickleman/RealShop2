package fr.crafter.tickleman.realshop2;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
		if (plugin.getEconomy().getEconomyPlugin().equalsIgnoreCase("Register")) {
			// remove payment method
			try {
				if (Methods.hasMethod()) {
					Boolean check = Methods.checkDisabled(event.getPlugin());
					if (check) {
						plugin.getEconomy().setPaymentMethod(null);
						plugin.getLog().info(
							"Payment method was disabled. No longer accepting payments"
						);
					}
				}
			} catch (Exception e) {
				plugin.getLog().info("Could not link to Register");
			}
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
