package com.zmyh.r.dailog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmyh.r.R;
import com.zmyh.r.handler.SystemHandle;
import com.zmyh.r.handler.WinTool;

import java.util.Timer;
import java.util.TimerTask;

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
 * Created by Hua on 15/9/21.
 */
public class CustomDialog {

    private Context context;
    private AlertDialog ad;
    private Window window;

    private EditText minEditText, maxEditText;
    private TextView commit;

    private InputMethodManager imm = null;

    public CustomDialog(Context context) {
        this.context = context;
        ad = new android.app.AlertDialog.Builder(context).create();

        ad.setView(((LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.inputdialog, null));
        ad.show();

        window = ad.getWindow();
        window.setContentView(R.layout.custom_dialog);

        imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        initView();
        setLayout();
        showImm();
    }

    private void initView() {
        minEditText = (EditText) window.findViewById(R.id.custom_dialog_minET);
        maxEditText = (EditText) window.findViewById(R.id.custom_dialog_maxET);
        commit = (TextView) window.findViewById(R.id.custom_dialog_commit);
    }

    private void setLayout() {
        setLayout(0.8, 0.5);
    }

    public void setLayout(double Xnum, double Ynum) {
        window.setLayout((int) (WinTool.getWinWidth(context) * Xnum),
                (int) (WinTool.getWinHeight(context) * Ynum));
    }

    private void showImm() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                imm.showSoftInput(minEditText, 0);
            }
        }, 1000);
    }

    public interface CustomListener {
        public void callback(int min, int max);
    }

    public void dismiss() {
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(minEditText.getApplicationWindowToken(),
                    0);
        }
        ad.dismiss();
    }

    public void setListener(final CustomListener listener) {
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

                int min = 0;
                int max = 0;

                try {
                    min = Integer.valueOf(minEditText.getText().toString());
                    max = Integer.valueOf(maxEditText.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                listener.callback(min, max);
            }
        });
    }

}
