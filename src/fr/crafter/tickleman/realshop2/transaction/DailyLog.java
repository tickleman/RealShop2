package fr.crafter.tickleman.realshop2.transaction;

import java.util.HashMap;

import fr.crafter.tickleman.realplugin.RealItemType;
import fr.crafter.tickleman.realplugin.RealLog;
import fr.crafter.tickleman.realshop2.RealShop2Plugin;

//######################################################################################## DailyLog
public class DailyLog
{

	private HashMap<String, Integer> moves = new HashMap<String, Integer>();

	@SuppressWarnings("unused")
	private final RealShop2Plugin plugin;

	//-------------------------------------------------------------------------------------- DailyLog
	public DailyLog(final RealShop2Plugin plugin)
	{
		this.plugin = plugin;
	}

	//------------------------------------------------------------------------------------------- add
	/**
	 * Add an amount of an item typeId to daily moves
	 * positive amount for buy, negative amount for sell
	 */
	public void add(final String typeIdDamage, final int amount)
	{
		Integer balance = moves.get(typeIdDamage);
		if (amount != 0) {
			if (balance == null) {
				moves.put(typeIdDamage, amount);
			} else if ((balance + amount) == 0) {
				moves.remove(typeIdDamage);
			} else {
				moves.put(typeIdDamage, balance + amount);
			}
		}
	}

	//----------------------------------------------------------------------------------------- reset
	/**
	 * Resets moves log
	 */
	public void reset()
	{
		moves.clear();
	}

	//----------------------------------------------------------------------------------------- toLog
	public void toLog(RealLog log)
	{
		log.info("RealShopDailyLog status");
		for (String typeIdVariant : moves.keySet()) {
			RealItemType itemType = RealItemType.parseItemType(typeIdVariant);
			int amount = moves.get(itemType);
			log.info(
				"- " + itemType.toString() + "(" + itemType.getName() + ") x" + amount
			);
		}
	}

}
