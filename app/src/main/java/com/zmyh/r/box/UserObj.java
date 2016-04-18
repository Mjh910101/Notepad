package com.zmyh.r.box;

public class UserObj {

    public final static String V = "__v";
    public final static String M_MOBILE = "m_mobile";
    public final static String M_PASSWORD = "m_password";
    public final static String ID = "_id";
    public final static String CREATE_AT = "createAt";
    public final static String M_LAST_LOGIN = "m_lastLogin";
    public final static String M_AVATAR = "m_avatar";
    public final static String M_NICK_NAME = "m_nick_name";
    public final static String M_TAG = "m_tag";
    public final static String M_AUTH_TAG = "m_auth_tag";
    public final static String M_DESCRIPTION = "m_description";
    public final static String EMAIL = "email";
    public final static String QQ = "qq";
    public final static String COMPANY = "company";
    public final static String M_CALLINGCARD = "m_CallingCard";
    public final static String IS_FRIEND = "isFriend";
    public final static String IS_BLOCK = "isBlock";

    private String v;
    private String m_mobile;
    private String email;
    private String qq;
    private String m_password;
    private String id;
    private long createAt;
    private long m_lastLogin;
    private String m_avatar;
    private String m_nick_name;
    private String[] m_tag;
    private String m_auth_tag;
    private String m_description;
    private String company;
    private String m_CallingCard;
    private CityObj mmArea;
    private int isFriend;
    private int isBlock;

    public boolean isFriend() {
        return isFriend == 1;
    }

    public boolean isBlock() {
        return isBlock == 1;
    }

    public int getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(int isFriend) {
        this.isFriend = isFriend;
    }

    public int getIsBlock() {
        return isBlock;
    }

    public void setIsBlock(int isBlock) {
        this.isBlock = isBlock;
    }

    public CityObj getMmArea() {
        return mmArea;
    }

    public void setMmArea(CityObj mmArea) {
        this.mmArea = mmArea;
    }

    public String getM_CallingCard() {
        return m_CallingCard;
    }

    public void setM_CallingCard(String m_CallingCard) {
        this.m_CallingCard = m_CallingCard;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getM_description() {
        if(m_description==null){
            return "";
        }
        if(m_description.equals("null")){
            return "";
        }
        return m_description;
    }

    public void setM_description(String m_description) {
        this.m_description = m_description;
    }

    public String getM_nick_name() {
        if (m_nick_name == null || m_nick_name.equals("")) {
            return getM_mobile();
        }
        return m_nick_name;
    }

    public void setM_nick_name(String m_nick_name) {
        this.m_nick_name = m_nick_name;
    }

    public String[] getM_tag() {
        if (m_tag == null) {
            return new String[0];
        }
        return m_tag;
    }

    public void setM_tag(String[] m_tag) {
        this.m_tag = m_tag;
    }

    public String getM_auth_tag() {
        if (m_auth_tag == null) {
            return "";
        }
        return m_auth_tag;
    }

    public void setM_auth_tag(String m_auth_tag) {
        this.m_auth_tag = m_auth_tag;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getM_mobile() {
        return m_mobile;
    }

    public void setM_mobile(String m_mobile) {
        this.m_mobile = m_mobile;
    }

    public String getM_password() {
        return m_password;
    }

    public void setM_password(String m_password) {
        this.m_password = m_password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getM_lastLogin() {
        return m_lastLogin;
    }

    public void setM_lastLogin(long m_lastLogin) {
        this.m_lastLogin = m_lastLogin;
    }

    public String getM_avatar() {
        return m_avatar;
    }

    public void setM_avatar(String m_avatar) {
        this.m_avatar = m_avatar;
    }

    public CharSequence getUserCity() {
        if (mmArea != null) {
            return mmArea.getArea_name();
        }
        return "";
    }

}
