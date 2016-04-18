package com.zmyh.r.camera.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageUtil {
	/**
	 * 旋转Bitmap
	 *
	 * @param b
	 * @param rotateDegree
	 * @return
	 */
	public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
		Matrix matrix = new Matrix();
		matrix.postRotate((float) rotateDegree);
		Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
				b.getHeight(), matrix, false);
		return rotaBitmap;
	}

	public static byte[][] getRotateBitmap(byte[][] b) {
		byte[][] temp = new byte[b[0].length][b.length];
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b[i].length; j++) {
				temp[i][j] = b[b[i].length - j - 1][i];
			}
		}
		return temp;
	}

	public static Bitmap getPostScaleBitmap(Bitmap b) {
		Matrix matrix = new Matrix();
		matrix.postScale(1, -1);
		Bitmap postScaleBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
				b.getHeight(), matrix, false);
		return postScaleBitmap;
	}

	public static byte[] getBitmapToBytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static byte[] getBytesFromFile(File f) {
		if (f == null) {
			return null;
		}
		try {
			FileInputStream stream = new FileInputStream(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1)
				out.write(b, 0, n);
			stream.close();
			out.flush();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
