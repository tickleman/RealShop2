package fr.crafter.tickleman.realshop2;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import com.nijikokun.register.payment.Methods;

//########################################################################## RealShopServerListener
public class RealShopServerListener extends ServerListener
{

	private RealShop2Plugin	plugin;

	// ----------------------------------------------------------------------- RealShopServerListener
	public RealShopServerListener(RealShop2Plugin plugin)
	{
		this.plugin = plugin;
	}

	// ------------------------------------------------------------------------------ OnPluginDisable
	@Override
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
	@Override
	public void onPluginEnable(PluginEnableEvent event)
	{
		plugin.getEconomy().initRegister();
		plugin.getEconomy().initVault();
		plugin.getPermissions().initPermissionsHandler();
	}

}
