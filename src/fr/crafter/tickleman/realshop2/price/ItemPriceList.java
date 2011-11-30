package fr.crafter.tickleman.realshop2.price;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fr.crafter.tickleman.realplugin.RealFileTools;
import fr.crafter.tickleman.realplugin.RealItemType;
import fr.crafter.tickleman.realplugin.RealItemStack;
import fr.crafter.tickleman.realplugin.RealRecipe;
import fr.crafter.tickleman.realshop2.RealShop2Plugin;

//################################################################################### ItemPriceList
public class ItemPriceList
{

	/** stored file name */
	private final String fileName;

	/** master plugin */
	private final RealShop2Plugin plugin;

	/** prices list : ItemType "typeId[:variant]" => Price(buy, sell) */
	private HashMap<String, Price> prices = new HashMap<String, Price>();

	/** anti-recurse security flag for recipes */
	private int recurseSecurity = 0;

	/** anti-recurse item type string list */
	private HashSet<String> recurseItemTypes = new HashSet<String>();

	//--------------------------------------------------------------------------------- ItemPriceList
	public ItemPriceList(final RealShop2Plugin plugin, final String fileName)
	{
		this.plugin = plugin;
		this.fileName = plugin.getDataFolder().getPath() + "/" + fileName + ".txt";
	}

	//----------------------------------------------------------------------------------------- clear
	public void clear()
	{
		prices.clear();
	}

	/*
	//------------------------------------------------------------------------ dailyPricesCalculation
	public void dailyPricesCalculation(DailyLog dailyLog)
	{
		dailyPricesCalculation(dailyLog, false);
	}
	*/

	//------------------------------------------------------------------------ dailyPricesCalculation
	/**
	 * Daily price calculation
	 * Takes care of :
	 * - the last day transactions log
	 * - the last items price
	 */
/*
	public void dailyPricesCalculation(DailyLog dailyLog, boolean simulation)
	{
		plugin.getLog().info("dailyPricesCalculation (" + (simulation ? "SIMULATION" : "FOR REAL") + ")");
		// take each item id that has had a movement today, and that has a price
		for (String typeIdDamage : dailyLog.moves.keySet()) {
			// recalculate price
			Price price = prices.get(typeIdDamage);
			if (price != null) {
				int amount = dailyLog.moves.get(typeIdDamage);
				double ratio;
				if (amount < 0) {
					ratio = Math.max(
						plugin.getConfig().minDailyRatio,
						(double)1 + ((double)amount / plugin.getConfig().amountRatio)
					);
				} else {
					ratio = Math.min(
						plugin.getConfig().maxDailyRatio,
						(double)1 + ((double)amount / plugin.getConfig().amountRatio)
					);
				}
				String log = "- "
					+ typeIdDamage + "(" + plugin.getDataValues().getName(typeIdDamage) + ") :"
					+ " amount " + amount + " ratio " + ratio
					+ " OLD " + price.getBuyPrice() + ", " + price.getSellPrice();
				price.setBuyPrice(Math.ceil(
					(double)100 * Math.min(plugin.getConfig().maxItemPrice, Math.max(
						plugin.getConfig().minItemPrice, price.getBuyPrice() * ratio
					))
				) / (double)100);
				price.setSellPrice(Math.floor(
					(double)100 * Math.min(plugin.getConfig().maxItemPrice, Math.max(
						plugin.getConfig().minItemPrice, price.getBuyPrice() * plugin.getConfig().buySellRatio
					))
				) / (double)100);
				log += " NEW " + price.getBuyPrice() + ", " + price.getSellPrice();
				plugin.getLog().info(log);
			} else {
				plugin.getLog().info(
					"- no market price for item " + plugin.getDataValues().getName(typeIdDamage)
				);
			}
		}
		if (!simulation) {
			plugin.getLog().info("- SAVE new prices into " + fileName);
			save();
		}
	}
	*/

	//------------------------------------------------------------------------------------ fromRecipe
	/**
	 * Calculate Price using crafting recipes
	 * - returns null if no price for any component
	 * - recurse if necessary
	 */
	private Price fromRecipe(RealItemType itemType, ItemPriceList marketFile)
	{
		if (!recurseItemTypes.contains(itemType.toString())) {
			Set<RealRecipe> recipes = RealRecipe.getItemRecipes(itemType);
			if (recipes.size() > 0) {
				recurseItemTypes.add(itemType.toString());
				Price cheapest = null;
				for (RealRecipe recipe : recipes) {
					Price price = new Price();
					// recurse security
					recurseSecurity++;
					if (recurseSecurity > 20) {
						plugin.getLog().severe("Recurse security error : " + itemType.toString());
						return null;
					} else if (recurseSecurity > 15) {
						plugin.getLog().warning("Recurse security warning : " + itemType.toString());
					}
					// resQty : result quantity
					double resQty = recipe.getResultItem().getAmount();
					// sum of components
					for (RealItemStack itemStack : recipe.getRecipeItems()) {
						Price compPrice = getPrice(itemStack.getItemType(), (short)0, marketFile);
						if (compPrice == null) {
							price = null;
							break;
						} else {
							price.setBuyPrice(price.getBuyPrice() + Math.ceil(
								(double)100 * compPrice.getBuyPrice() * itemStack.getAmount()
							) / (double)100);
							price.setSellPrice(price.getSellPrice() + Math.floor(
								(double)100 * compPrice.getSellPrice() * itemStack.getAmount()
							) / (double)100);
						}
					}
					if (price != null) {
						// round final price
						price.setBuyPrice(Math.ceil(
							price.getBuyPrice() / resQty * (double)100 * plugin.getRealConfig().workForceRatio
						) / (double)100);
						price.setSellPrice(Math.floor(
							price.getSellPrice() / resQty * (double)100 * plugin.getRealConfig().workForceRatio
						) / (double)100);
						// get the cheapest price
						if ((cheapest == null) || (price.getBuyPrice() < cheapest.getBuyPrice())) {
							cheapest = price;
						}
					}
					recurseSecurity--;
				}
				recurseItemTypes.remove(itemType.toString());
				return cheapest;
			}
		}
		return null;
	}

	//-------------------------------------------------------------------------------------- getPrice
	public Price getPrice(RealItemType itemType)
	{
		return getPrice(itemType, (short)0, null, true);
	}

	//-------------------------------------------------------------------------------------- getPrice
	public Price getPrice(RealItemType itemType, ItemPriceList marketPrices)
	{
		return getPrice(itemType, (short)0, marketPrices, true);
	}

	//-------------------------------------------------------------------------------------- getPrice
	public Price getPrice(RealItemType itemType, short damage, ItemPriceList marketPrices)
	{
		return getPrice(itemType, damage, marketPrices, true);
	}

	//-------------------------------------------------------------------------------------- getPrice
	public Price getPrice(
		RealItemType itemType, short damage, ItemPriceList marketPrices, boolean recipe
	) {
		Price price = prices.get(itemType.toString());
		if (price == null) {
			if (marketPrices != null) {
				// market file price
				price = marketPrices.getPrice(itemType, damage, null, false);
			}
			if ((price == null) && recipe) {
				// recipe price
				price = fromRecipe(itemType, marketPrices);
			}
		}
		if ((price != null) && (damage > 0)) {
			// damaged price calculation
			try {
				price.setDamagedBuyPrice(Math.max(
					0,
					price.getBuyPrice()
					- (price.getBuyPrice() * damage / RealItemStack.typeIdMaxDamage(itemType.getTypeId()))
				));
			} catch(Exception e) {
			}
			try {
				price.setDamagedSellPrice(Math.max(
					0,
					price.getSellPrice()
					- (price.getSellPrice() * damage / RealItemStack.typeIdMaxDamage(itemType.getTypeId()))
				));
			} catch(Exception e) {
			}
		}
		return price; 
	}

	//------------------------------------------------------------------------------------------ load
	public ItemPriceList load()
	{
		boolean willSave = false;
		if (fileName.contains("/market.txt") && !RealFileTools.fileExists(fileName)) {
			plugin.getLog().debug("extract default file for " + fileName);
			RealFileTools.extractDefaultFile(plugin, fileName);
			willSave = true;
		}
		try {
			prices.clear();
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String buffer;
			while ((buffer = reader.readLine()) != null) {
				String[] line = buffer.split(";");
				if ((buffer.length() > 0) && (line.length >= 3) && (buffer.charAt(0) != '#')) {
					try {
						String typeIdVariant = line[0].trim();
						Price price = new Price(
							Double.parseDouble(line[1].trim()),
							Double.parseDouble(line[2].trim())
						);
						prices.put(typeIdVariant, price);
					} catch (Exception e) {
						// when some values are not number, then ignore
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			if (fileName.contains("/market.txt")) {
				plugin.getLog().severe("Missing file " + fileName);
			}
		}
		if (willSave) {
			save();
		}
		return this;
	}

	//--------------------------------------------------------------------------- playerHasPricesFile
	public static boolean playerHasPricesFile(RealShop2Plugin plugin, String playerName)
	{
		return RealFileTools.fileExists(
			plugin.getDataFolder().getPath() + "/" + playerName + ".prices.txt"
		);
	}

	//------------------------------------------------------------------------------------------- put
	public void put(RealItemType itemType, Price price)
	{
		prices.put(itemType.toString(), price);
	}

	//------------------------------------------------------------------------------------------- put
	public void remove(RealItemType itemType)
	{
		prices.remove(itemType.toString());
	}

	//------------------------------------------------------------------------------------------ save
	public void save()
	{
		BufferedWriter writer = null;
		// save prices file
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			writer.write("#item:dm;buy;sell;name\n");
			for (String typeIdVariant : prices.keySet()) {
				Price price = prices.get(typeIdVariant);
				RealItemType itemType = RealItemType.parseItemType(typeIdVariant);
				writer.write(
					typeIdVariant + ";"
					+ price.getBuyPrice() + ";"
					+ price.getSellPrice() + ";"
					+ itemType.getName()
					+ "\n"
				);
			}
			writer.flush();
		} catch (Exception e) {
			plugin.getLog().severe("Error writting " + fileName);
			e.printStackTrace();
		}
		try { writer.close(); } catch (Exception e) {}
		// Save all current values (including calculated prices) into currentValues.txt
		/*
		if (fileName.contains("/market.txt")) {
			try {
				writer = new BufferedWriter(
					new FileWriter(plugin.getDataFolder().getPath() + "/currentValues.txt")
				);
				writer.write("#item:dm;buy;sell;name\n");
				for (String typeIdVariant : dataValues.getItemTypeList().getContent().keySet()) {
					ItemType itemType = ItemType.parseItemType(typeIdVariant);
					Price price = getPrice(itemType);
					if (price != null) {
						writer.write(
							typeIdVariant + ";"
							+ price.getBuyPrice() + ";"
							+ price.getSellPrice() + ";"
							+ itemType.getName()
							+ "\n"
						);
					} else {
						writer.write(
							typeIdVariant + ";0;0;"
							+ itemType.getName()
							+ "\n"
						);
					}
				}
				writer.flush();
			} catch (Exception e) {
				plugin.getLog().error(
					"Error writing " + plugin.getDataFolder().getPath() + "/currentValues.txt"
				);
				e.printStackTrace();
			}
			try { writer.close(); } catch (Exception e) {}
		}
		*/
	}

	//-------------------------------------------------------------------------------------- toString
	@Override
	public String toString()
	{
		String string = "";
		for (String typeIdVariant : prices.keySet()) {
			Price price = prices.get(typeIdVariant);
			if (!string.equals("")) {
				string += ", ";
			}
			string += typeIdVariant + "=" + price.toString();
		}
		return string;
	}

}
