package com.example.personrecognize;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecognizeFragment extends Fragment implements View.OnClickListener {
    Button toRecognizeBtn;
    Button takePhotoBtn;
    Button choosePhotoFromAlbumBtn;
    ImageView showPhotoIv;

    private File outImg;
    private Uri imgUri;
    private OkHttpClient client;
    private byte[] fileBuff;
    private Bitmap bitmap;
    private Response response;

    public Uri getImgUri() {
        return imgUri;
    }
    public void setImgUri(Uri imgUri) { this.imgUri = imgUri; }
    public File getOutImg() { return outImg; }
    public void setOutImg(File outImg) { this.outImg = outImg; }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recognize,container,false);
        toRecognizeBtn = root.findViewById(R.id.rf_toRecognizeBtn);
        takePhotoBtn = root.findViewById(R.id.rf_takePhotoBtn);
        choosePhotoFromAlbumBtn = root.findViewById(R.id.rf_choosePhotoFromAlbumBtn);
        showPhotoIv = root.findViewById(R.id.rf_showPhotoIv);

        toRecognizeBtn.setOnClickListener(this);
        takePhotoBtn.setOnClickListener(this);
        choosePhotoFromAlbumBtn.setOnClickListener(this);
        return root;
    }

    public static RecognizeFragment newInstance()
    {
        RecognizeFragment rf=new RecognizeFragment();
        return  rf;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rf_choosePhotoFromAlbumBtn:
                choosePhotoFromAlbum();
                break;
            case R.id.rf_takePhotoBtn:
                PhotoManager.takePhoto(this.getActivity(),this);
                break;
            case R.id.rf_toRecognizeBtn:
                recognize();
                break;
        }
    }

    public void choosePhotoFromAlbum()
    {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};  //存放请求哪种权限
        //进行sdcard的读写请求
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //如果没有此权限
            ActivityCompat.requestPermissions(getActivity(), permissions, 2); //进行权限申请，并设置请求码为1作为识别请求的标志
        } else {
            openGallery(); //若拥有此权限，打开相册，进行选择
        }
    }
    public void openGallery()
    {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        //通过Intent来启动Activity,此处为获取内容的action
        intent.setType("image/*");  //获取内容为图片
        getActivity().startActivityForResult(intent, 3);  //启动此Activity需要返回结果，请求码为2，标识其是哪一个请求
    }

    public void handleChoosePhoto(Intent intent)
    {
        Cursor cursor=null;  //游标，用于选取数据集中的数据
        Uri uri=intent.getData();
        ContentResolver contentResolver=getActivity().getContentResolver();
        cursor=contentResolver.query(uri,null,null,null,null);//参数：内容URI，返回哪几列的内容，筛选条件，用于替换上一个参数中？占位符的值，按什么进行排序

        if(cursor.moveToFirst()) //如果查询结果不是空
        {
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);   //获取指定列名的索引值
            String fileName;
            fileName = cursor.getString(index); //获取此Cursor所指行的名字列的值,即获取图片文件名
            Log.i("RF","图片文件名为"+fileName);

            try {
                InputStream inputStream=contentResolver.openInputStream(uri); //将URI文件转换为InputStream输入流
                fileBuff=convertToBytes(inputStream); //从输入流中读取数据，并转换成字节数组
                bitmap= BitmapFactory.decodeByteArray(fileBuff,0,fileBuff.length);
                showPhotoIv.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            cursor.close();
        }

    }
    private byte[] convertToBytes(InputStream inputStream) throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        inputStream.close();
        return  out.toByteArray();
    }


    public void recognize()
    {
        new Thread(){
            @Override
            public void run() {
                client=new OkHttpClient();
                String Base64Photo=PhotoManager.ChangeBitmapToBase64(bitmap);
                JSONObject obj=new JSONObject();
                try {
                    obj.put("image",Base64Photo);
                    obj.put("image_type","BASE64");
                    obj.put("group_id_list","group1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody=  RequestBody
                        .create(MediaType.parse("application/json; charset=utf-8"),""+obj.toString());
                Log.i("base64字符串",Base64Photo);

                //整个上传的请求体部分（普通表单+文件上传域）
                //RequestBody requestBody=new MultipartBody.Builder()
                        //.setType(MultipartBody.FORM)
                        //.addFormDataPart("image", "myPhoto.jpg",formBody)
                        //.build();
                Request request = new Request.Builder()
                        .url("https://aip.baidubce.com/rest/2.0/face/v3/search?access_token=24.29062b34a7fc64f68465706de4b9a2a8.2592000.1575358784.282335-17603369")
                        .post(requestBody)
                        .build();

                try {
                    Log.i("发送请求",""+requestBody);
                    response = client.newCall(request).execute();
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void parseJSON(String jsonData) {
        try {
            JSONObject object = new JSONObject(jsonData);
            String name = object.getString("RESULT");
            //日志
            Log.d("name", "结果是：" + name);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
