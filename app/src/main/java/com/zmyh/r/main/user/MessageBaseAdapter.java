package com.zmyh.r.main.user;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmyh.r.R;
import com.zmyh.r.box.ChannelObj;
import com.zmyh.r.box.DynamicObj;
import com.zmyh.r.box.MsgObj;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.DateHandle;
import com.zmyh.r.handler.MsgObjHandler;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.main.forum.ForumContentActivity;
import com.zmyh.r.main.server.ServerContentActivity;
import com.zmyh.r.tool.Passageway;

public class MessageBaseAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    List<MsgObj> dataObjList;

    int tap;

    public MessageBaseAdapter(Context context, List<MsgObj> dataObjList, int tap) {
        this.dataObjList = dataObjList;
        this.context = context;
        this.tap = tap;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItems(List<MsgObj> list) {
        for (MsgObj obj : list) {
            dataObjList.add(obj);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataObjList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataObjList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.message_list_item, null);
        }
        MsgObj obj = dataObjList.get(position);
        setView(convertView, obj);
        setTap(convertView, obj);
        setOnClick(convertView, obj);
        return convertView;
    }

    private void setOnClick(View view, final MsgObj obj) {
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                DynamicObj d = obj.getPost();
                if (tap == UserMessageActivity.GATHER) {
                    MsgObjHandler.watch(context, obj);
                }
                if (d != null) {
                    Bundle b = new Bundle();
                    if (d.getType().equals("services")) {
                        b.putString("id", d.getId());
                        Passageway.jumpActivity(context,
                                ServerContentActivity.class, b);
                    } else if (d.getType().equals("topic")) {
                        b.putString("id", d.getId());
                        Passageway.jumpActivity(context,
                                ForumContentActivity.class, b);
                    }
                }
            }
        });
    }

    private void setTap(View v, MsgObj obj) {
        TextView tap = (TextView) v
                .findViewById(R.id.message_listItem_messageTap);
        ChannelObj channel = obj.getMmChannel();
        if (channel != null) {
            tap.setVisibility(View.VISIBLE);
            tap.setText(channel.getTitle());
        } else {
            tap.setVisibility(View.GONE);
        }
    }

    private void setView(View v, MsgObj obj) {
        ImageView pic = (ImageView) v.findViewById(R.id.message_listItem_image);
        TextView time = (TextView) v.findViewById(R.id.message_listItem_time);
        TextView title = (TextView) v.findViewById(R.id.message_listItem_title);
        TextView city = (TextView) v.findViewById(R.id.message_listItem_city);

        title.setText(obj.getTitle());
        time.setText(DateHandle.getIsTodayFormat(obj.getCreateAt() * 1000));
        city.setText(obj.getCity());

        int w = WinTool.getWinWidth(context) / 4;
        pic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
        DownloadImageLoader.loadImage(pic, obj.getPost_thumbnail());

        LinearLayout box = (LinearLayout) v
                .findViewById(R.id.message_listItem_box);
        View line = v.findViewById(R.id.message_listItem_line);

        if (tap == UserMessageActivity.SEND) {
            box.setBackgroundResource(R.drawable.white_background_gray02_frame_0);
            line.setBackgroundResource(R.color.line_gray);
        } else {
            box.setBackgroundResource(R.drawable.green_background_green_frame_0);
            line.setBackgroundResource(R.color.green_01);
        }

    }

}
