package fr.crafter.tickleman.realplugin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

//###################################################################################### ValuesFile
public class DataValues
{

	private final String            fileName;
	private HashMap<String, String> names = new HashMap<String, String>();
	private final RealPlugin            plugin;
	private HashMap<String, String> recipes = new HashMap<String, String>();

	//------------------------------------------------------------------------------------ DataValues
	public DataValues(final RealPlugin plugin)
	{
		this(plugin, "dataValues");
	}

	//------------------------------------------------------------------------------------ DataValues
	public DataValues(final RealPlugin plugin, final String fileName)
	{
		this.plugin = plugin;
		this.fileName = plugin.getDataFolder().getPath() + "/" + fileName + ".txt";
	}

	//----------------------------------------------------------------------------------------- clear
	public void clear()
	{
		names.clear();
		recipes.clear();
	}

	//---------------------------------------------------------------------------------------- getIds
	/**
	 * Get full id list into a "typeId[:variant]" array
	 */
	/*
	public String[] getIds()
	{
		int i = 0;
		String[] ids = new String[names.size()];
		for (String id : names.keySet()) {
			ids[i++] = id;
		}
		return ids;
	}
	*/
	
	//------------------------------------------------------------------------------- getItemTypeList
	public ItemTypeList getItemTypeList()
	{
		return ItemTypeList.parseItemTypeList(names.keySet());
	}

	//--------------------------------------------------------------------------------------- getName
	/**
	 * Get name for given item type id
	 * Returns "#typeIdVariant" if no name known
	 */
	public String getName(ItemType itemType)
	{
		String result = names.get(itemType.toString());
		if (result == null) {
			result = "#" + itemType.toString();
		}
		return result;
	}

	//------------------------------------------------------------------------------------- getRecipe
	/**
	 * Get main recipe for given item type
	 * Returns empty string if no recipe known
	 */
	public String getRecipe(ItemType itemType)
	{
		String result = recipes.get(itemType.toString());
		return (result == null) ? "" : result;
	}

	//------------------------------------------------------------------------------------------ load
	/**
	 * Load data values file from hard drive
	 */
	public DataValues load()
	{
		if (!FileTools.fileExists(fileName)) {
			FileTools.extractDefaultFile(plugin, fileName);
		}
		try {
			names.clear();
			recipes.clear();
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String buffer;
			while ((buffer = reader.readLine()) != null) {
				String[] line = buffer.split(";");
				if ((buffer.length() > 0) && (line.length >= 2) && (buffer.charAt(0) != '#')) {
					try {
						String typeIdDamage = line[0].trim();
						String typeName = line[1].trim();
						String recipe = (line.length >= 3) ? line[2].trim() : "";  
						names.put(typeIdDamage, typeName);
						if (!recipe.equals("")) {
							recipes.put(typeIdDamage, recipe);
						}
					} catch (Exception e) {
						// when some typeId are not number, then ignore
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			plugin.getLog().severe("File read error " + fileName);
		}
		return this;
	}

}
