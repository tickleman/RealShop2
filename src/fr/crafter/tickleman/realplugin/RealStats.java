package fr.crafter.tickleman.realplugin;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

//####################################################################################### RealStats
public class RealStats
{

	//------------------------------------------------------------------------------------------ call
	public static void call(RealPlugin plugin, String action)
	{
		try {
			URLConnection connection = new URL(
				"http://plugins.crafter.fr/stats/call"
				+ "?plugin=" + plugin.getDescription().getName()
				+ "&action=" + action
			).openConnection();
			connection.setReadTimeout(500);
			connection.setUseCaches(false);
			InputStream input = connection.getInputStream();
			byte[] buffer = new byte[10240];
			while (input.read(buffer) > 0) {
				System.out.print("read " + buffer.toString());
			}
			input.close();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

}
