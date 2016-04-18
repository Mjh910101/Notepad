package com.zmyh.r.box;

public class ChannelObj {

	public final static String ID = "_id";
	public final static String TITLE = "title";
	public final static String ICO_URL = "ico_url";

	private String id;
	private String title;
	private String ico_url;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIco_url() {
		return ico_url;
	}

	public void setIco_url(String ico_url) {
		this.ico_url = ico_url;
	}

}
