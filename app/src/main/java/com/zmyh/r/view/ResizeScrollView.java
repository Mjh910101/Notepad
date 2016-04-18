package com.zmyh.r.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ResizeScrollView extends ScrollView {

	private OnResizeListener mListener;

	public ResizeScrollView(Context context) {
		super(context);
	}

	public ResizeScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public interface OnResizeListener {
		void OnResize(int w, int h, int oldw, int oldh);

		void scrollBottom();
	}

	public void setOnResizeListener(OnResizeListener l) {
		mListener = l;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mListener != null) {
			mListener.OnResize(w, h, oldw, oldh);
			if (h + getHeight() >= computeVerticalScrollRange()) {
				mListener.scrollBottom();
			}
		}

	}

}
