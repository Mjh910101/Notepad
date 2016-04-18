package com.zmyh.r.box;

import android.util.Log;

public class CommentObj {

	public final static String ID = "_id";
	public final static String COMMENT = "comment";
	public final static String POSTER = "poster";
	public final static String POST_ID = "post_id";
	public final static String CREATE_AT = "createAt";

	private String id;
	private String comment;
	private UserObj poster;
	private String post_id;
	private long createAt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public UserObj getPoster() {
		return poster;
	}

	public void setPoster(UserObj poster) {
		this.poster = poster;
	}

	public String getPost_id() {
		return post_id;
	}

	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}

	public long getCreateAt() {
		return createAt;
	}

	public void setCreateAt(long createAt) {
		this.createAt = createAt;
	}

	public String getUsetPic() {
		if (poster != null) {
			return poster.getM_avatar();
		}
		return "";
	}

	public String getUserName() {
		if (poster != null) {
			return poster.getM_mobile();
		}
		return "";
	}

}
