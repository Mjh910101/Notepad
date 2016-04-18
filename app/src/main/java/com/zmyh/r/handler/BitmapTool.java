package com.zmyh.r.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class BitmapTool {

	public static byte[] bmpToByte(Bitmap bmp) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, bos);
		byte[] b = bos.toByteArray();
		bmp.recycle();
		try {
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	public static Bitmap getLogo(Context context) {
		Bitmap image = null;
		try {
			InputStream is = context.getAssets().open("logo.png");
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

}
