package fr.crafter.tickleman.realshop2;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import com.nijikokun.register.payment.Method;
import com.nijikokun.register.payment.Methods;

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
		if ((paymentMethods != null) && paymentMethods.hasMethod()) {
			Boolean check = paymentMethods.checkDisabled(event.getPlugin());
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
		if (!paymentMethods.hasMethod()) {
			if (paymentMethods.setMethod(event.getPlugin())) {
				Method method = paymentMethods.getMethod();
				plugin.getEconomy().setPaymentMethod(method);
				plugin.getLog().info(
					"Payment method " + method.getName() + " version " + method.getVersion() + " enabled"
				);
			}
		}
	}

}
