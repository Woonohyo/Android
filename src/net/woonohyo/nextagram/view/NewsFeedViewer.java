package net.woonohyo.nextagram.view;

import java.util.ArrayList;

import net.woonohyo.nextagram.R;
import net.woonohyo.nextagram.db.ArticleDTO;
import net.woonohyo.nextagram.db.Dao;
import net.woonohyo.nextagram.network.Proxy;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.devspark.sidenavigation.*;
import com.devspark.sidenavigation.SideNavigationView.Mode;

public class NewsFeedViewer extends ActionBarActivity implements OnClickListener, OnItemClickListener {
	private final String TAG = NewsFeedViewer.class.getSimpleName();
	private ArrayList<ArticleDTO> articleList = new ArrayList<ArticleDTO>();
	private Dao dao;
	private Proxy proxy;
	private Button buWrite;
	private Button buRefresh;
	private SideNavigationView sideNavigationView;
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_feed_view);
		dao = new Dao(getApplicationContext());

		buWrite = (Button) findViewById(R.id.homeView_button_write);
		buRefresh = (Button) findViewById(R.id.homeView_button_refresh);

		buWrite.setOnClickListener(this);
		buRefresh.setOnClickListener(this);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
		sideNavigationView.setMenuItems(R.menu.side_bar_menu);
		sideNavigationView.setMenuClickCallback(sideNavigationCallback);
		sideNavigationView.setMode(Mode.LEFT);
		
		sharedPreferences = getSharedPreferences(getResources().getString(R.string.shared_preferences_name), MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(getResources().getString(R.string.server_url), getResources().getString(R.string.server_url_value));
		editor.putString(getResources().getString(R.string.pref_article_number), 0+"");
		editor.commit();
		
		Intent intentSyncData = new Intent("net.woonohyo.nextagram.service.SyncDataService");
		startService(intentSyncData);
		
		refreshData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String text = "";

		switch (item.getItemId()) {
		case R.id.action_item_add:
			text = "Action item, with text, displays if room exists";
			break;
		case R.id.action_item_normal:
			text = "Action item, icon only, always displays";
			break;
		case R.id.action_item_search:
			text = "Normal menu item";
			break;
		case android.R.id.home:
			text = "toggle side navigation";
			sideNavigationView.toggleMenu();
		default:
			return false;
		}
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		return true;
	}

	ISideNavigationCallback sideNavigationCallback = new ISideNavigationCallback() {
		@Override
		public void onSideNavigationItemClick(int itemId) {
			String text = "";
			Log.i(TAG, itemId+"");
			switch (itemId) {
			case R.id.side_navigation_menu_add:
				text = "ADD pressed";
				break;
			case R.id.side_navigation_menu_call:
				text = "CALL pressed";
				break;
			case R.id.side_navigation_menu_camera:
				text = "CAMERA pressed";
				break;
			case R.id.side_navigation_menu_delete:
				text = "DELETING!!";
				break;
			case R.id.side_navigation_menu_text:
				text = "SIMPLE TEXT";
				break;
			default:
				text = "";
			}
			Toast.makeText(getApplicationContext(), "SIDE NAVIGATION: " + text, Toast.LENGTH_LONG).show();
		}
	};

	private final Handler handler = new Handler();

	private void refreshData() {
//		Thread에서 메인UI의 접근은 허용되지 않기 때문에 handler를 이용한다.
		handler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				setListView();
			}
		});
		
//		new Thread() {
//			@Override
//			public void run() {
//				// network 작업을 하는 Proxy는 반드시 Thread 내부에서 실행되어야 한다.
//				proxy = new Proxy(getApplicationContext());
//				String jsonData = proxy.getJSON();
//				dao.insertJsonData(jsonData);
//
//				// Thread에서 메인UI의 접근은 허용되지 않기 때문에 handler를 이용한다.
//				handler.post(new Runnable() {
//					@Override
//					public void run() {
//						setListView();
//					}
//				});
//			}
//		}.start();
	}

	private void setListView() {
		// CustomAdapter 적용
		articleList = dao.getArticleList();
		ListView listView = (ListView) findViewById(R.id.main_listView);
		NewsFeedAdapter customAdapter = new NewsFeedAdapter(this, R.layout.main_list_row, articleList);
		listView.setAdapter(customAdapter);

		// listView에 ClickListener 설정
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.homeView_button_write:
			//Intent intent = new Intent(this, ArticleWriter.class);
			Intent intent = new Intent("net.woonohyo.nextagram.view.ArticleWriter");
			startActivity(intent);
			break;

		case R.id.homeView_button_refresh:
			refreshData();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		Intent intent = new Intent(this, ArticleViewer.class);
		Intent intent = new Intent("net.woonohyo.nextagram.view.ArticleViewer");
		intent.putExtra("ArticleNumber", articleList.get(position).getArticleNumber() + "");
		startActivity(intent);
	}
}
