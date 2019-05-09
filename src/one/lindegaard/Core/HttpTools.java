package one.lindegaard.Core;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import org.bukkit.scheduler.BukkitRunnable;

public class HttpTools {

	public interface httpCallback {
		void onSuccess();

		void onError();
	}

	public static void isHomePageReachable(URL url, httpCallback callback) {
		new BukkitRunnable() {

			@Override
			public void run() {

				try {
					// open a connection to that source
					HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();

					// trying to retrieve data from the source. If there
					// is no connection, this line will fail
					urlConnect.setConnectTimeout(5000);
					urlConnect.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
					urlConnect.addRequestProperty("User-Agent", "Mozilla");
					urlConnect.addRequestProperty("Referer", "google.com");

					boolean redirect = false;

					// normally, 3xx is redirect
					int status = urlConnect.getResponseCode();
					if (status != HttpURLConnection.HTTP_OK) {
						if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
								|| status == HttpURLConnection.HTTP_SEE_OTHER)
							redirect = true;
					}

					if (redirect) {

						// get redirect url from "location" header field
						String newUrl = urlConnect.getHeaderField("Location");

						// open the new connnection again
						urlConnect = (HttpURLConnection) new URL(newUrl).openConnection();

						status = urlConnect.getResponseCode();

					}
					if (status == HttpURLConnection.HTTP_OK)
						callback.onSuccess();
					else
						callback.onError();

				} catch (UnknownHostException e) {
					callback.onError();
				} catch (IOException e) {
					callback.onError();
				}
			}
		};
	}

}
