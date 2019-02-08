package com.kt.testapp.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kt.testapp.R;
import com.kt.testapp.adapter.PictureAdapter;

import java.util.ArrayList;

public class ImageDetailActivity extends BaseActivity {

	private ViewPager viewPager;
	private ArrayList<String> url = null;
	private PictureAdapter picDetailAdapter;

	private int position = -1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_popup_pic_detail);

//		setStatusBar();

		url = getIntent().getStringArrayListExtra("url");
		position = getIntent().getIntExtra("position",1);

		setLayout();
	}

	private void setLayout() {
		ImageButton closeBtn = (ImageButton) findViewById(R.id.ibtn_close);
		closeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Build.VERSION.SDK_INT >= 21) {
					finishAfterTransition();
				}
				else {
					finish();
				}
			}
		});

		final TextView tvCount = (TextView) findViewById(R.id.tv_count);

		if(url != null)
			tvCount.setText(position+1 + "/" + url.size());

		viewPager = (ViewPager) findViewById(R.id.viewpager_picture);
		viewPager.setOffscreenPageLimit(3);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			viewPager.setTransitionName("station");
		}


		picDetailAdapter = new PictureAdapter(this, url);
		viewPager.setAdapter(picDetailAdapter);
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				tvCount.setText(arg0+1 + "/" + url.size());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				if(arg0 == ViewPager.SCROLL_STATE_IDLE) {
					for(int i=0; i < url.size(); i++) {
						if(viewPager.getCurrentItem() != i) {
							picDetailAdapter.setInitPicture(i);
						}
					}
				}
			}
		});
		viewPager.setCurrentItem(position);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		if (Build.VERSION.SDK_INT >= 21) {
			finishAfterTransition();
		}
		else {
			finish();
		}
	}
}