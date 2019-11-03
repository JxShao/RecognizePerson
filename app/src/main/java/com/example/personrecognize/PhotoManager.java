package com.example.personrecognize;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

//处理照片请求类
public class PhotoManager {
    private static File outImg;
    private static Uri imgUri;
    public static int requestCode;
    public static void takePhoto(Activity activity, Fragment fragment)
    {
        //删除并创建临时文件，用于保存拍照后的照片
        //android 6以后，写Sdcard是危险权限，需要运行时申请，但此处使用的是"项目关联目录"，无需！

        outImg=new File(activity.getExternalCacheDir(),"temp.jpg");
        if(outImg.exists()) outImg.delete();
        try {
            outImg.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //复杂的Uri创建方式
        if(Build.VERSION.SDK_INT>=24)
        //这是Android 7后，更加安全的获取文件uri的方式（需要配合Provider,在Manifest.xml中加以配置）
        {
            imgUri = FileProvider.getUriForFile(activity, "cn.PersonRecognize.fileprovider", outImg);
        }
        else
            imgUri= Uri.fromFile(outImg);

        switch(fragment.getTag())
        {
            case "RecognizeFragment":
                ((RecognizeFragment) fragment).setOutImg(outImg);
                ((RecognizeFragment) fragment).setImgUri(imgUri);
                requestCode=1;
                break;
            case "UploadInformFragment":
                ((UploadInformFragment) fragment).setOutImg(outImg);
                ((UploadInformFragment) fragment).setImgUri(imgUri);
                requestCode=2;
                break;
        }

        //利用actionName和Extra,启动相机Activity
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
        activity.startActivityForResult(intent,requestCode);
    }


    public static Bitmap handleTakenPhoto(Activity activity,Uri imgUri) throws FileNotFoundException             //返回拍摄照片的bitmap
    {
        Bitmap map = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imgUri));
        int width = map.getWidth();
        int height = map.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        // 围绕原地进行旋转
        map = Bitmap.createBitmap(map, 0, 0, width, height, matrix, false);
        return map;
    }

    public static String ChangeBitmapToBase64(Bitmap bitmap)
    {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;

    }

}
