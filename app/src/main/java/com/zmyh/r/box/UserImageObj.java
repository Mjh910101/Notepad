package com.zmyh.r.box;

/**
 * Created by Hua on 15/7/15.
 */
public class UserImageObj {

    public final static String ID = "_id";
    public final static String CREATE_AT = "createAt";
    public final static String IMG = "img";

    private String id;
    private String img;
    private long createAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        if(img==null){
            return "";
        }
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public boolean isNull(){
        return getImg().equals("");
    }
}
