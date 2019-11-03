package com.example.personrecognize;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

    private File outImg;
    private Uri imgUri;
    //private byte[] fileBuff;

    public File getOutImg() { return outImg; }
    public void setOutImg(File outImg) { this.outImg = outImg; }
    public Uri getImgUri() { return imgUri; }
    public void setImgUri(Uri imgUri) { this.imgUri = imgUri; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_uploadinform,container,false);
        takePhotoBtn = root.findViewById(R.id.uf_takePhotoBtn);
        uploadInformBtn = root.findViewById(R.id.uf_uploadInformBtn);
        showPhotoIv = root.findViewById(R.id.uf_showPhotoIv);
        nameEt = root.findViewById(R.id.uf_nameEt);
        informEt = root.findViewById(R.id.uf_informTv);

        takePhotoBtn.setOnClickListener(this);
        uploadInformBtn.setOnClickListener(this);

        return root;
    }

    public static UploadInformFragment newInstance()
    {
        UploadInformFragment uf=new UploadInformFragment();
        return  uf;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.uf_takePhotoBtn:
                PhotoManager.takePhoto(this.getActivity(),this);
                break;
            case R.id.uf_uploadInformBtn:
        }

    }
}
