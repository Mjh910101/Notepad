package com.zmyh.r.main.user;

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
import com.zmyh.r.main.server.ServerContentActivity;
import com.zmyh.r.main.server.ServerContentNoLoginActivity;
import com.zmyh.r.main.server.ServerObjBox;
import com.zmyh.r.photo.ImageListAcitvity;
import com.zmyh.r.tool.Passageway;

import java.util.ArrayList;
import java.util.List;

public class PublishServerBaseAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<ServerObj> itemList;
    private Context context;
    private int now_tap;

    private ServerObj cObj;
    private UserPublishActivity.DeleteServerCallback callback;

    public PublishServerBaseAdapter(Context context, List<ServerObj> list, int now_tap) {
        this.itemList = list;
        this.context = context;
        this.now_tap = now_tap;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setDeleteCallback(UserPublishActivity.DeleteServerCallback callback) {
        this.callback = callback;
    }

    public void addItems(List<ServerObj> list) {
        for (ServerObj obj : list) {
            addItem(obj);
        }
        notifyDataSetChanged();
    }

    public void removeItem(ServerObj obj) {
        itemList.remove(obj);
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
            convertView = inflater.inflate(R.layout.server_publish_list_item, null);
        }
        ServerObj obj = itemList.get(position);

        setContent(convertView, obj);
        setContentMap(convertView, obj);
        setContentPic(convertView, obj);
//        setOnClickPic(convertView, obj);
        setOnClick(convertView, obj);
        setToolBox(convertView, obj);
        setOnAmendBtn(convertView);
        setOnDeleteBtn(convertView);
        return convertView;
    }

    private void setOnDeleteBtn(View view) {
        view.findViewById(R.id.punlish_list_deleteBtn).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (callback != null) {
                            callback.callback(cObj);
                        }
                    }
                });
    }

    private void setOnAmendBtn(View view) {
        view.findViewById(R.id.punlish_list_amendBtn).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        ServerBox.saveServerObj(cObj);
                        Passageway.jumpActivity(context,
                                AmendActivity.class);
                    }
                });
    }


    private void setToolBox(View view, ServerObj obj) {
        ImageView switchIcon = (ImageView) view
                .findViewById(R.id.publish_list_switch);
        LinearLayout toolBox = (LinearLayout) view
                .findViewById(R.id.punlish_list_toolBox);

        toolBox.setVisibility(View.GONE);
        if (now_tap == UserPublishActivity.DELETE) {
            switchIcon.setImageBitmap(null);
        } else {
            if (cObj != null && cObj == obj) {
                toolBox.setVisibility(View.VISIBLE);
                switchIcon.setImageResource(R.drawable.server_on_icon);
            } else {
                switchIcon.setImageResource(R.drawable.server_off_icon);
            }
        }
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
        if (now_tap != UserPublishActivity.DELETE) {
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (cObj != obj) {
                        cObj = obj;
                    } else {
                        cObj = null;
                    }
                    notifyDataSetChanged();
                }
            });
        }
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
        setIsNewContent(title, obj);

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
