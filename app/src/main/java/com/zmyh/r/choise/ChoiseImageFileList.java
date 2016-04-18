package com.zmyh.r.choise;

import java.util.ArrayList;
import java.util.List;

public class ChoiseImageFileList {

	private static List<String> fileList = new ArrayList<String>();

	public static void add(String path) {
		if (fileList == null) {
			fileList = new ArrayList<String>();
		}
		fileList.add(path);
	}

	public static void remove(String path) {
		fileList.remove(path);
	}

	public static void remove(int p) {
		fileList.remove(p);
	}

	public static void removeAll() {
		fileList.removeAll(fileList);
	}

	public static boolean isChoise(String path) {
		for (String p : fileList) {
			if (path.equals(p)) {
				return true;
			}
		}
		return false;
	}

	public static int getChoiseSiza() {
		return fileList.size();
	}

	public static List<String> getChoiseImageFileList() {
		return fileList;
	}

}
