package fr.crafter.tickleman.realplugin;

import org.bukkit.inventory.ItemStack;

//############################################################################# class RealItemStack
public class RealItemStack extends ItemType
{

	/**
	 * Amount of stored item (can be negative, greater than 64, no special limitation)
	 */
	private int amount;

	/**
	 * Damage code for item : 0 to 255
	 * Equals ItemStack.getDurability() for items that can be damaged
	 * Is 0 for non damaged on non-applicable items
	 */
	private short damage;

	//######################################################################################## PUBLIC

	//--------------------------------------------------------------------------------- RealItemStack
	public RealItemStack(ItemStack itemStack)
	{
		this(itemStack.getTypeId(), itemStack.getAmount(), itemStack.getDurability());
	}

	//--------------------------------------------------------------------------------- RealItemStack
	public RealItemStack(int typeId, int amount, short durability_variant)
	{
		super(typeId, durability_variant);
		setAmount(amount);
		setDamage(durability_variant);
	}

	//------------------------------------------------------------------------------------- getAmount
	public int getAmount()
	{
		return amount;
	}

	//------------------------------------------------------------------------------------- getDamage
	public short getDamage()
	{
		return damage;
	}

	//--------------------------------------------------------------------------------- getDurability
	public short getDurability()
	{
		return typeIdHasDamage(getTypeId()) ? getDamage() : getVariant();
	}

	//----------------------------------------------------------------------------------- getItemType
	public ItemType getItemType()
	{
		return new ItemType(getTypeId(),getVariant());
	}

	//------------------------------------------------------------------------------------- setAmount
	public void setAmount(int amount)
	{
		this.amount = amount;
	}

	//------------------------------------------------------------------------------------- setDamage
	public void setDamage(short damage)
	{
		if (typeIdHasDamage(getTypeId())) {
			this.damage = damage;
		}
	}

	//------------------------------------------------------------------------------------- setTypeId
	@Override
	public void setTypeId(int typeId)
	{
		super.setTypeId(typeId);
		if (!typeIdHasDamage(typeId)) {
			setDamage((short)0);
		}
	}

	//-------------------------------------------------------------------------------------- toString
	@Override
	public String toString()
	{
		return super.toString() + "x" + getAmount() + ((getDamage() > 0) ? "(" + getDamage() + ")" : "");
	}

}
