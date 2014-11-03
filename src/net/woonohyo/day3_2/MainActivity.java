package net.woonohyo.day3_2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				LinearLayout vgRoot = (LinearLayout) findViewById(R.id.vgRoot);
//				viewSizeAdjust(vgRoot);
//			}
//		}, 1000);
//		viewSizeAdjust(findViewById(R.id.vgRoot));
	}
	
	

	protected void viewSizeAdjust(View root) {
		if (root instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) root;
			for (int groupIdx = 0; groupIdx < group.getChildCount(); groupIdx++) {
				View child = group.getChildAt(groupIdx);
				if (child instanceof ViewGroup) {
					viewSizeAdjust(child);
				} else if (child instanceof ImageView) {
					ImageView iv = (ImageView) child;
					Log.i("test", "ImageView>>" + " width:" + iv.getWidth() + " height:" + iv.getHeight());
				} else if (child instanceof TextView) {
					TextView tv = (TextView) child;
					Log.i("test", "TextView>>" + "width: " + tv.getWidth() + " height:" + tv.getHeight() + " textSize:"
							+ tv.getTextSize());
				}
			}

		}
	}

}
