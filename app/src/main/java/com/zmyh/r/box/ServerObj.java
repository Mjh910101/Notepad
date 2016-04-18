package com.zmyh.r.box;

import java.util.List;

import android.util.Log;

import com.zmyh.r.handler.DateHandle;

import org.json.JSONObject;

public class ServerObj {

    public final static String ID = "_id";
    public final static String POST_THUMBNAIL = "post_thumbnail";
    public final static String TYPE = "type";
    public final static String TITLE = "title";
    public final static String PRSTER = "poster";
    public final static String CONTENT = "content";
    public final static String CREATE_AT = "createAt";
    public final static String FAVOR_COUNT = "favor_count";
    public final static String COMMENT_COUNT = "comment_count";
    public final static String PIC = "pic";
    public final static String MM_AREA = "mmArea";
    public final static String HOT = "hot";
    public final static String INTRO = "intro";
    public final static String MM_CHANNEL = "mmChannel";
    public final static String ADDRESS = "address";
    public final static String NAME = "name";
    public final static String PHONE = "phone";
    public final static String MU_TYPE = "mu_type";
    public final static String MU_GF = "mu_gf";
    public final static String MU_PRICE = "mu_price";
    public final static String MU_SZ_TYPE = "mu_sz_type";
    public final static String MU_JZ_TIME = "mu_jz_time";
    public final static String MU_TOTAL = "mu_total";
    public final static String MU_ZG = "mu_zg";
    public final static String MU_J = "mu_j";
    public final static String TREE = "tree";
    public final static String CONTACT_INFFO = "contact_info";
    public final static String IS_FAVOR = "isFavor";
    public final static String MU_COORDINATE_LAT = "mu_coordinate_lat";
    public final static String MU_COORDINATE_LONG = "mu_coordinate_long";
    public final static String MU_ZB = "mu_zb";
    public final static String ExpireDate = "expireDate";

    private String id;
    private String post_thumbnail;
    private String type;
    private String title;
    private UserObj poster;
    private String content;
    private long createAt;
    private int favor_count;
    private int comment_count;
    private List<String> pic;
    private CityObj mmArea;
    private int hot;
    private String intro;
    private ChannelObj mmChannel;
    private String address;
    private String name;
    private String phone;
    private String mu_type;
    private String mu_gf;
    private String mu_price;
    private String mu_sz_type;
    private String mu_jz_time;
    private String mu_total;
    private String mu_zg;
    private String mu_j;
    private boolean isFavor;
    private double mu_coordinate_lat;
    private double mu_coordinate_long;
    private int expireDate;
    private JSONObject json;

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public double getMu_coordinate_lat() {
        return mu_coordinate_lat;
    }

    public void setMu_coordinate_lat(double mu_coordinate_lat) {
        this.mu_coordinate_lat = mu_coordinate_lat;
    }

    public double getMu_coordinate_long() {
        return mu_coordinate_long;
    }

    public void setMu_coordinate_long(double mu_coordinate_long) {
        this.mu_coordinate_long = mu_coordinate_long;
    }

    public int getExpireDate() {
        return expireDate;
    }

    public String getExpireText() {
        if (expireDate < 0) {
            return "无限期";
        }

        double l = ((double) ((System.currentTimeMillis() / 1000) - createAt)) / 60d / 60d / 24d;
        Log.e("", createAt + "aaaaaaaaaaaaaaaaaaa : " + (int) l);
        int d = expireDate - (int) l;
        if (d > 0) {
            return d + "天后截止";
        }

        return "已经过期";

    }

    public void setExpireDate(int expireDate) {
        this.expireDate = expireDate;
    }

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

    public String getPost_thumbnail() {
        return post_thumbnail;
    }

    public void setPost_thumbnail(String post_thumbnail) {
        this.post_thumbnail = post_thumbnail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UserObj getPoster() {
        return poster;
    }

    public void setPoster(UserObj poster) {
        this.poster = poster;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public int getFavor_count() {
        return favor_count;
    }

    public void setFavor_count(int favor_count) {
        this.favor_count = favor_count;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public List<String> getPic() {
        return pic;
    }

    public void setPic(List<String> pic) {
        this.pic = pic;
    }

    public CityObj getMmArea() {
        return mmArea;
    }

    public void setMmArea(CityObj mmArea) {
        this.mmArea = mmArea;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public ChannelObj getMmChannel() {
        return mmChannel;
    }

    public void setMmChannel(ChannelObj mmChannel) {
        this.mmChannel = mmChannel;
    }

    public String getMmChannelID() {
        if (mmChannel != null) {
            return mmChannel.getId();
        }
        return "";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMu_type() {
        if (mu_type == null) {
            return "";
        }
        return mu_type;
    }

    public void setMu_type(String mu_type) {
        this.mu_type = mu_type;
    }

    public String getMu_gf() {
        if (mu_gf == null) {
            return "";
        }
        return mu_gf;
    }

    public void setMu_gf(String mu_gf) {
        this.mu_gf = mu_gf;
    }

    public String getMu_price() {
        return mu_price;
    }

    public void setMu_price(String mu_price) {
        this.mu_price = mu_price;
    }

    public String getMu_sz_type() {
        if (mu_sz_type == null) {
            return "";
        }
        return mu_sz_type;
    }

    public void setMu_sz_type(String mu_sz_type) {
        this.mu_sz_type = mu_sz_type;
    }

    public String getMu_jz_time() {
        if (mu_jz_time == null) {
            return "";
        }
        return mu_jz_time;
    }

    public void setMu_jz_time(String mu_jz_time) {
        this.mu_jz_time = mu_jz_time;
    }

    public String getMu_total() {
        if (mu_total == null) {
            return "";
        }
        return mu_total;
    }

    public void setMu_total(String mu_total) {
        this.mu_total = mu_total;
    }

    public String getMu_zg() {
        if (mu_zg == null) {
            return "";
        }
        return mu_zg;
    }

    public void setMu_zg(String mu_zg) {
        this.mu_zg = mu_zg;
    }

    public String getMu_j() {
        if (mu_j == null) {
            return "";
        }
        return mu_j;
    }

    public void setMu_j(String mu_j) {
        this.mu_j = mu_j;
    }

    public String getCreateTime() {
        return DateHandle.format(createAt * 1000, DateHandle.DATESTYP_10);
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

    public String getInfo() {
        return getInfo(true);
    }

    public String getInfo(boolean isShowAddress) {
        StringBuffer sb = new StringBuffer();

        if (mu_sz_type != null && !mu_sz_type.equals("null")
                && !mu_sz_type.equals("")) {
            sb.append("树种类别:");
            sb.append(mu_sz_type);
            sb.append(" ");
        }
        if (mu_j != null && !mu_j.equals("null") && !mu_j.equals("")) {
            sb.append("胸径/地径:");
            sb.append(mu_j);
            sb.append(" ");
        }
        if (mu_zg != null && !mu_zg.equals("null") && !mu_zg.equals("")) {
            sb.append("株高:");
            sb.append(mu_zg);
            sb.append(" ");
        }
        if (mu_gf != null && !mu_gf.equals("null") && !mu_gf.equals("")) {
            sb.append("冠幅:");
            sb.append(mu_gf);
            sb.append(" ");
        }
        if (mu_type != null && !mu_type.equals("null") && !mu_type.equals("")) {
            sb.append("苗木类别:");
            sb.append(mu_type);
            sb.append(" ");
        }
        if (mu_jz_time != null && !mu_jz_time.equals("null")
                && !mu_jz_time.equals("")) {
            sb.append("嫁植时间:");
            sb.append(mu_jz_time);
        }
        sb.append("\n");
        if (mu_total != null && !mu_total.equals("null")
                && !mu_total.equals("")) {
            sb.append("苗木数量:");
            sb.append(mu_total);
            sb.append("\n");
        }
        if (mu_price != null && !mu_price.equals("null")
                && !mu_price.equals("")) {
            sb.append("苗木价格:");
            sb.append(mu_price);
            sb.append("\n");
        }
        if (name != null && !name.equals("null") && !name.equals("")) {
            sb.append("联系人:");
            sb.append(name);
            if (phone != null && !phone.equals("null") && !phone.equals("")) {
                sb.append("(");
                sb.append(phone);
                sb.append(")");
                sb.append("\n");
            }
        }
        if (isShowAddress) {
            if (address != null && !address.equals("null")
                    && !address.equals("")) {
                sb.append("地址:");
                sb.append(address);
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    public String getPriceMessage() {
        StringBuffer sb = new StringBuffer();
//		if (mu_price != null && !mu_price.equals("null")
//				&& !mu_price.equals("")) {
//			sb.append(mu_price+"元");
//
//		}
        try {
            double p = Double.valueOf(mu_price);
            if (p > 0) {
                sb.append(mu_price + "元");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//		if (mu_total != null && !mu_total.equals("null")
//				&& !mu_total.equals("")) {
//			sb.append("(");
//			sb.append(mu_total);
//			sb.append(")");
//		}
        try {
            double t = Double.valueOf(mu_total);
            if (t > 0) {
                sb.append("(数量");
                sb.append(mu_total);
                sb.append(")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public boolean isHaveCoordinate() {
        Log.e("", "mu_coordinate_lat : " + mu_coordinate_lat
                + " mu_coordinate_long : " + mu_coordinate_long);
        if (mu_coordinate_lat > 0 && mu_coordinate_long > 0) {
            return true;
        }
        return false;
    }

    public String getPosterId() {
        if (poster != null) {
            return poster.getId();
        }
        return "";
    }
}
