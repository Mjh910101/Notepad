package com.zmyh.r.box;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;
import com.zmyh.r.camera.util.FileUtil;
import com.zmyh.r.handler.DateHandle;
import com.zmyh.r.handler.UserObjHandle;

@Table(name = "camera_pic")
public class CameraPicObj {

	/**
	 * 原始状态
	 */
	public final static int NOTSTARTES = 0;
	/**
	 * 等待
	 */
	public final static int WAIT = 11;
	/**
	 * 上传中
	 */
	public final static int HAVE_IN_HAND = 12;
	/**
	 * 完成
	 */
	public final static int FINISH = 13;
	/**
	 * 失败
	 */
	public final static int DEFEATED = 14;

	@Id(column = "id")
	private String id;

	@Column(column = "mu_id")
	private String mu_id = "";

	@Column(column = "createAt")
	private long createAt;

	@Column(column = "size")
	private long size = 0;

	@Column(column = "mu_photo_thumbnail")
	private String mu_photo_thumbnail = "";

	@Column(column = "mu_photo_key")
	private String mu_photo_key = "";

	@Column(column = "mu_photo_original")
	private String mu_photo_original = "";

	@Column(column = "mu_hq_photo_key")
	private String mu_hq_photo_key = "";

	@Column(column = "mu_zb")
	private String mu_zb = "";

	@Column(column = "mu_latitude")
	private double mu_latitude;

	@Column(column = "mu_longitude")
	private double mu_longitude;

	@Column(column = "state")
	private int state = NOTSTARTES;

	@Column(column = "max_state")
	private int max_state = NOTSTARTES;

	@Column(column = "show_pic")
	private boolean show_pic = true;

	@Column(column = "show_max_pic")
	private boolean show_max_pic = true;

	@Transient
	private String token;
	@Transient
	private Map<String, String> map;
	@Transient
	private double percent;
	@Transient
	private String mu_photo_type;

	public void initStart(Context context, String token, String mu_id) {
		this.token = token;
		map = new HashMap<String, String>();
		map.put("x:user_id", UserObjHandle.getUsetId(context));
		map.put("x:mu_id", mu_id);
		map.put("x:mu_photo_tag", "");
		map.put("x:mu_photo_remark", "");
		map.put("x:mu_photo_type", getMu_photo_type());

		Log.e("x:user_id", UserObjHandle.getUsetId(context));
		Log.e("x:mu_id", mu_id);
		Log.e("x:mu_photo_type", getMu_photo_type());
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public String getMu_photo_type() {
		return mu_photo_type;
	}

	public void setMu_photo_type(String mu_photo_type) {
		this.mu_photo_type = mu_photo_type;
	}

	public String getId() {
		return id;
	}

	public void setId(long id) {
		this.id = String.valueOf(id);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMu_id() {
		return mu_id;
	}

	public void setMu_id(String mu_id) {
		this.mu_id = mu_id;
	}

	public long getCreateAt() {
		return createAt;
	}

	public void setCreateAt(long createAt) {
		this.createAt = createAt;
	}

	public String getCreateTime() {
		return DateHandle.format(getCreateAt() * 1000, DateHandle.DATESTYP_10);
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getMu_photo_thumbnail() {
		return mu_photo_thumbnail;
	}

	public void setMu_photo_thumbnail(String mu_photo_thumbnail) {
		this.mu_photo_thumbnail = mu_photo_thumbnail;
	}

	public String getMu_photo_key() {
		return mu_photo_key;
	}

	public void setMu_photo_key(String mu_photo_key) {
		this.mu_photo_key = mu_photo_key;
	}

	public String getMu_photo_original() {
		return mu_photo_original;
	}

	public void setMu_photo_original(String mu_photo_original) {
		this.mu_photo_original = mu_photo_original;
	}

	public String getMu_hq_photo_key() {
		return mu_hq_photo_key;
	}

	public void setMu_hq_photo_key(String mu_hq_photo_key) {
		this.mu_hq_photo_key = mu_hq_photo_key;
	}

	public String getMu_zb() {
		return mu_zb;
	}

	public void setMu_zb(String mu_zb) {
		this.mu_zb = mu_zb;
	}

	public double getMu_latitude() {
		return mu_latitude;
	}

	public void setMu_latitude(double mu_latitude) {
		this.mu_latitude = mu_latitude;
	}

	public double getMu_longitude() {
		return mu_longitude;
	}

	public void setMu_longitude(double mu_longitude) {
		this.mu_longitude = mu_longitude;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getMax_state() {
		return max_state;
	}

	public void setMax_state(int max_state) {
		this.max_state = max_state;
	}

	public boolean isShow_pic() {
		return show_pic;
	}

	public void setShow_pic(boolean show_pic) {
		this.show_pic = show_pic;
	}

	public boolean isShow_max_pic() {
		return show_max_pic;
	}

	public void setShow_max_pic(boolean show_max_pic) {
		this.show_max_pic = show_max_pic;
	}

	/**
	 * 小图名称
	 *
	 * @return
	 */
	public String getSmallFileName() {
		// return "s_" + id + ".jpg";
		return getMediumFileName();
	}

	/**
	 * 中图名称
	 *
	 * @return
	 */
	public String getMediumFileName() {
		return "m_" + id + ".jpg";
	}

	/**
	 * 大图名称
	 *
	 * @return
	 */
	public String getOriginalFileName() {
		return "o_" + id + ".jpg";

	}

	/**
	 * 小图路径
	 *
	 * @return
	 */
	public String getSmallFilePath() {
		// return FileUtil.getSmallPath() + "/" + getSmallFileName();
		return getMediumFilePath();
	}

	/**
	 * 中图路径
	 *
	 * @return
	 */
	public String getMediumFilePath() {
		return FileUtil.getMediumPath() + "/" + getMediumFileName();
	}

	/**
	 * 大图路径
	 *
	 * @return
	 */
	public String getOriginalFilePath() {
		return FileUtil.getOriginalPath() + "/" + getOriginalFileName();
	}

	public static CameraPicObj copy(CameraPicObj obj) throws Exception {
		long createTime = DateHandle.getTime();

		CameraPicObj newObj = new CameraPicObj();
		newObj.setId(createTime);
		newObj.setCreateAt(createTime);

		InputStream is = new FileInputStream(new File(obj.getMediumFilePath()));
		FileUtil.saveImage(is, newObj.getMediumFilePath());
		return newObj;
	}

	public String toString() {
		return "id : " + id + " , mu_id : " + mu_id + " , createAt : "
				+ createAt + " , size : " + size + " , mu_photo_thumbnail : "
				+ mu_photo_thumbnail + " , mu_photo_key : " + mu_photo_key
				+ " , mu_photo_original : " + mu_photo_original
				+ " , mu_hq_photo_key : " + mu_hq_photo_key + " , mu_zb : "
				+ mu_zb + " , mu_latitude : " + mu_latitude
				+ " , mu_longitude : " + mu_longitude + " , state : " + state
				+ " , max_state : " + max_state + " , show_pic : " + show_pic
				+ " , show_max_pic : " + show_max_pic;
	}

}
