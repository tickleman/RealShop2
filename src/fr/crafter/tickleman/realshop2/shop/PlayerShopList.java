package fr.crafter.tickleman.realshop2.shop;

import java.util.HashMap;

import org.bukkit.entity.Player;

import fr.crafter.tickleman.realshop2.RealShop2Plugin;

//################################################################################## PlayerShopList
public class PlayerShopList
{

	RealShop2Plugin plugin;

	/**
	 * Selected shop list : Player => Shop
	 */
	private HashMap<Player, Shop> selectShop = new HashMap<Player, Shop>();

	/**
	 * Inside shop players list : Player => Shop
	 */
	private HashMap<Player, Shop> insideShop = new HashMap<Player, Shop>();

	//-------------------------------------------------------------------------------- PlayerShopList
	public PlayerShopList(RealShop2Plugin plugin)
	{
		this.plugin = plugin;
	}

	//------------------------------------------------------------------------------------- enterShop
	/**
	 * Player enters a shop
	 *
	 * @param Player player
	 * @param Shop shop
	 */
	public void enterShop(Player player, Shop shop)
	{
		selectShop(player, shop);
		insideShop.put(player, shop);
	}

	//-------------------------------------------------------------------------------------- exitShop
	/**
	 * Player exits a shop
	 *
	 * @param Player player
	 * @param Shop shop
	 */
	public void exitShop(Player player)
	{
		insideShop.remove(player);
	}

	//------------------------------------------------------------------------------- hasSelectedShop
	/**
	 * Return true if player has selected a shop
	 *
	 * @param Player player
	 * @return boolean
	 */
	public boolean hasSelectedShop(Player player)
	{
		return (selectedShop(player) != null);
	}

	//------------------------------------------------------------------------------------ insideShop
	/**
	 * Return shop inside which player is
	 *
	 * @param Player player
	 * @return Shop shop
	 */
	public Shop insideShop(Player player)
	{
		return insideShop.get(player);
	}

	//-------------------------------------------------------------------------------------- isInShop
	/**
	 * Return true if player is inside a shop
	 *
	 * @param Player player
	 * @return boolean
	 */
	public boolean isInShop(Player player)
	{
		return (insideShop(player) != null);
	}

	//------------------------------------------------------------------------------------ playerShop
	/**
	 * Return last shop player selected
	 *
	 * @param Player player
	 * @return Shop shop
	 */
	public Shop selectedShop(Player player)
	{
		return selectShop.get(player);
	}

	//------------------------------------------------------------------------------------ selectShop
	/**
	 * Set player's last selected shop
	 *
	 * @param Player player
	 * @param Shop shop
	 */
	public void selectShop(Player player, Shop shop)
	{
		selectShop.put(player, shop);
	}

	//---------------------------------------------------------------------------------- unselectShop
	/**
	 * Unselect shop
	 *
	 * @param Player player
	 * @param Shop shop
	 */
	public void unselectShop(Player player)
	{
		selectShop.remove(player);
	}

}
