package com.zmyh.r.photo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zmyh.r.R;
import com.zmyh.r.baidumap.BaiduMapActivity;
import com.zmyh.r.box.CameraPicObj;
import com.zmyh.r.box.TroopObj;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.photo.ImageListAcitvity;
import com.zmyh.r.photo.ParticularActivity;
import com.zmyh.r.photo.PhotoActivity;
import com.zmyh.r.photo.TroopObjBox;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.view.InsideGridView;

public class OnlineTroopBaseAdapter extends TroopBaseAdapter {

    public OnlineTroopBaseAdapter(Context context, List<TroopObj> dataList) {
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
        // setStatusTap(convertView, obj);
        setMapAddress(convertView, obj);
        // setParticularBall(convertView, obj);
        setMessageText(convertView, obj);
        return convertView;
    }

    private void setViewContent(View view, TroopObj obj) {
        TextView time = (TextView) view.findViewById(R.id.troop_time);
        TextView titleName = (TextView) view.findViewById(R.id.troop_titleName);
        TextView remark = (TextView) view.findViewById(R.id.troop_remark);

        time.setText(obj.getCreateTime());

        titleName.setText(obj.getMu_name());

        if (obj.getMu_desc().equals("")) {
            remark.setVisibility(View.VISIBLE);
            remark.setText("此处为详细描述");
        } else {
            remark.setVisibility(View.VISIBLE);
            remark.setText(obj.getMu_desc());
        }
    }

    private void setOnParticularBtn(View view, final TroopObj obj) {
        view.findViewById(R.id.troop_particular).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        TroopObjBox.saveTroopObj(obj);
                        Bundle b = new Bundle();
                        b.putInt("status", ParticularActivity.ONLINE);
                        Passageway.jumpActivity(context,
                                ParticularActivity.class,
                                PhotoActivity.ResultCode, b);
                    }
                });
    }

    private void setOnChoice(View view, final TroopObj obj) {
        ImageView onClickIcon = (ImageView) view
                .findViewById(R.id.troop_isChoice);

        if (choiceList.contains(obj)) {
            onClickIcon.setImageResource(R.drawable.on_click);
        } else {
            onClickIcon.setImageResource(R.drawable.on_click_off);
        }

        onClickIcon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addChoiceList(obj);
                notifyDataSetChanged();
            }
        });
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
        if (TroopObj.isIncomplete(obj)) {
            ball.setVisibility(View.VISIBLE);
        } else {
            ball.setVisibility(View.GONE);
        }
    }

    private void setMessageText(View view, TroopObj obj) {
        TextView msgText = (TextView) view.findViewById(R.id.troop_messageText);
        StringBuffer sb = new StringBuffer();
        if (!obj.getMu_sz_type().equals("")) {
            sb.append("种树类别:");
            sb.append(obj.getMu_sz_type());
        }
        if (!obj.getMu_j_min().equals("") && !obj.getMu_j_max().equals("") && !obj.getMu_j_min().equals("0") && !obj.getMu_j_max().equals("0")) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("胸径/地径:");
            sb.append(obj.getMu_j_min());
            sb.append("-");
            sb.append(obj.getMu_j_max());
        }
        if (!obj.getMu_zg_min().equals("") && !obj.getMu_zg_max().equals("") && !obj.getMu_zg_min().equals("0") && !obj.getMu_zg_max().equals("0")) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("株高:");
            sb.append(obj.getMu_zg_min());
            sb.append("-");
            sb.append(obj.getMu_zg_max());
        }
        if (!obj.getMu_gf_min().equals("") && !obj.getMu_gf_max().equals("") && !obj.getMu_gf_min().equals("0") && !obj.getMu_gf_max().equals("0")) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("冠幅:");
            sb.append(obj.getMu_gf_min());
            sb.append("-");
            sb.append(obj.getMu_gf_max());
        }
        if (!obj.getMu_type().equals("")) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("苗木类别:");
            sb.append(obj.getMu_type());
        }
        if (!obj.getMu_jz_time().equals("")) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("嫁接时间:");
            sb.append(obj.getMu_jz_time());
        }
        if (!obj.getMu_total().equals("") && !obj.getMu_total().equals("0")) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("苗木数量:");
            sb.append(obj.getMu_total());
        }
        if (!obj.getMu_price().equals("") && !obj.getMu_price().equals("0")) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("苗木价格:");
            sb.append(obj.getMu_price());
        }

        if (sb.length() > 0) {
            msgText.setVisibility(View.VISIBLE);
            msgText.setText(sb.toString());
        } else {
            msgText.setVisibility(View.GONE);
        }

    }

    private void setPicList(View view, TroopObj obj) {
        Log.e("Mu_id", obj.getMu_id());
        InsideGridView picGrid = (InsideGridView) view
                .findViewById(R.id.troop_picGrid);
        picGrid.setAdapter(new PicBaseAdapter(obj.getCameraPicObjList()));
    }

    class PicBaseAdapter extends BaseAdapter {

        List<CameraPicObj> cameraPicObjList;
        LayoutInflater inflater;

        public PicBaseAdapter(List<CameraPicObj> cameraPicObjList) {
            this.cameraPicObjList = cameraPicObjList;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return cameraPicObjList.size();
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
            CameraPicObj obj = cameraPicObjList.get(position);
            setImageMessage(convertView, obj, position);
            return convertView;
        }

        private ArrayList<String> getPicList() {
            ArrayList<String> list = new ArrayList<String>();
            for (CameraPicObj obj : cameraPicObjList) {
                list.add(obj.getMu_photo_thumbnail());
            }
            return list;
        }

        private void setImageMessage(View view, final CameraPicObj obj,
                                     final int position) {
            ImageView pic = (ImageView) view.findViewById(R.id.troop_pic);
            final String url = obj.getMu_photo_thumbnail();

            int w = WinTool.getWinWidth(context) / 3
                    - WinTool.dipToPx(context, 23);
            pic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
            Log.d("", url);
            if (url != null) {
                DownloadImageLoader.loadImage(pic, url);
            }

            pic.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putStringArrayList("iamge_list", getPicList());
                    b.putInt("position", position);
                    b.putBoolean("isOnline", true);
                    Passageway
                            .jumpActivity(context, ImageListAcitvity.class, b);
                }
            });
        }
    }

}
