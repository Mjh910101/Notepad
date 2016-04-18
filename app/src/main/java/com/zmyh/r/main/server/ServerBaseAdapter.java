package com.zmyh.r.main.server;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmyh.r.R;
import com.zmyh.r.baidumap.TreeMapActivity;
import com.zmyh.r.baidumap.TreeMapActivityV2;
import com.zmyh.r.box.ServerObj;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.handler.ServerObjHandler;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.photo.ImageListAcitvity;
import com.zmyh.r.tool.Passageway;

public class ServerBaseAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<ServerObj> itemList;
    private Context context;

    public ServerBaseAdapter(Context context) {
        this.context = context;
        itemList = new ArrayList<ServerObj>();
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ServerBaseAdapter(Context context, List<ServerObj> list) {
        this.itemList = list;
        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItems(List<ServerObj> list) {
        for (ServerObj obj : list) {
            addItem(obj);
        }
        notifyDataSetChanged();
    }

    private void addItem(ServerObj obj) {
        itemList.add(obj);
    }

    public void removeAll() {
        itemList.removeAll(itemList);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.server_list_item_v2, null);
        }
        ServerObj obj = itemList.get(position);
        setContent(convertView, obj);
        setContentMap(convertView, obj);
        setContentPic(convertView, obj);
        setOnClickPic(convertView, obj);
        setOnClick(convertView, obj);
        return convertView;
    }

    private void setContentMap(View view, final ServerObj obj) {
        ImageView map = (ImageView) view
                .findViewById(R.id.server_list_item_map);

        if (obj.isHaveCoordinate()) {
            map.setVisibility(View.VISIBLE);
        } else {
            map.setVisibility(View.GONE);
        }

        map.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ServerObjBox.saveServerObj(obj);
                Bundle b = new Bundle();
                b.putBoolean(TreeMapActivity.IS_ONE, true);
                b.putString(TreeMapActivity.MM_CHANNEL, obj.getMmChannelID());
                Passageway.jumpActivity(context,
                        Intent.FLAG_ACTIVITY_CLEAR_TOP, TreeMapActivityV2.class,
                        b);
            }
        });
    }

    private void setOnClick(View view, final ServerObj obj) {
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ServerObjHandler.watchServerObj(context, obj);
                notifyDataSetChanged();
                Bundle b = new Bundle();
                b.putString("id", obj.getId());
                b.putBoolean("isGy", true);
                if (UserObjHandle.isLogin(context)) {
                    Passageway
                            .jumpActivity(context, ServerContentActivity.class, b);
                } else {
                    Passageway
                            .jumpActivity(context, ServerContentNoLoginActivity.class, b);
                }
            }
        });
    }

    private void setContentPic(View view, ServerObj obj) {
        ImageView pic = (ImageView) view
                .findViewById(R.id.server_list_item_pic);
        int w = WinTool.getWinWidth(context) / 3;
//		if (obj.getPost_thumbnail() != null
//				&& !obj.getPost_thumbnail().equals("null")
//				&& !obj.getPost_thumbnail().equals("")) {
        // pic.setVisibility(View.VISIBLE);
        pic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
        DownloadImageLoader.loadImage(pic, obj.getPost_thumbnail());
//		} else {
//			 pic.setVisibility(View.GONE);
//			pic.setImageBitmap(null);
//			pic.setLayoutParams(new LinearLayout.LayoutParams(1, w));
//		}
    }

    private void setOnClickPic(View view, final ServerObj obj) {
        view.findViewById(R.id.server_list_item_pic).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Bundle b = new Bundle();
                        b.putStringArrayList("iamge_list",
                                (ArrayList<String>) obj.getPic());
                        b.putInt("position", 0);
                        b.putBoolean("isOnline", true);
                        Passageway.jumpActivity(context,
                                ImageListAcitvity.class, b);
                    }
                });
    }

    private void setContent(View view, ServerObj obj) {
        TextView title = (TextView) view
                .findViewById(R.id.server_list_item_title);
        TextView content = (TextView) view
                .findViewById(R.id.server_list_item_context);
        TextView time = (TextView) view
                .findViewById(R.id.server_list_item_time);
        TextView city = (TextView) view
                .findViewById(R.id.server_list_item_city);
        TextView msg = (TextView) view.findViewById(R.id.server_list_item_message);

        title.setText(obj.getTitle());
        // content.setText(obj.getIntro());
        time.setText(obj.getCreateTime());
        city.setText(obj.getCity());
        msg.setText(obj.getPriceMessage());

        content.setVisibility(View.GONE);
        if (ServerObjHandler.isWatch(context, obj)) {
            time.setTextColor(ColorBox.getColorForID(context,
                    R.color.text_gray_04));
            city.setTextColor(ColorBox.getColorForID(context,
                    R.color.text_gray_04));
            msg.setTextColor(ColorBox.getColorForID(context,
                    R.color.text_gray_04));
        } else {
            time.setTextColor(ColorBox.getColorForID(context,
                    R.color.text_gray));
            city.setTextColor(ColorBox.getColorForID(context,
                    R.color.text_gray));
            msg.setTextColor(ColorBox.getColorForID(context,
                    R.color.yellow_01));
        }

    }

    private void setIsNewContent(TextView view, ServerObj obj) {
        if (ServerObjHandler.isWatch(context, obj)) {
            view.setTextColor(ColorBox.getColorForID(context,
                    R.color.text_gray_04));
        } else {
            view.setTextColor(ColorBox.getColorForID(context,
                    R.color.text_green));
        }
    }

}
