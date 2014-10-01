package net.woonohyo.nextagram.view;

import java.util.ArrayList;

import net.woonohyo.nextagram.R;
import net.woonohyo.nextagram.controller.NewsFeedController;
import net.woonohyo.nextagram.db.ArticleDTO;
import net.woonohyo.nextagram.db.ProviderDao;
import net.woonohyo.nextagram.network.Proxy;
import net.woonohyo.nextagram.provider.NextagramContract;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.*;

public class NewsFeedViewer extends ActionBarActivity implements OnClickListener, OnItemClickListener {
	private final String TAG = NewsFeedViewer.class.getSimpleName();
	private ProviderDao dao;
	private Proxy proxy;
	private Button buWrite;
	private SideNavigationView sideNavigationView;
	private SharedPreferences sharedPreferences;
	private NewsFeedController newsFeedController;
	private int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String PROPERTY_REG_ID = "registration_id";
	private String SENDER_ID = "398662932365";
	private GoogleCloudMessaging gcm;
	private String regId;
	private Context context;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_feed_view);
		dao = new ProviderDao(getApplicationContext());

		buWrite = (Button) findViewById(R.id.homeView_button_write);

		buWrite.setOnClickListener(this);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
		sideNavigationView.setMenuItems(R.menu.side_bar_menu);
		sideNavigationView.setMenuClickCallback(sideNavigationCallback);
		sideNavigationView.setMode(Mode.LEFT);

		newsFeedController = new NewsFeedController(getApplicationContext());
		newsFeedController.initSharedPreferences();
		newsFeedController.startSyncDataService();
		newsFeedController.refreshData();
		
		setListView(dao.getArticleList());
		
		sharedPreferences = getSharedPreferences(getResources().getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);
		context = getApplicationContext();
		
		if(checkGooglePlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regId = getRegistrationId(context);
			if(regId.isEmpty()) {
				registerInBackground();
			}
		}
	}

	private void registerInBackground() {
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {
				try {
					if ( gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(SENDER_ID);
					Log.i(TAG, "Registration ID: " + regId);
					storeRegistrationId(context, regId);
					
				} catch (Exception e) {
					Log.e(TAG, "Error: " + e.getMessage());
				}
				return null;
			}
		}.execute(null, null, null);
		
	}
	
	private void storeRegistrationId(Context context, String regId) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.commit();
	}

	private String getRegistrationId(Context context) {
		String registrationId = sharedPreferences.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration Id Not Found");
			return "";
		}
		Log.i(TAG, "Registratino ID: " + registrationId);
		return registrationId;
	}

	@Override
	protected void onResume() {
		super.onResume();
		newsFeedController.refreshData();
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
			Log.i(TAG, itemId + "");
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


	private void setListView(ArrayList<ArticleDTO> arrayList) {
		// CustomAdapter 적용
		ListView listView = (ListView) findViewById(R.id.main_listView);
		Cursor mCursor = getContentResolver().query(NextagramContract.Articles.CONTENT_URI, NextagramContract.Articles.PROJECTION_ALL, null, null,
				NextagramContract.Articles._ID + " ASC");
		Log.i(TAG, mCursor.toString());
		// NewsFeedAdapter customAdapter = new NewsFeedAdapter(this,
		// R.layout.main_list_row, articleList);
		NewsFeedAdapter newsFeedAdapter = new NewsFeedAdapter(this, mCursor, R.layout.main_list_row);
		listView.setAdapter(newsFeedAdapter);
		// listView에 ClickListener 설정
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.homeView_button_write:
			// Intent intent = new Intent(this, ArticleWriter.class);
			Intent intent = new Intent("net.woonohyo.nextagram.view.ArticleWriter");
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// Intent intent = new Intent(this, ArticleViewer.class);
		Intent intent = new Intent("net.woonohyo.nextagram.view.ArticleViewer");
		intent.putExtra("ArticleNumber", dao.getArticleList().get(position).getArticleNumber() + "");
		Log.i(TAG, "Position: " + position + "ArticleNumber:" + dao.getArticleList().get(position).getArticleNumber());
		startActivity(intent);
	}
	
	private boolean checkGooglePlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "The device doesn't support Google Play Service.");
				finish();
			}
			return false;
		}
		return true;
	}
}
