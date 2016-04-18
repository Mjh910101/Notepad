package com.zmyh.r.main.user;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.zmyh.r.box.UserImageObj;
import com.zmyh.r.box.UserObj;
import com.zmyh.r.city.AreaActivity;
import com.zmyh.r.dailog.ListDialog;
import com.zmyh.r.dailog.MessageDialog;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.CityObjHandler;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.UserImageObjHandler;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.http.HttpFlieBox;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.PostFile;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.interfaces.CallbackForInteger;
import com.zmyh.r.interfaces.CallbackForString;
import com.zmyh.r.interfaces.PostFileCallback;
import com.zmyh.r.main.forum.ForumFrameLayout;
import com.zmyh.r.main.people.TagActivity;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;
import com.zmyh.r.view.InsideGridView;

public class UserSetingActivityV2 extends Activity {

    private static final String IMAGE_FILE_NAME = "usercard.jpg";
    private static final int CARD_IMAGE_REQUEST_CODE = 0;
    private static final int CARD_CAMERA_REQUEST_CODE = 1;
    private static final int IMAGE_CAMERA_REQUEST_CODE = 2;
    private static final int IMAGE_IMAGE_REQUEST_CODE = 3;

    private Context context;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_seek)
    private ImageView seekIcon;
    @ViewInject(R.id.user_seting_pic)
    private ImageView userPic;
    @ViewInject(R.id.user_seting_userNameInput)
    private EditText userNameInput;
    @ViewInject(R.id.user_seting_workText)
    private TextView workText;
    @ViewInject(R.id.user_seting_attestationText)
    private TextView attestationText;
    @ViewInject(R.id.user_seting_introInput)
    private EditText introInput;
    @ViewInject(R.id.user_seting_userMobileInput)
    private EditText userMobileInput;
    @ViewInject(R.id.user_seting_userMailInput)
    private EditText userMailInput;
    @ViewInject(R.id.user_seting_userQQInput)
    private EditText userQQInput;
    @ViewInject(R.id.user_seting_corporationInput)
    private EditText corporationInput;
    @ViewInject(R.id.user_seting_cityText)
    private TextView cityText;
    @ViewInject(R.id.user_seting_cardBox)
    private RelativeLayout cardBox;
    @ViewInject(R.id.user_seting_card)
    private ImageView cardPic;
    @ViewInject(R.id.user_seting_progress)
    private ProgressBar progress;
    @ViewInject(R.id.user_seting_imageGrid)
    private InsideGridView imageGrid;
    private List<String> imagePathList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uset_seting_v2);
        ViewUtils.inject(this);
        context = this;

        initActivity();
        downloadImage();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setUserPic();
        setCityText();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            close();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CARD_IMAGE_REQUEST_CODE:
                if (data != null) {
                    resizeImage(true, data.getData());
                }
                break;
            case IMAGE_IMAGE_REQUEST_CODE:
                if (data != null) {
                    resizeImage(false, data.getData());
                }
                break;
            case CARD_CAMERA_REQUEST_CODE:
                if (isSdcardExisting()) {
                    resizeImage(true, getImageFile());
                } else {
                    ShowMessage.showToast(context, "找不到SD卡");
                }
                break;
            case IMAGE_CAMERA_REQUEST_CODE:
                if (isSdcardExisting()) {
                    resizeImage(false, getImageFile());
                } else {
                    ShowMessage.showToast(context, "找不到SD卡");
                }
                break;
            case TagActivity.RC:
                if (data != null) {
                    Bundle b = data.getExtras();
                    setTagText(b.getStringArray(TagActivity.NOW_TAG));
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.title_back, R.id.user_seting_rewamp,
            R.id.user_seting_picBox, R.id.user_seting_addCard,
            R.id.user_seting_card, R.id.user_seting_city,
            R.id.user_seting_save, R.id.user_seting_work})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                close();
                break;
            case R.id.user_seting_rewamp:
                Passageway.jumpActivity(context, RevampActivity.class);
                break;
            case R.id.user_seting_picBox:
                Passageway.jumpActivity(context, PicCompileActivity.class);
                break;
            case R.id.user_seting_card:
            case R.id.user_seting_addCard:
                showCadrList();
                break;
            case R.id.user_seting_city:
                Passageway.jumpActivity(context, AreaActivity.class);
                break;
            case R.id.user_seting_save:
                uploadUserMessage();
                break;
            case R.id.user_seting_work:
                jumpTagActivity();
                break;
        }
    }


    private void close() {
        if (isFull()) {
            MessageDialog dialog = new MessageDialog(context);
            dialog.setMessage("确定放弃本次编辑?");
            dialog.setCommitStyle("离开");
            dialog.setCommitListener(new MessageDialog.CallBackListener() {

                @Override
                public void callback() {
                    finish();
                }
            });
            dialog.setCancelStyle("取消");
            dialog.setCancelListener(null);
        } else {
            finish();
        }
    }

    private void jumpTagActivity() {
        Bundle b = new Bundle();
        b.putBoolean(TagActivity.IS_ALL, false);
        b.putStringArray(TagActivity.NOW_TAG, getTextStringArray(workText, ","));
        Passageway.jumpActivity(context, TagActivity.class, TagActivity.RC, b);
    }

    private void initActivity() {
        titleName.setText("编辑个人资料");
        seekIcon.setVisibility(View.GONE);
        setUserData();
    }

    private void setUserData() {
        setUserPic();
        setCardBox();
        setTagText(UserObjHandle.getM_tap(context));
        userNameInput.setText(UserObjHandle.getM_nick_name(context));
        attestationText.setText(UserObjHandle.getM_auth_tag(context));
        introInput.setText(UserObjHandle.getM_description(context));
        userMobileInput.setText(UserObjHandle.getM_mobile(context));
        userMailInput.setText(UserObjHandle.getEmail(context));
        userQQInput.setText(UserObjHandle.getQQ(context));
        corporationInput.setText(UserObjHandle.getCompany(context));
        setCityText();
    }

    private void setTagText(String[] tags) {
        StringBuffer tag = new StringBuffer();
        for (int i = 0; i < tags.length; i++) {
            tag.append(tags[i]);
            if (i < tags.length - 1) {
                tag.append(",");
            }
        }
        workText.setText(tag);
    }

    private void setTagText(String tag) {
        workText.setText(tag);
    }

    private void setCityText() {
        cityText.setText(CityObjHandler.getCityName(context));
    }

    private void setCardBox() {
        double w = WinTool.getWinWidth(context) - WinTool.dipToPx(context, 10);
        double h = w / 209 * 119;
        cardBox.setLayoutParams(new LinearLayout.LayoutParams((int) w, (int) h));
        String cardUrl = UserObjHandle.getM_CallingCard(context);
        if (!cardUrl.equals("")) {
            DownloadImageLoader.loadImage(cardPic, cardUrl);
        }
    }

    private void setUserPic() {
        int w = WinTool.dipToPx(context, 40);
        userPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
        DownloadImageLoader.loadImage(userPic,
                UserObjHandle.getM_avatar(context), w / 2);
    }

    private void selectImage(int rc) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, rc);
    }

    private void takePhoto(int rc) {
        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
        cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        startActivityForResult(cameraIntent, rc);
    }

    private static File getImageFile() {
        return new File(HttpFlieBox.getImagePath(), IMAGE_FILE_NAME);
    }

    private static Uri getImageUri() {
        return Uri.fromFile(getImageFile());
    }

    private void showCadrList() {
        showList(new CallbackForInteger() {
            @Override
            public void callback(int p) {
                if (p == 0) {
                    takePhoto(CARD_CAMERA_REQUEST_CODE);
                } else {
                    selectImage(CARD_IMAGE_REQUEST_CODE);
                }
            }
        });
    }

    private void showImageLict() {

        showList(new CallbackForInteger() {
            @Override
            public void callback(int p) {
                if (p == 0) {
                    takePhoto(IMAGE_CAMERA_REQUEST_CODE);
                } else {
                    selectImage(IMAGE_IMAGE_REQUEST_CODE);
                }
            }
        });
    }

    private void showList(final CallbackForInteger listener) {
        final ListDialog dialog = new ListDialog(context);
        dialog.setTitleGone();
        dialog.setList(new String[]{"拍照", "本地相册"});
        dialog.setItemListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int p,
                                    long arg3) {
                listener.callback(p);
                dialog.dismiss();
            }

        });
    }

    private void setUserImage(List<UserImageObj> list) {
        if (list.size() < 9) {
            list.add(0, new UserImageObj());
        }

        UserImageBaseAdapter uiba = new UserImageBaseAdapter(context, list);
        uiba.setProgress(progress);
        uiba.setCallBack(new CallbackForBoolean() {
            @Override
            public void callback(boolean b) {
                if (b == UserImageBaseAdapter.ADD) {
                    showImageLict();
                } else {
                    downloadImage();
                }
            }
        });
        imageGrid.setAdapter(uiba);
    }

    private boolean isSdcardExisting() {
        final String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private void resizeImage(boolean isCard, File file) {
        Log.e("file_path", file.getPath());
        if (file.exists()) {
            if (isCard) {
                upLoadUserCardPic(file);
            } else {
                upLoadUserImagePic(file);
            }
        }
        // DownloadImageLoader.loadImageForFile(cardPic, file.getPath());
    }

    private void resizeImage(boolean isCard, Uri imageUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(imageUri, proj, null, null,
                null);
        int actual_image_column_index = actualimagecursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor
                .getString(actual_image_column_index);
        resizeImage(isCard, new File(img_path));
    }

    private RequestParams getRequestParams() {
        RequestParams params = HttpUtilsBox.getRequestParams(context);
        params.addBodyParameter("user_id", UserObjHandle.getUsetId(context));
        params.addBodyParameter("m_nick_name", getTextString(userNameInput));
        params.addBodyParameter("m_auth_tag", getTextString(attestationText));
        params.addBodyParameter("m_description", getTextString(introInput));
        params.addBodyParameter("m_mobile", getTextString(userMobileInput));
        params.addBodyParameter("m_contact.email", getTextString(userMailInput));
        params.addBodyParameter("m_contact.qq", getTextString(userQQInput));
        params.addBodyParameter("m_contact.company",
                getTextString(corporationInput));
        params.addBodyParameter("mmArea", CityObjHandler.getCityId(context));
        setMtagParams(params);
        return params;
    }

    private void setMtagParams(RequestParams params) {
        String[] mTags = getTextStringArray(workText, ",");
        if (mTags != null) {
            for (int i = 0; i < mTags.length; i++) {
                if (!mTags[i].equals("")) {
                    params.addBodyParameter("m_tag", mTags[i]);
                }
            }
        }

    }

    private String[] getTextStringArray(TextView view, String s) {
        String[] strs = getTextString(view).split(s);
        return strs;
    }

    private String getTextString(TextView tv) {
        return tv.getText().toString();
    }

    private String getTextString(EditText et) {
        return et.getText().toString();
    }

    private void uploadUserMessage() {
        String url = UrlHandle.getMmUser();

        RequestParams params = getRequestParams();
        upload(url, params, new CallbackForBoolean() {

            @Override
            public void callback(boolean b) {
                ShowMessage.showToast(context, "修改成功");
            }
        });
    }


    public List<String> getImagePathList(File imageFile) {
        List<String> list = new ArrayList<String>();
        list.add(imageFile.toString());
        return list;
    }

    private void upLoadUserImagePic(File imageFile) {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getMmUserAlbum();

        HttpFlieBox box = new HttpFlieBox();
        box.addParams("user_id", UserObjHandle.getUsetId(context));
        box.addFileList("photo", getImagePathList(imageFile));
        PostFile.getInstance().post(url, box, new PostFileCallback() {

            @Override
            public void callback(String result) {
                progress.setVisibility(View.GONE);
                Log.d("", result);
                JSONObject json = JsonHandle.getJSON(
                        JsonHandle.getJSON(result), "result");
                if (json != null) {
                    if (JsonHandle.getInt(JsonHandle.getJSON(json, "o"), "ok") == 1) {
                        downloadImage();
                    }
                }
            }

            @Override
            public void onFailure(Exception exception) {
                progress.setVisibility(View.GONE);
                ShowMessage.showFailure(context);
            }
        });

    }

    private void upLoadUserCardPic(File imageFile) {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getMmUser();

        HttpFlieBox box = new HttpFlieBox();
        box.addParams("user_id", UserObjHandle.getUsetId(context));
        box.addFileList("m_CallingCard", getImagePathList(imageFile));
        PostFile.getInstance().post(url, box, new PostFileCallback() {

            @Override
            public void callback(String result) {
                progress.setVisibility(View.GONE);
                Log.d("", result);
                JSONObject json = JsonHandle.getJSON(
                        JsonHandle.getJSON(result), "result");
                if (!ShowMessage.showException(context, json)) {
                    UserObj user = UserObjHandle.getUserBox(json);
                    UserObjHandle.savaUser(context, user);
                    setCardBox();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                progress.setVisibility(View.GONE);
                ShowMessage.showFailure(context);
            }
        });
    }

    private void upload(String url, RequestParams params,
                        final CallbackForBoolean callback) {
        progress.setVisibility(View.VISIBLE);
        HttpUtilsBox.getHttpUtil().send(HttpMethod.POST, url, params,
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
                        if (!ShowMessage.showException(context, json)) {
                            UserObj user = UserObjHandle.getUserBox(json);
                            UserObjHandle.savaUser(context, user);
                            callback.callback(true);
                        }
                    }

                });
    }

    private void downloadImage() {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getMmUserAlbum() + "?query=" + JsonHandle.getHttpJsonToString(new String[]{"user_id"}, new String[]{UserObjHandle.getUsetId(context)}) + "&p=1";

        RequestParams params = HttpUtilsBox.getRequestParams(context);

        HttpUtilsBox.getHttpUtil().send(HttpMethod.GET, url, params,
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

    public boolean isFull() {
        if (!UserObjHandle.getM_tap(context).equals(getTextString(workText))) {
            return true;
        }
        if (!UserObjHandle.getM_nick_name(context).equals(getTextString(userNameInput))) {
            return true;
        }
        if (!UserObjHandle.getM_auth_tag(context).equals(getTextString(attestationText))) {
            return true;
        }
        if (!UserObjHandle.getM_description(context).equals(getTextString(introInput))) {
            return true;
        }
        if (!UserObjHandle.getM_mobile(context).equals(getTextString(userMobileInput))) {
            return true;
        }
        if (!UserObjHandle.getEmail(context).equals(getTextString(userMailInput))) {
            return true;
        }
        if (!UserObjHandle.getQQ(context).equals(getTextString(userQQInput))) {
            return true;
        }
        if (!UserObjHandle.getCompany(context).equals(getTextString(corporationInput))) {
            return true;
        }
        return false;
    }

}
