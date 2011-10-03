package fr.crafter.tickleman.realplugin;

import org.bukkit.entity.Player;

import com.nijiko.permissions.PermissionHandler;

//########################################################################################### Perms
public class RealPermissions
{

	private RealPlugin plugin;
	private Object permissionsPlugin;
	private String permissionsPluginName;

	//----------------------------------------------------------------------------------------- Perms
	public RealPermissions(RealPlugin plugin, String permissionsPluginName)
	{
		this.plugin = plugin;
		this.permissionsPluginName = permissionsPluginName.toLowerCase();
		if (this.permissionsPluginName.equals("permissions")) {
			permissionsPlugin = plugin.getServer().getPluginManager().getPlugin("Permissions");
		} else {
			permissionsPlugin = null;
		}
	}

	//---------------------------------------------------------------------- getPermissionsPluginName
	public String getPermissionsPluginName()
	{
		return permissionsPluginName;
	}

	//--------------------------------------------------------------------------------- hasPermission
	public boolean hasPermission(Player player, String permissionString)
	{
		if (this.permissionsPluginName.equals("none")) {
			if (permissionString.contains(".")) {
				permissionString = permissionString.replace(
					plugin.getDescription().getName().toLowerCase() + ".", ""
				);
			}
			return player.isOp()
				? plugin.opHasPermission(permissionString)
				: plugin.playerHasPermission(permissionString);
		} else if (this.permissionsPluginName.equals("bukkit")) {
			boolean perm = player.hasPermission(permissionString);
			System.out.println("bukkit permission " + permissionString + " = " + perm);
			return perm;
		} else if (this.permissionsPluginName.equals("permissions")) {
			return ((PermissionHandler)permissionsPlugin).has(player, permissionString);
		} else {
			return false;
		}
	}

}
