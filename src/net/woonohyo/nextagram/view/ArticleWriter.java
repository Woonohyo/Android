package net.woonohyo.nextagram.view;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import net.woonohyo.nextagram.R;
import net.woonohyo.nextagram.R.id;
import net.woonohyo.nextagram.R.layout;
import net.woonohyo.nextagram.R.menu;
import net.woonohyo.nextagram.db.ArticleDTO;
import net.woonohyo.nextagram.network.ArticleWritingProxy;
import android.text.Editable;
import android.util.Log;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.MediaStore.Images;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class ArticleWriter extends Activity implements OnClickListener {
	private static final int REQUEST_PHOTO_ALBUM = 1;

	private final String TAG = ArticleWriter.class.getSimpleName(); 
	
	private EditText etWriter;
	private EditText etTitle;
	private EditText etContents;
	private ImageButton ibPhoto;
	private Button buWrite;

	private String filePath;
	private String fileName;
	
	private ProgressDialog progressDialog;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article_writer);
		
		etWriter = (EditText) findViewById(R.id.article_writer_editText_writer);
		etTitle = (EditText) findViewById(R.id.article_writer_editText_title);
		etContents = (EditText) findViewById(R.id.article_writer_editText_contents);
		ibPhoto = (ImageButton) findViewById(R.id.article_writer_imageButton_addPhoto);
		buWrite = (Button) findViewById(R.id.article_writer_button_write);
		
		ibPhoto.setOnClickListener(this);
		buWrite.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.article_writer_imageButton_addPhoto:
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType(Images.Media.CONTENT_TYPE);
			intent.setData(Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, REQUEST_PHOTO_ALBUM);
			break;
			
		case R.id.article_writer_button_write:
			final Handler handler = new Handler();

			new Thread(){
				public void run() {
					
					// 스레드 안에서 UI 제어 필요
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							progressDialog = ProgressDialog.show(ArticleWriter.this, "Notice", "Uploading your article..");
							
						}
					});
					
					String ID = Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
					String DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA).format(new Date());
					ArticleDTO article = new ArticleDTO(0, etTitle.getText().toString(), etWriter.getText().toString(), ID, etContents.getText().toString(), DATE, fileName);
					
					ArticleWritingProxy proxyUpload = new ArticleWritingProxy();
					proxyUpload.uploadArticle(article, filePath);
					Log.i(TAG, "Uploading to " + filePath);
					
					handler.post(new Runnable() {
						@Override
						public void run() {
							progressDialog.cancel();
							finish();
						}
					});
				};
			}.start();
			break;
			
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == REQUEST_PHOTO_ALBUM) {
			Uri uri = getRealPathUri(data.getData());
			filePath = uri.toString();
			fileName = uri.getLastPathSegment();
			
			Bitmap bm = BitmapFactory.decodeFile(filePath);
			ibPhoto.setImageBitmap(bm);
		}
	}
	
	private Uri getRealPathUri(Uri uri) {
        Uri filePathUri = uri;
        if (uri.getScheme().toString().compareTo("content") == 0) {
                Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null,null, null);
                if (cursor.moveToFirst()) {
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        filePathUri = Uri.parse(cursor.getString(column_index));
                }
        }
        return filePathUri;
}
}
