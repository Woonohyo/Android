package net.woonohyo.nextagram.db;

import java.util.ArrayList;

import net.woonohyo.nextagram.R;
import net.woonohyo.nextagram.provider.NextagramContract;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ProviderDao {
	private static final String TAG = ProviderDao.class.getSimpleName();
	private Context context;
	private SQLiteDatabase database;
	private SharedPreferences sharedPreferences;

	public ProviderDao(Context context) {
		this.context = context;
	}

	public void insertJsonData(String jsonData) {
		int articleNumber;
		String title;
		String writer;
		String id;
		String content;
		String writeDate;
		String imgName;

		FileDownloader fileDownloader = new FileDownloader(context);

		try {
			JSONArray jArr = new JSONArray(jsonData);

			for (int i = 0; i < jArr.length(); i++) {
				ContentValues values = new ContentValues();

				JSONObject jObj = jArr.getJSONObject(i);
				articleNumber = jObj.getInt("ArticleNumber");
				title = jObj.getString("Title");
				writer = jObj.getString("Writer");
				id = jObj.getString("Id");
				content = jObj.getString("Content");
				writeDate = jObj.getString("WriteDate");
				imgName = jObj.getString("ImgName");

				if (i == jArr.length() - 1) {
					String prefName = context.getResources().getString(R.string.shared_preferences_name);
					sharedPreferences = context.getSharedPreferences(prefName, context.MODE_PRIVATE);
					String prefArticleNumberKey = context.getResources().getString(R.string.pref_article_number);
					SharedPreferences.Editor editor = sharedPreferences.edit();

					editor.putString(prefArticleNumberKey, (articleNumber - 1) + "");
					editor.commit();
				}

				values.put("_id", articleNumber);
				values.put("Title", title);
				values.put("Writer", writer);
				values.put("Id", id);
				values.put("Content", content);
				values.put("WriteDate", writeDate);
				values.put("ImgName", imgName);

				Log.i(TAG, "ArticleNumber: " + articleNumber + " Title: " + title);

				context.getContentResolver().insert(NextagramContract.Articles.CONTENT_URI, values);

				fileDownloader.downloadFile("http://10.73.44.93/~stu20/image/" + imgName, imgName);
			}
		} catch (Exception e) {
			Log.e(TAG, "JSON ERROR: " + e);
			e.printStackTrace();
		}
	}

	public ArrayList<ArticleDTO> getArticleList() {

		ArrayList<ArticleDTO> articleList = new ArrayList<ArticleDTO>();

		int articleNumber;
		String title;
		String writer;
		String id;
		String contents;
		String writeDate;
		String imgName;

		// String sql = "SELECT * FROM Articles;";
		// Cursor cursor = database.rawQuery(sql, null);
		Cursor cursor = context.getContentResolver().query(NextagramContract.Articles.CONTENT_URI, NextagramContract.Articles.PROJECTION_ALL, null, null,
				NextagramContract.Articles._ID + " ASC");

		if (cursor != null) {
			cursor.moveToFirst();
			while (!(cursor.isAfterLast())) {
				articleNumber = cursor.getInt(0);
				title = cursor.getString(1);
				writer = cursor.getString(2);
				id = cursor.getString(3);
				contents = cursor.getString(4);
				writeDate = cursor.getString(5);
				imgName = cursor.getString(6);
				articleList.add(new ArticleDTO(articleNumber, title, writer, id, contents, writeDate, imgName));

				cursor.moveToNext();
			}
		}

		cursor.close();

		return articleList;
	}

	public ArticleDTO getArticleByArticleNumber(int articleNumber) {

		ArticleDTO article = null;

		String title;
		String writer;
		String id;
		String content;
		String writeDate;
		String imgName;

		String sql = "SELECT * FROM Articles WHERE ArticleNumber = " + articleNumber + ";";
		// Cursor cursor = database.rawQuery(sql, null);
		Cursor cursor = context.getContentResolver().query(NextagramContract.Articles.CONTENT_URI, NextagramContract.Articles.PROJECTION_ALL, null, null,
				NextagramContract.Articles._ID + " ASC");

		if (cursor != null) {
			cursor.moveToFirst();
			articleNumber = cursor.getInt(0);
			title = cursor.getString(1);
			writer = cursor.getString(2);
			id = cursor.getString(3);
			content = cursor.getString(4);
			writeDate = cursor.getString(5);
			imgName = cursor.getString(6);

			article = new ArticleDTO(articleNumber, title, writer, id, content, writeDate, imgName);
		}

		cursor.close();
		return article;

	}
}
