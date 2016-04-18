package com.zmyh.r.dailog;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.opengl.ETC1;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmyh.r.R;
import com.zmyh.r.handler.SystemHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.interfaces.CallbackForString;

public class InputDialog {

    private boolean isShow;

    private Context context;
    private AlertDialog ad;
    private Window window;

    private EditText mEditText;
    private ImageView isShowIcon;
    private TextView title, commit;
    private LinearLayout isShowBox;

    private InputMethodManager imm = null;

    public InputDialog(Context context) {
        this.context = context;
        ad = new android.app.AlertDialog.Builder(context).create();

        ad.setView(((LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.inputdialog, null));
        ad.show();

        window = ad.getWindow();
        window.setContentView(R.layout.inputdialog);

        title = (TextView) window.findViewById(R.id.inputDialog_title);
        mEditText = (EditText) window.findViewById(R.id.inputDialog_ET);
        commit = (TextView) window.findViewById(R.id.inputDialog_commit);
        isShowBox = (LinearLayout) window.findViewById(R.id.inputDialog_isShow);
        isShowIcon = (ImageView) window
                .findViewById(R.id.inputDialog_isShowIcon);

        imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        isShow = SystemHandle.getIsGoneShow(context);

        setLayout();
        setListener();

        showImm();
    }

    public void setIntutType(int type) {
        mEditText.setInputType(type);
    }

    private void setListener() {
        isShowBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isShow = !isShow;
                if (isShow) {
                    isShowIcon.setImageResource(R.drawable.click_on_icon);
                } else {
                    isShowIcon.setImageResource(R.drawable.click_off_icon);
                }
            }
        });
    }

    private void showImm() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                imm.showSoftInput(mEditText, 0);
            }
        }, 1000);
    }

    private void setLayout() {
        setLayout(0.8, 0.5);
    }

    public void setLayout(double Xnum, double Ynum) {
        window.setLayout((int) (WinTool.getWinWidth(context) * Xnum),
                (int) (WinTool.getWinHeight(context) * Ynum));
    }

    public void showBox() {
        isShowBox.setVisibility(View.VISIBLE);
        if (isShow) {
            isShowIcon.setImageResource(R.drawable.click_on_icon);
        } else {
            isShowIcon.setImageResource(R.drawable.click_off_icon);
        }
    }

    public void setTitle(String t) {
        if (t != null && !t.equals("")) {
            title.setVisibility(View.VISIBLE);
            title.setText(t);
        }
    }

    public void goneInput() {
        mEditText.setVisibility(View.GONE);
    }

    public void setInput(String str) {
        mEditText.setText(str);
    }

    public void setHint(String h) {
        if (h != null && !h.equals("")) {
            mEditText.setHint(h);
        }
    }

    public void setListener(final CallbackForString callback) {
        commit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String str = mEditText.getText().toString();
                if (callback != null) {
                    callback.callback(str);
                }
                dismiss();
            }
        });
    }

    public void dismiss() {
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(mEditText.getApplicationWindowToken(),
                    0);
        }
        SystemHandle.saveIsGoneShow(context, isShow);
        ad.dismiss();
    }

}
