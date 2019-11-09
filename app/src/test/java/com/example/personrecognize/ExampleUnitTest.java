package com.example.personrecognize;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test2() throws IOException {
        String path="C:/Users/36403/Desktop/pic/111.jpg";
        OkHttpClient client=new OkHttpClient();

        //上传文件域的请求体部分
        RequestBody formBody=  RequestBody
                .create(new File(path), MediaType.parse("image/jpeg"));

        //整个上传的请求体部分（普通表单+文件上传域）
        RequestBody requestBody=new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "Square Logo")
                //filename:avatar,originname:abc.jpg
                .addFormDataPart("myPicture", "pic1.jpg",formBody)
                .build();

        Request request = new Request.Builder()
                .url("http://localhost:8000/upload")
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }

}

