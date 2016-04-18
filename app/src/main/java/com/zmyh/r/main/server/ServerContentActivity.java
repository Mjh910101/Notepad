package com.zmyh.r.main.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.baidumap.TreeMapActivity;
import com.zmyh.r.baidumap.TreeMapActivityV2;
import com.zmyh.r.box.CommentObj;
import com.zmyh.r.box.ServerObj;
import com.zmyh.r.dailog.InputDialog;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.easemob.activitys.ChatActivity;
import com.zmyh.r.easemob.utils.UserUtils;
import com.zmyh.r.handler.CollectObjHandler;
import com.zmyh.r.handler.CommentObjHandler;
import com.zmyh.r.handler.CommentObjHandler.CommentCallbackListener;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.ServerObjHandler;
import com.zmyh.r.handler.SystemHandle;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.interfaces.CallbackForString;
import com.zmyh.r.main.CommentMessageView;
import com.zmyh.r.main.people.PeopleContentActivity;
import com.zmyh.r.photo.ImageListAcitvity;
import com.zmyh.r.tool.LineViewTool;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;
import com.zmyh.r.view.ResizeScrollView;
import com.zmyh.r.view.ResizeScrollView.OnResizeListener;

public class ServerContentActivity extends Activity {

    private Context context;

    private int pageIndex = 1, totalPage = 1;
    private ServerObj mServerObj;

    private InputMethodManager imm = null;

    private List<ImageView> ballList;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_seek)
    private ImageView seekIcon;
    @ViewInject(R.id.title_favor)
    private ImageView favorIcon;
    @ViewInject(R.id.server_content_userName)
    private TextView userName;
    @ViewInject(R.id.server_content_userPhone)
    private TextView userPhone;
    @ViewInject(R.id.server_content_viewPager)
    private ViewPager viewPager;
    @ViewInject(R.id.server_content_titleText)
    private TextView titleText;
    @ViewInject(R.id.server_content_timeText)
    private TextView timeText;
    @ViewInject(R.id.server_content_contentText)
    private TextView contentText;
    @ViewInject(R.id.server_content_infoText)
    private TextView infoText;
    @ViewInject(R.id.server_content_dataBox)
    private LinearLayout dataBox;
    @ViewInject(R.id.server_content_commentInput)
    private EditText commentInput;
    @ViewInject(R.id.server_content_scroll)
    private ResizeScrollView scroll;
    @ViewInject(R.id.server_content_progress)
    private ProgressBar progress;
    @ViewInject(R.id.server_content_viewPagerBox)
    private RelativeLayout viewPagerBox;
    @ViewInject(R.id.server_content_sizeBox)
    private LinearLayout sizeBox;
    @ViewInject(R.id.server_content_addressText)
    private TextView addressText;
    @ViewInject(R.id.server_content_address)
    private LinearLayout address;
    @ViewInject(R.id.server_content_maneyBox)
    private LinearLayout maneyBox;
    @ViewInject(R.id.server_content_maneyText)
    private TextView maneyText;
    @ViewInject(R.id.server_content_sumText)
    private TextView sumText;
    @ViewInject(R.id.server_content_depositText)
    private TextView depositText;
    @ViewInject(R.id.server_content_numberText)
    private TextView numberText;

    private int sum = 0;
    private double deposit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_content);
        ViewUtils.inject(this);
        context = this;

        initActivity();
        setResizeListener();
        setContentBoxListener();
    }

    @OnClick({R.id.title_back, R.id.title_favor, R.id.server_content_sendBtn,
            R.id.server_content_reportBtn, R.id.server_content_phoneBtn,
            R.id.server_content_sendMessageBtn, R.id.server_content_userBox,
            R.id.server_content_sendLetterBtn, R.id.server_content_address,
            R.id.server_content_subText, R.id.server_content_addText,
            R.id.server_content_numberText, R.id.server_content_payBtn})
    public void onClick(View v) {
        Bundle b = new Bundle();
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_favor:
                sendCollect();
                break;
            case R.id.server_content_sendBtn:
                sendComment();
                break;
            case R.id.server_content_reportBtn:
                b.putString("id", mServerObj.getId());
                Passageway.jumpActivity(context, ReportActivity.class, b);
                break;
            case R.id.server_content_phoneBtn:
                callUp();
                break;
            case R.id.server_content_sendMessageBtn:
                sendMessage();
                break;
            case R.id.server_content_sendLetterBtn:
//				ShowMessage.showToast(context, "暂未开放此功能");
                jumpEChat();
                break;
            case R.id.server_content_address:
                jimpMapActivity();
                break;
            case R.id.server_content_userBox:
                b.putString("id", mServerObj.getPosterId());
                Passageway.jumpActivity(context, PeopleContentActivity.class, b);
                break;
            case R.id.server_content_subText:
                setManey(-1);
                break;
            case R.id.server_content_addText:
                setManey(1);
                break;
            case R.id.server_content_numberText:
                showInputDialog();
                break;
            case R.id.server_content_payBtn:
                jumpPayActivity();
                break;
        }
    }

    private void showInputDialog() {
        InputDialog dialog = new InputDialog(context);
        dialog.setIntutType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        dialog.setListener(new CallbackForString() {
            @Override
            public void callback(String result) {
                try {
                    int inputSum = Integer.valueOf(result);
                    sum = 0;
//                    numberText.setText("0");
                    if (!setManey(inputSum)) {
                        setManey(1);
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    private void jumpPayActivity() {
        Bundle b = new Bundle();
        b.putInt("sum", sum);
        b.putString("id", mServerObj.getId());
        b.putString("message", "数量"+sum + " " + mServerObj.getTitle() + " 订金");
        b.putString("smsto", mServerObj.getPhone());
        b.putString("sms_body", "您好，我已通过【中苗花木】购买了您的“" + mServerObj.getTitle() + "”，已付订金" + deposit + "元，稍后我将直接联系您进行后续交易。");
        b.putDouble(PayActivity.TOPUP_MONEY, deposit);
        Passageway.jumpActivity(context, PayActivity.class, b);
    }

    private void jumpEChat() {
        UserUtils.saveUserInfo(mServerObj.getPoster());
        Intent intent = new Intent(context, ChatActivity.class);
//        intent.putExtra("chatType", ChatActivity.CHATTYPE_CHATROOM);
        intent.putExtra("userId", mServerObj.getPosterId());
        startActivity(intent);
    }

    private void jimpMapActivity() {
        ServerObjBox.saveServerObj(mServerObj);
        Bundle b = new Bundle();
        b.putBoolean(TreeMapActivity.IS_ONE, true);
        b.putString(TreeMapActivity.MM_CHANNEL, mServerObj.getMmChannelID());
        Passageway.jumpActivity(context, Intent.FLAG_ACTIVITY_CLEAR_TOP,
                TreeMapActivityV2.class, b);
    }

    private void callUp() {
        Uri uri = Uri.parse("tel:" + mServerObj.getPhone());
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(intent);
    }

    private void sendMessage() {
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(Uri.parse("smsto:" + mServerObj.getPhone()));
        sendIntent.putExtra("sms_body", "你好,我对你发布的\"" + mServerObj.getTitle()
                + "\"很有兴趣,想和你详细了解一下!");
        startActivity(sendIntent);
    }

    private void setResizeListener() {
        scroll.setOnResizeListener(new OnResizeListener() {

            @Override
            public void scrollBottom() {
                if (progress.getVisibility() == View.VISIBLE
                        && pageIndex < totalPage) {
                    getCommentList();
                }
            }

            @Override
            public void OnResize(int w, int h, int oldw, int oldh) {

            }
        });
    }

    private void setContentBoxListener() {
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                setOnBall(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    private void initActivity() {
        titleName.setText("详细");
        seekIcon.setVisibility(View.GONE);
        favorIcon.setVisibility(View.VISIBLE);
        Bundle b = getIntent().getExtras();
        imm = (InputMethodManager) commentInput.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (b != null) {
            downloadData(b.getString("id"));
            if (b.getBoolean("isGy")) {
                maneyBox.setVisibility(View.VISIBLE);
            }
        }

    }

    private void closeKeyboard() {
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(
                    commentInput.getApplicationWindowToken(), 0);
        }
    }

    private void setContent(ServerObj obj) {
        obj.setMu_total(String.valueOf(SystemHandle.getInt(context, ServerObj.MU_TOTAL + "_" + obj.getId())));
        userName.setText(obj.getName());
        userPhone.setText(obj.getPhone());
        titleText.setText(obj.getTitle());
        timeText.setText(obj.getCreateTime() + " " + obj.getCity());
        contentText.setText(obj.getContent());
        infoText.setText(obj.getInfo());
        setFavor(obj);
        setAddress(obj.getAddress());
        setImageList(obj.getPic());

        maneyText.setText("￥" + obj.getMu_price());
        setManey(1);
    }

    private void setAddress(String a) {
        if (a != null && !a.equals("null") && !a.equals("")) {
            address.setVisibility(View.VISIBLE);
            addressText.setText(a);
        } else {
            address.setVisibility(View.GONE);
        }
    }

    private void setFavor(ServerObj obj) {
        if (obj.isFavor()) {
            favorIcon.setImageResource(R.drawable.favor_on_icon);
        } else {
            favorIcon.setImageResource(R.drawable.favor_off_icon);
        }
    }

    private void setImageList(List<String> list) {
        if (list != null && !list.isEmpty()) {
            viewPagerBox.setVisibility(View.VISIBLE);
            viewPager.setAdapter(new ContentPagerAdapter(list));
            viewPager.setOffscreenPageLimit(1);
            viewPager.setCurrentItem(0);
            setBallList(list.size(), 0);
        } else {
            viewPagerBox.setVisibility(View.GONE);
        }

    }

    private void setBallList(int size, int position) {
        if (size > 1) {
            int w = WinTool.pxToDip(context, 40);
            ballList = new ArrayList<ImageView>(size);
            for (int i = 0; i < size; i++) {
                ImageView v = new ImageView(context);
                v.setImageResource(R.drawable.ppt_off);
                v.setLayoutParams(new LinearLayout.LayoutParams(w, w));

                View l = new View(context);
                l.setLayoutParams(new LinearLayout.LayoutParams(w, w));

                sizeBox.addView(v);
                sizeBox.addView(l);

                ballList.add(v);
            }
            setOnBall(position);
        }
    }

    private void setOnBall(int p) {
        for (ImageView v : ballList) {
            v.setImageResource(R.drawable.ppt_off);
        }
        ballList.get(p).setImageResource(R.drawable.ppt_on);
    }

    private void setCommentList(List<CommentObj> list) {
        if (list != null && !list.isEmpty()) {
            for (CommentObj obj : list) {
                CommentMessageView m = new CommentMessageView(context, obj);
                dataBox.addView(m);
                dataBox.addView(LineViewTool.getGrayLine(context));
            }
        } else {
            dataBox.addView(LineViewTool.getFirst(context, "成为第一个评论的人"));
        }
    }

    private void getCommentList() {
        progress.setVisibility(View.VISIBLE);
        CommentObjHandler.getComment(context, mServerObj.getId(), pageIndex,
                new CommentCallbackListener() {

                    @Override
                    public void callback(List<CommentObj> list, int p) {
                        progress.setVisibility(View.GONE);
                        if (list != null) {
                            pageIndex += 1;
                            totalPage = p;
                            setCommentList(list);
                        }

                    }

                });
    }

    private void sendComment() {
        String comment = commentInput.getText().toString();
        if (UserObjHandle.isLogin(context, true) && !comment.equals("")) {
            progress.setVisibility(View.VISIBLE);
            CommentObjHandler.sendComment(context, mServerObj.getId(), comment,
                    new CallbackForBoolean() {

                        @Override
                        public void callback(boolean b) {
                            progress.setVisibility(View.GONE);
                            if (b) {
                                ShowMessage.showToast(context, "发送成功");
                                closeKeyboard();
                                pageIndex = 1;
                                commentInput.setText("");
                                dataBox.removeAllViews();
                                getCommentList();
                            } else {
                                ShowMessage.showFailure(context);
                            }
                        }
                    });
        }
    }

    private void sendCollect() {
        if (UserObjHandle.isLogin(context, true)) {
            progress.setVisibility(View.VISIBLE);
            CollectObjHandler.sendCollect(context, mServerObj.getId(),
                    mServerObj.getFavor(), new CallbackForBoolean() {

                        @Override
                        public void callback(boolean b) {
                            progress.setVisibility(View.GONE);
                            if (b) {
                                ShowMessage.showToast(context, "发送成功");
                                mServerObj.setFavor(!mServerObj.isFavor());
                                setFavor(mServerObj);
                            } else {
                                ShowMessage.showFailure(context);
                            }
                        }
                    });
        }
    }

    private void downloadData(String id) {
        progress.setVisibility(View.VISIBLE);
        String url = UrlHandle.getMmPost(id) + "?user_id="
                + UserObjHandle.getUsetId(context);
        RequestParams params = HttpUtilsBox.getRequestParams(context);

        HttpUtilsBox.getHttpUtil().send(HttpMethod.PUT, url, params,
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
                        if (!ShowMessage.showException(context, json)) {
                            JSONObject serverJson = JsonHandle.getJSON(json,
                                    "o");
                            if (serverJson != null) {
                                mServerObj = ServerObjHandler
                                        .getServerObj(serverJson);
                                mServerObj.setFavor(JsonHandle.getInt(json,
                                        ServerObj.IS_FAVOR) == 1);
                                setContent(mServerObj);
                                getCommentList();
                            }
                        }

                    }

                });
    }

    public boolean setManey(int m) {
        double s;
        int x = sum + m;
        try {
            if (x <= 0 || x > Integer.valueOf(mServerObj.getMu_total())) {
                return false;
            }
            sum = x;
            s = sum * Double.valueOf(mServerObj.getMu_price());
            deposit = s * 0.2;
        } catch (Exception e) {
            sum = 0;
            s = 0;
            deposit = 0;
        }
        numberText.setText(String.valueOf(sum));
        sumText.setText("￥" + s);
        depositText.setText("￥" + deposit);
        return true;
    }

    class ContentPagerAdapter extends PagerAdapter {

        private List<String> iamgePhatList;
        private Map<Integer, ImageView> imageMap;

        public ContentPagerAdapter(List<String> iamgePhatList) {
            this.iamgePhatList = iamgePhatList;
            imageMap = new HashMap<Integer, ImageView>();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            try {
                container.removeView(imageMap.get(position));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getCount() {
            return iamgePhatList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView v;
            if (imageMap.containsKey(iamgePhatList.get(position))) {
                v = imageMap.get(iamgePhatList.get(position));
            } else {
                v = getImageView(iamgePhatList.get(position), position);
                imageMap.put(position, v);
            }
            Log.e("", iamgePhatList.get(position));
            container.addView(v, LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            return v;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        private ImageView getImageView(String pic, final int position) {
            ImageView photoView = new ImageView(context);
            DownloadImageLoader.loadImage(photoView, pic);
            photoView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Bundle b = new Bundle();
                    b.putStringArrayList("iamge_list",
                            (ArrayList<String>) iamgePhatList);
                    b.putInt("position", position);
                    b.putBoolean("isOnline", true);
                    Passageway
                            .jumpActivity(context, ImageListAcitvity.class, b);
                }
            });
            return photoView;
        }

    }

}
