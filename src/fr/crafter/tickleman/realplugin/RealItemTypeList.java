package fr.crafter.tickleman.realplugin;

import java.util.HashMap;
import java.util.Set;

//#################################################################################### ItemTypeList
public class RealItemTypeList
{

	private HashMap<String, RealItemType> content = new HashMap<String, RealItemType>();

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
					RealItemType itemType = RealItemType.parseItemType(elem);
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
	public RealItemType get(RealItemType itemType)
	{
		return content.get(itemType.toString());
	}

	//------------------------------------------------------------------------------------ getContent
	public HashMap<String, RealItemType> getContent()
	{
		return content;
	}

	//--------------------------------------------------------------------------------------- isEmpty
	public boolean isEmpty()
	{
		return content.isEmpty();
	}

	//----------------------------------------------------------------------------- parseItemTypeList
	public static RealItemTypeList parseItemTypeList(String list)
	{
		RealItemTypeList itemTypeList = new RealItemTypeList();
		for (String typeIdVariant : list.split(",")) {
			if (typeIdVariant.length() > 0) {
				itemTypeList.put(RealItemType.parseItemType(typeIdVariant));
			}
		}
		return itemTypeList;
	}

	//----------------------------------------------------------------------------- parseItemTypeList
	public static RealItemTypeList parseItemTypeList(Set<String> list)
	{
		RealItemTypeList itemTypeList = new RealItemTypeList();
		for (String typeIdVariant : list) {
			itemTypeList.put(RealItemType.parseItemType(typeIdVariant));
		}
		return itemTypeList;
	}

	//------------------------------------------------------------------------------------------- put
	public void put(RealItemType itemType)
	{
		content.put(itemType.toString(), itemType);
	}

	//---------------------------------------------------------------------------------------- remove
	public void remove(RealItemType itemType)
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
	public String toNamesString()
	{
		String result = "";
		for (String typeIdVariant : content.keySet()) {
			if (!result.isEmpty()) {
				result += ", ";
			}
			result += content.get(typeIdVariant).getName();
		}
		return result;
	}

}
