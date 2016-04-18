package com.zmyh.r.main.user;

import android.content.Context;
import android.os.Bundle;

import com.zmyh.r.dailog.MessageDialog;
import com.zmyh.r.tool.Passageway;

/**
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
 * Created by Hua on 15/7/20.
 */
public class VerifyDialog {

    public static void showVerifyDialog(final Context context, final int come, final String m_mobile, final String m_password) {
        if (context != null) {
            MessageDialog dialog = new MessageDialog(context);
            dialog.setTitel("请确认你的电话号码");
            dialog.setMessage("我们将发送验证码短信到这个号码:" + "\n" + m_mobile);
            dialog.setCancelStyle("取消");
            dialog.setCancelListener(null);
            dialog.setCommitStyle("确定");
            dialog.setCommitListener(new MessageDialog.CallBackListener() {
                @Override
                public void callback() {
                    Bundle b=new Bundle();
                    b.putInt(VerifyActivity.COME_KEY,come);
                    b.putString(VerifyActivity.TEL_KEY, m_mobile);
                    b.putString(VerifyActivity.PASSWORD_KEY,m_password);
                    Passageway.jumpActivity(context,VerifyActivity.class,VerifyActivity.RC,b);
                }
            });
        }
    }

}
