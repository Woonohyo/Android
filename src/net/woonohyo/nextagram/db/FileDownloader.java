package net.woonohyo.nextagram.db;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.util.Log;

public class FileDownloader {
	private final Context context;
	private final String TAG = FileDownloader.class.getSimpleName();

	public FileDownloader(Context context) {
		this.context = context;
	}

	public void downloadFile(String fileUrl, String fileName) {
		File filePath = new File(context.getFilesDir().getPath() + "/" + fileName);

		// 파일이 존재하지 않을 때에만 다운로드
		if (!filePath.exists()) {
			try {
				Log.i(TAG, "Downloading " + fileName + " to " + filePath);
				URL url = new URL(fileUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();

				conn.setConnectTimeout(10 * 1000);
				conn.setReadTimeout(10 * 1000);
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Accept-Charset", "UTF-8");
				conn.setRequestProperty("Cache-Control", "no-cache");
				conn.setRequestProperty("Accept", "*/*");
				conn.setDoInput(true);
				conn.connect();
				
				int status = conn.getResponseCode();
				Log.i(TAG, "Response Code: " + status);
				
				switch (status) {
				case 200:
				case 201:
					// Connection Success
					InputStream is = conn.getInputStream();
					BufferedInputStream bis = new BufferedInputStream(is);
					ByteArrayBuffer baf = new ByteArrayBuffer(50);
					
					int current = 0;
					
					while ((current = bis.read()) != -1) {
						baf.append((byte)current);
					}
					
					FileOutputStream fos = context.openFileOutput(fileName, 0);
					fos.write(baf.toByteArray());

					fos.close();
					bis.close();
					is.close();
				}

			} catch (Exception e) {
				Log.e(TAG, "Downloading Error - " + e);
			}

		} else {
			Log.i(TAG, "File already exists:" + fileName);
		}
	}
}
