package fr.crafter.tickleman.realplugin;

import java.util.HashMap;
import java.util.Set;

//#################################################################################### ItemTypeList
public class ItemTypeList
{

	private HashMap<String, ItemType> content = new HashMap<String, ItemType>();

	//----------------------------------------------------------------------------------------- clear
	public void clear()
	{
		content.clear();
	}

	//-------------------------------------------------------------------------------- addRemoveChain
	public void addRemoveChain(String chain)
	{
		for (String subChain : chain.split("\\+")) {
			boolean isPlus = true;
			for (String elem : subChain.split("\\-")) {
				if (elem.length() > 0) {
					ItemType itemType = ItemType.parseItemType(elem);
					if (isPlus) {
						put(itemType);
					} else {
						remove(itemType);
					}
				}
				isPlus = false;
			}
		}
	}

	//------------------------------------------------------------------------------------------- get
	public ItemType get(ItemType itemType)
	{
		return content.get(itemType.toString());
	}

	//------------------------------------------------------------------------------------ getContent
	public HashMap<String, ItemType> getContent()
	{
		return content;
	}

	//--------------------------------------------------------------------------------------- isEmpty
	public boolean isEmpty()
	{
		return content.isEmpty();
	}

	//----------------------------------------------------------------------------- parseItemTypeList
	public static ItemTypeList parseItemTypeList(String list)
	{
		ItemTypeList itemTypeList = new ItemTypeList();
		for (String typeIdVariant : list.split(";")) {
			if (typeIdVariant.length() > 0) {
				itemTypeList.put(ItemType.parseItemType(typeIdVariant));
			}
		}
		return itemTypeList;
	}

	//----------------------------------------------------------------------------- parseItemTypeList
	public static ItemTypeList parseItemTypeList(Set<String> list)
	{
		ItemTypeList itemTypeList = new ItemTypeList();
		for (String typeIdVariant : list) {
			itemTypeList.put(ItemType.parseItemType(typeIdVariant));
		}
		return itemTypeList;
	}

	//------------------------------------------------------------------------------------------- put
	public void put(ItemType itemType)
	{
		content.put(itemType.toString(), itemType);
	}

	//---------------------------------------------------------------------------------------- remove
	public void remove(ItemType itemType)
	{
		content.remove(itemType.toString());
	}

	//-------------------------------------------------------------------------------------- toString
	@Override
	public String toString()
	{
		String result = "";
		for (String typeIdVariant : content.keySet()) {
			if (result.isEmpty()) {
				result += typeIdVariant;
			} else {
				result += "," + typeIdVariant;
			}
		}
		return result;
	}

	//-------------------------------------------------------------------------------------- toString
	public String toNamesString(DataValues dataValues)
	{
		String result = "";
		for (String typeIdVariant : content.keySet()) {
			if (!result.isEmpty()) {
				result += ", ";
			}
			result += dataValues.getName(content.get(typeIdVariant));
		}
		return result;
	}

}
