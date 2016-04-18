package com.zmyh.r.photo;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.box.CameraPicObj;
import com.zmyh.r.box.TroopObj;
import com.zmyh.r.camera.util.FileUtil;
import com.zmyh.r.dailog.InputDialog;
import com.zmyh.r.dailog.MessageDialog;
import com.zmyh.r.dailog.MessageDialog.CallBackListener;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.CameraPicObjHandler;
import com.zmyh.r.handler.DateHandle;
import com.zmyh.r.handler.SystemHandle;
import com.zmyh.r.handler.TroopObjHandler;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.interfaces.CallbackForString;
import com.zmyh.r.tool.AddPicDialog;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;
import com.zmyh.r.view.InsideGridView;

public class NewTroopActivity extends Activity {

    private Context context;
    private Activity mActivity;
    private TroopObj mTroopObj;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_seek)
    private ImageView seekIcon;
    @ViewInject(R.id.title_next)
    private TextView commit;
    @ViewInject(R.id.newTroop_time)
    private TextView time;
    @ViewInject(R.id.newTroop_picGrid)
    private InsideGridView picGrid;
    @ViewInject(R.id.newTroop_titleName)
    private EditText troopNameInput;
    @ViewInject(R.id.newTroop_remark)
    private EditText remarkInput;
    @ViewInject(R.id.newTroop_gs)
    private EditText gsInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newtroop);
        ViewUtils.inject(this);
        context = this;
        mActivity = this;
        initActivity();
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

    @OnClick({R.id.title_back, R.id.title_next})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                close();
                break;
            case R.id.title_next:
                saveTroop();
                break;
        }
    }

    private void close() {
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
    }

    private void saveTroop() {
        if (mTroopObj.getCameraPicObjList().isEmpty()) {
            ShowMessage.showToast(context, "至少添加一张图片");
            return;
        }
        mTroopObj.setMu_name(troopNameInput.getText().toString());
        mTroopObj.setMu_desc(remarkInput.getText().toString());
        mTroopObj.setMu_gs(gsInput.getText().toString());

        for (CameraPicObj obj : mTroopObj.getCameraPicObjList()) {
            CameraPicObjHandler.saveCameraPicObj(context, obj);
        }
        mTroopObj.setUpload(true);
        TroopObjHandler.saveTroopObj(context, mTroopObj);

        Intent i = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isUpload", true);
        i.putExtras(bundle);
        setResult(PhotoActivity.ResultCode, i);
        finish();
    }

    private void initActivity() {
        titleName.setText("新建");
        seekIcon.setVisibility(View.GONE);
        commit.setVisibility(View.VISIBLE);
        commit.setText("完成");
        mTroopObj = new TroopObj();
        mTroopObj.setMu_createTime(DateHandle.getTime());
        mTroopObj.initMu_id();

        setActivity(mTroopObj);
    }

    private void setActivity(TroopObj obj) {
        time.setText(obj.getCreateTime());
        picGrid.setAdapter(new PicBaseAdapter(obj.getCameraPicObjList()));
    }

    private void savePic(List<String> list) {
        for (String id : list) {
            try {
                CameraPicObj obj = CameraPicObjHandler.getCameraPicObj(context,
                        id);
                mTroopObj.addChild(obj);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        setActivity(mTroopObj);
    }

    private void savePic(String id) {
        try {
            CameraPicObj obj = CameraPicObjHandler.getCameraPicObj(context, id);
            mTroopObj.addChild(obj);
            setActivity(mTroopObj);
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    private void savePic(Uri imgUri) {
        if (imgUri != null) {
            try {
                ContentResolver cr = getContentResolver();
                InputStream imgIS = cr.openInputStream(imgUri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(imgUri), null, options);

                int width = 600;
                int heigh = options.outWidth * width / options.outHeight;

                options.inJustDecodeBounds = false;
                Bitmap photo = BitmapFactory.decodeStream(cr.openInputStream(imgUri));
                photo = Bitmap.createScaledBitmap(photo, width, heigh, false);

                long createTime = DateHandle.getTime();

                CameraPicObj obj = new CameraPicObj();
                obj.setId(createTime);
                obj.setCreateAt(createTime);
                CameraPicObjHandler.saveCameraPicObj(context, obj);

                FileUtil.saveMediumBitmap(photo, obj);
                mTroopObj.addChild(obj);

                setActivity(mTroopObj);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    class PicBaseAdapter extends BaseAdapter {

        List<CameraPicObj> cameraPicObjList;
        LayoutInflater inflater;

        public PicBaseAdapter(List<CameraPicObj> cameraPicObjList) {
            this.cameraPicObjList = cameraPicObjList;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        private void remove(CameraPicObj obj) {
            cameraPicObjList.remove(obj);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return cameraPicObjList.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return cameraPicObjList.get(position);
        }

        @Override
        public long getItemId(int convertView) {
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater
                        .inflate(R.layout.compile_grid_item, null);
            }
            try {
                CameraPicObj obj = cameraPicObjList.get(position);
                setImageMessage(convertView, obj);
                setOnDeletePic(convertView, obj);
            } catch (Exception e) {
                setViewContent(convertView);
                setOnAddPic(convertView);
            }
            return convertView;
        }

        private void setImageMessage(View view, CameraPicObj obj) {
            ImageView pic = (ImageView) view.findViewById(R.id.compile_pic);
            final String path = obj.getMediumFilePath();

            int w = WinTool.getWinWidth(context) / 3
                    - WinTool.dipToPx(context, 20);
            pic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
            DownloadImageLoader.loadImageForFile(pic, path);

            pic.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ArrayList<String> picList = new ArrayList<String>();
                    picList.add(path);
                    Bundle b = new Bundle();
                    b.putStringArrayList("iamge_list", picList);
                    b.putInt("position", 0);
                    Passageway
                            .jumpActivity(context, ImageListAcitvity.class, b);
                }
            });
        }

        private void setViewContent(View view) {
            ImageView pic = (ImageView) view.findViewById(R.id.compile_pic);

            int w = WinTool.getWinWidth(context) / 3
                    - WinTool.dipToPx(context, 23);
            pic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
            pic.setImageResource(R.drawable.add_pic_icon);

            view.findViewById(R.id.compile_deletePic).setVisibility(
                    View.INVISIBLE);
        }

        private void setOnAddPic(View view) {
            view.findViewById(R.id.compile_pic).setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            AddPicDialog.showAddPicDialog(mActivity,
                                    cameraPicObjList.size());
                        }
                    });
        }

        private void setOnDeletePic(View view, final CameraPicObj obj) {
            view.findViewById(R.id.compile_deletePic).setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            remove(obj);

                        }
                    });
        }
    }

}
