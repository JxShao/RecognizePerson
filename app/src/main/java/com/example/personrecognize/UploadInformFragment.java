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
import java.util.ArrayList;

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
    private ArrayList<Person> personList;


    private int errorCode;
    public int getErrorCode() { return errorCode; }
    public void setErrorCode(int errorCode) { this.errorCode = errorCode; }

    public ArrayList<Person> getPersonList() { return personList; }
    public void setPersonList(ArrayList<Person> personList) { this.personList = personList; }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

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
        imgUri=null;
        errorCode=-1;
        takePhotoBtn.setOnClickListener(this);
        uploadInformBtn.setOnClickListener(this);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        score = -1;
        resultDialog = new AlertDialog.Builder(this.getContext())
                .setTitle("结果")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //getActivity().getSupportFragmentManager().popBackStack();
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
                Log.i("UF拍摄的照片", ((MainActivity) getActivity()).getBitmap() + "");
                break;
            case R.id.uf_uploadInformBtn:
                upLoadInform();
        }

    }

    public void upLoadInform() {
        final String uname = nameEt.getText().toString();
        //Log.i("填写的姓名为",uname.equals()+"啦啦啦啦啦");
        final String uinform = informEt.getText().toString();
        if(nameEt.length()<1 || informEt.length()<1)
        {
            resultDialog.setMessage("请填写必要信息！");
            resultDialog.show();
        }
        else if(imgUri==null)
        {
            resultDialog.setMessage("请拍摄照片！");
            resultDialog.show();
        }
        else {
            Thread t = new Thread() {
                @Override
                public void run() {
                    AccessBaiduManager.sendPhotoToMultiRecognize(getActivity(), ((MainActivity) getActivity()).getBitmap());
                }
            };
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            AccessBaiduManager.handleBaiDuRecognizeResponse(((MainActivity) getActivity()).getRecognizeResult(), getActivity(), this);
        }
        Log.i("UF", score + "");
        if(errorCode == 0) {
            if (personList.get(0).getScore() >= 80) {
                resultDialog.setMessage("此人信息已存在！");
                resultDialog.show();
            } else {

                Log.i("UF", "开始上传信息");
                new Thread() {
                    @Override
                    public void run() {
                        AccessBaiduManager.uploadFaceInform(((MainActivity) getActivity()).getBitmap(), uname, uinform);
                    }
                }.start();
                resultDialog.setMessage("成功添加至信息库！");
                resultDialog.show();
            }
        }
        else
        {
            if(errorCode == 222202) {
                resultDialog.setMessage("未识别到人脸");
                resultDialog.show();
            }
        }

    }

}



