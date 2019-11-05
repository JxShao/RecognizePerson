package com.example.personrecognize;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ShowRecognizeResultFragment extends Fragment{
    private TextView showInformTv;
    private String inform;
    private float score;
    private Bitmap bitmap;
    private boolean flag=false;

    public String getInform() { return inform; }
    public void setInform(String inform) { this.inform = inform; }
    public float getScore() { return score; }
    public void setScore(float score) { this.score = score; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recognizeresult,container,false);
        showInformTv=root.findViewById(R.id.rrf_showInformTv);
        bitmap=((MainActivity)getActivity()).getBitmap();
        inform = null;
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        new Thread() {
            @Override
            public void run() {
                AccessBaiduManager.sendPhotoToRecognize(getActivity(),bitmap);
                flag=true;
            }
        }.start();
        while(!flag)
        {
            //等待daidu返回结果
        }
        Log.i("返回的信息",""+((MainActivity)getActivity()).getRecognizeResult());
        AccessBaiduManager.handleBaiDuRecognizeResponse(((MainActivity)getActivity()).getRecognizeResult(),getActivity(),this);
        showInformTv.setText(inform);
    }

    public static ShowRecognizeResultFragment newInstance()
    {
        ShowRecognizeResultFragment srrf=new ShowRecognizeResultFragment();
        return  srrf;
    }

}
