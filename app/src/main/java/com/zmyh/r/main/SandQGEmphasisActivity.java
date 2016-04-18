package com.zmyh.r.main;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.box.CameraPicObj;
import com.zmyh.r.box.TroopObj;
import com.zmyh.r.camera.util.FileUtil;
import com.zmyh.r.city.AreaActivity;
import com.zmyh.r.dailog.ListDialog;
import com.zmyh.r.dailog.MessageDialog;
import com.zmyh.r.dailog.MessageDialog.CallBackListener;
import com.zmyh.r.handler.CameraPicObjHandler;
import com.zmyh.r.handler.CityObjHandler;
import com.zmyh.r.handler.DateHandle;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.MapHandler;
import com.zmyh.r.handler.MapHandler.MapListener;
import com.zmyh.r.handler.TypeDictBox;
import com.zmyh.r.handler.TypeDictHandle;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.http.HttpFlieBox;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.PostFile;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.interfaces.PostFileCallback;
import com.zmyh.r.main.forum.ForumFrameLayout;
import com.zmyh.r.photo.TroopObjBox;
import com.zmyh.r.tool.AddPicDialog;
import com.zmyh.r.tool.LineViewTool;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SandQGEmphasisActivity extends Activity {

    public final static String IS_EMPHASIS = "isEmphasis";
    public final static String MM_CHANNEL = "mmChannel";
    public final static String TYPE = "type";
    public final static int RC = 100;

    private Context context;

    private boolean isEmphasis, isShow = false;
    private String mmChannel = "", area_id = "", type = "";

    private TypeDictBox mTypeDictBox;
    private TroopObj mTroopObj;
    private List<CameraPicObj> sendPicList;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_seek)
    private ImageView seekIcon;
    @ViewInject(R.id.send_emphasis_picBox)
    private LinearLayout picBox;
    @ViewInject(R.id.send_emphasis_smphasisBox)
    private LinearLayout smphasisBox;
    @ViewInject(R.id.send_emphasis_progress)
    private ProgressBar progress;
    @ViewInject(R.id.send_emphasis_zsTypeText)
    private TextView zsTypeText;
    @ViewInject(R.id.send_emphasis_JText)
    private TextView jText;
    @ViewInject(R.id.send_emphasis_zgText)
    private TextView zgText;
    @ViewInject(R.id.send_emphasis_gfText)
    private TextView gfText;
    @ViewInject(R.id.send_emphasis_typeText)
    private TextView typeText;
    @ViewInject(R.id.send_emphasis_jzTimeText)
    private TextView jzTimeText;
    @ViewInject(R.id.send_emphasis_totalInput)
    private EditText totalInput;
    @ViewInject(R.id.send_emphasis_priceInput)
    private EditText priceInput;
    @ViewInject(R.id.send_emphasis_titleName)
    private EditText titleInput;
    @ViewInject(R.id.send_emphasis_remark)
    private EditText contentInput;
    @ViewInject(R.id.send_emphasis_people)
    private EditText nameInput;
    @ViewInject(R.id.send_emphasis_phone)
    private EditText phoneInput;
    @ViewInject(R.id.send_emphasis_addressInput)
    private EditText addressInput;
    @ViewInject(R.id.send_emphasis_cityText)
    private TextView cityText;
    @ViewInject(R.id.send_emphasis_mapText)
    private TextView mapText;
    @ViewInject(R.id.send_emphasis_indateText)
    private TextView indateText;
    @ViewInject(R.id.send_emphasis_showIcon)
    private ImageView showIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_qg_emphasis);
        ViewUtils.inject(this);
        context = this;
        initActivity();
        downloadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case AddPicDialog.CAMERA_IMAGE_REQUEST_CODE:
                    String id = data.getExtras().getString(AddPicDialog.CAMERA_KEY);
                    savePic(id);
                    break;
                case AddPicDialog.IMAGE_REQUEST_CODE:
                    Uri imgUri = data.getData();
                    savePic(imgUri);
                    break;
                case AreaActivity.RequestCode:
                    if (data != null) {
                        Bundle b = data.getExtras();
                        if (b.getBoolean("ok")) {
                            cityText.setText(b.getString("Area_name") + ">");
                            area_id = b.getString("Area_id");
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            close();
        }
        return false;
    }

    @OnClick({R.id.title_back, R.id.send_emphasis_submitBtn,
            R.id.send_emphasis_zsType, R.id.send_emphasis_J,
            R.id.send_emphasis_zg, R.id.send_emphasis_gf,
            R.id.send_emphasis_type, R.id.send_emphasis_jzTime,
            R.id.send_emphasis_city, R.id.send_emphasis_map,
            R.id.send_emphasis_indate, R.id.send_emphasis_showMap})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                close();
                break;
            case R.id.send_emphasis_submitBtn:
                submit();
                break;
            case R.id.send_emphasis_zsType:
                setMuZsType();
                break;
            case R.id.send_emphasis_J:
                setMuJ();
                break;
            case R.id.send_emphasis_zg:
                setMuZg();
                break;
            case R.id.send_emphasis_gf:
                setMuGf();
                break;
            case R.id.send_emphasis_type:
                setMuType();
                break;
            case R.id.send_emphasis_jzTime:
                setMuJzTime();
                break;
            case R.id.send_emphasis_city:
                setCity();
                break;
            case R.id.send_emphasis_map:
                setLocation();
                break;
            case R.id.send_emphasis_indate:
                showIndateList();
                break;
            case R.id.send_emphasis_showMap:
                setShowMap(!isShow);
                break;
        }
    }

    private void setShowMap(boolean b) {
        isShow = b;
        if (isShow) {
            showIcon.setImageResource(R.drawable.show_on_icon);
        } else {
            showIcon.setImageResource(R.drawable.show_off_icon);
        }
    }

    private void close() {
        if (isCompile()) {
            MessageDialog dialog = new MessageDialog(context);
            dialog.setMessage("确定放弃本次编辑?");
            dialog.setCommitStyle("离开");
            dialog.setCommitListener(new CallBackListener() {

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

    private void showIndateList() {
        final String[] strs = new String[]{"7天", "14天", "21天", "28天", "35天",
                "42天"};
        final ListDialog dialog = new ListDialog(context);
        dialog.setTitleGone();
        dialog.setList(strs);
        dialog.setItemListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int p,
                                    long arg3) {
                indateText.setText(strs[p]);
                dialog.dismiss();
            }
        });
    }

    private void setLocation() {
        mapText.setText("正在获取");
        MapHandler.getPicAddress(context, new MapListener() {

            @Override
            public void callback(BDLocation location) {
                mapText.setText("获取成功");
                mTroopObj.setMu_latitude(location.getLatitude());
                mTroopObj.setMu_longitude(location.getLongitude());
                if (addressInput.getText().toString().equals("")) {
                    addressInput.setText(location.getAddrStr());
                }
            }
        });
    }

    private void setCity() {
        Bundle b = new Bundle();
        b.putBoolean(AreaActivity.COMMON, true);
        Passageway.jumpActivity(context, AreaActivity.class,
                AreaActivity.RequestCode, b);
    }

    private void setMuZsType() {
        final List<String> typeList = mTypeDictBox.getMuZsTypeList();
        final ListDialog dialog = new ListDialog(context);
        dialog.setTitleGone();
        dialog.setList(typeList);
        dialog.setItemListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int p,
                                    long arg3) {
                zsTypeText.setText(typeList.get(p));
                initMessage(typeList.get(p));
                dialog.dismiss();
            }

        });
    }

    private void initMessage(String type) {
        if (mTypeDictBox.getMuJList(type) == null) {
            jText.setText("无");
        }

        if (mTypeDictBox.getMuZgList(type) == null) {
            zgText.setText("无");
        }

        if (mTypeDictBox.getMuGfList(type) == null) {
            gfText.setText("无");
        }

        if (mTypeDictBox.getMuTypeList(type) == null) {
            typeText.setText("无");
        }

        if (mTypeDictBox.getMuJzTimeList(type) == null) {
            jzTimeText.setText("无");
        }
    }

    private void setMuJzTime() {
        String type = zsTypeText.getText().toString();
        if (!type.equals("＞")) {
            final List<String> strList = mTypeDictBox.getMuJzTimeList(type);
            if (strList == null) {
                jzTimeText.setText("无");
            } else {
                final ListDialog dialog = new ListDialog(context);
                dialog.setTitleGone();
                dialog.setList(strList);
                dialog.setItemListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int p, long arg3) {
                        jzTimeText.setText(strList.get(p));
                        dialog.dismiss();
                    }
                });
            }
        } else {
            ShowMessage.showToast(context, "请先选择类别");
        }
    }

    private void setMuType() {
        String type = zsTypeText.getText().toString();
        if (!type.equals("＞")) {
            final List<String> strList = mTypeDictBox.getMuTypeList(type);
            if (strList == null) {
                typeText.setText("无");
            } else {
                final ListDialog dialog = new ListDialog(context);
                dialog.setTitleGone();
                dialog.setList(strList);
                dialog.setItemListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int p, long arg3) {
                        typeText.setText(strList.get(p));
                        dialog.dismiss();
                    }
                });
            }
        } else {
            ShowMessage.showToast(context, "请先选择类别");
        }
    }

    private void setMuGf() {
        String type = zsTypeText.getText().toString();
        if (!type.equals("＞")) {
            final List<String> strList = mTypeDictBox.getMuGfList(type);
            if (strList == null) {
                gfText.setText("无");
            } else {
                final ListDialog dialog = new ListDialog(context);
                dialog.setTitleGone();
                dialog.setList(strList);
                dialog.setItemListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int p, long arg3) {
                        gfText.setText(strList.get(p));
                        dialog.dismiss();
                    }
                });
            }
        } else {
            ShowMessage.showToast(context, "请先选择类别");
        }
    }

    private void setMuZg() {
        String type = zsTypeText.getText().toString();
        if (!type.equals("＞")) {
            final List<String> strList = mTypeDictBox.getMuZgList(type);
            if (strList == null) {
                zgText.setText("无");
            } else {
                final ListDialog dialog = new ListDialog(context);
                dialog.setTitleGone();
                dialog.setList(strList);
                dialog.setItemListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int p, long arg3) {
                        zgText.setText(strList.get(p));
                        dialog.dismiss();
                    }
                });
            }
        } else {
            ShowMessage.showToast(context, "请先选择类别");
        }
    }

    private void setMuJ() {
        String type = zsTypeText.getText().toString();
        if (!type.equals("＞")) {
            final List<String> strList = mTypeDictBox.getMuJList(type);
            if (strList == null) {
                jText.setText("无");
            } else {
                final ListDialog dialog = new ListDialog(context);
                dialog.setTitleGone();
                dialog.setList(strList);
                dialog.setItemListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int p, long arg3) {
                        jText.setText(strList.get(p));
                        dialog.dismiss();
                    }
                });
            }
        } else {
            ShowMessage.showToast(context, "请先选择类别");
        }

    }

    private void savePic(Uri imgUri) {
        if (imgUri != null) {
            try {
                ContentResolver cr = getContentResolver();
                InputStream imgIS = cr.openInputStream(imgUri);

                long createTime = DateHandle.getTime();

                CameraPicObj obj = new CameraPicObj();
                obj.setId(createTime);
                obj.setCreateAt(createTime);
                CameraPicObjHandler.saveCameraPicObj(context, obj);

                FileUtil.saveImage(imgIS, obj.getMediumFilePath());

                sendPicList.add(obj);
                initPicBox();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void savePic(String id) {
        CameraPicObj obj;
        try {
            obj = CameraPicObjHandler.getCameraPicObj(context, id);
            sendPicList.add(obj);
            initPicBox();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void initActivity() {
        titleName.setText("供应发布");
        seekIcon.setVisibility(View.GONE);
        sendPicList = new ArrayList<CameraPicObj>();
        Bundle b = getIntent().getExtras();
        if (b != null) {
            type = b.getString(TYPE);
            if (type == null || type.equals("")) {
                type = "services";
            }
            mmChannel = b.getString(MM_CHANNEL);
            setContentView(b.getBoolean(IS_EMPHASIS));
        }

        if (UserObjHandle.isLogin(context)) {
            phoneInput.setText(UserObjHandle.getM_mobile(context));
            nameInput.setText(UserObjHandle.getM_nick_name(context));
        }

        mTroopObj = TroopObjBox.getSaveTroopObj();
        if (mTroopObj != null) {
            initContent(mTroopObj);
        } else {
            mTroopObj = new TroopObj();
        }
        setShowMap(false);
        initCity();
        indateText.setText("42天");
    }

    private void initCity() {
        cityText.setText(CityObjHandler.getCityName(context) + ">");
        area_id = CityObjHandler.getCityId(context);
    }

    private void initContent(TroopObj obj) {
        titleInput.setText(obj.getMu_name());
        contentInput.setText(obj.getMu_desc());
        nameInput.setText(obj.getMu_contact());
        phoneInput.setText(obj.getMu_phone_1());
        addressInput.setText(obj.getMu_zb());
        if (!obj.getMu_sz_type().equals("")) {
            zsTypeText.setText(obj.getMu_sz_type());
        }
        if (!obj.getMu_j_min().equals("") && !obj.getMu_j_max().equals("")) {
            jText.setText(obj.getMu_j_min() + "-" + obj.getMu_j_max());
        }
        if (!obj.getMu_zg_min().equals("") && !obj.getMu_zg_max().equals("")) {
            zgText.setText(obj.getMu_zg_min() + "-" + obj.getMu_zg_max());
        }
        if (!obj.getMu_gf_min().equals("") && !obj.getMu_gf_max().equals("")) {
            gfText.setText(obj.getMu_gf_min() + "-" + obj.getMu_gf_max());
        }
        if (!obj.getMu_type().equals("")) {
            typeText.setText(obj.getMu_type());
        }
        if (!obj.getMu_jz_time().equals("")) {
            jzTimeText.setText(obj.getMu_jz_time());
        }
        totalInput.setText(obj.getMu_total());
        priceInput.setText(obj.getMu_price());
        for (CameraPicObj c : obj.getCameraPicObjList()) {
            sendPicList.add(c);
        }
        if (obj.getMu_latitude() > 0 && obj.getMu_longitude() > 0) {
            mapText.setText("获取成功");
        }
        initPicBox();
    }

    private void setContentView(boolean b) {
        titleName.setText("发布");
        isEmphasis = b;
        if (b) {
            smphasisBox.setVisibility(View.VISIBLE);
        } else {
            smphasisBox.setVisibility(View.GONE);
        }
        initPicBox();
    }

    private void initPicBox() {
        picBox.removeAllViews();
        if (sendPicList.size() < 9) {
            if (isEmphasis) {
                picBox.addView(getPicView(R.drawable.add_pic_e_icon));
            } else {
                picBox.addView(getPicView(R.drawable.add_pic_f_icon));
            }
        }
        for (CameraPicObj obj : sendPicList) {
            picBox.addView(LineViewTool.getSpaceView(context));
            picBox.addView(getPicView(obj));
        }
    }

    private View getPicView(int id) {
        SendPicView img = new SendPicView(this, id);
        return img;
    }

    private SendPicView getPicView(final CameraPicObj obj) {
        SendPicView img = new SendPicView(this, obj);
        img.setListener(new CallbackForBoolean() {

            @Override
            public void callback(boolean b) {
                if (b) {
                    sendPicList.remove(obj);
                    initPicBox();
                }
            }
        });
        return img;
    }

    private HttpFlieBox getSubmitParams() {
        HttpFlieBox box = new HttpFlieBox();

        box.addParams("poster", UserObjHandle.getUsetId(context));
        box.addParams("title", titleInput.getText().toString());
        box.addParams("content", contentInput.getText().toString());
        box.addParams("type", type);
        box.addParams("contact_info.name", nameInput.getText().toString());
        box.addParams("contact_info.phone", phoneInput.getText().toString());
        box.addParams("contact_info.address", addressInput.getText().toString());
        box.addParams("mmArea", area_id);
        box.addParams("mmChannel", mmChannel);
        box.addParams("tree.mu_sz_type", getTextString(zsTypeText));
        box.addParams("tree.mu_j", getTextString(jText));
        box.addParams("tree.mu_zg", getTextString(zgText));
        box.addParams("tree.mu_gf", getTextString(gfText));
        box.addParams("tree.mu_type", getTextString(typeText));
        box.addParams("tree.mu_jz_time", getTextString(jzTimeText));
        box.addParams("tree.mu_total", totalInput.getText().toString());
        box.addParams("tree.mu_price", priceInput.getText().toString());
        box.addParams("tree.mu_zb", mTroopObj.getMu_zb());
        box.addFileList("pic", getImagePathList());
        box.addParams("expireDate", getIndateDay(getTextString(indateText)));

        if (isShow) {
            box.addParams("tree.mu_coordinate_lat",
                    String.valueOf(mTroopObj.getMu_latitude()));
            box.addParams("tree.mu_coordinate_long",
                    String.valueOf(mTroopObj.getMu_longitude()));
        }
        return box;
    }

    private String getIndateDay(String str) {
        StringBuffer sb = new StringBuffer();
        if (str != null && !str.equals("")) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= '0' && str.charAt(i) <= '9') {
                    sb.append(str.charAt(i));
                }
            }
        }
        return sb.toString();
    }

    private String getTextString(TextView view) {
        String str = view.getText().toString();
        if (str.equals("＞")) {
            return "";
        }
        if (str.equals("无")) {
            return "";
        }
        return str;
    }

    private List<String> getImagePathList() {
        List<String> pathList = new ArrayList<String>();
        if (sendPicList != null) {
            for (CameraPicObj obj : sendPicList) {
                if (FileUtil.fileIsExists(obj.getMediumFilePath())) {
                    pathList.add(obj.getMediumFilePath());
                }
            }
        }
        return pathList;
    }

    private void submit() {
        if (isFull()) {
            progress.setVisibility(View.VISIBLE);

            String url = UrlHandle.getMmPost();
            HttpFlieBox box = getSubmitParams();
            PostFile.getInstance().post(url, box, new PostFileCallback() {

                @Override
                public void callback(String result) {
                    progress.setVisibility(View.GONE);
                    Log.d("", result);
                    JSONObject json = JsonHandle.getJSON(
                            JsonHandle.getJSON(result), "result");
                    if (!ShowMessage.showException(context, json)) {
                        int r = JsonHandle.getInt(json, "ok");
                        if (r == 1) {
                            ForumFrameLayout.UPLOAD = true;
                            ShowMessage.showToast(context, "发送成功");
                            Intent i = new Intent();
                            Bundle b = new Bundle();
                            b.putBoolean("ok", true);
                            i.putExtras(b);
                            setResult(RC, i);
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Exception exception) {
                    progress.setVisibility(View.GONE);
                    ShowMessage.showFailure(context);
                }
            });
        } else {
            ShowMessage.showToast(context, "必填内容未完成，请看看~!");
        }
    }

    private boolean isCompile() {
        if (!titleInput.getText().toString().equals("") ||
                !contentInput.getText().toString().equals("") ||
                !nameInput.getText().toString().equals("") ||
                !phoneInput.getText().toString().equals("") ||
                !totalInput.getText().toString().equals("")) {
            return true;

        }
        return false;
    }

    private boolean isFull() {
        if (titleInput.getText().toString().equals("")) {
            return false;
        }
        if (contentInput.getText().toString().equals("")) {
            return false;
        }
        if (nameInput.getText().toString().equals("")) {
            return false;
        }
        if (phoneInput.getText().toString().equals("")) {
            return false;
        }
        if (area_id.equals("")) {
            return false;
        }
        if (totalInput.getText().toString().equals("")) {
            return false;
        }
        if (isEmphasis) {
            if (sendPicList.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void downloadData() {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getMmDict();

        RequestParams params = HttpUtilsBox.getRequestParams();

        HttpUtilsBox.getHttpUtil().send(HttpMethod.GET, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        exception.printStackTrace();
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
                            JSONArray array = JsonHandle.getArray(json,
                                    "typeDict");
                            mTypeDictBox = TypeDictHandle.getTypeDictBox(array);
                        }

                    }
                });

    }

    public boolean isModification(TroopObj obj) {
        if (!obj.getMu_name().equals(getTextString(titleInput))) {
            return true;
        }
        if (!obj.getMu_desc().equals(getTextString(contentInput))) {
            return true;
        }
        if (!obj.getMu_contact().equals(getTextString(nameInput))) {
            return true;
        }
        if (!obj.getMu_phone_1().equals(getTextString(phoneInput))) {
            return true;
        }
        if (!obj.getMu_zb().equals(getTextString(addressInput))) {
            return true;
        }
        if (!obj.getMu_sz_type().equals(getTextString(zsTypeText))) {
            return true;
        }
        if (!obj.getMu_j_min().equals("") && !obj.getMu_j_max().equals("")) {
            if (!(obj.getMu_j_min() + "-" + obj.getMu_j_max()).equals(getTextString(jText))) {
                return true;
            }
        }
        if (!obj.getMu_zg_min().equals("") && !obj.getMu_zg_max().equals("")) {
            if (!(obj.getMu_zg_min() + "-" + obj.getMu_zg_max()).equals(getTextString(zgText))) {
                return true;
            }
        }
        if (!obj.getMu_gf_min().equals("") && !obj.getMu_gf_max().equals("")) {
            if (!(obj.getMu_gf_min() + "-" + obj.getMu_gf_max()).equals(getTextString(gfText))) {
                return true;
            }
        }
        if (!obj.getMu_type().equals(getTextString(typeText))) {
            return true;
        }
        if (!obj.getMu_jz_time().equals(getTextString(jzTimeText))) {
            return true;
        }
        if (!obj.getMu_total().equals(getTextString(totalInput))) {
            return true;
        }
        if (!obj.getMu_price().equals(getTextString(priceInput))) {
            return true;
        }
        return false;
    }
}
