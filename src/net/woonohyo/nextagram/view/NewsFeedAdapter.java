package net.woonohyo.nextagram.view;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import net.woonohyo.nextagram.R;
import net.woonohyo.nextagram.R.id;
import net.woonohyo.nextagram.db.ArticleDTO;
import net.woonohyo.nextagram.provider.NextagramContract;
import net.woonohyo.nextagram.util.ImageLoader;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsFeedAdapter extends CursorAdapter {
	private final String TAG = NewsFeedAdapter.class.getSimpleName();
	private Context context;
	private int layoutResourceId;
	private ArrayList<ArticleDTO> article;
	private WeakReference<ImageView> imageViewWeakReference;
	private Cursor cursor;
	private SharedPreferences sharedPreferences;
	private LayoutInflater layoutInflater;
	private Bitmap bitmap;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private String THUMBNAIL = "TN";
	
	public NewsFeedAdapter(Context context, Cursor cursor, int layoutId) {
		super(context, cursor, layoutId);
		this.context = context;
		this.cursor = cursor;
		this.layoutResourceId = layoutId;
		sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preferences_name), context.MODE_PRIVATE);
		layoutInflater = LayoutInflater.from(context);
	}
	
	static class ViewHolderItem {
		TextView tvTitle;
		TextView tvContents;
		ImageView imageView;
		String articleNumber;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View row = layoutInflater.inflate(layoutResourceId, parent, false);
		ViewHolderItem viewHolder = new ViewHolderItem();
		
		viewHolder.tvTitle = (TextView) row.findViewById(R.id.main_list_title_text_view);
		viewHolder.tvContents = (TextView) row.findViewById(R.id.main_list_contents_text_view);
		viewHolder.imageView = (ImageView) row.findViewById(R.id.main_list_image_view);
		row.setTag(viewHolder);
		
		return row;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String title = cursor.getString(cursor.getColumnIndex(NextagramContract.Articles.TITLE));
		String contents = cursor.getString(cursor.getColumnIndex(NextagramContract.Articles.CONTENTS));
		String imageName = cursor.getString(cursor.getColumnIndex(NextagramContract.Articles.IMAGE_NAME));
		String articleNumber = cursor.getString(cursor.getColumnIndex(NextagramContract.Articles._ID));
		
		ViewHolderItem viewHolder = (ViewHolderItem) view.getTag();
		viewHolder.tvTitle.setText(title);
		viewHolder.tvContents.setText(contents);
		viewHolder.articleNumber = articleNumber;
		
		String imgPath = context.getFilesDir().getPath() + "/" + imageName;
		bitmap = imageLoader.get(imgPath);
		imageViewWeakReference = new WeakReference<ImageView>(viewHolder.imageView);

		if (bitmap != null) {
			Log.i(TAG, "Cache Hit! - " + imgPath);
			Bitmap resized = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
			imageViewWeakReference.get().setImageBitmap(resized);
			
		} else {
			Log.i(TAG, "Saving new one" + imgPath);
			File fileAtImgPath = new File(imgPath);
			
			if (fileAtImgPath.exists()) {
				int sampleSize = 4;
				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
				bmOptions.inPurgeable = true;
				bmOptions.inSampleSize = sampleSize;
				bitmap = BitmapFactory.decodeFile(imgPath, bmOptions);
				Bitmap resized = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
				imageViewWeakReference.get().setImageBitmap(resized);
				imageLoader.put(imgPath, bitmap);
			}
		}
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent();
			
			Log.e(TAG, "ArticleNumber: " + ((ViewHolderItem)view.getTag()).articleNumber);
			intent.setAction("net.woonohyo.nextagram.Article");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("ArticleNumber", ((ViewHolderItem)view.getTag()).articleNumber);
			
			context.startActivity(intent);
		}
		
	};
}
