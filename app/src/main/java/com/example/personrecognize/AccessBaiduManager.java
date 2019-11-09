package com.example.personrecognize;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccessBaiduManager {
    private static OkHttpClient client;
    private static Response response;
    private static int errorCode;
    private static ArrayList<Person> personList;
    private static String inform;
    private static int score;

    public static void sendPhotoToRecognize(Activity activity, Bitmap bitmap) { //仅识别一张脸
        bitmap=PhotoManager.compressScale(bitmap);
        client = new OkHttpClient();
        String Base64Photo = PhotoManager.ChangeBitmapToBase64(bitmap);
        JSONObject obj = new JSONObject();
        try {
            obj.put("image", Base64Photo);
            obj.put("image_type", "BASE64");
            obj.put("group_id_list", "group1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody
                .create(MediaType.parse("application/json; charset=utf-8"), "" + obj.toString());

        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/search?access_token=24.29062b34a7fc64f68465706de4b9a2a8.2592000.1575358784.282335-17603369")
                .post(requestBody)
                .build();

        try {
            response = client.newCall(request).execute();
            String res = response.body().string();
            ((MainActivity) activity).setRecognizeResult(res);
            System.out.println(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendPhotoToMultiRecognize(Activity activity, Bitmap bitmap) { //识别图片中的多张脸
        bitmap=PhotoManager.compressScale(bitmap);
        client = new OkHttpClient();
        String Base64Photo = PhotoManager.ChangeBitmapToBase64(bitmap);
        JSONObject obj = new JSONObject();
        try {
            obj.put("image", Base64Photo);
            obj.put("image_type", "BASE64");
            obj.put("group_id_list", "group1");
            obj.put("max_face_num",10);
            obj.put("match_threshold",10);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody
                .create(MediaType.parse("application/json; charset=utf-8"), "" + obj.toString());

        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/multi-search?access_token=24.29062b34a7fc64f68465706de4b9a2a8.2592000.1575358784.282335-17603369")
                .post(requestBody)
                .build();

        try {
            response = client.newCall(request).execute();
            String res = response.body().string();
            ((MainActivity) activity).setRecognizeResult(res);
            System.out.println(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void handleBaiDuRecognizeResponse(String jsonData, Activity activity, Fragment fragment) {
        try {
            JSONObject object = new JSONObject(jsonData);
            errorCode = object.getInt("error_code");

            //JSONArray userList = result.getJSONArray("user_list");
            if(errorCode==0) {
                JSONObject result = object.getJSONObject("result");
                JSONArray faceList = result.getJSONArray("face_list");
                //Log.i("结果", "" + result);
                //Log.i("结果", "" + userList);
                //inform = userList[0]
                int face_num = result.getInt("face_num");
                 personList = new ArrayList<>();
                for (int i = 0; i < face_num; i++) {
                    JSONArray userList = faceList.getJSONObject(i).getJSONArray("user_list");
                    Person temp = new Person();
                    JSONObject p = userList.getJSONObject(0);
                    String str = p.getString("user_info");
                    String[] strs = str.split("////");  //将返回结果中的姓名和信息分开
                    Log.i("截取的此人信息为", strs[0] + strs[1]);
                    temp.setScore(p.getInt("score"));
                    if(p.getInt("score")<80)
                    {
                        temp.setName("第"+i+"张脸");
                        temp.setInfo("未检测到此人信息");
                    }
                    else {
                        temp.setName(strs[0]);
                        temp.setInfo(strs[1]);
                    }
                    Log.d("图片中的人名为", temp.getName());
                    Log.d("图片中的人脸信息为", temp.getInfo());
                    personList.add(temp);
                }
            }

            //inform = userList.getJSONObject(0).getString("user_info");
            //score = userList.getJSONObject(0).getInt("score");
            //日志
            Log.d("inform", "结果是：" + inform);
            Log.d("score", "结果是：" + score);
            Log.i("错误码是", "啦啦啦啦" + errorCode);
            switch (fragment.getTag()) {
                case "ShowRecognizeResultFragment":
                    ((ShowRecognizeResultFragment) fragment).setPersonList(personList);
                    ((ShowRecognizeResultFragment) fragment).setErrorCode(errorCode);
                    //((ShowRecognizeResultFragment) fragment).setInform(inform);
                    //((ShowRecognizeResultFragment) fragment).setScore(score);
                    break;
                case"UploadInformFragment":
                    //((UploadInformFragment)fragment).setScore(score);
                    ((UploadInformFragment) fragment).setPersonList(personList);
                    ((UploadInformFragment) fragment).setErrorCode(errorCode);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void uploadFaceInform(Bitmap bitmap,String uname,String uinform)
    {
        client = new OkHttpClient();
        String Base64Photo = PhotoManager.ChangeBitmapToBase64(bitmap);
        JSONObject obj = new JSONObject();
        try {
            obj.put("image", Base64Photo);
            obj.put("image_type", "BASE64");
            obj.put("group_id", "group1");
            obj.put("user_id","User"+System.currentTimeMillis());
            obj.put("user_info",uname+"////"+uinform);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody
                .create(MediaType.parse("application/json; charset=utf-8"), "" + obj.toString());
        Log.i("BaiduManager","正在上传人脸信息");

        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add?access_token=24.29062b34a7fc64f68465706de4b9a2a8.2592000.1575358784.282335-17603369")
                .post(requestBody)
                .build();

        try {
            Log.i("发送请求", "" + requestBody);
            response = client.newCall(request).execute();
            String res = response.body().string();
            System.out.println(res);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
