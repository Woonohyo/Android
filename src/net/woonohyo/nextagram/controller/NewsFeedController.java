package net.woonohyo.nextagram.controller;

import java.util.ArrayList;

import net.woonohyo.nextagram.R;
import net.woonohyo.nextagram.db.ArticleDTO;
import net.woonohyo.nextagram.db.ProviderDao;
import net.woonohyo.nextagram.network.Proxy;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class NewsFeedController {
	private Context context;
	private SharedPreferences sharedPreferences;
	protected Proxy proxy;
	protected ProviderDao dao;

	public NewsFeedController(Context context) {
		this.context = context;
		this.proxy = new Proxy(context);
		this.dao = new ProviderDao(context);
	}

	public void initSharedPreferences() {
		sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences_name), context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(context.getResources().getString(R.string.server_url), context.getResources().getString(R.string.server_url_value));
		editor.putString(context.getResources().getString(R.string.pref_article_number), 0 + "");
		editor.commit();
	}
	
	public void refreshData() {
		new Thread() {
			@Override
			public void run() {
				// network 작업을 하는 Proxy는 반드시 Thread 내부에서 실행되어야 한다.
				ArrayList<ArticleDTO> articleList = proxy.getArticleDTO();
				dao.insertData(articleList);
			}
		}.start();
	}
	
	public void startSyncDataService() {
		Intent intentSyncData = new Intent("net.woonohyo.nextagram.service.SyncDataService");
		context.startService(intentSyncData);
	}
	
}
