package net.woonohyo.nextagram.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.woonohyo.nextagram.R;
import net.woonohyo.nextagram.db.ArticleDTO;
import android.content.ContentValues;
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

	public ArrayList<ArticleDTO> getArticleDTO() {
		ArrayList<ArticleDTO> articleList = new ArrayList<ArticleDTO>();

		JSONArray jArr;
		int articleNumber;
		String title;
		String writer;
		String id;
		String content;
		String writeDate;
		String imgName;

		String jsonData = getJSON();
		ArticleDTO articleDTO;

		try {
			jArr = new JSONArray(jsonData);
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject jObj = jArr.getJSONObject(i);
				articleNumber = jObj.getInt("ArticleNumber");
				title = jObj.getString("Title");
				writer = jObj.getString("Writer");
				id = jObj.getString("Id");
				content = jObj.getString("Content");
				writeDate = jObj.getString("WriteDate");
				imgName = jObj.getString("ImgName");

				articleDTO = new ArticleDTO(articleNumber, title, writer, id, content, writeDate, imgName);
				articleList.add(articleDTO);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return articleList;
	}

}
