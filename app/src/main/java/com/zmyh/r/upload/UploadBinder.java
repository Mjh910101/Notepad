package com.zmyh.r.upload;

import android.os.Binder;

import com.zmyh.r.upload.services.UploadService;

public class UploadBinder extends Binder {

	private UploadService s;

	public UploadBinder(UploadService s) {
		this.s = s;
	}

	/**
	 * 获取当前Service的实例
	 *
	 * @return
	 */
	public UploadService getService() {
		return s;
	}
}
