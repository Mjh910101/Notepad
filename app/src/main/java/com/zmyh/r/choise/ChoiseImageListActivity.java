package com.zmyh.r.choise;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zmyh.r.R;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.main.forum.ShareForumActivity;

public class ChoiseImageListActivity extends Activity {

	private Context context;

	private int position;
	private boolean isShow = false;

	@ViewInject(R.id.title_bg)
	private RelativeLayout titleBg;
	@ViewInject(R.id.title_titleName)
	private TextView titleName;
	@ViewInject(R.id.title_seek)
	private ImageView seekIcon;
	@ViewInject(R.id.title_size)
	private TextView titleSize;
	@ViewInject(R.id.title_trash)
	private ImageView titleTrash;

	@ViewInject(R.id.choiseImageList_dataViewPager)
	private ViewPager dataPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choiseimagelist);

		context = this;
		ViewUtils.inject(this);

		initActivity();
		setContentBoxListener();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			close();
		}
		return false;
	}

	@OnClick({ R.id.title_back, R.id.title_trash })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			close();
			break;
		case R.id.title_trash:
			deleteImage();
			break;
		}
	}

	private void close() {
		Intent i = new Intent();
		Bundle b = new Bundle();
		b.putBoolean("data", true);
		i.putExtras(b);
		setResult(ShareForumActivity.resultCode, i);
		finish();
	}

	private void setContentBoxListener() {
		dataPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				setTitleSiza(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	private void initActivity() {
		titleBg.setVisibility(View.INVISIBLE);
		titleName.setVisibility(View.INVISIBLE);
		titleTrash.setVisibility(View.VISIBLE);
		titleSize.setVisibility(View.VISIBLE);
		seekIcon.setVisibility(View.GONE);
		titleTrash.setBackgroundResource(R.drawable.bg_stype_03);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			// color = b.getInt(MessageBox.COLOR);
			// titleBg.setBackgroundColor(color);
			position = b.getInt("position");
			setImageList(ChoiseImageFileList.getChoiseImageFileList(), position);
		}

	}

	private void deleteImage() {
		ChoiseImageFileList.remove(position);
		// dataPager.setAdapter(null);
		int p = getPosition();
		setImageList(ChoiseImageFileList.getChoiseImageFileList(), p);
	}

	private int getPosition() {
		if (position >= ChoiseImageFileList.getChoiseSiza()) {
			position = ChoiseImageFileList.getChoiseSiza() - 1;
		}
		if (position <= 0) {
			position = 0;
		}
		return position;
	}

	private void setImageList(List<String> list, int position) {
		dataPager.setAdapter(new ContentPagerAdapter(list));
		dataPager.setOffscreenPageLimit(1);
		dataPager.setCurrentItem(position);
		setTitleSiza(position);
	}

	private void setTitleSiza(int position) {
		this.position = position;
		titleSize.setText("(" + (position + 1) + "/"
				+ ChoiseImageFileList.getChoiseSiza() + ")");
	}

	private ImageView getImageView(String pic) {
		ImageView view = new ImageView(context);
		DownloadImageLoader.loadImageForFile(view, pic,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						Log.e("", "onLoadingStarted:::::" + imageUri);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						Log.e("", "onLoadingFailed:::::" + imageUri);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						Log.e("", "onLoadingComplete:::::" + imageUri);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						Log.e("", "onLoadingCancelled:::::" + imageUri);
					}
				});
		view.setOnClickListener(onClickListener);
		return view;
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (isShow) {
				titleBg.setVisibility(View.INVISIBLE);
			} else {
				titleBg.setVisibility(View.VISIBLE);
			}
			isShow = !isShow;
		}

	};

	class ContentPagerAdapter extends PagerAdapter {

		private List<String> iamgePhatList;
		private Map<String, ImageView> imageMap;

		public ContentPagerAdapter(List<String> iamgePhatList) {
			this.iamgePhatList = iamgePhatList;
			imageMap = new HashMap<String, ImageView>();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			try {
				container.removeView(imageMap.get(iamgePhatList.get(position)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public int getCount() {
			return iamgePhatList.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView v;
			if (imageMap.containsKey(iamgePhatList.get(position))) {
				v = imageMap.get(iamgePhatList.get(position));
			} else {
				v = getImageView(iamgePhatList.get(position));
				imageMap.put(iamgePhatList.get(position), v);
			}
			Log.e("", iamgePhatList.get(position));
			container.addView(v, 0);
			return v;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

}
