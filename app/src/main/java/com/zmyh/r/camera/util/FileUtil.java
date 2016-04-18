package com.zmyh.r.camera.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import com.zmyh.r.box.CameraPicObj;

public class FileUtil {
    private static final String TAG = "FileUtil";
    private static final File parentPath = Environment
            .getExternalStorageDirectory();
    private static String storagePath = "";
    private static final String DST_FOLDER_NAME = "MMJJR";
    private static final String ORIGINAL_PATH = "original";
    private static final String MEDIUM_PATH = "medium";
    private static final String SMALL_PATH = "small";

    /**
     * 初始化保存路径
     *
     * @return
     */
    private static String initPath() {
        if (storagePath.equals("")) {
            storagePath = parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME;
            File f = new File(storagePath);
            if (!f.exists()) {
                f.mkdir();
            }
        }
        return storagePath;
    }

    private static void createNomedia(String path) {
        File f = new File(path + "/.nomedia");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 大图
     *
     * @return
     */
    public static String getOriginalPath() {
        String path = initPath() + "/" + ORIGINAL_PATH;
        File f = new File(path);
        if (!f.exists()) {
            f.mkdir();
        }
        createNomedia(path);
        return path;
    }

    /**
     * 中图
     *
     * @return
     */
    public static String getMediumPath() {
        String path = initPath() + "/" + MEDIUM_PATH;
        File f = new File(path);
        if (!f.exists()) {
            f.mkdir();
        }
        createNomedia(path);
        return path;
    }

    /**
     * 小图
     *
     * @return
     */
    public static String getSmallPath() {
        String path = initPath() + "/" + SMALL_PATH;
        File f = new File(path);
        if (!f.exists()) {
            f.mkdir();
        }
        createNomedia(path);
        return path;
    }

    // /**
    // * 保存Bitmap到sdcard
    // *
    // * @param b
    // */
    // public static void saveBitmap(Bitmap b) {
    // saveBitmap(b, String.valueOf(System.currentTimeMillis()) + ".jpg");
    // }

    /**
     * 保存Bitmap到sdcard
     *
     * @param b
     * @param obj
     */
    public static void saveBitmap(Bitmap b, CameraPicObj obj) {
        saveOriginalBitmap(b, obj);
        saveMediumBitmap(b, obj);
        saveSmallBitmap(b, obj);
    }

    /**
     * 保存Bitmap到sdcard
     *
     * @param data
     * @param s
     * @param obj
     */
    public static void saveBitmap(byte[] data, Size s, CameraPicObj obj) {
        saveOriginalBitmap(data, obj);
        saveMediumBitmap(obj);
        // saveSmallBitmap(data, obj);
    }

    public static void saveSmallBitmap(Bitmap b, CameraPicObj obj) {
        String path = getSmallPath();
        String jpegName = path + "/" + obj.getSmallFileName();
        Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
        saveImage(b, jpegName);
    }

    public static void saveSmallBitmap(byte[] data, CameraPicObj obj) {
        Options o = new Options();
        o.inSampleSize = 15;
        Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length, o);
        saveSmallBitmap(b, obj);
    }

    public static void saveMediumBitmap(Bitmap b, CameraPicObj obj) {
        String path = getMediumPath();
        String jpegName = path + "/" + obj.getMediumFileName();
        Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
        saveImage(b, jpegName);
    }

    public static void saveMediumBitmap(byte[] data, CameraPicObj obj) {
        Options o = new Options();
        o.inSampleSize = 10;
        Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length, o);
        b = ImageUtil.getRotateBitmap(b, 90.0f);
        saveMediumBitmap(b, obj);
    }

    public static void saveMediumBitmap(CameraPicObj obj) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(getOriginalPath() + "/" + obj.getOriginalFileName(), options);

        int maxHeigh = 600;
        int width = options.outWidth * maxHeigh / options.outHeight;
        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;
        Bitmap b = BitmapFactory.decodeFile(getOriginalPath() + "/" + obj.getOriginalFileName(), options);
        b = Bitmap.createScaledBitmap(b, width, maxHeigh, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 70, baos);

        byte[] data = baos.toByteArray();
        Bitmap bit = BitmapFactory.decodeByteArray(data, 0, data.length);
        bit = ImageUtil.getRotateBitmap(bit, 90.0f);
        saveMediumBitmap(bit, obj);
    }

    public static void saveOriginalBitmap(Bitmap b, CameraPicObj obj) {
        String path = getOriginalPath();
        String jpegName = path + "/" + obj.getOriginalFileName();
        Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
        saveImage(b, jpegName);
    }

    public static void saveOriginalBitmap(byte[] data, CameraPicObj obj) {
        String path = getOriginalPath();
        String jpegName = path + "/" + obj.getOriginalFileName();
        Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
        saveImage(data, jpegName);
    }

    public static void saveOriginalBitmap(String url, CameraPicObj obj) {
        try {

            String path = getOriginalPath();
            String jpegName = path + "/" + obj.getOriginalFileName();

            URL uri = new URL(url);
            URLConnection ucon = uri.openConnection();
            InputStream is = ucon.getInputStream();
            FileUtil.saveImage(is, jpegName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveImage(byte[] data, String jpegName) {
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            fout.write(data);
            fout.flush();
            fout.close();
            try {
                Log.e("ExifInterface", jpegName);
                ExifInterface exifInterface = new ExifInterface(jpegName);
                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_90));
                exifInterface.saveAttributes();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(TAG, "saveBitmap成功");
        } catch (IOException e) {
            Log.i(TAG, "saveBitmap:失败");
            e.printStackTrace();
        }


    }

    private static void saveImage(Bitmap b, String jpegName) {
        FileOutputStream fout = null;
        BufferedOutputStream bos = null;
        try {
            fout = new FileOutputStream(jpegName);
            bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            fout.flush();
            fout.close();
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        b.recycle();
        fout = null;
        bos = null;
        b = null;
    }

    public static void saveImage(InputStream imgIS, String jpegName) {
        int bytesum = 0;
        int byteread = 0;
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            byte[] buffer = new byte[1024];
            while ((byteread = imgIS.read(buffer)) != -1) {
                bytesum += byteread; // 字节数 文件大小
                fout.write(buffer, 0, byteread);
            }
            fout.flush();
            fout.close();
            imgIS.close();
            Log.i(TAG, "saveBitmap成功");
        } catch (IOException e) {
            Log.i(TAG, "saveBitmap:失败");
            e.printStackTrace();
        }
    }

    // /**
    // * 保存Bitmap到sdcard
    // *
    // * @param b
    // * @param name
    // */
    // public static void saveBitmap(Bitmap b, String name) {
    // String path = initPath();
    // String jpegName = path + "/" + name;
    // Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
    // try {
    // FileOutputStream fout = new FileOutputStream(jpegName);
    // BufferedOutputStream bos = new BufferedOutputStream(fout);
    // b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
    // bos.flush();
    // bos.close();
    // Log.i(TAG, "saveBitmap成功");
    // } catch (IOException e) {
    // Log.i(TAG, "saveBitmap:失败");
    // e.printStackTrace();
    // }
    // }

    // public static void saveBitmap(String url, String name) {
    // try {
    // URL uri = new URL(url);
    // URLConnection ucon = uri.openConnection();
    // InputStream is = ucon.getInputStream();
    // FileUtil.saveBitmap(is, name);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    // /**
    // * 复制图片
    // *
    // * @param imgIS
    // * @param name
    // */
    // public static void saveBitmap(InputStream imgIS, String name) {
    // int bytesum = 0;
    // int byteread = 0;
    // String path = initPath();
    // String jpegName = path + "/" + name;
    // Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
    // try {
    // FileOutputStream fout = new FileOutputStream(jpegName);
    // byte[] buffer = new byte[1024];
    // while ((byteread = imgIS.read(buffer)) != -1) {
    // bytesum += byteread; // 字节数 文件大小
    // fout.write(buffer, 0, byteread);
    // }
    // fout.flush();
    // fout.close();
    // imgIS.close();
    // Log.i(TAG, "saveBitmap成功");
    // } catch (IOException e) {
    // Log.i(TAG, "saveBitmap:失败");
    // e.printStackTrace();
    // }
    // }

    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void deleteImage(CameraPicObj obj) {
        deleteFile(obj.getMediumFilePath());
        deleteFile(obj.getOriginalFilePath());
    }

    public static boolean deleteFile(String path) {
        try {
            File f = new File(path);
            if (f.exists()) {
                f.delete();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
