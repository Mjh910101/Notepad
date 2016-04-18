package com.zmyh.r.main.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmyh.r.R;
import com.zmyh.r.box.ServerObj;
import com.zmyh.r.easemob.activitys.ChatActivity;
import com.zmyh.r.easemob.utils.UserUtils;
import com.zmyh.r.handler.ServerObjHandler;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.main.server.ServerContentActivity;
import com.zmyh.r.main.server.ServerContentNoLoginActivity;
import com.zmyh.r.tool.Passageway;

import java.util.ArrayList;
import java.util.List;

/**
 * *
 * * ┏┓      ┏┓
 * *┏┛┻━━━━━━┛┻┓
 * *┃          ┃
 * *┃          ┃
 * *┃ ┳┛   ┗┳  ┃
 * *┃          ┃
 * *┃    ┻     ┃
 * *┃          ┃
 * *┗━┓      ┏━┛
 * *  ┃      ┃
 * *  ┃      ┃
 * *  ┃      ┗━━━┓
 * *  ┃          ┣┓
 * *  ┃         ┏┛
 * *  ┗┓┓┏━━━┳┓┏┛
 * *   ┃┫┫   ┃┫┫
 * *   ┗┻┛   ┗┻┛
 * Created by Hua on 15/9/9.
 */
public class PublishServerQGBaseAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<ServerObj> itemList;
    private Context context;
    private int now_tap;

    private ServerObj cObj;
    private UserPublishActivity.DeleteServerCallback callback;

    public PublishServerQGBaseAdapter(Context context, List<ServerObj> list, int now_tap) {
        this.itemList = list;
        this.context = context;
        this.now_tap = now_tap;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void removeItem(ServerObj obj) {
        itemList.remove(obj);
        notifyDataSetChanged();
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.server_publish_list_item_qg, null);
        }

        ServerObj obj = itemList.get(i);
        initView(convertView, obj);
        setOnClick(convertView, obj);
        setToolBox(convertView, obj);
        setOnAmendBtn(convertView);
        setOnDeleteBtn(convertView);
        return convertView;
    }

    private void setOnDeleteBtn(View view) {
        view.findViewById(R.id.punlish_list_deleteBtn).setOnClickListener(
                new View.OnClickListener() {

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
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        ServerBox.saveServerObj(cObj);
                        Passageway.jumpActivity(context,
                                AmendQGActivity.class);
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

    private void initView(View view, ServerObj obj) {
        TextView title = (TextView) view
                .findViewById(R.id.gq_icom_title);
        TextView city = (TextView) view
                .findViewById(R.id.gq_icom_time);
        TextView sum = (TextView) view
                .findViewById(R.id.gq_icom_sum);
        TextView day = (TextView) view
                .findViewById(R.id.gq_icom_day);

        title.setText(obj.getTitle());
        city.setText(obj.getCity());
        sum.setText(obj.getMu_total() + "株");
        day.setText(obj.getExpireText());
    }

    private void setOnClick(View view, final ServerObj obj) {
        if (now_tap != UserPublishActivity.DELETE) {
            view.setOnClickListener(new View.OnClickListener() {

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

}
