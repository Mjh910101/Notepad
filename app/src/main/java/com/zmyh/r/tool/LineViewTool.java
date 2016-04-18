package com.zmyh.r.tool;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmyh.r.R;
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.handler.WinTool;

public class LineViewTool {

	private static int P = 15;

	public static View getBlackLine(Context context) {
		View v = new View(context);
		v.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 2));
		v.setBackgroundColor(ColorBox.getColorForID(context, R.color.black));
		v.setPadding(P, 0, P, 0);
		return v;
	}

	public static View getSpaceView(Context context) {
		return getSpaceView(context, 3);
	}

	public static View getSpaceView(Context context, int p) {
		View v = new View(context);
		v.setLayoutParams(new LinearLayout.LayoutParams(WinTool.dipToPx(
				context, p), LayoutParams.MATCH_PARENT));
		v.setBackgroundColor(ColorBox.getColorForID(context,
				R.color.white_lucency));
		return v;
	}

	public static View getHorizontalSpaceView(Context context) {
		View v = new View(context);
		v.setLayoutParams(new LinearLayout.LayoutParams(WinTool.dipToPx(
				context, 3), WinTool.dipToPx(context, 3)));
		v.setBackgroundColor(ColorBox.getColorForID(context,
				R.color.white_lucency));
		return v;
	}

	public static View getGrayLine(Context context) {
		View v = new View(context);
		v.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 2));
		v.setBackgroundColor(ColorBox.getColorForID(context, R.color.line_gray));
		v.setPadding(P, 0, P, 0);
		return v;
	}

	public static TextView getFirst(Context context, String text) {
		TextView v = new TextView(context);
		v.setTextSize(16);
		v.setText(text);
		v.setTextColor(ColorBox.getColorForID(context, R.color.text_gray));
		v.setPadding(0, 100, 0, 180);
		v.setGravity(Gravity.CENTER);
		v.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		return v;
	}
}
