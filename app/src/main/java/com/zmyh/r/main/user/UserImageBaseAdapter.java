package com.zmyh.r.main.user;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zmyh.r.R;
import com.zmyh.r.box.UserImageObj;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.UserImageObjHandler;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.photo.ImageListAcitvity;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hua on 15/7/15.
 */
public class UserImageBaseAdapter extends BaseAdapter {

    public final static boolean ADD = true;
    public final static boolean DELETE = false;

    private Context context;
    private LayoutInflater inflater;
    private List<UserImageObj> dataObjList;

    private CallbackForBoolean callback;
    private ProgressBar progress;

    private boolean isShow = true;

    public UserImageBaseAdapter(Context context, List<UserImageObj> list) {
        this.dataObjList = list;
        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setCallBack(CallbackForBoolean callback) {
        this.callback = callback;
    }

    public void setProgress(ProgressBar progress) {
        this.progress = progress;
    }

    public void setProgress(int i) {
        if (progress != null) {
            progress.setProgress(i);
        }
    }

    public void setShowDeleteBtn(boolean isShow) {
        this.isShow = isShow;
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
            convertView = inflater.inflate(R.layout.compile_grid_item, null);
        }

        UserImageObj obj = dataObjList.get(position);

        setImageView(convertView, obj);
        setOnClickImage(convertView, obj);
        setDeleteBtn(convertView, obj);

        return convertView;
    }

    private void setDeleteBtn(View view, final UserImageObj obj) {
        ImageView deleteBtn = (ImageView) view.findViewById(R.id.compile_deletePic);
        if (!obj.isNull() && isShow) {
            deleteBtn.setVisibility(View.VISIBLE);
        } else {
            deleteBtn.setVisibility(View.GONE);
        }

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteImage(obj);
            }
        });

    }

    private void setOnClickImage(View view, final UserImageObj obj) {
        if (obj.isNull()) {
            view.findViewById(R.id.compile_picBox).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callback != null) {
                        callback.callback(ADD);
                    }
                }
            });
        } else {
            view.findViewById(R.id.compile_picBox).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle b = new Bundle();
                    b.putStringArrayList("iamge_list", getImagelist(obj));
                    b.putInt("position", 0);
                    b.putBoolean("isOnline", true);
                    Passageway.jumpActivity(context, ImageListAcitvity.class, b);
                }
            });

        }
    }

    private ArrayList<String> getImagelist(UserImageObj obj) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(obj.getImg());
        return list;
    }

    private void setImageView(View view, UserImageObj obj) {
        ImageView image = (ImageView) view.findViewById(R.id.compile_pic);

        int w = WinTool.getWinWidth(context) / 3
                - WinTool.dipToPx(context, 20);
        image.setLayoutParams(new LinearLayout.LayoutParams(w, w));
        if (!obj.isNull()) {
            DownloadImageLoader.loadImage(image, obj.getImg());
        } else {
            DownloadImageLoader.loadImageForID(image, R.drawable.add_pic_icon);
        }
    }

    private void deleteImage(UserImageObj obj) {
        setProgress(View.VISIBLE);

        String url = UrlHandle.getMmUserAlbum() + "/" + obj.getId();

        RequestParams params = HttpUtilsBox.getRequestParams(context);

        HttpUtilsBox.getHttpUtil().send(HttpRequest.HttpMethod.DELETE, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        setProgress(View.GONE);
                        ShowMessage.showFailure(context);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        setProgress(View.GONE);
                        String result = responseInfo.result;
                        Log.d("", result);
                        JSONObject json = JsonHandle.getJSON(
                                JsonHandle.getJSON(result), "result");
                        if (json != null) {
                            if (JsonHandle.getInt(JsonHandle.getJSON(json, "o"), "ok") == 1) {
                                if (callback != null) {
                                    callback.callback(DELETE);
                                }
                            }
                        }
                    }

                });

    }

}
