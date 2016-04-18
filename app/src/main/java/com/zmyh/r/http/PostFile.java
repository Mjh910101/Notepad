package com.zmyh.r.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.interfaces.PostFileCallback;

public class PostFile {

    private static PostFile postFile = new PostFile();

    private final static String LINEND = "\r\n";
    private final static String BOUNDARY = "---------------------------7da2137580612"; // 鏁版嵁鍒嗛殧绾�
    private final static String PREFIX = "--";
    private final static String MUTIPART_FORMDATA = "multipart/form-data";
    private final static String CHARSET = "utf-8";
    private final static String CONTENTTYPE = "application/octet-stream";

    private PostFileCallback callback = null;

    private PostFile() {
    }

    public static PostFile getInstance() {
        return postFile;
    }

    public void post(String actionUrl, HttpFlieBox box,
                     PostFileCallback callback) {
        Log.e("url", actionUrl);
        setCallback(callback);
        post(actionUrl, box);
    }

    private void post(final String actionUrl, final HttpFlieBox box) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    HttpURLConnection urlConn = getURLConnection(actionUrl, box);

                    DataOutputStream dos = new DataOutputStream(urlConn
                            .getOutputStream());
                    // 鏋勫缓琛ㄥ崟鏁版嵁
                    String entryText = bulidFormText(box.getParamMap());
                    Log.i("---------------------", entryText);
                    dos.write(entryText.getBytes());
                    // ******************
                    for (File file : box.getFileList()) {
                        StringBuffer sb = new StringBuffer("");
                        sb.append(PREFIX).append(BOUNDARY).append(LINEND);
                        sb.append("Content-Disposition: form-data; name=\""
                                + box.getFileKey() + "\"; filename=\""
                                + file.getName() + "\"" + LINEND);
                        sb.append("Content-Type:" + CONTENTTYPE + ";charset="
                                + CHARSET + LINEND);
                        sb.append(LINEND);
                        dos.write(sb.toString().getBytes());

//                        InputStream is = new FileInputStream(file);
//                        byte[] buffer = new byte[1024 * 1024];
//                        int len = 0;
//                        while ((len = is.read(buffer)) != -1) {
//                            dos.write(buffer, 0, len);
//                        }
//                        is.close();

                        byte[] buffer = reduceImage(file);

                        dos.write(buffer, 0, buffer.length);

                        dos.write(LINEND.getBytes());
                    }
                    // 璇锋眰鐨勭粨鏉熸爣蹇�
                    byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND)
                            .getBytes();
                    dos.write(end_data);
                    dos.flush();

                    int code = urlConn.getResponseCode();
                    if (code != 200) {
                        urlConn.disconnect();
                        sean(new Exception("404"));
                    } else {
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(urlConn.getInputStream()));
                        String result = "";
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            result += line;
                        }
                        br.close();
                        urlConn.disconnect();
                        sean(result);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    sean(e);
                }
            }

        }).start();
    }

    private byte[] reduceImage(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file.toString(), options);

        int maxWidth = 600;
        int height = -1;
        if (options.outWidth > 600) {
            height = options.outHeight * maxWidth / options.outWidth;
        }
//        if (options.outWidth >= 800 && options.outWidth <= 1600) {
//            options.inSampleSize = 2;
//        } else if (options.outWidth > 1600 && options.outWidth <= 3200) {
//            options.inSampleSize = 4;
//        } else if (options.outWidth > 3200) {
//            options.inSampleSize = 6;
//        }
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(file.toString(), options);
        if (height > 0) {
            bmp = Bitmap.createScaledBitmap(bmp, maxWidth, height, false);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        return baos.toByteArray();
    }

    private HttpURLConnection getURLConnection(String actionUrl, HttpFlieBox box)
            throws Exception {
        URL url = new URL(actionUrl);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setDoOutput(true); // 鍏佽杈撳嚭
        urlConn.setDoInput(true); // 鍏佽杈撳叆
        urlConn.setUseCaches(false);
        urlConn.setRequestMethod("POST");
        urlConn.setRequestProperty("connection", "Keep-Alive");
        urlConn.setRequestProperty("Charset", CHARSET);
        urlConn.setRequestProperty("Content-Type", MUTIPART_FORMDATA
                + ";boundary=" + BOUNDARY);
        urlConn.setConnectTimeout(10 * 60 * 1000);
        urlConn.setReadTimeout(10 * 60 * 1000);
        // *****************************************
        Map<String, String> haedMap = box.getHeadMap();
        for (Entry<String, String> entry : haedMap.entrySet()) {
            urlConn.setRequestProperty(entry.getKey(), entry.getValue());
        }
        return urlConn;
    }

    private void setCallback(PostFileCallback callback) {
        this.callback = callback;
    }

    private void sean(String result) {
        Message.obtain(handler, 0, result).sendToTarget();
    }

    private void sean(Exception e) {
        Message.obtain(handler, 1, e).sendToTarget();
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (callback != null) {
                switch (msg.what) {
                    case 0:
                        callback.callback((String) msg.obj);
                        break;
                    case 1:
                        callback.onFailure((Exception) msg.obj);
                        break;
                }
            }
        }
    };

    /**
     * 灏佽琛ㄥ崟鏂囨湰鏁版嵁
     *
     * @param paramText
     * @return
     */
    private String bulidFormText(Map<String, String> paramText) {
        if (paramText == null || paramText.isEmpty())
            return "";
        StringBuffer sb = new StringBuffer("");
        for (Entry<String, String> entry : paramText.entrySet()) {
            sb.append(PREFIX).append(BOUNDARY).append(LINEND);
            sb.append("Content-Disposition:form-data;name=\"" + entry.getKey()
                    + "\"" + LINEND);
            // sb.append("Content-Type:text/plain;charset=" + CHARSET + LINEND);
            sb.append(LINEND);
            sb.append(entry.getValue());
            sb.append(LINEND);
        }
        return sb.toString();
    }

    /**
     * 灏佽鏂囦欢鏂囨湰鏁版嵁
     *
     * @param files
     * @return
     */
    private String buildFromFile(FileInfo[] files) {
        StringBuffer sb = new StringBuffer();
        for (FileInfo file : files) {
            sb.append(PREFIX).append(BOUNDARY).append(LINEND);
            sb.append("Content-Disposition: form-data; name=\""
                    + file.getFileTextName() + "\"; filename=\""
                    + file.getFile().getAbsolutePath() + "\"" + LINEND);
            sb.append("Content-Type:" + CONTENTTYPE + ";charset=" + CHARSET
                    + LINEND);
            sb.append(LINEND);
        }
        return sb.toString();
    }

}
