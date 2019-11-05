package com.example.personrecognize;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;

public class UploadInformFragment extends Fragment implements View.OnClickListener {
    private Button takePhotoBtn;
    private Button uploadInformBtn;
    private ImageView showPhotoIv;
    private EditText nameEt;
    private EditText informEt;
    private AlertDialog resultDialog;

    private File outImg;
    private Uri imgUri;
    private int score;
    private boolean flag;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    //private byte[] fileBuff;

    public File getOutImg() {
        return outImg;
    }

    public void setOutImg(File outImg) {
        this.outImg = outImg;
    }

    public Uri getImgUri() {
        return imgUri;
    }

    public void setImgUri(Uri imgUri) {
        this.imgUri = imgUri;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_uploadinform, container, false);
        takePhotoBtn = root.findViewById(R.id.uf_takePhotoBtn);
        uploadInformBtn = root.findViewById(R.id.uf_uploadInformBtn);
        showPhotoIv = root.findViewById(R.id.uf_showPhotoIv);
        nameEt = root.findViewById(R.id.uf_nameEt);
        informEt = root.findViewById(R.id.uf_informTv);

        takePhotoBtn.setOnClickListener(this);
        uploadInformBtn.setOnClickListener(this);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        flag=false;
        score = -1;
        resultDialog = new AlertDialog.Builder(this.getContext())
                .setTitle("结果")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
    }

    public static UploadInformFragment newInstance() {
        UploadInformFragment uf = new UploadInformFragment();
        return uf;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.uf_takePhotoBtn:
                PhotoManager.takePhoto(this.getActivity(), this);
                Log.i("UF", ((MainActivity) getActivity()).getBitmap() + "");
                break;
            case R.id.uf_uploadInformBtn:
                upLoadInform();
        }

    }

    public void upLoadInform() {
        final String uname = nameEt.getText().toString();
        final String uinform = informEt.getText().toString();
        Log.i("UF", "信息为" + uname + uinform);
        new Thread() {
            @Override
            public void run() {
                AccessBaiduManager.sendPhotoToRecognize(getActivity(), ((MainActivity) getActivity()).getBitmap());
                flag=true;
            }
        }.start();

        while(!flag)
        {
            //等待baidu返回结果
        }
        AccessBaiduManager.handleBaiDuRecognizeResponse(((MainActivity)getActivity()).getRecognizeResult(),getActivity(),this);

        Log.i("UF", score + "");
        if (score >= 80) {
            //Toast t=Toast.makeText(getContext(),"此人信息已存在",Toast.LENGTH_SHORT);
            //t.show();
            resultDialog.setMessage("此人信息已存在！");
            resultDialog.show();
        }
        else {

            Log.i("UF", "开始上传信息");
            new Thread(){
                @Override
                public void run()
                {
                    AccessBaiduManager.uploadFaceInform(((MainActivity) getActivity()).getBitmap(), uname, uinform);
                }
            }.start();
            resultDialog.setMessage("成功添加至信息库！");
            resultDialog.show();
        }

        //resultDialog.setMessage("成功添加至信息库！");
        //resultDialog.show();
    }

}



