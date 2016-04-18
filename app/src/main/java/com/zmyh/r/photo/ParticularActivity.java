package com.zmyh.r.photo;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
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
import com.zmyh.r.dailog.InputDialog;
import com.zmyh.r.dailog.ListDialog;
import com.zmyh.r.dailog.MessageDialog;
import com.zmyh.r.dailog.MessageDialog.CallBackListener;
import com.zmyh.r.dao.DBHandler;
import com.zmyh.r.handler.CameraPicObjHandler;
import com.zmyh.r.handler.DateHandle;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.SystemHandle;
import com.zmyh.r.handler.TroopObjHandler;
import com.zmyh.r.handler.TypeDictBox;
import com.zmyh.r.handler.TypeDictHandle;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.interfaces.CallbackForString;
import com.zmyh.r.tool.AddPicDialog;
import com.zmyh.r.tool.LineViewTool;
import com.zmyh.r.tool.ShowMessage;

public class ParticularActivity extends Activity {

    private final static String LAST_PEOPLE = "last_people";
    private final static String LAST_TEL_01 = "last_tel_01";
    private final static String LAST_TEL_02 = "last_tel_02";

    public final static int ONLINE = 1000;
    public final static int MNT = 2000;

    private Context context;
    private Activity mActivity;
    private TroopObj mTroopObj;

    private TypeDictBox mTypeDictBox;

    // private DbUtils db;

    private int status;

    private List<CameraPicObj> deleteCameraPicObjList;
    private List<CameraPicObj> addCameraPicObjList;

    @ViewInject(R.id.title_titleName)
    private TextView title;
    @ViewInject(R.id.title_seek)
    private ImageView seekIcon;
    @ViewInject(R.id.particular_picBox)
    private LinearLayout picBox;
    @ViewInject(R.id.particular_titleName)
    private EditText titleNameInput;
    @ViewInject(R.id.particular_muGs)
    private EditText muGsInput;
    @ViewInject(R.id.particular_remark)
    private EditText remarkInput;
    @ViewInject(R.id.particular_people)
    private EditText peopleInput;
    @ViewInject(R.id.particular_phone1)
    private EditText phoneInput1;
    @ViewInject(R.id.particular_phone2)
    private EditText phoneInput2;
    @ViewInject(R.id.particular_progress)
    private ProgressBar progress;
    @ViewInject(R.id.particular_submitBtn)
    private ImageView submitBtn;
    @ViewInject(R.id.particular_zsTypeText)
    private TextView zsTypeText;
    @ViewInject(R.id.particular_JText)
    private TextView jText;
    @ViewInject(R.id.particular_zgText)
    private TextView zgText;
    @ViewInject(R.id.particular_gfText)
    private TextView gfText;
    @ViewInject(R.id.particular_typeText)
    private TextView typeText;
    @ViewInject(R.id.particular_jzTimeText)
    private TextView jzTimeText;
    @ViewInject(R.id.particular_totalInput)
    private EditText totalInput;
    @ViewInject(R.id.particular_priceInput)
    private EditText priceInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular);
        ViewUtils.inject(this);
        context = this;
        mActivity = this;
        initActivity();
        setOnFocusChangeListener();
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
                case AddPicDialog.APP_IMAGE_REQUEST_CODE:
                    List<String> list = data.getExtras().getStringArrayList(
                            AddPicDialog.CHOICE_KEY);
                    savePic(list);
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

    @OnClick({R.id.title_back, R.id.particular_submitBtn,
            R.id.particular_zsType, R.id.particular_J, R.id.particular_zg,
            R.id.particular_gf, R.id.particular_type, R.id.particular_jzTime})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                close();
                break;
            case R.id.particular_submitBtn:
                saveTroopObj(true);
                break;
            case R.id.particular_zsType:
                setMuZsType();
                break;
            case R.id.particular_J:
                setMuJ();
                break;
            case R.id.particular_zg:
                setMuZg();
                break;
            case R.id.particular_gf:
                setMuGf();
                break;
            case R.id.particular_type:
                setMuType();
                break;
            case R.id.particular_jzTime:
                setMuJzTime();
                break;
        }
    }

    private void setMuJzTime() {
        String type = zsTypeText.getText().toString();
        if (!type.equals("＞") && getTypeDictBox() != null) {
            final List<String> strList = getTypeDictBox().getMuJzTimeList(type);
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
        if (!type.equals("＞") && getTypeDictBox() != null) {
            final List<String> strList = getTypeDictBox().getMuTypeList(type);
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
        if (!type.equals("＞") && getTypeDictBox() != null) {
            final List<String> strList = getTypeDictBox().getMuGfList(type);
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
        if (!type.equals("＞") && getTypeDictBox() != null) {
            final List<String> strList = getTypeDictBox().getMuZgList(type);
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
        if (!type.equals("＞") && getTypeDictBox() != null) {
            final List<String> strList = getTypeDictBox().getMuJList(type);
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

    private void setMuZsType() {
        if (getTypeDictBox() != null) {
            final List<String> typeList = getTypeDictBox().getMuZsTypeList();
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

    }

    private void initMessage(String type) {
        if (getTypeDictBox() != null) {
            if (getTypeDictBox().getMuJList(type) == null) {
                jText.setText("无");
            } else {
                jText.setText("＞");
            }
            if (getTypeDictBox().getMuZgList(type) == null) {
                zgText.setText("无");
            } else {
                zgText.setText("＞");
            }
            if (getTypeDictBox().getMuGfList(type) == null) {
                gfText.setText("无");
            } else {
                gfText.setText("＞");
            }
            if (getTypeDictBox().getMuTypeList(type) == null) {
                typeText.setText("无");
            } else {
                typeText.setText("＞");
            }
            if (getTypeDictBox().getMuJzTimeList(type) == null) {
                jzTimeText.setText("无");
            } else {
                jzTimeText.setText("＞");
            }
        }

    }

    private void close() {
        if (status == ONLINE || !isUpload()) {
            finish();
        } else {
            MessageDialog dialog = new MessageDialog(context);
            dialog.setMessage("确定放弃本次编辑?");
            dialog.setCommitStyle("离开");
            dialog.setCommitListener(new CallBackListener() {

                @Override
                public void callback() {
                    if (!deleteCameraPicObjList.isEmpty()) {
                        for (CameraPicObj obj : deleteCameraPicObjList) {
                            mTroopObj.addChild(obj);
                        }
                    }
                    if (!addCameraPicObjList.isEmpty()) {
                        for (CameraPicObj obj : addCameraPicObjList) {
                            mTroopObj.getCameraPicObjList().remove(obj);
                        }
                    }
                    // TroopObj.save(context, mTroopObj);
                    TroopObjHandler.saveTroopObj(context, mTroopObj);
                    finish();
                }
            });
            dialog.setCancelStyle("取消");
            dialog.setCancelListener(null);
        }
    }

    private boolean isUpload() {
        if (!deleteCameraPicObjList.isEmpty()) {
            Log.e("", "deleteCameraPicObjList");
            return true;
        }
        if (!addCameraPicObjList.isEmpty()) {
            Log.e("", "addCameraPicObjList");
            return true;
        }

        if (!titleNameInput.getText().toString().equals(mTroopObj.getMu_name())) {
            Log.e("", "titleNameInput");
            return true;
        }

        if (!remarkInput.getText().toString().equals(mTroopObj.getMu_desc())) {
            Log.e("", "remarkInput");
            return true;
        }

        if (!peopleInput.getText().toString().equals(mTroopObj.getMu_contact())) {
            Log.e("", "peopleInput");
            return true;
        }

        if (!phoneInput1.getText().toString().equals(mTroopObj.getMu_phone_1())) {
            Log.e("", "phoneInput1");
            return true;
        }

        if (!phoneInput2.getText().toString().equals(mTroopObj.getMu_phone_2())) {
            Log.e("", "phoneInput2");
            return true;
        }

        if (!zsTypeText.getText().toString().equals("＞")
                && !zsTypeText.getText().toString().equals("无")) {
            if (!zsTypeText.getText().toString()
                    .equals(mTroopObj.getMu_sz_type())) {
                Log.e("", "zsTypeText");
                return true;
            }
        }

        if (!jText.getText().toString().equals("＞")
                && !jText.getText().toString().equals("无")) {
            String[] jStrs = jText.getText().toString().split("-");
            if (jStrs != null) {
                if (!jStrs[1].equals(mTroopObj.getMu_j_max())
                        || !jStrs[0].equals(mTroopObj.getMu_j_min())) {
                    Log.e("", "jText");
                    return true;
                }
            }
        }

        if (!zgText.getText().toString().equals("＞")
                && !zgText.getText().toString().equals("无")) {
            String[] zgStrs = zgText.getText().toString().split("-");
            if (zgStrs != null) {
                if (!zgStrs[1].equals(mTroopObj.getMu_zg_max())
                        || !zgStrs[0].equals(mTroopObj.getMu_zg_min())) {
                    Log.e("", "zgText");
                    return true;
                }
            }
        }

        if (!gfText.getText().toString().equals("＞")
                && !gfText.getText().toString().equals("无")) {
            String[] gfStrs = gfText.getText().toString().split("-");
            if (gfStrs != null) {
                if (!gfStrs[1].equals(mTroopObj.getMu_gf_max())
                        || !gfStrs[0].equals(mTroopObj.getMu_gf_min())) {
                    Log.e("", "gfText");
                    return true;
                }
            }
        }

        if (!typeText.getText().toString().equals("＞")
                && !typeText.getText().toString().equals("无")) {
            if (!typeText.getText().toString().equals(mTroopObj.getMu_type())) {
                Log.e("", "typeText");
                return true;
            }
        }

        if (!jzTimeText.getText().toString().equals("＞")
                && !jzTimeText.getText().toString().equals("无")) {
            if (!jzTimeText.getText().toString()
                    .equals(mTroopObj.getMu_jz_time())) {
                Log.e("", "jzTimeText");
                return true;
            }
        }

        if (!totalInput.getText().toString().equals(mTroopObj.getMu_total())) {
            Log.e("", "totalText");
            return true;
        }

        if (!priceInput.getText().toString().equals(mTroopObj.getMu_price())) {
            Log.e("", "priceText");
            return true;
        }
        return false;
    }

    private void saveTroopObj(boolean isFinish) {
        mTroopObj.setMu_name(titleNameInput.getText().toString());
        mTroopObj.setMu_desc(remarkInput.getText().toString());
        mTroopObj.setMu_contact(peopleInput.getText().toString());
        mTroopObj.setMu_phone_1(phoneInput1.getText().toString());
        mTroopObj.setMu_phone_2(phoneInput2.getText().toString());
        mTroopObj.setMu_gs(muGsInput.getText().toString());

        if (!zsTypeText.getText().toString().equals("＞")
                && !zsTypeText.getText().toString().equals("无")) {
            mTroopObj.setMu_sz_type(zsTypeText.getText().toString());
        }
        if (!jText.getText().toString().equals("＞")
                && !jText.getText().toString().equals("无")) {
            String[] strs = jText.getText().toString().split("-");
            mTroopObj.setMu_j_max(strs[1]);
            mTroopObj.setMu_j_min(strs[0]);
        }
        if (!zgText.getText().toString().equals("＞")
                && !zgText.getText().toString().equals("无")) {
            String[] strs = zgText.getText().toString().split("-");
            mTroopObj.setMu_zg_max(strs[1]);
            mTroopObj.setMu_zg_min(strs[0]);
        }
        if (!gfText.getText().toString().equals("＞")
                && !gfText.getText().toString().equals("无")) {
            String[] strs = gfText.getText().toString().split("-");
            mTroopObj.setMu_gf_max(strs[1]);
            mTroopObj.setMu_gf_min(strs[0]);
        }
        if (!typeText.getText().toString().equals("＞")
                && !typeText.getText().toString().equals("无")) {
            mTroopObj.setMu_type(typeText.getText().toString());
        }
        if (!jzTimeText.getText().toString().equals("＞")
                && !jzTimeText.getText().toString().equals("无")) {
            mTroopObj.setMu_jz_time(jzTimeText.getText().toString());
        }

        mTroopObj.setMu_total(totalInput.getText().toString());
        mTroopObj.setMu_price(priceInput.getText().toString());

        for (CameraPicObj obj : mTroopObj.getCameraPicObjList()) {
            // CameraPicObj.save(context, obj);
            CameraPicObjHandler.saveCameraPicObj(context, obj);
        }
        mTroopObj.setUpload(true);
        TroopObjHandler.saveTroopObj(context, mTroopObj);
        // TroopObj.save(context, mTroopObj);

        SystemHandle.saveStringMessage(context, LAST_PEOPLE, peopleInput.getText().toString());
        SystemHandle.saveStringMessage(context, LAST_TEL_01, phoneInput1.getText().toString());
        SystemHandle.saveStringMessage(context, LAST_TEL_02, phoneInput2.getText().toString());
        if (isFinish) {
            for (CameraPicObj obj : deleteCameraPicObjList) {
                // FileUtil.deleteFile(obj.getMediumFilePath());
                CameraPicObjHandler.saveDeleteID(context, obj);
                // CameraPicObj.delete(context, obj);
            }
            finish();
            // if (deleteCameraPicObjList.isEmpty()) {
            // finish();
            // } else if (isFinish) {
            // deletepic(isFinish);
            // }
        }

    }

    private void setOnFocusChangeListener() {
        OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean b) {
                if (b) {
                    v.setBackgroundResource(R.drawable.white_background_green_frame_0);
                } else {
                    v.setBackgroundResource(R.drawable.white_background_white_frame_0);
                }
            }

        };
        titleNameInput.setOnFocusChangeListener(focusChangeListener);
        remarkInput.setOnFocusChangeListener(focusChangeListener);
        peopleInput.setOnFocusChangeListener(focusChangeListener);
        phoneInput1.setOnFocusChangeListener(focusChangeListener);
        phoneInput2.setOnFocusChangeListener(focusChangeListener);
        muGsInput.setOnFocusChangeListener(focusChangeListener);
        totalInput.setOnFocusChangeListener(focusChangeListener);
        priceInput.setOnFocusChangeListener(focusChangeListener);
    }

    private void initActivity() {
        title.setText("苗木详细");
        seekIcon.setVisibility(View.INVISIBLE);
        mTroopObj = TroopObjBox.getSaveTroopObj();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            status = b.getInt("status");
            if (status == ONLINE) {
                submitBtn.setVisibility(View.GONE);
                titleNameInput.setEnabled(false);
                remarkInput.setEnabled(false);
                peopleInput.setEnabled(false);
                phoneInput1.setEnabled(false);
                phoneInput2.setEnabled(false);
            }
        }

        if (mTroopObj == null) {
            finish();
        } else {
            setActivity(mTroopObj);
        }
        addCameraPicObjList = new ArrayList<CameraPicObj>();
        deleteCameraPicObjList = new ArrayList<CameraPicObj>();
    }

    private void setActivity(TroopObj obj) {
        initPicBox(obj.getCameraPicObjList());
        titleNameInput.setText(obj.getMu_name());
        remarkInput.setText(obj.getMu_desc());

        if (obj.getMu_contact().equals("")) {
            peopleInput.setText(SystemHandle.getString(context, LAST_PEOPLE));
        } else {
            peopleInput.setText(obj.getMu_contact());
        }

        if (obj.getMu_phone_1().equals("")) {
            phoneInput1.setText(SystemHandle.getString(context, LAST_TEL_01));
        } else {
            phoneInput1.setText(obj.getMu_phone_1());
        }

        if (obj.getMu_phone_2().equals("")) {
            phoneInput2.setText(SystemHandle.getString(context, LAST_TEL_02));
        } else {
            phoneInput2.setText(obj.getMu_phone_2());
        }

        muGsInput.setText(obj.getMu_gs());
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
    }

    private void savePic(String id) {
        // CameraPicObj obj = CameraPicObj.getSaveObj(SystemHandle.getString(
        // context, id));
        CameraPicObj obj;
        try {
            obj = CameraPicObjHandler.getCameraPicObj(context, id);
            mTroopObj.addChild(obj);
            addCameraPicObjList.add(obj);
            setActivity(mTroopObj);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void savePic(List<String> list) {
        for (String id : list) {
            CameraPicObj obj;
            try {
                obj = CameraPicObjHandler.getCameraPicObj(context, id);
                try {
                    CameraPicObj newObj = CameraPicObj.copy(obj);
                    mTroopObj.addChild(newObj);
                    addCameraPicObjList.add(newObj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (DbException e1) {
                e1.printStackTrace();
            }

        }
        setActivity(mTroopObj);
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

                mTroopObj.addChild(obj);
                addCameraPicObjList.add(obj);
                setActivity(mTroopObj);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteTroop() {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setMessage("是否确定删除整组苗木标记~~");
        dialog.setCommitListener(new CallBackListener() {

            @Override
            public void callback() {
                progress.setVisibility(View.VISIBLE);
                TroopObjHandler.deleteTroopObj(context, mTroopObj,
                        new CallbackForBoolean() {

                            @Override
                            public void callback(boolean b) {
                                Intent i = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("isUpload", true);
                                i.putExtras(bundle);
                                setResult(PhotoActivity.ResultCode, i);
                                finish();
                            }
                        });
            }
        });
        dialog.setCancelListener(null);
    }

    private void initPicBox(List<CameraPicObj> list) {
        picBox.removeAllViews();
        if (list.size() < 9 && status != ONLINE) {
            picBox.addView(getPicView(null));
        }
        for (CameraPicObj obj : list) {
            picBox.addView(LineViewTool.getSpaceView(context));
            picBox.addView(getPicView(obj));
        }
    }

    private PicView getPicView(final CameraPicObj obj) {
        PicView img = new PicView(mActivity, mTroopObj, obj);
        // img.goneDelete();
        if (status == ONLINE) {
            img.goneDelete();
        } else {
            img.setListener(new CallbackForBoolean() {

                @Override
                public void callback(boolean b) {
                    if (b) {
                        deleteCameraPicObjList.add(obj);
                        mTroopObj.getCameraPicObjList().remove(obj);
                        // saveTroopObj(false);
                        setActivity(mTroopObj);
                    } else {
                        deleteTroop();
                    }
                }
            });
        }
        return img;
    }

    private void deletepic(final boolean isFinish) {
        progress.setVisibility(View.VISIBLE);

        StringBuffer key = new StringBuffer();
        for (CameraPicObj obj : deleteCameraPicObjList) {
            key.append(obj.getMu_photo_key());
            key.append("%7C");
        }

        String url = UrlHandle.getDeletePic() + "?user_id="
                + UserObjHandle.getUsetId(context) + "&keys="
                + key.toString().substring(0, key.length() - 3);

        RequestParams params = HttpUtilsBox.getRequestParams();

        HttpUtilsBox.getHttpUtil().send(HttpMethod.DELETE, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        exception.printStackTrace();
                        progress.setVisibility(View.GONE);
                        ShowMessage.showFailure(context);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        Log.d("", result);
                        JSONObject json = JsonHandle.getJSON(result);
                        if (json != null) {
                            for (CameraPicObj obj : deleteCameraPicObjList) {
                                // CameraPicObj.delete(context, obj);
                                CameraPicObjHandler.deleteCameraPicObj(context,
                                        obj, null);
                            }
                            if (isFinish) {
                                finish();
                            }
                        }
                    }
                });

    }

    private TypeDictBox getTypeDictBox() {
        if (mTypeDictBox != null) {
            return mTypeDictBox;
        }
        ShowMessage.showToast(context, "网络不佳，无法获取数据，请检查网络");
        return null;
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
                        mTypeDictBox = TypeDictHandle.getTypeDictBox(context);
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

}
