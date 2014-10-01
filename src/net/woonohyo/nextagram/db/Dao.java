package net.woonohyo.nextagram.db;

import java.util.ArrayList;

import net.woonohyo.nextagram.R;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Dao {
	private static final String TAG = Dao.class.getSimpleName();
	private Context context;
	private SQLiteDatabase database;
	private SharedPreferences sharedPreferences;

	public Dao(Context context) {
		this.context = context;

		// init SQLite
		database = context.openOrCreateDatabase("LocalDATA.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);

		try {
//			 String sqlDelete = "DROP TABLE Articles;";
			String sql = "CREATE TABLE IF NOT EXISTS Articles(ID integer primary key autoincrement," + "ArticleNumber integer UNIQUE not null,"
					+ "Title text not null," + "WriterName text not null," + "WriterId text not null," + "Content text not null," + "WriteDate text not null,"
					+ "ImgName text UNIQUE not null);";

			database.execSQL(sql);

		} catch (Exception e) {
			Log.e(TAG, "failed to create table");
		}

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
					Log.i(TAG, "set prefArticleNumber to " + (articleNumber-1));
				}

				Log.i(TAG, "ArticleNumber: " + articleNumber + " Title: " + title);

				String sql = "INSERT INTO Articles(ArticleNumber, Title, WriterName, WriterID, Content, WriteDate, ImgName)" + " VALUES(" + articleNumber
						+ ", '" + title + "', '" + writer + "', '" + id + "', '" + content + "', '" + writeDate + "', '" + imgName + "');";
				try {
					database.execSQL(sql);

				} catch (Exception e) {
					Log.e(TAG, "DB ERROR: " + e);
					e.printStackTrace();
				}

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
		String content;
		String writeDate;
		String imgName;

		String sql = "SELECT * FROM Articles;";
		Cursor cursor = database.rawQuery(sql, null);

		while (cursor.moveToNext()) {
			articleNumber = cursor.getInt(1);
			title = cursor.getString(2);
			writer = cursor.getString(3);
			id = cursor.getString(4);
			content = cursor.getString(5);
			writeDate = cursor.getString(6);
			imgName = cursor.getString(7);

			articleList.add(new ArticleDTO(articleNumber, title, writer, id, content, writeDate, imgName));
		}

		cursor.close();

		return articleList;
	}

	public ArticleDTO getArticleByArticleNumber(int articleNumber) {

		ArticleDTO article;

		String title;
		String writer;
		String id;
		String content;
		String writeDate;
		String imgName;

		String sql = "SELECT * FROM Articles WHERE ArticleNumber = " + articleNumber + ";";
		Cursor cursor = database.rawQuery(sql, null);

		cursor.moveToNext();

		articleNumber = cursor.getInt(1);
		title = cursor.getString(2);
		writer = cursor.getString(3);
		id = cursor.getString(4);
		content = cursor.getString(5);
		writeDate = cursor.getString(6);
		imgName = cursor.getString(7);

		article = new ArticleDTO(articleNumber, title, writer, id, content, writeDate, imgName);

		cursor.close();

		return article;
	}
}
