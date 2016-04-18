package com.zmyh.r.http;

import java.io.File;

public class FileInfo {

	private static final long serialVersionUID = 1L;

	private File file; // 文件
	private String fileTextName; // 表单名称

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileTextName() {
		return fileTextName;
	}

	public void setFileTextName(String fileTextName) {
		this.fileTextName = fileTextName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
