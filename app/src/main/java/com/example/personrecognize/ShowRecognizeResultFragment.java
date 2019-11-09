package com.example.personrecognize;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class ShowRecognizeResultFragment extends Fragment implements View.OnClickListener {
    private String inform;
    private float score;
    private Bitmap bitmap;
    private boolean flag;
    //private Button backBtn;
    private ListView personLv;
    private PersonAdapter personAdapter;
    private View root;
    private ArrayList<Person> personList;
    private int errorCode;
    private AlertDialog resultDialog;

    public int getErrorCode() { return errorCode; }
    public void setErrorCode(int errorCode) { this.errorCode = errorCode; }

    public ArrayList<Person> getPersonList() { return personList; }
    public void setPersonList(ArrayList<Person> personList) { this.personList = personList; }

    public String getInform() { return inform; }
    public void setInform(String inform) { this.inform = inform; }

    public float getScore() { return score; }
    public void setScore(float score) { this.score = score; }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("绘制SRRF","OnCreateView");
        root = inflater.inflate(R.layout.fragment_recognizeresult,container,false);
        bitmap=((MainActivity)getActivity()).getBitmap();
        inform = null;

        //backBtn=root.findViewById(R.id.rrf_backBtn);
        //backBtn.setOnClickListener(this);
        personLv=root.findViewById(R.id.rrf_personLv);

        resultDialog = new AlertDialog.Builder(this.getContext())
                .setTitle("错误")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                })
                .create();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Thread t=new Thread() {
            @Override
            public void run() {
                //AccessBaiduManager.sendPhotoToRecognize(getActivity(),bitmap);
                AccessBaiduManager.sendPhotoToMultiRecognize(getActivity(),bitmap);
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.i("返回的信息",""+((MainActivity)getActivity()).getRecognizeResult());

        AccessBaiduManager.handleBaiDuRecognizeResponse(((MainActivity)getActivity()).getRecognizeResult(),getActivity(),this);
            //showInformTv.setText(inform);
        Log.i("错误码是",""+errorCode);
        if(errorCode == 0) {
            personAdapter = new PersonAdapter(this.getContext(), R.layout.showperson, personList);
            personLv.setAdapter(personAdapter);
        }
        else
        {
            if(errorCode == 222202)
            {
                resultDialog.setMessage("未识别到人脸");
                resultDialog.show();
            }
        }


    }

    public static ShowRecognizeResultFragment newInstance()
    {
        ShowRecognizeResultFragment srrf=new ShowRecognizeResultFragment();
        return  srrf;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {

        }

    }

}
