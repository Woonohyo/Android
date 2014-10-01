package net.woonohyo.nextagram.service;

import java.util.*;

import net.woonohyo.nextagram.db.ProviderDao;
import net.woonohyo.nextagram.network.Proxy;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SyncDataService extends Service {
	private static final String TAG = SyncDataService.class.getSimpleName();
	private TimerTask timerTask;
	private Timer timer;
	private Proxy proxy;
	private ProviderDao dao;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
		proxy = new Proxy(getApplicationContext());
		dao = new ProviderDao(getApplicationContext());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");
		
		timerTask = new TimerTask() {
			
			@Override
			public void run() {
				String jsonData = proxy.getJSON();
				dao.insertJsonData(jsonData);
				Log.i(TAG, jsonData);
			}
		};
		
		timer = new Timer();
		// 작업, 시작시점, 주기
		timer.schedule(timerTask, 1000*5, 1000*5);
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
	}
}
