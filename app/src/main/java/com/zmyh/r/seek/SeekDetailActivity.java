package com.zmyh.r.seek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.zmyh.r.dailog.CustomDialog;
import com.zmyh.r.dailog.ListDialog;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.MuNameJHandler;
import com.zmyh.r.handler.TypeDictBox;
import com.zmyh.r.handler.TypeDictHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

public class SeekDetailActivity extends Activity {

    public final static String IS_MAP = "is_map";

    private final static String TITLE = "title.$like";
    private final static String TREE_MU_SZ_TYPE = "tree.mu_sz_type";
    private final static String TREE_MU_J_MIN_GTE = "tree.mu_j_min";
    private final static String TREE_MU_J_MAX_LTE = "tree.mu_j_max";
    private final static String TREE_MU_ZG_MIN_GTE = "tree.mu_zg_min";
    private final static String TREE_MU_ZG_MAX_LTE = "tree.mu_zg_max";
    private final static String TREE_MU_GF_MIN_GTE = "tree.mu_gf_min";
    private final static String TREE_MU_GF_MAX_LTE = "tree.mu_gf_max";
    private final static String TREE_MU_TYPE = "tree.mu_type";
    private final static String TREE_MU_JZ_TIME = "tree.mu_jz_time";
    private final static String TREE_MU_TOTAL_GTE = "tree.mu_total.$gte";
    private final static String TREE_MU_PRICE_LTE = "tree.mu_price.$lte";

    private Context context;
    private String mmChannel = "", mmArea = "";
    private boolean is_map;

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
    @ViewInject(R.id.seek_detail_toolBox)
    private LinearLayout toolBox;
    @ViewInject(R.id.seek_detail_toolText)
    private TextView toolText;
    @ViewInject(R.id.seek_detail_confirmBtn)
    private TextView confirmBtn;

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
            is_map = b.getBoolean(IS_MAP);
            mmChannel = b.getString(SeekActivity.MM_CHANNEL);
            mmArea = b.getString(SeekActivity.MM_AREA);
            if (mmChannel.equals("00001")) {
                toolBox.setVisibility(View.GONE);
                toolText.setVisibility(View.GONE);
                confirmBtn.setVisibility(View.GONE);
            } else {
                toolBox.setVisibility(View.VISIBLE);
                toolText.setVisibility(View.VISIBLE);
                confirmBtn.setVisibility(View.VISIBLE);
            }
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
        String url = UrlHandle.getMmPost() + "?query="
                + JsonHandle.getHttpJsonToString(keys, values)
                + "&sort=-createAt&p=1&l=99";

        if (is_map) {
            Intent i = new Intent();
            Bundle b = new Bundle();
            b.putBoolean("ok", true);
            b.putString("url", url);
            i.putExtras(b);
            setResult(TreeMapActivityV2.RC, i);
            finish();
        } else {
            Bundle b = new Bundle();
            b.putString("url", url);
            b.putString(SeekActivity.MM_CHANNEL, mmChannel);
            Passageway.jumpActivity(context, SeekContentActivity.class, b);
        }
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

        keys.add("type");
        values.add("services");

        keys.add("mmChannel");
        values.add(mmChannel);

        keys.add("mmArea");
        values.add(mmArea);

        String seek = getTextString(seekInput);
        if (!seek.equals("")) {
            keys.add(TITLE);
            values.add(seek);
        }

        String muName = getTextString(muNameText);
        if (!muName.equals("")) {
            keys.add(TITLE);
            values.add(muName);
        }

        String zsType = getTextString(zsTypeText);
        if (!zsType.equals("")) {
            keys.add(TREE_MU_SZ_TYPE);
            values.add(zsType);
        }

        String j = getTextString(jText);
        if (!j.equals("")) {
            String[] js = getTextValue(j);
            keys.add(TREE_MU_J_MIN_GTE);
            values.add(js[0]);
            keys.add(TREE_MU_J_MAX_LTE);
            values.add(js[1]);
        }

        String zg = getTextString(zgText);
        if (!zg.equals("")) {
            String[] zgs = getTextValue(zg);
            keys.add(TREE_MU_ZG_MIN_GTE);
            values.add(zgs[0]);
            keys.add(TREE_MU_ZG_MAX_LTE);
            values.add(zgs[1]);
        }

        String gf = getTextString(gfText);
        if (!gf.equals("")) {
            String[] gfs = getTextValue(gf);
            keys.add(TREE_MU_GF_MIN_GTE);
            values.add(gfs[0]);
            keys.add(TREE_MU_GF_MAX_LTE);
            values.add(gfs[1]);
        }

        String type = getTextString(typeText);
        if (!type.equals("")) {
            keys.add(TREE_MU_TYPE);
            values.add(type);
        }

        String jzTime = getTextString(jzTimeText);
        if (!jzTime.equals("")) {
            keys.add(TREE_MU_JZ_TIME);
            values.add(jzTime);
        }

        String total = getTextString(totalInput);
        if (!total.equals("")) {
            keys.add(TREE_MU_TOTAL_GTE);
            values.add(total);
        }

        String price = getTextString(priceInput);
        if (!price.equals("")) {
            keys.add(TREE_MU_PRICE_LTE);
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

    private String[] getKsys() {
        String[] k = new String[]{"type", "mmChannel", "mmArea", TITLE,
                TITLE, TREE_MU_SZ_TYPE, TREE_MU_J_MIN_GTE, TREE_MU_J_MAX_LTE,
                TREE_MU_ZG_MIN_GTE, TREE_MU_ZG_MAX_LTE, TREE_MU_GF_MIN_GTE,
                TREE_MU_GF_MAX_LTE, TREE_MU_TYPE, TREE_MU_JZ_TIME,
                TREE_MU_TOTAL_GTE, TREE_MU_PRICE_LTE};
        return k;
    }

    private String getTextString(TextView view) {
        String str = view.getText().toString();
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
                        if (p == 0) {
                            jzTimeText.setText(">");
                        } else {
                            jzTimeText.setText(strList.get(p));
                        }
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
                        if (p == 0) {
                            typeText.setText(">");
                        } else {
                            typeText.setText(strList.get(p));
                        }
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
        if (!type.equals(">")) {
            final List<String> strList = mTypeDictBox.getMuGfList(type);
            if (strList == null) {
                gfText.setText("无");
            } else {
                strList.add(0, "全部");
                strList.add("自定义");
                final ListDialog dialog = new ListDialog(context);
                dialog.setTitleGone();
                dialog.setList(strList);
                dialog.setLayout();
                dialog.setItemListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int p, long arg3) {
                        if (p == 0) {
                            gfText.setText(">");
                        } else if (p == strList.size() - 1) {
                            showCustomDialog(gfText);
                        } else {
                            gfText.setText(strList.get(p));
                        }
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
        if (!type.equals(">")) {
            final List<String> strList = mTypeDictBox.getMuZgList(type);
            if (strList == null) {
                zgText.setText("无");
            } else {
                strList.add(0, "全部");
                strList.add("自定义");
                final ListDialog dialog = new ListDialog(context);
                dialog.setTitleGone();
                dialog.setList(strList);
                dialog.setLayout();
                dialog.setItemListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int p, long arg3) {
                        if (p == 0) {
                            zgText.setText(">");
                        } else if (p == strList.size() - 1) {
                            showCustomDialog(zgText);
                        } else {
                            zgText.setText(strList.get(p));
                        }
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
        if (!type.equals(">")) {
            final List<String> strList = mTypeDictBox.getMuJList(type);
            if (strList == null) {
                jText.setText("无");
            } else {
                strList.add(0, "全部");
                strList.add("自定义");
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
                        } else if (p == strList.size() - 1) {
                            showCustomDialog(jText);
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

    private void showCustomDialog(final TextView view) {
        CustomDialog dialog = new CustomDialog(context);
        dialog.setListener(new CustomDialog.CustomListener() {
            @Override
            public void callback(int min, int max) {
                view.setText(min + "-" + max);
            }
        });
    }

    private void setMuZsType() {
        final List<String> typeList = mTypeDictBox.getMuZsTypeList();
        typeList.add(0, "全部");
        final ListDialog dialog = new ListDialog(context);
        dialog.setTitleGone();
        dialog.setLayout();
        dialog.setList(typeList);
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
