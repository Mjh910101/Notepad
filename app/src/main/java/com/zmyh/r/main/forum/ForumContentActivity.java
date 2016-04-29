package com.zmyh.r.main.forum;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
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
import com.zmyh.r.box.CommentObj;
import com.zmyh.r.box.DynamicObj;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.CollectObjHandler;
import com.zmyh.r.handler.CollectObjHandler.CollectCallbackListener;
import com.zmyh.r.handler.CommentObjHandler;
import com.zmyh.r.handler.CommentObjHandler.CommentCallbackListener;
import com.zmyh.r.handler.DynamicObjHandler;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.main.CollectMessageView;
import com.zmyh.r.main.CommentMessageView;
import com.zmyh.r.photo.ImageListAcitvity;
import com.zmyh.r.tool.LineViewTool;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;
import com.zmyh.r.view.LazyWebView;
import com.zmyh.r.view.ResizeScrollView;
import com.zmyh.r.view.ResizeScrollView.OnResizeListener;
import com.zmyh.r.view.VestrewWebView;

public class ForumContentActivity extends Activity {

    private final static int COMMENT = 100;
    private final static int COLLECT = 200;

    private Context context;

    private String comment;
    private boolean isSend = false;
    private DynamicObj mDynamicObj;
    private int pageIndex = 1, totalPage = 1, now_tap = COMMENT;

    private InputMethodManager imm = null;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_seek)
    private ImageView seekIcon;
    @ViewInject(R.id.forum_content_userPic)
    private ImageView userPic;
    @ViewInject(R.id.forum_content_userName)
    private TextView userName;
    @ViewInject(R.id.forum_content_time)
    private TextView time;
    @ViewInject(R.id.forum_content_title)
    private TextView title;
    @ViewInject(R.id.forum_content_contextWeb)
    private LazyWebView contextWeb;
    @ViewInject(R.id.forum_content_collectTitle)
    private TextView collectTitle;
    @ViewInject(R.id.forum_content_commentTitle)
    private TextView commentTitle;
    @ViewInject(R.id.forum_content_progress)
    private ProgressBar progress;
    @ViewInject(R.id.forum_content_sendBox)
    private RelativeLayout sendBox;
    @ViewInject(R.id.forum_content_contextInput)
    private EditText contextInput;
    @ViewInject(R.id.forum_content_sendBtn)
    private ImageView sendBtn;
    @ViewInject(R.id.forum_content_dataBox)
    private LinearLayout dataBox;
    @ViewInject(R.id.forum_content_scroll)
    private ResizeScrollView scrool;
    @ViewInject(R.id.forum_content_favorIcon)
    private ImageView favorIcon;
    @ViewInject(R.id.forum_content_commentIcon)
    private ImageView commentIcon;
    @ViewInject(R.id.forum_content_collectIcon)
    private ImageView collectIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_content);
        ViewUtils.inject(this);
        context = this;
        initActivity();
        setTextChangedListener();
        setResizeListener();
    }

    @OnClick({R.id.title_back, R.id.forum_content_commentHandleBox,
            R.id.forum_content_closeSend, R.id.forum_content_sendBtn,
            R.id.forum_content_commentBox, R.id.forum_content_collectBox,
            R.id.forum_content_favorHandleBox})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.forum_content_commentHandleBox:
                showSendBox();
                break;
            case R.id.forum_content_closeSend:
                closeSendBox(false);
                break;
            case R.id.forum_content_sendBtn:
                sendComment();
                break;
            case R.id.forum_content_commentBox:
                setDataBox(COMMENT);
                break;
            case R.id.forum_content_collectBox:
                setDataBox(COLLECT);
                break;
            case R.id.forum_content_favorHandleBox:
                sendCollect();
                break;
        }

    }

    private void setTextChangedListener() {
        contextInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                comment = contextInput.getText().toString();
                if (comment.equals("")) {
                    isSend = false;
                    sendBtn.setImageResource(R.drawable.send_gray_icon);
                } else {
                    isSend = true;
                    sendBtn.setImageResource(R.drawable.send_green_icon);
                }
            }
        });
    }

    private void setResizeListener() {
        scrool.setOnResizeListener(new OnResizeListener() {

            @Override
            public void scrollBottom() {
                if (progress.getVisibility() == View.VISIBLE
                        && pageIndex < totalPage) {
                    switch (now_tap) {
                        case COMMENT:
                            getCommentList();
                            break;
                        case COLLECT:
                            getCollectList();
                            break;
                    }
                }
            }

            @Override
            public void OnResize(int w, int h, int oldw, int oldh) {

            }
        });
    }

    private void showSendBox() {
        if (UserObjHandle.isLogin(context, true)) {
            sendBox.setVisibility(View.VISIBLE);
        }
    }

    private void closeSendBox() {
        closeSendBox(true);
    }

    private void closeSendBox(boolean b) {
        sendBox.setVisibility(View.GONE);
        if (b) {
            contextInput.setText("");
        }
        closeKeyboard();
    }

    private void closeKeyboard() {
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(
                    contextInput.getApplicationWindowToken(), 0);
        }
    }

    private void setDataBox(int i) {
        now_tap = i;
        initDataBox();
        switch (i) {
            case COMMENT:
                getCommentList();
                break;
            case COLLECT:
                getCollectList();
                break;
        }
    }

    private void initDataBox() {
        collectIcon.setImageResource(R.drawable.collect_off_icon);
        commentIcon.setImageResource(R.drawable.comment_icon);
        dataBox.removeAllViews();
        pageIndex = 1;
        totalPage = 1;
    }

    private void initActivity() {
        titleName.setText("贴子详细");
        seekIcon.setVisibility(View.GONE);

        imm = (InputMethodManager) contextInput.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            downloadData(b.getString("id"));
        }
    }

    private void setActivityContent(DynamicObj obj) {
        int w = WinTool.getWinWidth(context) / 10;
        userPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
        DownloadImageLoader.loadImage(userPic, obj.getUserPic(), w / 2);

        title.setText(obj.getTitle());
        userName.setText(obj.getUserName());
        time.setText(obj.getCreateTime());
        setCommentText(obj);
        setCollectText(obj);
        setFavor(obj);
        setContextWeb(obj);
    }

    private void setCollectText(DynamicObj obj) {
        collectTitle.setText(String.valueOf(obj.getFavor_count()));
    }

    private void setCommentText(DynamicObj obj) {
        commentTitle.setText(String.valueOf(obj.getComment_count()));
    }

    private void setFavor(DynamicObj obj) {
        if (obj.isFavor()) {
            favorIcon.setImageResource(R.drawable.collect_click_icon);
        } else {
            favorIcon.setImageResource(R.drawable.collect_off_icon);
        }
    }

    @SuppressLint("JavascriptInterface")
    private void setContextWeb(DynamicObj obj) {
        Log.e("", obj.getContent());
        Log.e("", Html.fromHtml(obj.getContent()).toString());
        // String contentStr = VestrewWebView.addJavaScript(Html.fromHtml(
        // obj.getContent()).toString());
        String contentStr = VestrewWebView.addJavaScript(obj.getContent());
        // String contentStr = VestrewWebView.addJavaScript(obj.getContent());
        contextWeb.getSettings().setJavaScriptEnabled(true);
        contextWeb.addJavascriptInterface(this, "ImageOnClick");
        contextWeb.setWebChromeClient(new WebChromeClient());
        contextWeb.setFocusable(false);
        contextWeb.loadData(contentStr);
    }

    @JavascriptInterface
    public void onClickForImg(final String imgURL) {
        Log.d("OnClick", imgURL);
        Bundle b = new Bundle();
        b.putStringArrayList("iamge_list",
                (ArrayList<String>) getImageList(imgURL));
        b.putInt("position", 0);
        b.putBoolean("isOnline", true);
        Passageway.jumpActivity(context, ImageListAcitvity.class, b);
    }

    private List<String> getImageList(String imgURL) {
        List<String> imgList = new ArrayList<String>();
        imgList.add(imgURL);
        return imgList;
    }

    private void setCollectList(List<CommentObj> list) {
        if (list != null && !list.isEmpty()) {
            for (CommentObj obj : list) {
                CollectMessageView m = new CollectMessageView(context, obj);
                dataBox.addView(m);
                dataBox.addView(LineViewTool.getGrayLine(context));
            }
        } else {
            dataBox.addView(LineViewTool.getFirst(context, "成为第一个收藏的人"));
        }
    }

    private void getCollectList() {
        collectIcon.setImageResource(R.drawable.collect_on_icon);
        progress.setVisibility(View.VISIBLE);
        CollectObjHandler.getCollect(context, mDynamicObj.getId(), pageIndex,
                new CollectCallbackListener() {

                    @Override
                    public void callback(List<CommentObj> list, int p) {
                        progress.setVisibility(View.GONE);
                        if (list != null) {
                            pageIndex += 1;
                            totalPage = p;
                            setCollectList(list);
                        }
                    }

                });
    }

    private void sendCollect() {
        if (UserObjHandle.isLogin(context, true)) {
            progress.setVisibility(View.VISIBLE);
            CollectObjHandler.sendCollect(context, mDynamicObj.getId(),
                    mDynamicObj.getFavor(), new CallbackForBoolean() {

                        @Override
                        public void callback(boolean b) {
                            progress.setVisibility(View.GONE);
                            if (b) {
                                ShowMessage.showToast(context, "发送成功");
                                mDynamicObj.setFavor(!mDynamicObj.isFavor());
                                setFavor(mDynamicObj);
                                setDataBox(COLLECT);
                                if (mDynamicObj.isFavor()) {
                                    mDynamicObj.setFavor_count(mDynamicObj
                                            .getFavor_count() + 1);
                                } else {
                                    mDynamicObj.setFavor_count(mDynamicObj
                                            .getFavor_count() - 1);
                                }
                                setCollectText(mDynamicObj);
                            } else {
                                ShowMessage.showFailure(context);
                            }
                        }
                    });
        }
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
        commentIcon.setImageResource(R.drawable.comment_on_icon);
        progress.setVisibility(View.VISIBLE);
        CommentObjHandler.getComment(context, mDynamicObj.getId(), pageIndex,
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
        if (isSend && UserObjHandle.isLogin(context, true)) {
            progress.setVisibility(View.VISIBLE);
            CommentObjHandler.sendComment(context, mDynamicObj.getId(),
                    comment, new CallbackForBoolean() {

                        @Override
                        public void callback(boolean b) {
                            progress.setVisibility(View.GONE);
                            if (b) {
                                ShowMessage.showToast(context, "发送成功");
                                closeSendBox();
                                setDataBox(COMMENT);
                                mDynamicObj.setComment_count(mDynamicObj
                                        .getComment_count() + 1);
                                setCommentText(mDynamicObj);
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
                            JSONObject dynamicJson = JsonHandle.getJSON(json,
                                    "o");
                            if (dynamicJson != null) {
                                mDynamicObj = DynamicObjHandler
                                        .getDynamicObj(dynamicJson);
                                mDynamicObj.setFavor(JsonHandle.getInt(json,
                                        DynamicObj.IS_FAVOR) == 1);
                                setActivityContent(mDynamicObj);
                                setDataBox(COMMENT);

                            }
                        }
                    }

                });
    }
}
