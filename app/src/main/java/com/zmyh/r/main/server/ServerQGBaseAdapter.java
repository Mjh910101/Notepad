package com.zmyh.r.main.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zmyh.r.R;
import com.zmyh.r.box.ServerObj;
import com.zmyh.r.easemob.activitys.ChatActivity;
import com.zmyh.r.easemob.utils.UserUtils;
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.handler.ServerObjHandler;
import com.zmyh.r.handler.UserObjHandle;
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
public class ServerQGBaseAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<ServerObj> itemList;
    private Context context;

    public ServerQGBaseAdapter(Context context) {
        this.context = context;
        itemList = new ArrayList<ServerObj>();
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ServerQGBaseAdapter(Context context, List<ServerObj> list) {
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.server_list_item_qg, null);
        }

        ServerObj obj = itemList.get(i);
        initView(convertView, obj);
        setOnClick(convertView, obj);
        setOnSendMessage(convertView, obj);

        return convertView;
    }

    private void setOnSendMessage(View view, final ServerObj obj) {
        view.findViewById(R.id.qg_sendMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserUtils.saveUserInfo(obj.getPoster());
                Intent intent = new Intent(context, ChatActivity.class);
//        intent.putExtra("chatType", ChatActivity.CHATTYPE_CHATROOM);
                intent.putExtra("userId", obj.getPosterId());
                context.startActivity(intent);
            }
        });
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

        if (ServerObjHandler.isWatch(context, obj)) {
            city.setTextColor(ColorBox.getColorForID(context,
                    R.color.text_gray_04));
            sum.setTextColor(ColorBox.getColorForID(context,
                    R.color.text_gray_04));
            day.setTextColor(ColorBox.getColorForID(context,
                    R.color.text_gray_04));
            day.setBackgroundResource(R.drawable.gray_background_gray_frame_0);

        } else {
            city.setTextColor(ColorBox.getColorForID(context,
                    R.color.text_gray));
            sum.setTextColor(ColorBox.getColorForID(context,
                    R.color.yellow_01));
            day.setTextColor(ColorBox.getColorForID(context,
                    R.color.yellow_01));
            day.setBackgroundResource(R.drawable.orange_background_orange_frame_0);
        }
    }

    private void setOnClick(View view, final ServerObj obj) {
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ServerObjHandler.watchServerObj(context, obj);
                notifyDataSetChanged();
                Bundle b = new Bundle();
                b.putString("id", obj.getId());
                b.putBoolean("isQG", true);
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

}
