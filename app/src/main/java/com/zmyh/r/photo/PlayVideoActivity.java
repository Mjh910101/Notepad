package com.zmyh.r.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.http.HttpFlieBox;

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
 * Created by Hua on 16/1/15.
 */
public class PlayVideoActivity extends Activity {

    public final static int REQUEST_CODE = 104;

    public final static String IS_FILE = "isFile";
    public final static String IS_DELETE = "isDelete";
    public final static String PATH = "path";
    public final static String URL = "url";


    @ViewInject(R.id.title_back)
    private ImageView backBtn;
    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_bg)
    private RelativeLayout titleBg;
    @ViewInject(R.id.video_videoView)
    private VideoView videoView;
    @ViewInject(R.id.video_playIcon)
    private ImageView playIcon;
    @ViewInject(R.id.video_btnBox)
    private LinearLayout btnBox;

    private Bundle mBundle;
    private boolean isFile;
    private MediaController controller;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_video);
        context = this;
        ViewUtils.inject(this);

        initActivity();
        setOnCompletionListener();
    }

    @OnClick({R.id.title_back, R.id.video_saveBtn, R.id.video_playIcon, R.id.video_deleteBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
            case R.id.video_saveBtn:
                finish();
                break;
            case R.id.video_playIcon:
                playStart();
                break;
            case R.id.video_deleteBtn:
                closeForDelete();
                break;
        }
    }

    private void closeForDelete() {
        Intent i = new Intent();
        Bundle b = new Bundle();
        b.putBoolean(IS_DELETE, true);
        i.putExtras(b);
        setResult(REQUEST_CODE, i);
        finish();
    }

    private void playStart() {
//        playIcon.setVisibility(View.GONE);
        videoView.start();
        videoView.requestFocus();
    }

    private void setOnCompletionListener() {
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
//                playIcon.setVisibility(View.VISIBLE);
            }
        });
    }


    private void initActivity() {
        backBtn.setVisibility(View.VISIBLE);
        titleName.setVisibility(View.GONE);
        titleBg.setBackgroundResource(R.color.black);
        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            controller = new MediaController(context);
            videoView.setMediaController(controller);
            controller.setMediaPlayer(videoView);
            controller.show(0);

            isFile = mBundle.getBoolean(IS_FILE);
            if (isFile) {
//                btnBox.setVisibility(View.VISIBLE);
//                Toast.makeText(context, mBundle.getString(PATH), Toast.LENGTH_LONG).show();
                videoView.setVideoPath(HttpFlieBox.getImagePath() + "/" + mBundle.getString(PATH));
            } else {
//                btnBox.setVisibility(View.VISIBLE);
                videoView.setVideoURI(Uri.parse(mBundle.getString(URL)));
            }
            playStart();
        } else {
            finish();
        }

    }
}
