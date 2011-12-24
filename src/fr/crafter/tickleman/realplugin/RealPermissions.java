package fr.crafter.tickleman.realplugin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

//########################################################################################### Perms
public class RealPermissions
{

	private RealPlugin plugin;
	private String permissionsPluginName;
	private PermissionHandler permissionsHandler;

	//----------------------------------------------------------------------------------------- Perms
	public RealPermissions(RealPlugin plugin, String permissionsPluginName)
	{
		this.plugin = plugin;
		this.permissionsPluginName = permissionsPluginName.toLowerCase();
	}

	//---------------------------------------------------------------------- getPermissionsPluginName
	public String getPermissionsPluginName()
	{
		return permissionsPluginName;
	}

	//--------------------------------------------------------------------------------- hasPermission
	public boolean hasPermission(Player player, String permissionString)
	{
		boolean result;
		if (permissionsPluginName.equals("none")) {
			if (permissionString.contains(".")) {
				permissionString = permissionString.replace(
					plugin.getDescription().getName().toLowerCase() + ".", ""
				);
			}
			result = player.isOp()
				? plugin.opHasPermission(permissionString)
				: plugin.playerHasPermission(permissionString);
		} else if (permissionsPluginName.equals("bukkit")) {
			result = player.hasPermission(permissionString);
		} else if (permissionsPluginName.equals("permissions")) {
			result = permissionsHandler.has(player, permissionString);
		} else {
			result = false;
		}
		// permission universal .* manager
		if (!result && !permissionString.contains(".*")) {
			result = hasPermission(player, permissionString + ".*");
			while (!result && permissionString.contains(".")) {
				permissionString = permissionString.substring(0, permissionString.lastIndexOf("."));
				result = hasPermission(player, permissionString + ".*");
			}
		}
		return result;
	}

	//------------------------------------------------------------------------ initPermissionsHandler
	public void initPermissionsHandler()
	{
		if (permissionsPluginName.equals("permissions")) {
			Plugin permissions = plugin.getServer().getPluginManager().getPlugin("Permissions");
			if (permissions != null) {
				permissionsHandler = ((Permissions)permissions).getHandler();
			}
		}
	}

}
