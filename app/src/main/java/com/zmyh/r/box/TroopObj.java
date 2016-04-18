package com.zmyh.r.box;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NotNull;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;
import com.zmyh.r.handler.DateHandle;
import com.zmyh.r.http.HttpFlieBox;

@Table(name = "troop")
public class TroopObj {

    @Transient
    private List<CameraPicObj> cameraPicObjList;

    @Id(column = "mu_id")
    private String mu_id;// 植物ID : mu_id *

    @Column(column = "mu_name")
    private String mu_name = "";// 植物名称: mu_name *

    @Column(column = "mu_desc")
    private String mu_desc = "";// 描述 : mu_desc

    @Column(column = "mu_contact")
    private String mu_contact = "";// 联系人 : mu_contact

    @Column(column = "mu_phone_1")
    private String mu_phone_1 = "";// 电话1 : mu_phone_1

    @Column(column = "mu_phone_2")
    private String mu_phone_2 = "";// 电话2 : mu_phone_2

    @Column(column = "mu_sz_type")
    private String mu_sz_type = "";// 树种类别 : mu_sz_type

    @Column(column = "mu_j_min")
    private String mu_j_min = "";// 胸径/地径 最小: mu_j_min

    @Column(column = "mu_j_max")
    private String mu_j_max = "";// 胸径/地径 最大: mu_j_max

    @Column(column = "mu_zg_min")
    private String mu_zg_min = "";// 株高 min: mu_zg_min,

    @Column(column = "mu_zg_max")
    private String mu_zg_max = "";// 株高 max : mu_zg_max

    @Column(column = "mu_gf_min")
    private String mu_gf_min = "";// 冠幅 min: mu_gf_min

    @Column(column = "mu_gf_max")
    private String mu_gf_max = "";// 冠幅 max: mu_gf_max

    @Column(column = "mu_type")
    private String mu_type = "";// 苗木类别 : mu_type

    @Column(column = "mu_jz_time")
    private String mu_jz_time = "";// 嫁植时间 : mu_jz_time

    @Column(column = "mu_total")
    private String mu_total = "";// 苗木数量 : mu_total

    @Column(column = "mu_price")
    private String mu_price = "";// 苗木价格 : mu_price

    @Column(column = "mu_zb")
    private String mu_zb = "";// 地标 : mu_zb

    @Column(column = "mu_latitude")
    private double mu_latitude = 0;

    @Column(column = "mu_longitude")
    private double mu_longitude = 0;

    @Column(column = "mu_createTime")
    private long mu_createTime;

    @Column(column = "mu_lastUpdateTime")
    private long mu_lastUpdateTime;

    @Column(column = "mu_photo")
    private String mu_photo = "";

    @Column(column = "is_upload")
    private boolean isUpload = false;

    @Column(column = "is_show")
    private boolean isShow = true;

    @Column(column = "is_online")
    private boolean isOnline = false;

    @Column(column = "mu_from")
    private String mu_from = "";

    @Column(column = "mu_gs")
    private String mu_gs = "";

    @Column(column = "video_file")
    private String video_file = "null";

    public boolean isVideoExists() {
        if (video_file.equals("")) {
            return false;
        }
        if (video_file.equals("null")) {
            return false;
        }
        return getVideoFile().exists();
    }

    public File getVideoFile() {
        return new File(HttpFlieBox.getImagePath() + "/" + video_file);
    }

    public String getVideoName() {
        return video_file;
    }

    public void setVideoName(String video_file) {
        this.video_file = video_file;
    }

    public String getMu_gs() {
        return mu_gs;
    }

    public void setMu_gs(String mu_gs) {
        this.mu_gs = mu_gs;
    }

    public String getMu_id() {
        return mu_id;
    }

    public void setMu_id(String mu_id) {
        this.mu_id = mu_id;
    }

    public void initMu_id() {
        DecimalFormat df = new DecimalFormat("0000");
        int r = new Random().nextInt(10000);
        mu_id = DateHandle.format(mu_createTime * 1000, DateHandle.DATESTYP_2)
                + df.format(r);
    }

    public String getMu_name() {
        if (mu_name == null || mu_name.equals("") || mu_name.equals("null")) {
            return "未命名";
        }
        return mu_name;
    }

    public void setMu_name(String mu_name) {
        this.mu_name = mu_name;
    }

    public String getMu_desc() {
        return mu_desc;
    }

    public void setMu_desc(String mu_desc) {
        this.mu_desc = mu_desc;
    }

    public String getMu_contact() {
        return mu_contact;
    }

    public void setMu_contact(String mu_contact) {
        this.mu_contact = mu_contact;
    }

    public String getMu_phone_1() {
        return mu_phone_1;
    }

    public void setMu_phone_1(String mu_phone_1) {
        this.mu_phone_1 = mu_phone_1;
    }

    public String getMu_phone_2() {
        return mu_phone_2;
    }

    public void setMu_phone_2(String mu_phone_2) {
        this.mu_phone_2 = mu_phone_2;
    }

    public String getMu_sz_type() {
        return mu_sz_type;
    }

    public void setMu_sz_type(String mu_sz_type) {
        this.mu_sz_type = mu_sz_type;
    }

    public String getMu_j_min() {
        return mu_j_min;
    }

    public void setMu_j_min(String mu_j_min) {
        this.mu_j_min = mu_j_min;
    }

    public String getMu_j_max() {
        return mu_j_max;
    }

    public void setMu_j_max(String mu_j_max) {
        this.mu_j_max = mu_j_max;
    }

    public String getMu_zg_min() {
        return mu_zg_min;
    }

    public void setMu_zg_min(String mu_zg_min) {
        this.mu_zg_min = mu_zg_min;
    }

    public String getMu_zg_max() {
        return mu_zg_max;
    }

    public void setMu_zg_max(String mu_zg_max) {
        this.mu_zg_max = mu_zg_max;
    }

    public String getMu_gf_min() {
        return mu_gf_min;
    }

    public void setMu_gf_min(String mu_gf_min) {
        this.mu_gf_min = mu_gf_min;
    }

    public String getMu_gf_max() {
        return mu_gf_max;
    }

    public void setMu_gf_max(String mu_gf_max) {
        this.mu_gf_max = mu_gf_max;
    }

    public String getMu_type() {
        return mu_type;
    }

    public void setMu_type(String mu_type) {
        this.mu_type = mu_type;
    }

    public String getMu_jz_time() {
        return mu_jz_time;
    }

    public void setMu_jz_time(String mu_jz_time) {
        this.mu_jz_time = mu_jz_time;
    }

    public String getMu_total() {
        return mu_total;
    }

    public void setMu_total(String mu_total) {
        this.mu_total = mu_total;
    }

    public String getMu_price() {
        return mu_price;
    }

    public void setMu_price(String mu_price) {
        this.mu_price = mu_price;
    }

    public String getMu_zb() {
        if (mu_zb == null || mu_zb.equals("")) {
            if (cameraPicObjList != null && !cameraPicObjList.isEmpty()) {
                return cameraPicObjList.get(0).getMu_zb();
            }
        }
        return mu_zb;
    }

    public void setMu_zb(String mu_zb) {
        this.mu_zb = mu_zb;
    }

    public double getMu_latitude() {
        if (mu_latitude <= 0) {
            if (cameraPicObjList != null && !cameraPicObjList.isEmpty()) {
                return cameraPicObjList.get(0).getMu_latitude();
            }
        }
        return mu_latitude;
    }

    public void setMu_latitude(double mu_latitude) {
        this.mu_latitude = mu_latitude;
    }

    public double getMu_longitude() {
        if (mu_longitude <= 0) {
            if (cameraPicObjList != null && !cameraPicObjList.isEmpty()) {
                return cameraPicObjList.get(0).getMu_longitude();
            }
        }
        return mu_longitude;
    }

    public void setMu_longitude(double mu_longitude) {
        this.mu_longitude = mu_longitude;
    }

    public long getMu_createTime() {
        return mu_createTime;
    }

    public String getCreateTime() {
        return DateHandle.format(getMu_createTime() * 1000,
                DateHandle.DATESTYP_10);
    }

    public void setMu_createTime(long mu_createTime) {
        this.mu_createTime = mu_createTime;
    }

    public long getMu_lastUpdateTime() {
        return mu_lastUpdateTime;
    }

    public void setMu_lastUpdateTime(long mu_lastUpdateTime) {
        this.mu_lastUpdateTime = mu_lastUpdateTime;
    }

    public String getMu_photo() {
        if (cameraPicObjList != null) {
            StringBuffer ids = new StringBuffer();
            for (CameraPicObj obj : cameraPicObjList) {
                ids.append(obj.getId());
                ids.append("|");
            }
            return ids.toString().substring(0, ids.length() - 1);
        }
        // return mu_photo;
        return mu_photo;
    }

    public void setMu_photo(String mu_photo) {
        this.mu_photo = mu_photo;
    }

    public List<CameraPicObj> getCameraPicObjList() {
        if (cameraPicObjList == null) {
            cameraPicObjList = new ArrayList<CameraPicObj>();
        }
        return cameraPicObjList;
    }

    public void setCameraPicObjList(List<CameraPicObj> cameraPicObjList) {
        for (CameraPicObj obj : cameraPicObjList) {
            addChild(obj);
        }
    }

    public void addChild(CameraPicObj obj) {
        if (cameraPicObjList == null) {
            cameraPicObjList = new ArrayList<CameraPicObj>();
        }
        obj.setMu_id(mu_id);
        cameraPicObjList.add(obj);

    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }

    public String getMu_from() {
        return mu_from;
    }

    public void setMu_from(String mu_from) {
        this.mu_from = mu_from;
    }

    public static String getContent(TroopObj obj) {
        StringBuffer sb = new StringBuffer();
        if (!obj.getMu_gs().equals("")) {
            sb.append("苗木归属:");
            sb.append(obj.getMu_gs());
        }
        if (!obj.getMu_sz_type().equals("")) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("种树类别:");
            sb.append(obj.getMu_sz_type());
        }
        if (!obj.getMu_j_min().equals("") && !obj.getMu_j_max().equals("") && !obj.getMu_j_min().equals("0") && !obj.getMu_j_max().equals("0")) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("胸径/地径:");
            sb.append(obj.getMu_j_min());
            sb.append("-");
            sb.append(obj.getMu_j_max());
        }
        if (!obj.getMu_zg_min().equals("") && !obj.getMu_zg_max().equals("") && !obj.getMu_zg_min().equals("0") && !obj.getMu_zg_max().equals("0")) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("株高:");
            sb.append(obj.getMu_zg_min());
            sb.append("-");
            sb.append(obj.getMu_zg_max());
        }
        if (!obj.getMu_gf_min().equals("") && !obj.getMu_gf_max().equals("") && !obj.getMu_gf_min().equals("0") && !obj.getMu_gf_max().equals("0")) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("冠幅:");
            sb.append(obj.getMu_gf_min());
            sb.append("-");
            sb.append(obj.getMu_gf_max());
        }
        if (!obj.getMu_type().equals("")) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("苗木类别:");
            sb.append(obj.getMu_type());
        }
        if (!obj.getMu_jz_time().equals("")) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("嫁接时间:");
            sb.append(obj.getMu_jz_time());
        }
        if (!obj.getMu_total().equals("") && !obj.getMu_total().equals("0")) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("苗木数量:");
            sb.append(obj.getMu_total());
        }
        if (!obj.getMu_price().equals("") && !obj.getMu_price().equals("0")) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("苗木价格:");
            sb.append(obj.getMu_price());
        }
        return sb.toString();
    }

    public String getContent() {
        StringBuffer sb = new StringBuffer();
        if (!getMu_name().equals("") && !getMu_name().equals("null")) {
            sb.append("苗木名称:");
            sb.append(getMu_name());
            sb.append("\n");
        }
        if (!getMu_gs().equals("") && !getMu_gs().equals("null")) {
            sb.append("苗木归属:");
            sb.append(getMu_gs());
            sb.append("\n");
        }
        if (!getMu_desc().equals("") && !getMu_desc().equals("null")) {
            sb.append("描述:");
            sb.append(getMu_desc());
            sb.append("\n");
        }
        if (!getMu_contact().equals("") && !getMu_contact().equals("null")) {
            sb.append("联系人:");
            sb.append(getMu_contact());
            sb.append("\n");
        }
        if (!getMu_phone_1().equals("") && !getMu_phone_1().equals("null")) {
            sb.append("电话1:");
            sb.append(getMu_phone_1());
            sb.append("\n");
        }
        if (!getMu_phone_2().equals("") && !getMu_phone_2().equals("null")) {
            sb.append("电话2:");
            sb.append(getMu_phone_2());
            sb.append("\n");
        }
        if (!getMu_total().equals("") && !getMu_total().equals("null")
                && !getMu_total().equals("0")) {
            sb.append("苗木数量:");
            sb.append(getMu_total());
            sb.append("\n");
        }
        if (!getMu_price().equals("") && !getMu_price().equals("null")
                && !getMu_price().equals("0")) {
            sb.append("苗木价钱:");
            sb.append(getMu_price());
            sb.append("\n");
        }
        if (!getMu_j_max().equals("") && !getMu_j_max().equals("null")
                && !getMu_j_min().equals("") && !getMu_j_min().equals("null")) {
            sb.append("胸径/地径:");
            sb.append(getMu_j_min() + "-" + getMu_j_max());
            sb.append("\n");
        }
        if (!getMu_zg_max().equals("") && !getMu_zg_max().equals("null")
                && !getMu_zg_max().equals("") && !getMu_zg_max().equals("null")) {
            sb.append("株高:");
            sb.append(getMu_zg_max() + "-" + getMu_zg_max());
            sb.append("\n");
        }
        if (!getMu_gf_max().equals("") && !getMu_gf_max().equals("null")
                && !getMu_gf_min().equals("") && !getMu_gf_min().equals("null")) {
            sb.append("冠幅:");
            sb.append(getMu_gf_min() + "-" + getMu_gf_max());
            sb.append("\n");
        }
        if (!getMu_type().equals("") && !getMu_type().equals("null")) {
            sb.append("苗木类别:");
            sb.append(getMu_type());
            sb.append("\n");
        }
        if (!getMu_jz_time().equals("") && !getMu_jz_time().equals("null")) {
            sb.append("嫁植时间:");
            sb.append(getMu_jz_time());
        }
        return sb.toString();
    }

    public String getInfo() {
        StringBuffer sb = new StringBuffer();
        if (!getMu_gs().equals("") && !getMu_gs().equals("null")) {
            sb.append("苗木归属:");
            sb.append(getMu_gs());
            sb.append("|");
        }
        if (!getMu_j_max().equals("") && !getMu_j_max().equals("null")
                && !getMu_j_min().equals("") && !getMu_j_min().equals("null")) {
            sb.append("胸径/地径:");
            sb.append(getMu_j_min() + "-" + getMu_j_max());
            sb.append("|");
        }
        if (!getMu_zg_max().equals("") && !getMu_zg_max().equals("null")
                && !getMu_zg_max().equals("") && !getMu_zg_max().equals("null")) {
            sb.append("株高:");
            sb.append(getMu_zg_max() + "-" + getMu_zg_max());
            sb.append("|");
        }
        if (!getMu_gf_max().equals("") && !getMu_gf_max().equals("null")
                && !getMu_gf_min().equals("") && !getMu_gf_min().equals("null")) {
            sb.append("冠幅:");
            sb.append(getMu_gf_min() + "-" + getMu_gf_max());
            sb.append("|");
        }
        if (!getMu_type().equals("") && !getMu_type().equals("null")) {
            sb.append("苗木类别:");
            sb.append(getMu_type());
            sb.append("|");
        }
        if (!getMu_jz_time().equals("") && !getMu_jz_time().equals("null")) {
            sb.append("嫁植时间:");
            sb.append(getMu_jz_time());
        }
        return sb.toString();
    }

    public boolean isHavePeople() {
        if (getMu_phone_1().equals("")
                || getMu_phone_1().equals("null")) {
            return false;
        }
        if (getMu_contact().equals("")
                || getMu_contact().equals("null")) {
            return false;
        }
        return true;
    }

    public static boolean isIncomplete(TroopObj obj) {
        boolean incomplete = true;
        if (!obj.getMu_contact().equals("")
                && !obj.getMu_contact().equals("null")) {
            incomplete = false;
        }
        if (!obj.getMu_phone_1().equals("")
                && !obj.getMu_phone_1().equals("null")) {
            incomplete = false;
        }
        if (!obj.getMu_phone_2().equals("")
                && !obj.getMu_phone_2().equals("null")) {
            incomplete = false;
        }
        if (!obj.getMu_sz_type().equals("")
                && !obj.getMu_sz_type().equals("null")) {
            incomplete = false;
        }
        if (!obj.getMu_j_max().equals("") && !obj.getMu_j_max().equals("null")) {
            incomplete = false;
        }
        if (!obj.getMu_j_min().equals("") && !obj.getMu_j_min().equals("null")) {
            incomplete = false;
        }
        if (!obj.getMu_zg_max().equals("")
                && !obj.getMu_zg_max().equals("null")) {
            incomplete = false;
        }
        if (!obj.getMu_zg_max().equals("")
                && !obj.getMu_zg_max().equals("null")) {
            incomplete = false;
        }
        if (!obj.getMu_gf_max().equals("")
                && !obj.getMu_gf_max().equals("null")) {
            incomplete = false;
        }
        if (!obj.getMu_gf_min().equals("")
                && !obj.getMu_gf_min().equals("null")) {
            incomplete = false;
        }
        if (!obj.getMu_type().equals("") && !obj.getMu_type().equals("null")) {
            incomplete = false;
        }
        if (!obj.getMu_jz_time().equals("")
                && !obj.getMu_jz_time().equals("null")) {
            incomplete = false;
        }
        if (!obj.getMu_total().equals("") && !obj.getMu_total().equals("null")
                && !obj.getMu_total().equals("0")) {
            incomplete = false;
        }
        if (!obj.getMu_price().equals("") && !obj.getMu_price().equals("null")
                && !obj.getMu_price().equals("0")) {
            incomplete = false;
        }
        return incomplete;
    }

    public String toString() {
        return "mu_id : " + mu_id + " , mu_name : " + mu_name + " , mu_desc : "
                + mu_desc + " , mu_contact : " + mu_contact
                + " , mu_phone_1 : " + mu_phone_1 + " , mu_phone_2 : "
                + mu_phone_2 + " , mu_sz_type : " + mu_sz_type
                + " , mu_j_min : " + mu_j_min + " , mu_j_max" + mu_j_max
                + " , mu_zg_min : " + mu_zg_min + " , mu_zg_max : " + mu_zg_max
                + " , mu_gf_min : " + mu_gf_min + " , mu_gf_max : " + mu_gf_max
                + " , mu_type : " + mu_type + " , mu_jz_time : " + mu_jz_time
                + " , mu_total : " + mu_total + " , mu_price : " + mu_price
                + " , mu_zb : " + mu_zb + " , mu_latitude : " + mu_latitude
                + " , mu_longitude : " + mu_longitude + " , mu_createTime : "
                + mu_createTime + " , mu_lastUpdateTime : " + mu_lastUpdateTime
                + " , mu_photo : " + mu_photo + " , isUpload : " + isUpload
                + " , isOnline : " + isOnline + " , isShow : " + isShow
                + " , mu_gs : " + mu_gs + " , Video File : " + video_file;
    }
}
