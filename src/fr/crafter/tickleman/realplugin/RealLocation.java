package fr.crafter.tickleman.realplugin;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class RealLocation extends Location
{

	//---------------------------------------------------------------------------------- RealLocation
	public RealLocation(Location location)
	{
		this(
			location.getWorld(), location.getX(), location.getY(), location.getZ(),
			location.getYaw(), location.getPitch()
		);
	}

	//---------------------------------------------------------------------------------- RealLocation
	public RealLocation(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}

	//---------------------------------------------------------------------------------- RealLocation
	public RealLocation(World world, double x, double y, double z, float yaw, float pitch)
	{
		super(world, x, y, z, yaw, pitch);
	}

	//----------------------------------------------------------------------------------------- getId
	public String getId()
	{
		return getId(this);
	}

	//----------------------------------------------------------------------------------------- getId
	public static String getId(Location location)
	{
		return 
		+ Math.round(Math.floor(location.getX())) + ";"
		+ Math.round(Math.floor(location.getY())) + ";"
		+ Math.round(Math.floor(location.getZ())) + ";"
		+ location.getWorld();
	}

	//----------------------------------------------------------------------------------------- getId
	public static String getId(RealLocation location)
	{
		return getId((Location)location);
	}

	//-------------------------------------------------------------------------------- identicalBlock
	/**
	 * @return location at relative coordinates, but only if the same block typeId lives in here
	 */
	private RealLocation identicalLocation(int dx, int dz)
	{
		RealLocation location = new RealLocation(getWorld(), getX() + dx, getY(), getZ() + dz);
		Block block = getWorld().getBlockAt(location);
		return block.getType().equals(getBlock().getType()) ? location : null;
	}

	//------------------------------------------------------------------------------------- neightbor
	/**
	 * @return neighbor location having the same block typeId (useful to get big chests)
	 */
	public RealLocation neighbor()
	{
		RealLocation location = identicalLocation(+1, 0);
		if (location == null) {
			location = identicalLocation(-1, 0);
			if (location == null) {
				location = identicalLocation(0, +1);
				if (location == null) {
					location = identicalLocation(0, -1);
				}
			}
		}
		return location;
	}

}
