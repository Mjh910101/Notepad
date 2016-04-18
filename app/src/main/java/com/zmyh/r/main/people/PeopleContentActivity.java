package com.zmyh.r.main.people;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.exceptions.EaseMobException;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.box.UserImageObj;
import com.zmyh.r.box.UserObj;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.easemob.activitys.ChatActivity;
import com.zmyh.r.easemob.utils.UserUtils;
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.UserImageObjHandler;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.main.user.UserImageBaseAdapter;
import com.zmyh.r.photo.ImageListAcitvity;
import com.zmyh.r.tool.LineViewTool;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;
import com.zmyh.r.view.InsideGridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Hua on 15/7/13.
 */
public class PeopleContentActivity extends Activity {

    private Context context;
    private UserObj mUserObj;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.people_content_progress)
    private ProgressBar progress;
    @ViewInject(R.id.people_content_userName)
    private TextView userName;
    @ViewInject(R.id.people_content_userAuthTag)
    private ImageView userAuthTag;
    @ViewInject(R.id.people_content_userTag)
    private LinearLayout userTagBox;
    @ViewInject(R.id.people_content_userPic)
    private ImageView userPic;
    @ViewInject(R.id.people_content_userDescription)
    private TextView userDescription;
    @ViewInject(R.id.people_content_userPhoto)
    private TextView userPhoto;
    @ViewInject(R.id.people_content_userMail)
    private TextView userMail;
    @ViewInject(R.id.people_content_userQQ)
    private TextView userQQ;
    @ViewInject(R.id.people_content_userCorporation)
    private TextView userCorporation;
    @ViewInject(R.id.people_content_cityText)
    private TextView cityText;
    @ViewInject(R.id.people_content_cardBox)
    private RelativeLayout cardBox;
    @ViewInject(R.id.people_content_card)
    private ImageView cardPic;
    @ViewInject(R.id.people_content_cardText)
    private TextView cardText;
    @ViewInject(R.id.people_content_notFriendTool)
    private LinearLayout notFriendTool;
    @ViewInject(R.id.people_content_friendTool)
    private LinearLayout friendTool;
    @ViewInject(R.id.people_content_addBlack_1)
    private TextView addBlack_1;
    @ViewInject(R.id.people_content_addBlack_2)
    private TextView addBlack_2;
    @ViewInject(R.id.people_content_imageGridBox)
    private LinearLayout imageGridBox;
    @ViewInject(R.id.people_content_imageGrid)
    private InsideGridView imageGrid;
    @ViewInject(R.id.people_content_deleteFriend)
    private TextView deleteFriend;
    @ViewInject(R.id.people_content_userDescriptioBox)
    private LinearLayout userDescriptioBox;
    @ViewInject(R.id.people_content_toolBox)
    private LinearLayout toolBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_content);
        ViewUtils.inject(this);
        context = this;

        initActivity();

    }

    @OnClick({R.id.title_back, R.id.people_content_addFriend, R.id.people_content_addBlack_1, R.id.people_content_addBlack_2, R.id.people_content_deleteFriend, R.id.people_content_phoneIcon
            , R.id.people_content_send_1, R.id.people_content_send_2, R.id.people_content_card})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.people_content_addFriend:
                friendBtn();
                break;
            case R.id.people_content_addBlack_1:
            case R.id.people_content_addBlack_2:
                blackBtn();
                break;
            case R.id.people_content_deleteFriend:
                deleteRelation();
                break;
            case R.id.people_content_phoneIcon:
                tellBtn();
                break;
            case R.id.people_content_send_1:
            case R.id.people_content_send_2:
                if (UserObjHandle.isLogin(context, true)) {
                    sendPeople(mUserObj);
                }
                break;
            case R.id.people_content_card:
                Bundle b = new Bundle();
                b.putStringArrayList("iamge_list", getImagelist(mUserObj));
                b.putInt("position", 0);
                b.putBoolean("isOnline", true);
                Passageway.jumpActivity(context, ImageListAcitvity.class, b);
                break;
        }

    }

    private ArrayList<String> getImagelist(UserObj obj) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(obj.getM_CallingCard());
        return list;
    }

    private void sendPeople(UserObj mUserObj) {
        Intent intent = new Intent(context, ChatActivity.class);
//        intent.putExtra("chatType", ChatActivity.CHATTYPE_CHATROOM);
        intent.putExtra("userId", mUserObj.getId());
        startActivity(intent);
    }

    private void tellBtn() {
        Uri uri = Uri.parse("tel:" + mUserObj.getM_mobile());
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(intent);
    }

    private void blackBtn() {
        if (UserObjHandle.isLogin(context, true)) {
            if (mUserObj.isBlock()) {
                deleteRelation();
            } else {
                String url = UrlHandle.getMmFriend();
                RequestParams params = HttpUtilsBox.getRequestParams(context);
                params.addBodyParameter("user_id", UserObjHandle.getUsetId(context));
                params.addBodyParameter("friend_id", mUserObj.getId());
                params.addBodyParameter("isBlock", "1");
                uploadData(HttpRequest.HttpMethod.POST, url, params, new CallbackForBoolean() {
                    @Override
                    public void callback(boolean b) {
                        if (b) {
                            mUserObj.setIsBlock(1);
                            mUserObj.setIsFriend(0);
                            showFriendTool(mUserObj);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        EMContactManager.getInstance().addUserToBlackList(mUserObj.getId(), true);
                                    } catch (EaseMobException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();

                        }
                    }
                });
            }
        }
    }

    private void friendBtn() {
        if (UserObjHandle.isLogin(context, true)) {
            String url = UrlHandle.getMmFriend();
            RequestParams params = HttpUtilsBox.getRequestParams(context);
            params.addBodyParameter("user_id", UserObjHandle.getUsetId(context));
            params.addBodyParameter("friend_id", mUserObj.getId());
            params.addBodyParameter("isBlock", "0");
            uploadData(HttpRequest.HttpMethod.POST, url, params, new CallbackForBoolean() {
                @Override
                public void callback(boolean b) {
                    if (b) {
                        mUserObj.setIsBlock(0);
                        mUserObj.setIsFriend(1);
                        showFriendTool(mUserObj);

                    }
                }
            });
        }
    }

    private void deleteRelation() {
        if (UserObjHandle.isLogin(context, true)) {
            String url = UrlHandle.getMmFriend() + "?friend_id=" + mUserObj.getId() + "&user_id=" + UserObjHandle.getUsetId(context);
            RequestParams params = HttpUtilsBox.getRequestParams(context);
            uploadData(HttpRequest.HttpMethod.DELETE, url, params, new CallbackForBoolean() {
                @Override
                public void callback(boolean b) {
                    if (b) {
                        mUserObj.setIsBlock(0);
                        mUserObj.setIsFriend(0);
                        showFriendTool(mUserObj);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    EMContactManager.getInstance().deleteUserFromBlackList(mUserObj.getId());
                                } catch (EaseMobException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            });
        }
    }

    private void initActivity() {
        titleName.setText("个人信息");

        if (UserObjHandle.isLogin(context)) {
            toolBox.setVisibility(View.VISIBLE);
        } else {
            toolBox.setVisibility(View.GONE);
        }

        Bundle b = getIntent().getExtras();
        if (b != null) {
            String id = b.getString("id");
            downloadData(id);
            downloadImage(id);
        }
    }

    private void setContent(UserObj obj) {
        userName.setText(obj.getM_nick_name());
        userPhoto.setText(obj.getM_mobile());
        userMail.setText(obj.getEmail());
        userQQ.setText(obj.getQq());
        userCorporation.setText(obj.getCompany());
        cityText.setText(obj.getUserCity());
        setuserDescriptio(obj);
        setUserAuthTag(obj);
        setUserTag(obj);
        setUserPic(obj);
        setCardBox(obj);
        showFriendTool(obj);
    }

    private void setuserDescriptio(UserObj obj) {
        String str = obj.getM_description();
        if (!str.equals("")) {
            userDescription.setText(str);
        } else {
            userDescriptioBox.setVisibility(View.GONE);
        }
    }

    private void showFriendTool(UserObj obj) {
        if (obj.isFriend() || obj.isBlock()) {
            notFriendTool.setVisibility(View.GONE);
            friendTool.setVisibility(View.VISIBLE);
            if (obj.isBlock()) {
                addBlack_2.setText("取消屏蔽");
                deleteFriend.setVisibility(View.GONE);
            } else {
                addBlack_2.setText("屏蔽他");
                deleteFriend.setVisibility(View.VISIBLE);
            }
        } else {
            friendTool.setVisibility(View.GONE);
            notFriendTool.setVisibility(View.VISIBLE);
            if (obj.isBlock()) {
                addBlack_1.setText("取消屏蔽");
            } else {
                addBlack_1.setText("屏蔽他");
            }
        }
    }

    private void setCardBox(UserObj obj) {
        double w = WinTool.getWinWidth(context) - WinTool.dipToPx(context, 10);
        double h = w / 209 * 119;
        cardBox.setLayoutParams(new LinearLayout.LayoutParams((int) w, (int) h));
        String cardUrl = obj.getM_CallingCard();
        if (!cardUrl.equals("")) {
            cardPic.setVisibility(View.VISIBLE);
            DownloadImageLoader.loadImage(cardPic, cardUrl);
            cardText.setVisibility(View.INVISIBLE);
        }
    }

    private void setUserPic(UserObj obj) {
        int w = WinTool.getWinWidth(context) / 4;
        userPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
        DownloadImageLoader.loadImage(userPic, obj.getM_avatar(), w / 2);
    }

    private void setUserAuthTag(UserObj obj) {
        userAuthTag.setVisibility(View.GONE);
        String authTag = obj.getM_auth_tag();
        if (authTag.equals("知名")) {
            userAuthTag.setImageResource(R.drawable.zhiming_icomn);
            userAuthTag.setVisibility(View.VISIBLE);
        } else if (authTag.equals("实名")) {
            userAuthTag.setImageResource(R.drawable.shiming_icon);
            userAuthTag.setVisibility(View.VISIBLE);
        }
    }

    private void setUserTag(UserObj obj) {
        String[] tags = obj.getM_tag();
        if (tags != null) {
            for (int i = 0; i < tags.length; i++) {
                userTagBox.addView(getTagView(tags[i]));
                userTagBox.addView(getReplaceView());
            }
        }
    }

    private View getReplaceView() {
        return LineViewTool.getHorizontalSpaceView(context);
    }

    private TextView getTagView(String tag) {
        TextView view = new TextView(context);
        view.setText(tag);
        view.setPadding(6, 3, 6, 3);
        view.setBackgroundResource(R.drawable.green_background_green_frame_0);
        view.setTextColor(ColorBox.getColorForID(context, R.color.green_01));
        return view;
    }

    private void setUserImage(List<UserImageObj> list) {
        if (!list.isEmpty()) {
            UserImageBaseAdapter uiba = new UserImageBaseAdapter(context, list);
            uiba.setShowDeleteBtn(false);
            imageGrid.setAdapter(uiba);
        } else {
            imageGridBox.setVisibility(View.GONE);
        }
    }

    private void downloadData(String id) {
        progress.setVisibility(View.VISIBLE);
        String url = UrlHandle.getMmUser() + "/" + id + "?user_id=" + UserObjHandle.getUsetId(context);

        RequestParams params = HttpUtilsBox.getRequestParams(context);

        HttpUtilsBox.getHttpUtil().send(HttpRequest.HttpMethod.PUT, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        progress.setVisibility(View.GONE);
                        ShowMessage.showFailure(context);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {

                        progress.setVisibility(View.GONE);
                        String result = responseInfo.result;
                        Log.d("", result);
                        JSONObject json = JsonHandle.getJSON(result);
                        if (json != null) {
                            JSONObject userJson = JsonHandle.getJSON(json, "o");
                            if (userJson != null) {
                                mUserObj = UserObjHandle.getUserBox(userJson);
                                setContent(mUserObj);
                                if (UserObjHandle.isLogin(context)) {
                                    UserUtils.saveUserInfo(mUserObj);
                                }
                            }
                        }
                    }

                });
    }

    private void downloadImage(String id) {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getMmUserAlbum() + "?query=" + JsonHandle.getHttpJsonToString(new String[]{"user_id"}, new String[]{id}) + "&p=1";

        RequestParams params = HttpUtilsBox.getRequestParams(context);

        HttpUtilsBox.getHttpUtil().send(HttpRequest.HttpMethod.GET, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        progress.setVisibility(View.GONE);
                        ShowMessage.showFailure(context);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        progress.setVisibility(View.GONE);
                        String result = responseInfo.result;
                        Log.d("", result);
                        JSONObject json = JsonHandle.getJSON(
                                JsonHandle.getJSON(result), "result");
                        if (json != null) {
                            JSONArray array = JsonHandle.getArray(json, "data");
                            if (array != null) {
                                List<UserImageObj> list = UserImageObjHandler.getUserImageObjList(array);
                                setUserImage(list);
                            }
                        }
                    }

                });
    }

    private void uploadData(HttpRequest.HttpMethod httpMethod, String url, RequestParams params, final CallbackForBoolean callback) {
        progress.setVisibility(View.VISIBLE);
        HttpUtilsBox.getHttpUtil().send(httpMethod, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        progress.setVisibility(View.GONE);
                        ShowMessage.showFailure(context);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        progress.setVisibility(View.GONE);
                        String result = responseInfo.result;
                        Log.d("", result);
                        JSONObject json = JsonHandle.getJSON(
                                JsonHandle.getJSON(result), "result");
                        if (json != null) {
                            JSONObject oJson = JsonHandle.getJSON(json, "o");
                            if (oJson != null) {
                                callback.callback(isOperation(oJson));
                            } else {
                                callback.callback(isOperation(json));
                            }
                        }
                    }

                    private boolean isOperation(JSONObject json) {
                        if (JsonHandle.getInt(json, "ok") == 1) {
                            ShowMessage.showToast(context, "操作成功");
                            return true;
                        } else {
                            ShowMessage.showToast(context, "操作失败");
                            return false;
                        }
                    }

                });

    }

}
