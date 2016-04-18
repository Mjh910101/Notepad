package com.zmyh.r.choise;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class Util {

	public static String getfileinfo(String data) {
		String filename[] = data.split("/");
		if (filename != null) {
			return filename[filename.length - 2];
		}
		return null;
	}

	public static ArrayList<String> listAlldir(Context context) {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		Uri uri = intent.getData();
		ArrayList<String> list = new ArrayList<String>();
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(uri, proj, null,
				null, null);// managedQuery(uri, proj, null, null, null);
		while (cursor.moveToNext()) {
			String path = cursor.getString(0);
			list.add(new File(path).getAbsolutePath());
		}
		return list;
	}

	public static List<FileTraversal> LocalImgFileList(Context context) {
		List<FileTraversal> data = new ArrayList<FileTraversal>();
		String filename = "";
		List<String> allimglist = listAlldir(context);
		List<String> retulist = new ArrayList<String>();
		if (allimglist != null) {
			Set set = new TreeSet();
			String[] str;
			for (int i = 0; i < allimglist.size(); i++) {
				retulist.add(getfileinfo(allimglist.get(i)));
			}
			for (int i = 0; i < retulist.size(); i++) {
				set.add(retulist.get(i));
			}
			str = (String[]) set.toArray(new String[0]);
			for (int i = 0; i < str.length; i++) {
				filename = str[i];
				FileTraversal ftl = new FileTraversal();
				ftl.filename = filename;
				data.add(ftl);
			}

			for (int i = 0; i < data.size(); i++) {
				for (int j = 0; j < allimglist.size(); j++) {
					if (data.get(i).filename.equals(getfileinfo(allimglist
							.get(j)))) {
						data.get(i).filecontent.add(allimglist.get(j));
					}
				}
			}
		}
		return data;
	}
}
