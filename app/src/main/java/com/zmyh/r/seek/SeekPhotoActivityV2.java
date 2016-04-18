package com.zmyh.r.seek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.dailog.ListDialog;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.MuNameJHandler;
import com.zmyh.r.handler.TypeDictBox;
import com.zmyh.r.handler.TypeDictHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SeekPhotoActivityV2 extends Activity {

    private final static String MU_NAME = "mu_name";
    private final static String MU_SZ_TYPE = "mu_sz_type";
    private final static String MU_J_MIN = "mu_j_min";
    private final static String MU_J_MAX = "mu_j_max";
    private final static String MU_ZG_MIN = "mu_zg_min";
    private final static String MU_ZG_MAX = "mu_zg_max";
    private final static String MU_GF_MIN = "mu_gf_min";
    private final static String MU_GF_MAX = "mu_gf_max";
    private final static String MU_TYPE = "mu_type";
    private final static String MU_JZ_TIME = "mu_jz_time";
    private final static String MU_TOTAL = "mu_total";
    private final static String MU_PRICE = "mu_price";

    private Context context;
    private String mmChannel = "", mmArea = "";

    private TypeDictBox mTypeDictBox;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.seek_detail_seekInput)
    private EditText seekInput;
    @ViewInject(R.id.seek_detail_progress)
    private ProgressBar progress;
    @ViewInject(R.id.seek_detail_zsTypeText)
    private TextView zsTypeText;
    @ViewInject(R.id.seek_detail_JText)
    private TextView jText;
    @ViewInject(R.id.seek_detail_zgText)
    private TextView zgText;
    @ViewInject(R.id.seek_detail_gfText)
    private TextView gfText;
    @ViewInject(R.id.seek_detail_typeText)
    private TextView typeText;
    @ViewInject(R.id.seek_detail_jzTimeText)
    private TextView jzTimeText;
    @ViewInject(R.id.seek_detail_muNameText)
    private TextView muNameText;
    @ViewInject(R.id.seek_detail_totalInput)
    private EditText totalInput;
    @ViewInject(R.id.seek_detail_priceInput)
    private EditText priceInput;

    private InputMethodManager imm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek_detail);
        ViewUtils.inject(this);
        context = this;

        initActivity();
        downloadData();
    }

    private void initActivity() {
        titleName.setText("搜索");
        imm = (InputMethodManager) seekInput.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            mmChannel = b.getString(SeekActivity.MM_CHANNEL);
            mmArea = b.getString(SeekActivity.MM_AREA);
        }
    }

    @OnClick({R.id.title_back, R.id.seek_detail_zsType, R.id.seek_detail_J,
            R.id.seek_detail_zg, R.id.seek_detail_gf, R.id.seek_detail_type,
            R.id.seek_detail_jzTime, R.id.seek_detail_muName,
            R.id.seek_detail_seekBtn, R.id.seek_detail_confirmBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.seek_detail_zsType:
                setMuZsType();
                break;
            case R.id.seek_detail_J:
                setMuJ();
                break;
            case R.id.seek_detail_zg:
                setMuZg();
                break;
            case R.id.seek_detail_gf:
                setMuGf();
                break;
            case R.id.seek_detail_type:
                setMuType();
                break;
            case R.id.seek_detail_jzTime:
                setMuJzTime();
                break;
            case R.id.seek_detail_muName:
                setMuName();
                break;
            case R.id.seek_detail_confirmBtn:
            case R.id.seek_detail_seekBtn:
                seekBtn();
                break;
        }
    }

    private void seekBtn() {
        Map<String, ArrayList<String>> query = getQuery();
        String[] keys = getArray(query.get("keys"));
        String[] values = getArray(query.get("values"));

        Bundle b = new Bundle();
        b.putStringArray("keys", keys);
        b.putStringArray("values", values);
        Passageway.jumpActivity(context, SeekContentActivity.class, b);
    }

    private String[] getArray(ArrayList<String> arrayList) {
        String[] strs = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            strs[i] = arrayList.get(i);
        }
        return strs;
    }

    private Map<String, ArrayList<String>> getQuery() {
        Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();
        map.put("keys", keys);
        map.put("values", values);

        String seek = getTextString(seekInput);
        if (!seek.equals("")) {
            keys.add(MU_NAME);
            values.add(seek);
        }

        String muName = getTextString(muNameText);
        if (!muName.equals("")) {
            keys.add(MU_NAME);
            values.add(muName);
        }

        String zsType = getTextString(zsTypeText);
        if (!zsType.equals("")) {
            keys.add(MU_SZ_TYPE);
            values.add(zsType);
        }

        String j = getTextString(jText);
        if (!j.equals("")) {
            String[] js = getTextValue(j);
            keys.add(MU_J_MIN);
            values.add(js[0]);
            keys.add(MU_J_MAX);
            values.add(js[1]);
        }

        String zg = getTextString(zgText);
        if (!zg.equals("")) {
            String[] zgs = getTextValue(zg);
            keys.add(MU_ZG_MIN);
            values.add(zgs[0]);
            keys.add(MU_ZG_MAX);
            values.add(zgs[1]);
        }

        String gf = getTextString(gfText);
        if (!gf.equals("")) {
            String[] gfs = getTextValue(gf);
            keys.add(MU_GF_MIN);
            values.add(gfs[0]);
            keys.add(MU_GF_MAX);
            values.add(gfs[1]);
        }

        String type = getTextString(typeText);
        if (!type.equals("")) {
            keys.add(MU_TYPE);
            values.add(type);
        }

        String jzTime = getTextString(jzTimeText);
        if (!jzTime.equals("")) {
            keys.add(MU_JZ_TIME);
            values.add(jzTime);
        }

        String total = getTextString(totalInput);
        if (!total.equals("")) {
            keys.add(MU_TOTAL);
            values.add(total);
        }

        String price = getTextString(priceInput);
        if (!price.equals("")) {
            keys.add(MU_PRICE);
            values.add(price);
        }

        return map;

    }

    private String[] getValues() {
        String[] j = getTextValue(getTextString(jText));
        String[] zg = getTextValue(getTextString(zgText));
        String[] gf = getTextValue(getTextString(gfText));
        String[] v = new String[]{"services", mmChannel, mmArea,
                getTextString(seekInput), getTextString(muNameText),
                getTextString(zsTypeText), j[0], j[1], zg[0], zg[1], gf[0],
                gf[1], getTextString(typeText), getTextString(jzTimeText),
                getTextString(totalInput), getTextString(priceInput)};
        return v;
    }

    private String getTextString(TextView view) {
        String str = view.getText().toString();
        if (str.equals(">")) {
            return "";
        }
        if (str.equals(">")) {
            return "";
        }
        if (str.equals("无")) {
            return "";
        }
        return str;
    }

    private String getTextString(EditText view) {
        String str = view.getText().toString();
        return str;
    }

    private String[] getTextValue(String text) {
        String[] strs = text.split("-");
        if (strs.length != 2) {
            return new String[]{"", ""};
        }
        return strs;
    }

    private void setMuName() {
        final String[] muNameList = MuNameJHandler.getMuName();
        final ListDialog dialog = new ListDialog(context);
        dialog.setTitleGone();
        dialog.setList(muNameList);
        dialog.setLayout();
        dialog.setItemListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int p,
                                    long arg3) {
                if (p == 0) {
                    muNameText.setText(">");
                } else {
                    muNameText.setText(muNameList[p]);
                }
                dialog.dismiss();
            }
        });
    }

    private void setMuJzTime() {
        String type = zsTypeText.getText().toString();
        if (!type.equals(">")) {
            final List<String> strList = mTypeDictBox.getMuJzTimeList(type);
            if (strList == null) {
                jzTimeText.setText("无");
            } else {
                strList.add(0, "全部");
                final ListDialog dialog = new ListDialog(context);
                dialog.setTitleGone();
                dialog.setList(strList);
                dialog.setLayout();
                dialog.setItemListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int p, long arg3) {
                        dialog.dismiss();
                        if (p == 0) {
                            jzTimeText.setText(">");
                        } else {
                            jzTimeText.setText(strList.get(p));
                        }
                    }
                });
            }
        } else {
            ShowMessage.showToast(context, "请先选择类别");
        }
    }

    private void setMuType() {
        String type = zsTypeText.getText().toString();
        if (!type.equals(">")) {
            final List<String> strList = mTypeDictBox.getMuTypeList(type);
            if (strList == null) {
                typeText.setText("无");
            } else {
                strList.add(0, "全部");
                final ListDialog dialog = new ListDialog(context);
                dialog.setTitleGone();
                dialog.setList(strList);
                dialog.setLayout();
                dialog.setItemListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int p, long arg3) {
                        dialog.dismiss();
                        if (p == 0) {
                            typeText.setText(">");
                        } else {
                            typeText.setText(strList.get(p));
                        }
                    }
                });
            }
        } else {
            ShowMessage.showToast(context, "请先选择类别");
        }
    }

    private void setMuGf() {
        String type = zsTypeText.getText().toString();
        if (!type.equals(">")) {
            final List<String> strList = mTypeDictBox.getMuGfList(type);
            if (strList == null) {
                gfText.setText("无");
            } else {
                strList.add(0, "全部");
                final ListDialog dialog = new ListDialog(context);
                dialog.setTitleGone();
                dialog.setList(strList);
                dialog.setLayout();
                dialog.setItemListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int p, long arg3) {
                        dialog.dismiss();
                        if (p == 0) {
                            gfText.setText(">");
                        } else {
                            gfText.setText(strList.get(p));
                        }

                    }
                });
            }
        } else {
            ShowMessage.showToast(context, "请先选择类别");
        }
    }

    private void setMuZg() {
        String type = zsTypeText.getText().toString();
        if (!type.equals(">")) {
            final List<String> strList = mTypeDictBox.getMuZgList(type);
            if (strList == null) {
                zgText.setText("无");
            } else {
                strList.add(0, "全部");
                final ListDialog dialog = new ListDialog(context);
                dialog.setTitleGone();
                dialog.setList(strList);
                dialog.setLayout();
                dialog.setItemListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int p, long arg3) {
                        dialog.dismiss();
                        if (p == 0) {
                            zgText.setText(">");
                        } else {
                            zgText.setText(strList.get(p));
                        }

                    }
                });
            }
        } else {
            ShowMessage.showToast(context, "请先选择类别");
        }
    }

    private void setMuJ() {
        String type = zsTypeText.getText().toString();
        if (!type.equals(">")) {
            final List<String> strList = mTypeDictBox.getMuJList(type);
            if (strList == null) {
                jText.setText("无");
            } else {
                strList.add(0, "全部");
                final ListDialog dialog = new ListDialog(context);
                dialog.setTitleGone();
                dialog.setList(strList);
                dialog.setLayout();
                dialog.setItemListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int p, long arg3) {
                        dialog.dismiss();
                        if (p == 0) {
                            jText.setText(">");
                        } else {
                            jText.setText(strList.get(p));
                        }
                    }
                });
            }
        } else {
            ShowMessage.showToast(context, "请先选择类别");
        }

    }

    private void setMuZsType() {
        final List<String> typeList = mTypeDictBox.getMuZsTypeList();
        typeList.add(0, "全部");
        final ListDialog dialog = new ListDialog(context);
        dialog.setTitleGone();
        dialog.setList(typeList);
        dialog.setLayout();
        dialog.setItemListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int p,
                                    long arg3) {
                if (p == 0) {
                    zsTypeText.setText(">");
                } else {
                    zsTypeText.setText(typeList.get(p));
                }
                initMessage(typeList.get(p));
                dialog.dismiss();
            }

        });
    }

    private void initMessage(String type) {
        if (mTypeDictBox.getMuJList(type) == null) {
            jText.setText("无");
        } else {
            jText.setText(">");
        }

        if (mTypeDictBox.getMuZgList(type) == null) {
            zgText.setText("无");
        } else {
            zgText.setText(">");
        }

        if (mTypeDictBox.getMuGfList(type) == null) {
            gfText.setText("无");
        } else {
            gfText.setText(">");
        }

        if (mTypeDictBox.getMuTypeList(type) == null) {
            typeText.setText("无");
        } else {
            typeText.setText(">");
        }

        if (mTypeDictBox.getMuJzTimeList(type) == null) {
            jzTimeText.setText("无");
        } else {
            jzTimeText.setText(">");
        }
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
}
