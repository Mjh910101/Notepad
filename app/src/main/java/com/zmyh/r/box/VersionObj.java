package com.zmyh.r.box;

public class VersionObj {

	public final static String VERSION = "version";
	public final static String VERSION_SHORT = "versionShort";
	public final static String UPDATE_URL = "update_url";
	public final static String CHANGE_LOG = "changelog";

	private int version;
	private int versionShort;
	private String update_url;
	private String changelog;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getVersionShort() {
		return versionShort;
	}

	public void setVersionShort(int versionShort) {
		this.versionShort = versionShort;
	}

	public String getUpdate_url() {
		return update_url;
	}

	public void setUpdate_url(String update_url) {
		this.update_url = update_url;
	}

	public String getChangelog() {
		return changelog;
	}

	public void setChangelog(String changelog) {
		this.changelog = changelog;
	}

}
