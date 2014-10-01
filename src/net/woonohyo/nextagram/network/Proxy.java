package net.woonohyo.nextagram.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.woonohyo.nextagram.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Proxy {
	private final String TAG = Proxy.class.getSimpleName();
	private Context context;
	private String serverUrl;
	private SharedPreferences sharedPreferences;
	
	public Proxy(Context context) {
		this.context = context;
		String prefName = context.getResources().getString(R.string.shared_preferences_name);
		sharedPreferences = context.getSharedPreferences(prefName, context.MODE_PRIVATE);
		serverUrl = sharedPreferences.getString(context.getResources().getString(R.string.server_url), "");
	}
	
	public String getJSON() {
		try {
			String prefArticleNumberKey = context.getResources().getString(R.string.pref_article_number);
			String articleNumber = sharedPreferences.getString(prefArticleNumberKey, "0");
			String serverUrl = this.serverUrl + "loadData.php/?articleNumber=" + articleNumber;
			URL url = new URL(serverUrl);
			Log.i(TAG, "Connecting to " + url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setConnectTimeout(10 * 1000);
			conn.setReadTimeout(10 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("Cache-Control", "no-cache");
			conn.setRequestProperty("Accept", "application/json");
			conn.setDoInput(true);
			conn.connect();
			
			int status = conn.getResponseCode();
			Log.i(TAG, "Response Code: " + status);
			
			switch (status) {
			case 200:
			case 201:
				// Connection Success
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				
				return sb.toString();
			}
			
		} catch (Exception e) {
			Log.e(TAG, "URL Connection Failed" + e);
		}

		return null;
	}

}
