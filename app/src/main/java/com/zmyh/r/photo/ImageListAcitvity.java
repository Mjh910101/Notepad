package com.zmyh.r.photo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.senab.photoview.PhotoView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zmyh.r.R;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.view.HackyViewPager;

public class ImageListAcitvity extends Activity {
	private Context context;

	private int position;
	private boolean isOnline, isRotate;

	private List<ImageView> ballList;

	@ViewInject(R.id.title_bg)
	private RelativeLayout titleBg;
	@ViewInject(R.id.choiseImageList_dataViewPager)
	private HackyViewPager dataPager;
	@ViewInject(R.id.choiseImageList_sizeBox)
	private LinearLayout sizeBox;

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

	private void close() {
		finish();
	}

	private void setContentBoxListener() {
		dataPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				setOnBall(position);
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
		titleBg.setVisibility(View.GONE);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			// color = b.getInt(MessageBox.COLOR);
			// titleBg.setBackgroundColor(color);
			position = b.getInt("position");
			isOnline = b.getBoolean("isOnline");
			isRotate = b.getBoolean("isRotate");
			setImageList(b.getStringArrayList("iamge_list"), position);
		}

	}

	private void setImageList(List<String> list, int position) {
		dataPager.setAdapter(new ContentPagerAdapter(list));
		dataPager.setOffscreenPageLimit(1);
		dataPager.setCurrentItem(position);
		setBallList(list.size(), position);
	}

	private void setBallList(int size, int position) {
		if (size > 1) {
			int w = WinTool.pxToDip(context, 40);
			ballList = new ArrayList<ImageView>(size);
			for (int i = 0; i < size; i++) {
				ImageView v = new ImageView(context);
				v.setImageResource(R.drawable.ppt_off);
				v.setLayoutParams(new LinearLayout.LayoutParams(w, w));

				View l = new View(context);
				l.setLayoutParams(new LinearLayout.LayoutParams(w, w));

				sizeBox.addView(v);
				sizeBox.addView(l);

				ballList.add(v);
			}
			setOnBall(position);
		}
	}

	private void setOnBall(int p) {
		for (ImageView v : ballList) {
			v.setImageResource(R.drawable.ppt_off);
		}
		ballList.get(p).setImageResource(R.drawable.ppt_on);
	}

	private PhotoView getImageView(String pic) {
		final PhotoView photoView = new PhotoView(context);
		// FreedomImageView view = new FreedomImageView(context);
		// view.initForPath(pic);
		if (isOnline) {
			DownloadImageLoader.loadImage(photoView, pic, new ImageListener(
					photoView));
		} else {
			DownloadImageLoader.loadMeteorImageForFile(photoView, pic,
					new ImageListener(photoView));
		}
		// view.setOnClickListener(onClickListener);
		return photoView;
	}

	private int getPicDegree(String pic) {
		int degree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(pic);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (exif != null) {
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			Log.e("Degree", "Degree " + orientation);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		}
		return degree;
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
		}

	};

	class ImageListener implements ImageLoadingListener {

		PhotoView photoView;

		public ImageListener(PhotoView photoView) {
			this.photoView = photoView;
		}

		@Override
		public void onLoadingStarted(String imageUri, View view) {
		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
									FailReason failReason) {
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
									  Bitmap loadedImage) {
			Log.e("Degree",
					loadedImage.getWidth() + " : " + loadedImage.getHeight());
//			if (loadedImage.getWidth() > loadedImage.getHeight() && isRotate) {
//				// photoView.setRotation(90);
//				photoView.setPhotoViewRotation(-90);
//			}
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
		}
	};

	class ContentPagerAdapter extends PagerAdapter {

		private List<String> iamgePhatList;
		private Map<Integer, PhotoView> imageMap;

		public ContentPagerAdapter(List<String> iamgePhatList) {
			this.iamgePhatList = iamgePhatList;
			imageMap = new HashMap<Integer, PhotoView>();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			try {
				container.removeView(imageMap.get(position));
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
			PhotoView v;
			if (imageMap.containsKey(iamgePhatList.get(position))) {
				v = imageMap.get(position);
			} else {
				v = getImageView(iamgePhatList.get(position));
				imageMap.put(position, v);
			}
			Log.e("", iamgePhatList.get(position));
			container.addView(v);
			return v;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
}
