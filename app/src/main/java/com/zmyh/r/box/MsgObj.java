package com.zmyh.r.box;

public class MsgObj {

    public final static String POSTER = "poster";
    public final static String POST_ID = "post_id";
    public final static String CREATE_AT = "createAt";
    public final static String COMMENT = "comment";
    public final static String ID = "_id";
    public final static String MM_CHANNEL = "mmChannel";

    private String id;
    private UserObj poster;
    private String comment;
    private DynamicObj post;
    private ChannelObj mmChannel;
    private long createAt;

    public ChannelObj getMmChannel() {
        return mmChannel;
    }

    public void setMmChannel(ChannelObj mmChannel) {
        this.mmChannel = mmChannel;
    }

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

    public DynamicObj getPost() {
        return post;
    }

    public String getTitle() {
        if (post != null) {
            return post.getTitle();
        }
        return "";
    }

    public String getPost_thumbnail() {
        if (post != null) {
            return post.getPost_thumbnail();
        }
        return "";
    }

    public String getCity() {
        if (post != null) {
            return post.getCity();
        }
        return "";
    }

    public void setPost(DynamicObj post) {
        this.post = post;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

}
