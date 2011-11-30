package fr.crafter.tickleman.realshop2;

import java.io.BufferedReader;
import java.io.FileReader;

import org.bukkit.entity.Player;

import fr.crafter.tickleman.realplugin.RealFileTools;
import fr.crafter.tickleman.realplugin.RealColor;

//#################################################################################### RealShopHelp
public class RealShopHelp
{

	RealShop2Plugin plugin;

	//---------------------------------------------------------------------------------- RealShopHelp
	public RealShopHelp(RealShop2Plugin plugin)
	{
		this.plugin = plugin;
	}

	//-------------------------------------------------------------------------------------- showHelp
	public void showHelp(Player player, String page)
	{
		// choose help file
		String fileName = plugin.getDataFolder() + "/" + plugin.getRealConfig().language + ".help.txt";
		if (!RealFileTools.fileExists(fileName)) {
			RealFileTools.extractDefaultFile(plugin, fileName);
			if (!RealFileTools.fileExists(fileName)) {
				fileName = plugin.getDataFolder() + "/en.help.txt";
				if (!RealFileTools.fileExists(fileName)) {
					RealFileTools.extractDefaultFile(plugin, fileName);
					if (!RealFileTools.fileExists(fileName)) {
						plugin.getLog().severe("No help file " + fileName);
						player.sendMessage(RealColor.cancel + plugin.tr("/rshop HELP is not available"));
						return;
					}
				}
			}
		}
		// display help file
		try {
			if (page.equals("")) {
				player.sendMessage(RealColor.text + plugin.tr("/rshop HELP summary"));
			}
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String buffer;
			boolean inside = false;
			while ((buffer = reader.readLine()) != null) {
				buffer = buffer.trim();
				if (!buffer.equals("") && (buffer.charAt(0) != '#')) {
					String[] hlp = buffer.split(":");
					hlp[0] = hlp[0].trim();
					if (hlp.length > 1) {
						hlp[1] = hlp[1].trim();
					}
					if ((buffer.charAt(0) == '[') && (buffer.charAt(buffer.length() - 1) == ']')) {
						// section header [help1|h|1 : text]
						hlp[0] = hlp[0].substring(1).trim();
						hlp[1] = hlp[1].substring(0, hlp[1].length() - 1).trim();
						if (page.equals("")) {
							// summary : display
							player.sendMessage(
								RealColor.command + "/rshop help " + hlp[0] + " "
								+ RealColor.message + hlp[1]
							);
						} else {
							// help page : check if in this section header and display title
							inside = (("|" + hlp[0] + "|").indexOf("|" + page + "|") > -1);
							if (inside) {
								player.sendMessage(
									RealColor.text
									+ "/rshop HELP " + hlp[1]
									+ " (" + hlp[0] + ")"
								);
							}
						}
					} else if (inside && buffer.charAt(0) == '/') {
						// display help page command
						player.sendMessage(RealColor.command + hlp[0] + " " + RealColor.message + hlp[1]);
					} else if (inside) {
						// display help page line
						player.sendMessage(RealColor.message + buffer);
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			plugin.getLog().severe(e.getMessage());
			plugin.getLog().severe(e.getStackTrace().toString());
		}
	}

}
