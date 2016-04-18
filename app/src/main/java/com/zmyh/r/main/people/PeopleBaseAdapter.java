package com.zmyh.r.main.people;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmyh.r.R;
import com.zmyh.r.box.UserObj;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.tool.LineViewTool;
import com.zmyh.r.tool.Passageway;

public class PeopleBaseAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<UserObj> itemList;
    private Context context;

    public PeopleBaseAdapter(Context context, List<UserObj> itemList) {
        this.itemList = itemList;
        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItems(List<UserObj> list) {
        for (UserObj obj : list) {
            addItem(obj);
        }
        notifyDataSetChanged();
    }

    private void addItem(UserObj obj) {
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
            convertView = inflater.inflate(R.layout.people_list_item, null);
        }

        UserObj obj = itemList.get(position);
        setContent(convertView, obj);
        setUserPic(convertView, obj);
        setUserAuthTag(convertView, obj);
        setUserTag(convertView, obj);
        setOnClickView(convertView, obj);
        return convertView;
    }

    private void setOnClickView(View view, final UserObj obj) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putString("id", obj.getId());
                Passageway.jumpActivity(context, PeopleContentActivity.class, b);
            }
        });
    }

    private void setUserTag(View view, UserObj obj) {
        LinearLayout tagBox = (LinearLayout) view
                .findViewById(R.id.people_list_userTagBox);
        tagBox.removeAllViews();
        String[] tags = obj.getM_tag();
        if (tags != null) {
            for (int i = 0; i < tags.length; i++) {
                tagBox.addView(getTagView(tags[i]));
                tagBox.addView(getReplaceView());
            }
        }
    }

    private View getReplaceView() {
        return LineViewTool.getHorizontalSpaceView(context);
    }

    private TextView getTagView(String tag) {
        TextView view = new TextView(context);
        view.setText(tag);
        view.setPadding(6, 3, 6, 3);
        view.setBackgroundResource(R.drawable.green_background_green_frame_0);
        view.setTextColor(ColorBox.getColorForID(context, R.color.green_01));
        return view;
    }

    private void setUserAuthTag(View view, UserObj obj) {
        ImageView userAuthTag = (ImageView) view
                .findViewById(R.id.people_list_userAuthTag);
        userAuthTag.setVisibility(View.GONE);
        String authTag = obj.getM_auth_tag();
        if (authTag.equals("知名")) {
            userAuthTag.setImageResource(R.drawable.zhiming_icomn);
            userAuthTag.setVisibility(View.VISIBLE);
        } else if (authTag.equals("实名")) {
            userAuthTag.setImageResource(R.drawable.shiming_icon);
            userAuthTag.setVisibility(View.VISIBLE);
        }
    }

    private void setUserPic(View view, UserObj obj) {
        ImageView userPic = (ImageView) view
                .findViewById(R.id.people_list_userPic);

        int w = WinTool.getWinWidth(context) / 5;
        userPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
        DownloadImageLoader.loadImage(userPic, obj.getM_avatar(), 5);
    }

    private void setContent(View view, UserObj obj) {
        TextView userName = (TextView) view
                .findViewById(R.id.people_list_userName);
        TextView userDescription = (TextView) view
                .findViewById(R.id.people_list_userDescription);
        TextView userCity = (TextView) view
                .findViewById(R.id.people_list_userCity);
        TextView friendText = (TextView) view.findViewById(R.id.people_list_friendText);

        userName.setText(obj.getM_nick_name());
        userDescription.setText(obj.getM_description());
        userCity.setText(obj.getUserCity());

        if (UserObjHandle.isLogin(context)) {
            if (obj.isBlock()) {
                friendText.setVisibility(View.VISIBLE);
                friendText.setText("已屏蔽");
            } else if (obj.isFriend()) {
                friendText.setVisibility(View.VISIBLE);
                friendText.setText("好友");
            } else {
                friendText.setVisibility(View.GONE);
            }
        }

    }
}
