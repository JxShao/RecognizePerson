package com.example.personrecognize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity{
    private RecognizeFragment rf;
    private UploadInformFragment uf;

    private Bitmap bitmap;
    private String RecognizeResult;

    public Bitmap getBitmap() { return bitmap; }
    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }
    public String getRecognizeResult() { return RecognizeResult; }
    public void setRecognizeResult(String recognizeResult) { RecognizeResult = recognizeResult; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragContainer,HomeFragment.newInstance(),"HomeFragment")
                .commit();


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //判断若成功获取权限
                    rf = (RecognizeFragment) getSupportFragmentManager().findFragmentByTag("RecognizeFragment");
                    rf.openGallery();
                } else {
                    Toast.makeText(this, "读相册的操作被拒绝", Toast.LENGTH_LONG).show();
                    //给用户弹出提示
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case 1:
                //此时，相机拍照完毕
                //处理识别fragment中的拍照请求
                if (resultCode == RESULT_OK) {

                    try {
                        rf = (RecognizeFragment)getSupportFragmentManager().findFragmentByTag("RecognizeFragment");
                        bitmap = PhotoManager.handleTakenPhoto(this,rf.getImgUri());
                        ((ImageView)rf.getView().findViewById(R.id.rf_showPhotoIv)).setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                //处理上传fragment中的拍照请求
                if (resultCode == RESULT_OK) {

                    try {
                        uf = (UploadInformFragment) getSupportFragmentManager().findFragmentByTag("UploadInformFragment");
                        bitmap = PhotoManager.handleTakenPhoto(this,uf.getImgUri());
                        ((ImageView)uf.getView().findViewById(R.id.uf_showPhotoIv)).setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 3:
                //处理Recognizefragment中的从相册中选择照片请求
                rf = (RecognizeFragment)getSupportFragmentManager().findFragmentByTag("RecognizeFragment");
                rf.handleChoosePhoto(data);  //选择的照片保存在名为data的Intent中
                break;
        }
    }


}
