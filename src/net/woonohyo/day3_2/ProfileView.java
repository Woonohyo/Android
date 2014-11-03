package net.woonohyo.day3_2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProfileView extends LinearLayout {

	private ImageView ivPhoto;
	private TextView tvName;
	
	public ProfileView(Context context) {
		super(context);
		initialize(context);
	}
	
	public ProfileView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public ProfileView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}
	
	private void initialize(Context context) {
		LayoutInflater li = ((Activity) context).getLayoutInflater();
		li.inflate(R.layout.view_profile, this);

		ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
		tvName = (TextView) findViewById(R.id.tvName);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int ivPhotoWidth = ivPhoto.getWidth();
		int ivPhotoHeight = ivPhoto.getHeight();
		Log.i("test", "ivPhoto>>" + ivPhoto + "width:" + ivPhotoWidth+" height:"+ivPhotoHeight);
		int padding = 0;
		int size = 0;
		if (ivPhotoWidth > 0) {
			int tmp = 0;
			if (ivPhotoWidth > ivPhotoHeight) {
				tmp = ivPhotoHeight;
			} else {
				tmp = ivPhotoWidth;
			}

			size = tmp / 7;
			padding = tmp / 20;
			ivPhoto.setPadding(padding, padding, padding, padding);
		}

		tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = 0;
		switch (heightMode) {
		case MeasureSpec.UNSPECIFIED:
			heightSize = heightMeasureSpec;
			break;
		case MeasureSpec.AT_MOST:
			heightSize = 20;
		case MeasureSpec.EXACTLY:
			heightSize = MeasureSpec.getSize(heightMeasureSpec);
			break;
		}
	}
}
