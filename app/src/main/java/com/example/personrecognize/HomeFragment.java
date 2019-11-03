package com.example.personrecognize;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private Button home_recognizeBtn;
    private Button home_uploadInformBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home,container,false);
        //返回fragment的UI布局。参数为fragment的layout界面，container容器，第三个参数如果为true则会把fragment的布局添加到container两次
        home_recognizeBtn = root.findViewById(R.id.home_recognizeBtn);
        home_uploadInformBtn = root.findViewById(R.id.home_uploadInformBtn);
        home_recognizeBtn.setOnClickListener(this);
        home_uploadInformBtn.setOnClickListener(this);
        return root;
    }

    public static HomeFragment newInstance()
    {
        HomeFragment hf=new HomeFragment();
        return  hf;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.home_recognizeBtn:
                startRecognizeFragment();
                break;
            case R.id.home_uploadInformBtn:
                startUpLoadInformFragment();
                break;
        }
    }

    public void startRecognizeFragment()
    {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragContainer,RecognizeFragment.newInstance(),"RecognizeFragment")
                .addToBackStack(null)
                .commit();
    }

    public void startUpLoadInformFragment()
    {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragContainer,UploadInformFragment.newInstance(),"UploadInformFragment")
                .addToBackStack(null)
                .commit();

    }
}
