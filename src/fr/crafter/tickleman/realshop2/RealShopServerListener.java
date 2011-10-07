package fr.crafter.tickleman.realshop2;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import com.LRFLEW.register.payment.Method;
import com.LRFLEW.register.payment.Methods;

//########################################################################## RealShopServerListener
public class RealShopServerListener extends ServerListener
{

	private RealShop2Plugin	plugin;
	private Methods paymentMethods;

	// ----------------------------------------------------------------------- RealShopServerListener
	public RealShopServerListener(RealShop2Plugin plugin)
	{
		this.plugin = plugin;
		this.paymentMethods = new Methods();
	}

	// ------------------------------------------------------------------------------ OnPluginDisable
	@Override
	public void onPluginDisable(PluginDisableEvent event)
	{
		// Remove payment method
		if ((paymentMethods != null) && Methods.hasMethod()) {
			Boolean check = Methods.checkDisabled(event.getPlugin());
			if (check) {
				plugin.getEconomy().setPaymentMethod(null);
				plugin.getLog().info(
					"Payment method was disabled. No longer accepting payments"
				);
			}
		}
	}

	//-------------------------------------------------------------------------------- onPluginEnable
	@Override
	public void onPluginEnable(PluginEnableEvent event)
	{
		// Add payment method
		if (!Methods.hasMethod()) {
			if (Methods.setMethod(event.getPlugin().getServer().getPluginManager())) {
				Method method = Methods.getMethod();
				plugin.getEconomy().setPaymentMethod(method);
				plugin.getLog().info(
					"Payment method " + method.getName() + " version " + method.getVersion() + " enabled"
				);
			}
		}
		// Add Permissions
		plugin.getPermissions().initPermissionsHandler();
	}

}
