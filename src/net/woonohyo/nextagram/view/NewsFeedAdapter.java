package net.woonohyo.nextagram.view;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import net.woonohyo.nextagram.R;
import net.woonohyo.nextagram.R.id;
import net.woonohyo.nextagram.db.ArticleDTO;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsFeedAdapter extends ArrayAdapter<ArticleDTO> {
	private Context context;
	private int layoutResourceId;
	private ArrayList<ArticleDTO> article;
	private WeakReference<ImageView> imageViewWeakReference;

	public NewsFeedAdapter(Context context, int layoutResourceId, ArrayList<ArticleDTO> article) {
		super(context, layoutResourceId, article);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.article = article;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// MainList의 각각의 칸
		View row = convertView;

		if (row == null) {
			// LayoutInflater inflater = LayoutInflater.from(context);
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			Log.i("Adapter", "" + inflater);
			row = inflater.inflate(layoutResourceId, parent, false);
		}

		TextView tvText1 = (TextView) row.findViewById(R.id.main_list_title_text_view);
		TextView tvText2 = (TextView) row.findViewById(R.id.main_list_contents_text_view);
		tvText1.setText(article.get(position).getTitle());
		tvText2.setText(article.get(position).getContent());

		ImageView imgView = (ImageView) row.findViewById(R.id.main_list_image_view);
		imageViewWeakReference = new WeakReference<ImageView>(imgView);

		String imgPath = context.getFilesDir().getPath() + "/" + article.get(position).getImgName();
		File imgLoadPath = new File(imgPath);

		if (imgLoadPath.exists()) {
			int sampleSize = 16;
			
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inPurgeable = true;
			bmOptions.inSampleSize = sampleSize;

			Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
			Bitmap resized = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
			imageViewWeakReference.get().setImageBitmap(resized);
		}

		return row;
	}
}
