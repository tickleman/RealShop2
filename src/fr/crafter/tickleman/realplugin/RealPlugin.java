package fr.crafter.tickleman.realplugin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

//########################################################################################## Plugin
public class RealPlugin extends JavaPlugin
{

	protected Config      config = null;
	private   Translation lang   = null;
	private   RealLog         log    = null;
	private   RealPermissions       perms  = null;

	//------------------------------------------------------------------------------------- getConfig
	/**
	 * Plugin developer must override this to get it's own configuration object
	 */
	public Config getConfig()
	{
		if (config == null) {
			loadConfig();
		}
		return config;
	}

	//---------------------------------------------------------------------------------------- getLog
	public RealLog getLog()
	{
		return log;
	}

	//----------------------------------------------------------------------------- hasFullPermission
	public boolean hasFullPermission(Player player, String permissionString)
	{
		return perms.hasPermission(player, permissionString);
	}

	//--------------------------------------------------------------------------- hasGlobalPermission
	public boolean hasGlobalPermission(Player player)
	{
		return perms.hasPermission(player, getDescription().getName().toLowerCase());
	}

	//--------------------------------------------------------------------------- hasGlobalPermission
	public boolean hasGlobalPermission(Player player, String permissionString)
	{
		return perms.hasPermission(player, permissionString);
	}

	//--------------------------------------------------------------------------------- hasPermission
	public boolean hasPermission(Player player, String permissionString)
	{
		return perms.hasPermission(
			player, getDescription().getName().toLowerCase() + "." + permissionString.toLowerCase()
		);
	}

	//--------------------------------------------------------------------------------- hasPermission
	public boolean hasPermission(Player player, String permissionPrefix, String permissionString)
	{
		return perms.hasPermission(
			player, permissionPrefix.toLowerCase() + "." + permissionString.toLowerCase()
		);
	}

	//------------------------------------------------------------------------------------ loadConfig
	/**
	 * Plugin developer must override this to load it's own configuration object
	 */
	protected void loadConfig()
	{
		config = new Config(this).load();
	}

	//------------------------------------------------------------------------------------- onDisable
	@Override
	public void onDisable()
	{
		// disabled
		getLog().info(
			"version [" + getDescription().getVersion()
			+ "] (" + getDescription().getAuthors().toString()
			+ ") un-loaded",
			true
		);
		// disable associated objects
		config = null;
		log    = null;
		perms  = null;
		lang   = null;
	}

	//-------------------------------------------------------------------------------------- onEnable
	@Override
	public void onEnable()
	{
		getDataFolder().mkdirs();
		reload();
		getLog().info(
			"version [" + getDescription().getVersion() + "] ("
			+ getDescription().getAuthors().toString() + ") loaded",
			true
		);
	}

	//------------------------------------------------------------------------------------- opHasPerm
	/**
	 * Plugin developer can override this to set default op permissions
	 * if permission system is "none"
	 */
	public boolean opHasPermission(String permissionString)
	{
		return true;
	}

	//------------------------------------------------------------------------------------- opHasPerm
	/**
	 * Plugin developer can override this to set default non-op players some permissions
	 * if permission system is "none"
	 */
	public boolean playerHasPermission(String permissionString)
	{
		return false;
	}

	//---------------------------------------------------------------------------------------- reload
	public void reload()
	{
		loadConfig();
		log   = new RealLog(this, getConfig().debug, getConfig().pluginLog);
		perms = new RealPermissions(this, getConfig().permissionsPlugin);
		lang  = new Translation(this, getConfig().language).load();
	}

	//-------------------------------------------------------------------------------------------- tr
	/**
	 * Call this to translate text using chosen language file
	 */
	public String tr(String text)
	{
		return lang.tr(text);
	}

}
