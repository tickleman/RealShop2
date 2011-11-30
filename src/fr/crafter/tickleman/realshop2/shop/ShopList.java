package fr.crafter.tickleman.realshop2.shop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;

import fr.crafter.tickleman.realplugin.RealPlugin;
import fr.crafter.tickleman.realplugin.RealChest;
import fr.crafter.tickleman.realplugin.RealLocation;

//######################################################################################## ShopList
public class ShopList
{

	private final String fileName;

	private final RealPlugin plugin;

	/** Shops list : "x;y;z;world" => Shop */
	private HashMap<String, Shop> shops = new HashMap<String, Shop>();

	//-------------------------------------------------------------------------------------- ShopList
	public ShopList(final RealPlugin plugin)
	{
		this(plugin, "shops");
	}

	//-------------------------------------------------------------------------------------- ShopList
	public ShopList(final RealPlugin plugin, String fileName)
	{
		this.plugin = plugin;
		this.fileName = plugin.getDataFolder().getPath() + "/" + fileName + ".txt";
	}

	//----------------------------------------------------------------------------------------- clear
	public void clear()
	{
		shops.clear();
	}

	//---------------------------------------------------------------------------------------- delete
	public void delete(Shop shop)
	{
		shops.remove(shop.getId());
	}

	//---------------------------------------------------------------------------------------- isShop
	public boolean isShop(Block block)
	{
		return isShop(block.getLocation());
	}

	//---------------------------------------------------------------------------------------- isShop
	public boolean isShop(Location location)
	{
		return (shopAt(location) != null);
	}

	//------------------------------------------------------------------------------------------ load
	public ShopList load()
	{
		plugin.getLog().debug("ShopList.load()");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String buffer;
			while ((buffer = reader.readLine()) != null) {
				if ((buffer.length() > 0) && (buffer.charAt(0) != '#') && (buffer.split(";").length > 4)) {
					Shop shop = Shop.parseShop(plugin.getServer(), buffer);
					if (shop != null) {
						plugin.getLog().debug("shop load " + shop.toString());
						put(shop);
					}
				}
			}
		} catch (Exception e) {
			plugin.getLog().warning("File read error " + fileName + " (will create one)");
			save();
		}
		try { reader.close(); } catch (Exception e) {}
		return this;
	}

	//------------------------------------------------------------------------------------------- put
	public void put(Shop shop)
	{
		shops.put(shop.getId(), shop);
	}

	//------------------------------------------------------------------------------------------ save
	public void save()
	{
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(
				"#world;x;y;z;x2;y2;z2;owner;name;buyOnly;sellOnly;buyExclude;sellExclude;opened;"
				+ "infiniteBuy;infiniteSell;marketItemsOnly;damagedItems\n"
			);
			for (Shop shop : shops.values()) {
				writer.write(shop.toString() + "\n");
			}
			writer.flush();
		} catch (Exception e) {
			plugin.getLog().severe("File save error " + fileName);
		}
		try { writer.close(); } catch (Exception e) {}
	}

	//---------------------------------------------------------------------------------------- shopAt
	public Shop shopAt(Block block)
	{
		return shopAt(block.getLocation());
	}

	//---------------------------------------------------------------------------------------- shopAt
	public Shop shopAt(Location location)
	{
		location = new RealChest(location).getLocation();
		Shop shop = shops.get(RealLocation.getId(location));
		return shop;
	}

}
