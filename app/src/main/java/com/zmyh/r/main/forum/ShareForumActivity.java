package com.zmyh.r.main.forum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.box.CameraPicObj;
import com.zmyh.r.box.ServerObj;
import com.zmyh.r.box.TroopObj;
import com.zmyh.r.choise.ChoiseImageFileList;
import com.zmyh.r.choise.ChoiseImageListActivity;
import com.zmyh.r.choise.ImgFileListActivity;
import com.zmyh.r.city.AreaActivity;
import com.zmyh.r.dailog.ListDialog;
import com.zmyh.r.dailog.MessageDialog;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.CityObjHandler;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.ServerObjHandler;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.http.HttpFlieBox;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.PostFile;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.PostFileCallback;
import com.zmyh.r.photo.TroopObjBox;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

public class ShareForumActivity extends Activity {

    public final static String IS_EMPHASIS = "isEmphasis";

    public final static int resultCode = 2046;
    private final static String NULL = "null";

    private Context context;
    private String fileName = "", mmArea = "";

    private GridBaseAdapter gba = null;

    private List<String> localList;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_next)
    private TextView confirm;
    @ViewInject(R.id.forum_share_titleInput)
    private EditText titleInput;
    @ViewInject(R.id.forum_share_contentInput)
    private EditText contentInput;
    @ViewInject(R.id.forum_share_imgGrid)
    private GridView imgGrid;
    @ViewInject(R.id.forum_share_progress)
    private ProgressBar progress;
    @ViewInject(R.id.forum_share_cityText)
    private TextView cityText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_share);
        context = this;
        ViewUtils.inject(this);

        initAcitvity();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChoiseImageFileList.removeAll();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ShareForumActivity.resultCode:
                if (data != null) {
                    Bundle b = data.getExtras();
                    if (b != null) {
                        setImagerGrid();
                    }
                }
                break;
            case Passageway.CAMERA_REQUEST_CODE:
                if (!fileName.equals("")) {
                    File f = new File(HttpFlieBox.getImagePath(), fileName);
                    if (f.exists()) {
                        ChoiseImageFileList.add(f
                                .toString());
                        setImagerGrid();
                    }

                }
                break;
            case AreaActivity.RequestCode:
                if (data != null) {
                    Bundle b = data.getExtras();
                    if (b.getBoolean("ok")) {
                        setCity(b.getString("Area_name"), b.getString("Area_id"));
                    }
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            close();
        }
        return false;
    }

    @OnClick({R.id.title_back, R.id.title_next, R.id.forum_share_city})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                close();
                break;
            case R.id.title_next:
                if (UserObjHandle.isLogin(context, true)) {
                    uploadSpeak();
                }
                break;
            case R.id.forum_share_city:
                Bundle b = new Bundle();
                b.putBoolean(AreaActivity.COMMON, true);
                Passageway.jumpActivity(context, AreaActivity.class,
                        AreaActivity.RequestCode, b);
                break;
        }
    }

    private void close() {
        if (isCompile()) {
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

    private void setImagerGrid() {
        if (gba != null) {
            gba = null;
            imgGrid.setAdapter(null);
        }
        gba = new GridBaseAdapter(
                checkImgList(ChoiseImageFileList.getChoiseImageFileList()));
        imgGrid.setAdapter(gba);
    }

    private void initAcitvity() {
        titleName.setText("发帖内容");
        confirm.setVisibility(View.VISIBLE);
        confirm.setText("发送");

        setCity(CityObjHandler.getCityName(context),
                CityObjHandler.getCityId(context));

        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.getBoolean(IS_EMPHASIS)) {
                setContent(TroopObjBox.getSaveTroopObj());
            }
        }

        gba = new GridBaseAdapter(
                checkImgList(ChoiseImageFileList.getChoiseImageFileList()));
        imgGrid.setAdapter(gba);
    }

    private void setCity(String cityName, String cityId) {
        cityText.setText(cityName);
        mmArea = cityId;
    }

    private List<String> checkImgList(List<String> arrayList) {
        localList = new ArrayList<String>();
        localList.addAll(arrayList);
        if (localList.size() < 9) {
            localList.add(NULL);
        }
        return localList;
    }

    private boolean isCompile() {
        if (!contentInput.getText().toString().equals("")) {
            return true;
        }
        return false;
    }

    private boolean isFull() {
        boolean isFull = true;
        if (contentInput.getText().toString().equals("")) {
            isFull = false;
        }
        if (!isFull) {
            ShowMessage.showToast(context, "信息不完整");
        }
        return isFull;
    }

    private void uploadSpeak() {
        if (isFull()) {
            progress.setVisibility(View.VISIBLE);
            String url = UrlHandle.getMmPost();
            HttpFlieBox box = new HttpFlieBox();
            box.addParams("poster", UserObjHandle.getUsetId(context));
            // box.addParams("title", titleInput.getText().toString());
            box.addParams("content", contentInput.getText().toString());
            box.addParams("type", "topic");
            box.addParams("mmArea", mmArea);
            box.addFileList("pic", localList);
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
                            setResult(resultCode, i);
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

        }

    }

    public void setContent(TroopObj obj) {
        contentInput.setText(obj.getMu_desc() + "\n" + TroopObj.getContent(obj));
        for (CameraPicObj c : obj.getCameraPicObjList()) {
            ChoiseImageFileList.add(c.getMediumFilePath());
        }
    }

    class GridBaseAdapter extends BaseAdapter {

        private List<String> list;
        private LayoutInflater infater;

        public GridBaseAdapter(List<String> list) {
            this.list = list;
            infater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        private void showImageComform() {
            final ListDialog dialog = new ListDialog(context);
            dialog.setTitleGone();
            dialog.setList(new String[]{"拍照", "本地相册"});
            dialog.setItemListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int p,
                                        long arg3) {
                    switch (p) {
                        case 0:
                            fileName = Passageway.takePhoto(context);
                            break;
                        case 1:
                            Passageway.jumpActivity(context,
                                    ImgFileListActivity.class,
                                    ShareForumActivity.resultCode);
                            break;
                    }
                    dialog.dismiss();
                }
            });
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            if (convertView == null) {
                convertView = infater.inflate(R.layout.choise_image, null);
            }

            final String path = list.get(position);

            ImageView img = (ImageView) convertView
                    .findViewById(R.id.choise_image_img);

            int width = WinTool.getWinWidth(context) / 4 - 20;
            img.setLayoutParams(new RelativeLayout.LayoutParams(width, width));
            int p = 0;
            if (!path.equals(NULL)) {
                img.setPadding(p, p, p, p);
                DownloadImageLoader.loadImageForFile(img, path);
            } else {
                p = 28;
                img.setPadding(p, p, p, p);
                DownloadImageLoader.loadImageForID(img, R.drawable.check_bg);
            }

            final int i = position;
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (path.equals(NULL)) {
                        showImageComform();
                    } else {
                        Bundle b = new Bundle();
                        b.putInt("position", i);
                        Passageway.jumpActivity(context,
                                ChoiseImageListActivity.class,
                                ShareForumActivity.resultCode, b);
                    }
                }

            });

            return convertView;
        }
    }

}
