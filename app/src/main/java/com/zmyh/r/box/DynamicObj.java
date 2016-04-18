package com.zmyh.r.box;

import java.util.ArrayList;
import java.util.List;

import com.zmyh.r.handler.DateHandle;

public class DynamicObj {

    public final static String ID = "_id";
    public final static String CONTENT = "content";
    public final static String TYPE = "type";
    public final static String PORTER = "poster";
    public final static String TITLE = "title";
    public final static String CREATE_AT = "createAt";
    public final static String PIC = "pic";
    public final static String POST_THUMBNAIL = "post_thumbnail";
    public final static String INTRO = "intro";
    public final static String COMMENT_COUNT = "comment_count";
    public final static String FAVOR_COUNT = "favor_count";
    public final static String MM_AREA = "mmArea";
    public final static String IS_FAVOR = "isFavor";

    private String id;
    private String content;
    private String type;
    private UserObj poster;
    private String title;
    private long createAt;
    private String[] pic;
    private String post_thumbnail;
    private String intro;
    private int comment_count;
    private int favor_count;
    private CityObj mmArea;
    private boolean isFavor;

    public boolean isFavor() {
        return isFavor;
    }

    public void setFavor(boolean isFavor) {
        this.isFavor = isFavor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserObj getPoster() {
        return poster;
    }

    public void setPoster(UserObj poster) {
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String[] getPic() {
        return pic;
    }

    public void setPic(String[] pic) {
        this.pic = pic;
    }

    public CharSequence getUserName() {
        if (poster != null) {
            return poster.getM_nick_name();
//			String str = poster.getM_mobile();
//			if (str.length() > 6) {
//				String s = str.substring(0, 3);
//				String e = str.substring(str.length() - 3, str.length());
//				StringBuffer sb = new StringBuffer();
//				sb.append(s);
//				for (int i = 0; i < str.length() - 6; i++) {
//					sb.append("*");
//				}
//				sb.append(e);
//				return sb.toString();
//			}
//			return str;
        }
        return "";
    }

    public String getCreateTime() {
        return DateHandle.format(createAt * 1000, DateHandle.DATESTYP_10);
    }

    public List<String> getPicList() {
        List<String> list = new ArrayList<String>();
        if (pic != null) {
            for (int i = 0; i < pic.length; i++) {
                list.add(pic[i]);
            }
        }
        return list;
    }

    public String getUserPic() {
        if (poster != null) {
            return poster.getM_avatar();
        }
        return "";
    }

    public String getPost_thumbnail() {
        return post_thumbnail;
    }

    public void setPost_thumbnail(String post_thumbnail) {
        this.post_thumbnail = post_thumbnail;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public int getFavor_count() {
        return favor_count;
    }

    public void setFavor_count(int favor_count) {
        this.favor_count = favor_count;
    }

    public CityObj getMmArea() {
        return mmArea;
    }

    public void setMmArea(CityObj mmArea) {
        this.mmArea = mmArea;
    }

    public String getCity() {
        if (mmArea != null) {
            return mmArea.getArea_name();
        }
        return "";
    }

    public int getFavor() {
        if (isFavor) {
            return 0;
        }
        return 1;
    }

    public String getPosterId() {
        if (poster != null) {
            return poster.getId();
        }
        return "";
    }
}
