package net.woonohyo.nextagram.view;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import net.woonohyo.nextagram.R;
import net.woonohyo.nextagram.R.id;
import net.woonohyo.nextagram.R.layout;
import net.woonohyo.nextagram.R.menu;
import net.woonohyo.nextagram.db.ArticleDTO;
import net.woonohyo.nextagram.db.ProviderDao;
import net.woonohyo.nextagram.util.ImageLoader;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ArticleViewer extends Activity {
	private final String TAG = ArticleViewer.class.getSimpleName();
	private Bitmap bitmap;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private WeakReference<ImageView> imageViewWeakReference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article_viewer);

		TextView tvWriter = (TextView) findViewById(R.id.article_viewer_textView_writer);
		TextView tvWriteDate = (TextView) findViewById(R.id.article_viewer_textView_writeDate);
		TextView tvTitle = (TextView) findViewById(R.id.article_viewer_textView_title);
		TextView tvContents = (TextView) findViewById(R.id.article_viewer_textView_contents);
		ImageView ivImage = (ImageView) findViewById(R.id.article_viewer_imageView_photo);
		imageViewWeakReference = new WeakReference<ImageView>(ivImage);

		String articleNumber = getIntent().getExtras().getString("ArticleNumber");
		Log.i(TAG, "ArticleNumber: " + articleNumber);

		ProviderDao dao = new ProviderDao(getApplicationContext());

		ArticleDTO article = dao.getArticleByArticleNumber(Integer.parseInt(articleNumber));

		tvWriter.setText(article.getWriter());
		tvWriteDate.setText(article.getWriteDate());
		tvTitle.setText(article.getTitle());
		tvContents.setText(article.getContent());

		String imgPath = getApplicationContext().getFilesDir().getPath() + "/" + article.getImgName();
		Log.i(TAG, imgPath);
		bitmap = imageLoader.get(imgPath);
		
		if (bitmap != null) {
			Log.i(TAG, "Cache Hit! - " + imgPath);
			imageViewWeakReference.get().setImageBitmap(bitmap);
			
		} else {
			Log.i(TAG, "Saving new one" + imgPath);
			File fileAtImgPath = new File(imgPath);
			
			if (fileAtImgPath.exists()) {
				int sampleSize = 4;
				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
				bmOptions.inPurgeable = true;
				bmOptions.inSampleSize = sampleSize;
				bitmap = BitmapFactory.decodeFile(imgPath, bmOptions);
				Bitmap resized = Bitmap.createScaledBitmap(bitmap, 1600, 900, true);
				imageViewWeakReference.get().setImageBitmap(resized);
				imageLoader.put(imgPath, resized);
			}

		}
	}
}
