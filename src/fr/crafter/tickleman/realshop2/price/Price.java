package fr.crafter.tickleman.realshop2.price;

//########################################################################################### Price
public class Price
{

	private double buy = 0;
	private double sell = 0;
	private Double damagedBuy = null;
	private Double damagedSell = null;

	//----------------------------------------------------------------------------------------- Price
	public Price()
	{
	}

	//----------------------------------------------------------------------------------------- Price
	public Price(double buy, double sell)
	{
		this.buy = buy;
		this.sell = sell;
	}

	//----------------------------------------------------------------------------------- getBuyPrice
	public double getBuyPrice()
	{
		return getBuyPrice(1);
	}

	//----------------------------------------------------------------------------------- getBuyPrice
	public double getBuyPrice(int quantity)
	{
		if (damagedBuy != null) {
			if (quantity <= 1) {
				return Math.floor((double)100 * damagedBuy * (double)quantity) / (double)100;
			} else {
				// damaged quantity only on first item of the stack
				return damagedBuy + Math.floor((double)100 * buy * (double)(quantity - 1)) / (double)100;
			}
		} else {
			return Math.floor((double)100 * buy * (double)quantity) / (double)100;
		}
	}

	//---------------------------------------------------------------------------------- getSellPrice
	public double getSellPrice()
	{
		return getSellPrice(1);
	}

	//---------------------------------------------------------------------------------- getSellPrice
	public double getSellPrice(int quantity)
	{
		if (damagedSell != null) {
			if (quantity <= 1) {
				return Math.ceil((double)100 * damagedSell * (double)quantity) / (double)100;
			} else {
				return damagedSell + Math.ceil((double)100 * sell * (double)(quantity - 1)) / (double)100;
			}
		} else {
			return Math.ceil((double)100 * sell * (double)quantity) / (double)100;
		}
	}

	//----------------------------------------------------------------------------------- setBuyPrice
	public void setBuyPrice(double price)
	{
		buy = price;
	}

	//---------------------------------------------------------------------------- setDamagedBuyPrice
	public void setDamagedBuyPrice(Double price)
	{
		damagedBuy = price;
	}

	//--------------------------------------------------------------------------- setDamagedSellPrice
	public void setDamagedSellPrice(Double price)
	{
		damagedSell = price;
	}

	//---------------------------------------------------------------------------------- setSellPrice
	public void setSellPrice(double price)
	{
		sell = price;
	}

	//-------------------------------------------------------------------------------------- toString
	@Override
	public String toString()
	{
		String string = "[buy " + getBuyPrice() + ", sell " + getSellPrice();
		if (damagedBuy != null) {
			string += ", damagedBuy " + damagedBuy;
		}
		if (damagedSell != null) {
			string += ", damagedSell " + damagedSell;
		}
		return string + "]";
	}

}
