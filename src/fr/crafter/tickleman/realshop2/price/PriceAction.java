package fr.crafter.tickleman.realshop2.price;

import org.bukkit.entity.Player;

import fr.crafter.tickleman.realplugin.ItemType;
import fr.crafter.tickleman.realplugin.RealColor;
import fr.crafter.tickleman.realshop2.RealShop2Plugin;

//################################################################################### PricesActions
public class PriceAction
{

	private RealShop2Plugin plugin;

	private ItemPriceList itemPriceList;

	//--------------------------------------------------------------------------------- PricesActions
	public PriceAction(RealShop2Plugin plugin, ItemPriceList itemPriceList)
	{
		this.plugin = plugin;
		this.itemPriceList = itemPriceList;
	}

	//------------------------------------------------------------------------------------------- del
	public void del(Player player, ItemType itemType)
	{
		getItemPriceList().remove(itemType);
		getItemPriceList().save();
		player.sendMessage(
			RealColor.message
			+ plugin.tr(getPriceType() + " price deleted for +item")
			.replace("+item", RealColor.item + plugin.getDataValues().getName(itemType) + RealColor.message)
		);
	}

	//--------------------------------------------------------------------------------------- display
	public void display(Player player, ItemType itemType)
	{
		Price price = getItemPriceList().getPrice(itemType);
		if (price == null) {
			player.sendMessage(
				RealColor.cancel
				+ plugin.tr("No " + getPriceType().toLowerCase() + " price for +item")
				.replace("+item", RealColor.item + plugin.getDataValues().getName(itemType) + RealColor.cancel)
			);
			price = getItemPriceList().getPrice(itemType, plugin.getMarketPrices());
			if (price == null) {
				player.sendMessage(
					RealColor.cancel
					+ plugin.tr("Price can't be calculated from recipes for +item")
					.replace("+item", RealColor.item + plugin.getDataValues().getName(itemType) + RealColor.cancel)
				);
			} else {
				player.sendMessage(
					RealColor.message
					+ plugin.tr("Calculated price (from market/recipes) for +item : buy +buy, sell +sell")
					.replace("+item", RealColor.item + plugin.getDataValues().getName(itemType) + RealColor.message)
					.replace("+buy", RealColor.price + price.getBuyPrice() + RealColor.message)
					.replace("+sell", RealColor.price + price.getSellPrice() + RealColor.message)
				);
			}
		} else {
			player.sendMessage(
				RealColor.message
				+ plugin.tr(getPriceType() + " price for +item : buy +buy, sell +sell")
				.replace("+item", RealColor.item + plugin.getDataValues().getName(itemType) + RealColor.message)
				.replace("+buy", RealColor.price + price.getBuyPrice() + RealColor.message)
				.replace("+sell", RealColor.price + price.getSellPrice() + RealColor.message)
			);
		}
	}

	//------------------------------------------------------------------------------ getItemPriceList
	private ItemPriceList getItemPriceList()
	{
		return itemPriceList;
	}

	//---------------------------------------------------------------------------------- getPriceType
	public String getPriceType()
	{
		return "";
	}

	//------------------------------------------------------------------------------------------ info
	public void info(Player player, int page)
	{
		player.sendMessage(RealColor.message + plugin.tr(getPriceType() + " prices list") + " :");
		// generate info pages (one page is max 64 char x 10, 50% of chat max lines count being 20)
		String pageContent = "";
		int pageCount = 1;
		for (ItemType itemType : plugin.getDataValues().getItemTypeList().getContent().values()) {
			Price price = getItemPriceList().getPrice(itemType);
			if (price != null) {
				String priceInfo = itemType.toString() + " : " + price.toString();
				if (pageContent.length() + 2 + priceInfo.length() > 640) {
					if (pageCount == page) {
						player.sendMessage(RealColor.item + pageContent);
						break;
					}
					pageContent = "";
				}
				pageContent += (pageContent.isEmpty() ? "" : ", ") + priceInfo;
			}
		}
	}

	//------------------------------------------------------------------------------------------- set
	public void set(Player player, ItemType itemType, Double buyPrice, Double sellPrice)
	{
		try {
			Price price = new Price(buyPrice, (sellPrice == null) ? buyPrice : sellPrice);
			getItemPriceList().put(itemType, price);
			getItemPriceList().save();
			player.sendMessage(
				RealColor.message
				+ plugin.tr(getPriceType() + " price for +item : buy +buy, sell +sell")
				.replace("+item", RealColor.item + plugin.getDataValues().getName(itemType) + RealColor.message)
				.replace("+buy", RealColor.price + price.getBuyPrice() + RealColor.message)
				.replace("+sell", RealColor.price + price.getSellPrice() + RealColor.message)
			);
		} catch (Exception e) {
			player.sendMessage(
				RealColor.cancel
				+ plugin.tr("Error while setting " + getPriceType().toLowerCase() + " price for +item")
				.replace("+item", RealColor.item + plugin.getDataValues().getName(itemType) + RealColor.cancel)
			);
			player.sendMessage(
				RealColor.message
				+ plugin.tr("Usage: +command")
				.replace("+command", RealColor.command + plugin.tr("/rshop " + getPriceType().toLowerCase() + " <itemId>[:<variant>] <sellPrice> <buyPrice>") + RealColor.message)
			);
		}
	}

}
