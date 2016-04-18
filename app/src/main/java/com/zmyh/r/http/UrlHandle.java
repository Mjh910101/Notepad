package com.zmyh.r.http;

public class UrlHandle {

    private static String RootIndex = "http://114.215.151.164:6688";

    public final static String getIndex() {
        return RootIndex;
    }

    /**
     * 注册
     *
     * @return
     */
    public static String getRegister() {
        return getIndex() + "/register";
    }

    /**
     * 忘记密码
     *
     * @return
     */
    public static String getForget() {
        return getIndex() + "/forget";
    }

    /**
     * get:获取用户列表 post:修改用户信息
     *
     * @return
     */
    public static String getMmUser() {
        return getIndex() + "/api/v1/mmUser";
    }

    /**
     * 登录
     *
     * @return
     */
    public static String getLogin() {
        return getIndex() + "/login";
    }

    /**
     * 登出
     *
     * @return
     */
    public static String getLogout() {
        return getIndex() + "/logout";
    }

    /**
     * post上传
     *
     * @return
     */
    public static String getMmTree() {
        return getIndex() + "/mmTree";
    }

    /**
     * 苗木字典
     *
     * @return
     */
    public static String getMmDict() {
        return getIndex() + "/mmDict";
    }

    /**
     * 删除图片
     *
     * @param id
     * @return
     */
    public static String getDeletePic(String mu_photo_key) {
        return getIndex() + "/mmTreePhoto/" + mu_photo_key;
    }

    /**
     * 删除多个图片
     *
     * @return
     */
    public static String getDeletePic() {
        return getIndex() + "/mmTreePhoto";
    }

    /**
     * 删除组
     *
     * @param mu_id
     * @return
     */
    public static String getDeleteTroop(String mu_id) {
        return getIndex() + "/mmTree/" + mu_id;
    }

    /**
     * 删除组
     *
     * @return
     */
    public static String getDeleteTroop() {
        return getIndex() + "/mmTree";
    }

    /**
     * 云端
     *
     * @return
     */
    public static String getMmTreeHqPhoto() {
        return getIndex() + "/mmTreeHqPhoto";
    }

    /**
     * 内容数据接口
     *
     * @return
     */
    public static String getMmPost() {
        return getIndex() + "/api/v1/mmpost";
    }

    /**
     * 获取分享朋友圈地址
     *
     * @return
     */
    public static String getMmShare() {
        return getIndex() + "/mmShare";
    }

    /**
     * put内容详细
     *
     * @param id
     * @return
     */
    public static String getMmPost(String id) {
        return getMmPost() + "/" + id;
    }

    /**
     * 首页频道列表
     *
     * @return
     */
    public static String getChannel() {
        return getIndex() + "/api/v1/channel";
    }

    /**
     * 全国地区接口
     *
     * @return
     */
    public static String getMmArea() {
        return getIndex() + "/api/v1/mmarea";
    }

    /**
     * 内容评论数据接口
     * <p/>
     * post:发布评论 get:获取发布评论
     *
     * @return
     */
    public static String getMmPostComment() {
        return getIndex() + "/api/v1/mmPostComment";
    }

    /**
     * 内容收藏数据接口
     * <p/>
     * post:收藏 get:获取收藏
     *
     * @return
     */
    public static String getMmPostFavor() {
        return getIndex() + "/api/v1/mmPostFavor";
    }

    /**
     * 举报文章
     *
     * @return
     */
    public static String getMmReport() {
        return getIndex() + "/api/v1/mmReport";
    }

    /**
     * 下载分享
     *
     * @return
     */
    public static String getWeiXinUrl() {
        return getIndex() + "/dl";
    }

    /**
     * 帮助
     *
     * @return
     */
    public static String getHelpUrl() {
        return getIndex() + "/mm_help";
    }

    /**
     * 关于我们
     *
     * @return
     */
    public static String getAboutusUrl() {
        return getIndex() + "/mm_aboutus";
    }

    /**
     * post，	isBlock 0:列为好友；1：列表黑名单
     * DELETE，删除用户关系
     * 朋友
     *
     * @return
     */
    public static String getMmFriend() {
        return getIndex() + "/api/v1/mmFriend";
    }

    /**
     * get 查看用户相册
     * post 用户相册上传
     * delete 删除用户相册
     *
     * @return
     */
    public static String getMmUserAlbum() {
        return getIndex() + "/api/v1/mmUserAlbum";
    }

    public static String getMmUserTag() {
        return getIndex() + "/api/v1/static/user/tag";
    }

    public static String getVersion() {
        return getIndex() + "/mm_version";
    }
}
