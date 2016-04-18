package com.zmyh.r.handler;

import java.io.File;

import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.interfaces.CallbackForString;

public class ImageHandle {

	// private final static String path = DownloadImageLoader.getImagePath();

	public static void delete(final CallbackForBoolean callback) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					seekAll(new File(DownloadImageLoader.getImagePath()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (callback != null) {
					callback.callback(true);
				}

			}
		}).start();
	}

	private static void seekAll(File file) {
		File[] files = file.listFiles();
		if (files == null) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				seekAll(files[i]);
			} else {
				files[i].delete();
			}
		}
	}

	private static double size = 0;

	public static void getFileSum(final CallbackForString callback) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				size = 0;
				try {
					getAllSize(new File(DownloadImageLoader.getImagePath()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (callback != null) {
					String r = String.format("%.2f", size / 1024 / 1024);
					callback.callback(r);
				}

			}
		}).start();
	}

	private static void getAllSize(File file) {
		File[] files = file.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					getAllSize(files[i]);
				} else {
					size += files[i].length();
				}
			}
		}
	}

}
