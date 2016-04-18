package com.zmyh.r.photo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zmyh.r.R;
import com.zmyh.r.box.CameraPicObj;
import com.zmyh.r.box.TroopObj;
import com.zmyh.r.camera.util.FileUtil;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.photo.ImageListAcitvity;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.upload.services.UploadService;

public class MntPicBaseAdapter extends PicBaseAdapter {

    private UploadService mUploadService;

    public MntPicBaseAdapter(Context context,
                             List<CameraPicObj> cameraPicObjList) {
        super(context);
        dataList = cameraPicObjList;
    }

    public void setUploadService(UploadService mUploadService) {
        this.mUploadService = mUploadService;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.mnt_image_list_item, null);
        }

        CameraPicObj obj = dataList.get(position);
        setViewContent(convertView, obj);
        setImageMessage(convertView, obj, position);
        setOnChoice(convertView, obj);
        setOnUpload(convertView, obj, position);

        return convertView;
    }

    private void setOnUpload(View view, final CameraPicObj obj,
                             final int position) {
        ImageView uploadIcon = (ImageView) view
                .findViewById(R.id.image_item_upload);
        TextView uploadText = (TextView) view
                .findViewById(R.id.image_item_uploadText);

        if (obj.getMax_state() == CameraPicObj.NOTSTARTES) {
            uploadIcon.setVisibility(View.VISIBLE);
            uploadText.setVisibility(View.INVISIBLE);
        } else {
            uploadIcon.setVisibility(View.INVISIBLE);
            uploadText.setVisibility(View.VISIBLE);
            if (obj.getMax_state() == CameraPicObj.WAIT) {
                uploadText.setText("等待");
            } else if (obj.getMax_state() == CameraPicObj.HAVE_IN_HAND) {
                int p = (int) (obj.getPercent() * 100);
                uploadText.setText(p + "%");
            } else if (obj.getMax_state() == CameraPicObj.FINISH) {
                uploadText.setText("已上传");
            } else if (obj.getMax_state() == CameraPicObj.DEFEATED) {
                uploadText.setText("失败");
            }
        }

        uploadText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (obj.getMax_state() == CameraPicObj.FINISH) {
                    seePic(position);
                }
            }

        });

        uploadIcon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mUploadService != null) {
                    TroopObj t = new TroopObj();
                    t.setMu_id(obj.getMu_id());
                    t.addChild(obj);
                    mUploadService.addTroopObj(context, t, "o");
                }
            }
        });
    }

    private void setViewContent(View view, CameraPicObj obj) {
        TextView tapName = (TextView) view
                .findViewById(R.id.image_item_tapName);

        tapName.setText(obj.getCreateTime());
    }

    private void setImageMessage(View view, final CameraPicObj obj,
                                 final int position) {
        ImageView pic = (ImageView) view.findViewById(R.id.image_item_pic);
        final String path = obj.getOriginalFilePath();

        int w = WinTool.getWinWidth(context) / 4;
        pic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
        DownloadImageLoader.loadImageForFile(pic, path,
                new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                        if (loadedImage != null) {
                            if (loadedImage.getWidth() > loadedImage
                                    .getHeight()) {
                                view.setRotation(90);
                            }
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });

        pic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                seePic(position);
            }
        });
    }

    private void setOnChoice(View view, final CameraPicObj obj) {
        ImageView onClickIcon = (ImageView) view
                .findViewById(R.id.image_item_onClick);

        if (choiceList.contains(obj)) {
            onClickIcon.setImageResource(R.drawable.on_click);
        } else {
            onClickIcon.setImageResource(R.drawable.on_click_off);
        }

        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                addChoiceList(obj);
                notifyDataSetChanged();
            }
        });
    }

    private void seePic(int position) {
        Bundle b = new Bundle();
        b.putStringArrayList("iamge_list", getPicList());
        b.putInt("position", position);
        b.putBoolean("isRotate", true);
        Passageway.jumpActivity(context, ImageListAcitvity.class, b);
    }

    private ArrayList<String> getPicList() {
        ArrayList<String> list = new ArrayList<String>();
        for (CameraPicObj obj : dataList) {
            list.add(obj.getOriginalFilePath());
        }
        return list;
    }

}
