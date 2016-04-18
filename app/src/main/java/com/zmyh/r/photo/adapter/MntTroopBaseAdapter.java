package com.zmyh.r.photo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zmyh.r.R;
import com.zmyh.r.baidumap.BaiduMapActivity;
import com.zmyh.r.box.CameraPicObj;
import com.zmyh.r.box.TroopObj;
import com.zmyh.r.camera.util.FileUtil;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.handler.TroopObjHandler;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.photo.ImageListAcitvity;
import com.zmyh.r.photo.ParticularActivity;
import com.zmyh.r.photo.PhotoActivity;
import com.zmyh.r.photo.PlayVideoActivity;
import com.zmyh.r.photo.TroopObjBox;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.view.InsideGridView;

public class MntTroopBaseAdapter extends TroopBaseAdapter {

    public MntTroopBaseAdapter(Context context, List<TroopObj> dataList) {
        super(context);
        this.dataList = dataList;
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
            convertView = inflater.inflate(R.layout.mnt_troop_list_item_v3,
                    null);
        }
        TroopObj obj = dataList.get(position);
        setViewContent(convertView, obj);
        setPicList(convertView, obj);
        setOnParticularBtn(convertView, obj);
        setOnChoice(convertView, obj);
        setStatusTap(convertView, obj);
        setMapAddress(convertView, obj);
        setParticularBall(convertView, obj);
        setMessageText(convertView, obj);
        return convertView;
    }

    private void setViewContent(View view, TroopObj obj) {

        TextView remark = (TextView) view.findViewById(R.id.troop_remark);
        TextView time = (TextView) view.findViewById(R.id.troop_time);

        time.setText(obj.getCreateTime());


        if (obj.getMu_desc().equals("")) {
            remark.setVisibility(View.VISIBLE);
            remark.setText("此处为详细描述");
        } else {
            remark.setVisibility(View.VISIBLE);
            remark.setText(obj.getMu_desc());
        }
    }

    private void setOnParticularBtn(View view, final TroopObj obj) {
//		view.findViewById(R.id.troop_particular)
        view.setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        TroopObjBox.saveTroopObj(obj);
                        Bundle b = new Bundle();
                        b.putInt("status", ParticularActivity.MNT);
                        Passageway.jumpActivity(context,
                                ParticularActivity.class,
                                PhotoActivity.ResultCode, b);
                    }
                });
    }

    private void setOnChoice(View view, final TroopObj obj) {
        ImageView onClickIcon = (ImageView) view
                .findViewById(R.id.troop_isChoice);
        TextView titleName = (TextView) view.findViewById(R.id.troop_titleName);

        titleName.setText(obj.getMu_name());

        if (choiceList.contains(obj)) {
            onClickIcon.setImageResource(R.drawable.on_click);
        } else {
            onClickIcon.setImageResource(R.drawable.on_click_off);
        }

        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                addChoiceList(obj);
                notifyDataSetChanged();
            }
        };

        onClickIcon.setOnClickListener(clickListener);
        titleName.setOnClickListener(clickListener);
    }

    private void setStatusTap(View view, TroopObj obj) {
        TextView tap = (TextView) view.findViewById(R.id.troop_tap);
        if (obj.isUpload()) {
            tap.setText("有更新,未上传");
            tap.setTextColor(ColorBox.getColorForID(context, R.color.red));
        } else {
            if (obj.isOnline()) {
                tap.setText("云端资料");
            } else {
                tap.setText("已经上传");
            }
            tap.setTextColor(ColorBox.getColorForID(context, R.color.black));
        }
    }

    private void setMapAddress(View view, final TroopObj obj) {
        RelativeLayout addressBox = (RelativeLayout) view
                .findViewById(R.id.troop_addressBox);
        TextView address = (TextView) view.findViewById(R.id.troop_address);
        ImageView addressIcon = (ImageView) view
                .findViewById(R.id.troop_addressIcon);

        String addText = "";
        if (obj.getCameraPicObjList() != null
                && !obj.getCameraPicObjList().isEmpty()) {
            addText = obj.getCameraPicObjList().get(0).getMu_zb();
        }

        if (addText.equals("")) {
            addText = obj.getMu_zb();
        }

        if (!addText.equals("")) {
            addressBox.setVisibility(View.VISIBLE);
            address.setText(addText);
        } else {
            addressBox.setVisibility(View.GONE);
        }

        addressBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString(BaiduMapActivity.TITLE_NAME, obj.getMu_name());
                b.putString(BaiduMapActivity.TIME, obj.getCreateTime());
                b.putString(BaiduMapActivity.DESC, obj.getMu_desc());
                b.putDouble(BaiduMapActivity.LAT, obj.getMu_latitude());
                b.putDouble(BaiduMapActivity.LON, obj.getMu_longitude());
                Passageway.jumpActivity(context, BaiduMapActivity.class, b);
            }
        });
    }

    private void setParticularBall(View view, TroopObj obj) {
        ImageView ball = (ImageView) view
                .findViewById(R.id.troop_particularBall);
        if (!obj.isHavePeople()) {
            ball.setVisibility(View.VISIBLE);
        } else {
            ball.setVisibility(View.GONE);
        }
    }

    private void setMessageText(View view, TroopObj obj) {
        TextView msgText = (TextView) view.findViewById(R.id.troop_messageText);
        String c = TroopObj.getContent(obj);
        if (c.length() > 0) {
            msgText.setVisibility(View.VISIBLE);
            msgText.setText(c);
        } else {
            msgText.setVisibility(View.GONE);
        }

    }

    private void setPicList(View view, TroopObj obj) {
        Log.e("Mu_id", obj.getMu_id());
        InsideGridView picGrid = (InsideGridView) view
                .findViewById(R.id.troop_picGrid);
        picGrid.setAdapter(new PicBaseAdapter(obj));
    }

    class PicBaseAdapter extends BaseAdapter {

        TroopObj fatherObj;
        List<CameraPicObj> cameraPicObjList;
        LayoutInflater inflater;

        public PicBaseAdapter(TroopObj obj) {
            this.fatherObj = obj;
            this.cameraPicObjList = obj.getCameraPicObjList();
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return cameraPicObjList.size() + 1;
//            switch (cameraPicObjList.size()) {
//                case 1:
//                case 2:
//                case 3:
//                    return 3;
//                case 4:
//                case 5:
//                case 6:
//                    return 6;
//                case 7:
//                case 8:
//                case 9:
//                    return 9;
//                default:
//                    return cameraPicObjList.size();
//            }
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
                convertView = inflater.inflate(R.layout.troop_grid_item, null);
            }
            if (position < cameraPicObjList.size()) {
                CameraPicObj obj = cameraPicObjList.get(position);
                setImageMessage(convertView, obj, position);
                return convertView;
            } else {
                setVideoMessage(convertView, fatherObj);
//                return getNullView();
            }
            return convertView;

        }

        private void setVideoMessage(View view, final TroopObj obj) {
            ImageView pic = (ImageView) view.findViewById(R.id.troop_pic);
//            Toast.makeText(context, obj.getVideoFile().toString(), Toast.LENGTH_LONG).show();
//            Toast.makeText(context, obj.getVideoName(), Toast.LENGTH_LONG).show();
            int w = WinTool.getWinWidth(context) / 3
                    - WinTool.dipToPx(context, 23);
            pic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
            int p;
            if (obj.isVideoExists()) {
                p = 20;
                pic.setPadding(p, p, p, p);
                pic.setBackgroundResource(R.color.black);
                pic.setImageResource(R.drawable.play_icon);
            } else {
                p = 0;
                pic.setPadding(p, p, p, p);
                pic.setBackgroundResource(R.color.white_lucency);
                pic.setImageResource(R.drawable.video_icon);
            }

            pic.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!obj.isVideoExists()) {
                        String vidoPath = Passageway.takeVideo(context);
                        obj.setVideoName(vidoPath);
                        TroopObjHandler.saveTroopObj(context, obj);
                    } else {
                        Bundle b = new Bundle();
                        b.putBoolean(PlayVideoActivity.IS_FILE, true);
                        b.putString(PlayVideoActivity.PATH, obj.getVideoName());
                        Passageway.jumpActivity(context, PlayVideoActivity.class, PlayVideoActivity.REQUEST_CODE, b);
                    }
                }
            });
        }

        public View getNullView() {
            int w = WinTool.getWinWidth(context) / 3
                    - WinTool.dipToPx(context, 23);
            View v = new View(context);
            v.setLayoutParams(new AbsListView.LayoutParams(w, w));
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    TroopObjBox.saveTroopObj(fatherObj);
                    Bundle b = new Bundle();
                    b.putInt("status", ParticularActivity.MNT);
                    Passageway.jumpActivity(context,
                            ParticularActivity.class,
                            PhotoActivity.ResultCode, b);
                }
            });
            return v;
        }

        private ArrayList<String> getPicList() {
            ArrayList<String> list = new ArrayList<String>();
            for (CameraPicObj obj : cameraPicObjList) {
                if (FileUtil.fileIsExists(obj.getOriginalFilePath())
                        && obj.isShow_max_pic()) {
                    list.add(obj.getOriginalFilePath());
                } else {
                    list.add(obj.getMediumFilePath());
                }
            }
            return list;
        }

        private void setImageMessage(View view, final CameraPicObj obj,
                                     final int position) {
            ImageView pic = (ImageView) view.findViewById(R.id.troop_pic);
            final String path = obj.getMediumFilePath();

            int w = WinTool.getWinWidth(context) / 3
                    - WinTool.dipToPx(context, 23);
            pic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
            Log.e("M-path", path);
            if (FileUtil.fileIsExists(path)) {
                Log.e("M-path", "path");
                DownloadImageLoader.loadImageForFile(pic, path);
            } else {
                if (obj.getMu_photo_thumbnail() != null) {
                    DownloadImageLoader.loadImage(pic,
                            obj.getMu_photo_thumbnail(),
                            new ImageLoadingListener() {

                                @Override
                                public void onLoadingStarted(String imageUri,
                                                             View view) {
                                }

                                @Override
                                public void onLoadingFailed(String imageUri,
                                                            View view, FailReason failReason) {
                                }

                                @Override
                                public void onLoadingComplete(String imageUri,
                                                              View view, final Bitmap loadedImage) {
                                    Bitmap b = Bitmap.createBitmap(loadedImage);
                                    FileUtil.saveMediumBitmap(b, obj);
                                }

                                @Override
                                public void onLoadingCancelled(String imageUri,
                                                               View view) {
                                }
                            });
                }
            }

            pic.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putStringArrayList("iamge_list", getPicList());
                    b.putBoolean("isRotate", true);
                    b.putInt("position", position);
                    Passageway
                            .jumpActivity(context, ImageListAcitvity.class, b);
                }
            });
        }

    }

}
