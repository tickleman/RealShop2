package fr.crafter.tickleman.realshop2.price;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.crafter.tickleman.realplugin.RealColor;
import fr.crafter.tickleman.realplugin.RealItemType;
import fr.crafter.tickleman.realshop2.RealShop2Plugin;

//############################################################################# PlayerPricesActions
public class PlayerPriceAction extends PriceAction
{

	private static Map<Player, RealItemType> chatChangePrice = new HashMap<Player, RealItemType>();

	//--------------------------------------------------------------------------- PlayerPricesActions
	public PlayerPriceAction(RealShop2Plugin plugin, Player player)
	{
		super(plugin, new ItemPriceList(plugin, player.getName().toLowerCase()).load());
	}

	//--------------------------------------------------------------------------- chatChangePriceChat
	public boolean chatChangePriceChat(Player player, String text)
	{
		RealItemType itemType = chatChangePrice.get(player);
		if (itemType != null) {
			chatChangePrice.remove(player);
			String[] priceStrings = text.split(" ");
			Double[] prices = new Double[2];
			boolean doIt = true;
			if ((priceStrings.length < 1) || (priceStrings.length > 2)) {
				doIt = false;
			} else {
				try {
					prices[0] = Double.parseDouble(priceStrings[0]);
					prices[1] = (priceStrings.length > 1) ? Double.parseDouble(priceStrings[1]) : prices[0];
				} catch (NumberFormatException e) {
					doIt = false;
				}
			}
			if (doIt) {
				set(player, itemType, prices[0], prices[1]);
			} else {
				player.sendMessage(RealColor.cancel + plugin.tr("Price change cancelled"));
				player.sendMessage(
					RealColor.cancel
					+ plugin.tr("Buy price and sell price separated with space, or buy price alone")
				);
			}
			return true;
		} else {
			return false;
		}
	}

	//-------------------------------------------------------------------------- chatChangePriceClick
	public void chatChangePriceClick(Player player)
	{
		ItemStack itemStack = player.getItemInHand();
		if ((itemStack != null) && !itemStack.getType().equals(Material.AIR)) {
			RealItemType itemType = new RealItemType(itemStack);
			new PlayerPriceAction(plugin, player).display(player, itemType);
			player.sendMessage(
				RealColor.message
				+ plugin.tr("Please enter your new price for +item in chat")
				.replace("+item", RealColor.item +plugin.trItemName(itemType) + RealColor.message)
			);
			player.sendMessage(
				RealColor.message
				+ plugin.tr("Buy price and sell price separated with space, or buy price alone")
			);
			player.sendMessage(
				RealColor.message
				+ "(" + plugin.tr("buy price is the price your clients will buy it") + ")"
			);
			chatChangePrice.put(player, itemType);
		}
	}

	//---------------------------------------------------------------------------------- getPriceType
	@Override
	public String getPriceType()
	{
		return "Your";
	}

	//------------------------------------------------------------------------------- isChangingPrice
	public static boolean isChangingPrice(Player player)
	{
		return chatChangePrice.containsKey(player);
	}

}
