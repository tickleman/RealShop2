package fr.crafter.tickleman.realplugin;

import net.minecraft.server.Block;
import net.minecraft.server.Item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

//#################################################################################### RealItemType
public class ItemType
{

	/**
	 * Minecraft type identifier of item
	 */
	private int typeId;

	/**
	 * Variant code of item, for items than can have variants
	 * Equals ItemStack.getDurability() for items that can be damaged
	 * Is null for non-applicable items
	 */
	private short variant;

	//------------------------------------------------------------------------------------- ItemStack
	public ItemType(ItemStack itemStack)
	{
		this(itemStack.getTypeId(), itemStack.getDurability());
	}

	//-------------------------------------------------------------------------------------- ItemType
	public ItemType(net.minecraft.server.ItemStack itemStack)
	{
		this(itemStack.id, (short)itemStack.getData());
	}

	//------------------------------------------------------------------------------------------ Item
	public ItemType(net.minecraft.server.Item item)
	{
		this(item.id, (short)0);
	}

	//------------------------------------------------------------------------------------------ Item
	public ItemType(net.minecraft.server.Item item, short variant)
	{
		this(item.id, variant);
	}

	//-------------------------------------------------------------------------------------- ItemType
	public ItemType(Material material)
	{
		this(material.getId(), (short)0);
	}

	//-------------------------------------------------------------------------------------- ItemType
	public ItemType(Material material, short variant)
	{
		this(material.getId(), variant);
	}

	//-------------------------------------------------------------------------------------- ItemType
	public ItemType(int typeId)
	{
		this(typeId, (short)0);
	}

	//-------------------------------------------------------------------------------------- ItemType
	public ItemType(int typeId, short variant)
	{
		setTypeIdVariant(typeId, variant);
	}

	//--------------------------------------------------------------------------------------- getName
	public String getName()
	{
		String name;
		if (typeId < 256) {
			// block name
			Block block = Block.byId[typeId];
			name = (block == null) ? ("#" + typeId) : block.l();
		} else {
			// item name
			Item item = Item.byId[typeId];
			name = (item == null) ? ("#" + typeId) : item.b();
		}
		if ((name == null) || (name.length() == 0)) {
			// #id for unknown blocks/items
			name = "#" + typeId;
		} else if (name.contains(".")) {
			// remove "*." from "*.name"
			name = name.substring(name.indexOf(".") + 1);
		}
		// change "dyePowder" into "dye powder"
		for (int i = 0; i < name.length(); i ++) {
			if ((name.charAt(i) >= 'A') && (name.charAt(i) <= 'Z')) {
				name = name.substring(0, i) + " " + (char)(name.charAt(i) - 'A' + 'a') + name.substring(i + 1);
			}
		}
		return name;
	}

	//------------------------------------------------------------------------------------- getTypeId
	public int getTypeId()
	{
		return typeId;
	}

	//------------------------------------------------------------------------------------ getVariant
	public short getVariant()
	{
		return variant;
	}

	//--------------------------------------------------------------------------------- parseItemType
	public static ItemType parseItemType(String typeIdVariant)
	{
		if (typeIdVariant.contains(":")) {
			String[] split = typeIdVariant.split(":");
			return new ItemType(Integer.parseInt(split[0]), Short.parseShort(split[1]));
		} else {
			return new ItemType(Integer.parseInt(typeIdVariant));
		}
	}

	//------------------------------------------------------------------------------------- setTypeId
	public void setTypeId(int typeId)
	{
		setTypeIdVariant(typeId, variant);
	}

	//------------------------------------------------------------------------------------ isSameItem
	public boolean isSameItem(ItemType itemType)
	{
		return (itemType.getTypeId() == getTypeId()) && (itemType.getVariant() == getVariant());
	}

	//------------------------------------------------------------------------------ setTypeIdVariant
	public void setTypeIdVariant(int typeId, short variant)
	{
		this.typeId = typeId;
		setVariant(variant);
	}

	//------------------------------------------------------------------------------------ setVariant
	public void setVariant(short variant)
	{
		if (typeIdHasVariant(typeId)) {
			this.variant = ((variant < 0) ? 0 : variant);
		} else {
			this.variant = 0;
		}
	}

	//--------------------------------------------------------------------------------- toNamedString
	public String toNamedString()
	{
		return getName() + ((getVariant() != 0) ? " : " + getVariant() : "");
	}

	//-------------------------------------------------------------------------------------- toString
	@Override
	public String toString()
	{
		return getTypeId() + ((getVariant() != 0) ? " : " + getVariant() : "");
	}

	//------------------------------------------------------------------------------- typeIdHasDamage
	public static Boolean typeIdHasDamage(int typeId)
	{
		return !typeIdHasVariant(typeId);
	}

	//------------------------------------------------------------------------------ typeIdHasVariant
	public static Boolean typeIdHasVariant(int typeId)
	{
		return
			// those codes have variant : durability is an item variant instead of damage
			(typeId == Material.LOG.getId())
			|| (typeId == Material.LEAVES.getId())
			|| (typeId == Material.MONSTER_EGGS.getId())
			|| (typeId == Material.WOOL.getId())
			|| (typeId == Material.DOUBLE_STEP.getId())
			|| (typeId == Material.STEP.getId())
			|| (typeId == Material.COAL.getId())
			|| (typeId == Item.INK_SACK.id)
		;
	}

	//------------------------------------------------------------------------------- typeIdMaxDamage
	public static short typeIdMaxDamage(int typeId)
	{
		if (typeIdHasVariant(typeId)) {
			return 0;
		} else if (typeId < 256) {
			return (short)Block.byId[typeId].c();
		} else {
			return (short)Item.byId[typeId].getMaxDurability();
		}
	}

}
