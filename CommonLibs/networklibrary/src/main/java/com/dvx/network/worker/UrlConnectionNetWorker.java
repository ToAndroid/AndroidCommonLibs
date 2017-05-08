package com.dvx.network.worker;

import android.text.TextUtils;
import android.util.Log;

import com.dvx.network.NetCallback;
import com.dvx.network.interfaces.IGetRequest;
import com.dvx.network.interfaces.INetWorker;
import com.dvx.network.interfaces.IPostRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by andoop on 2016/11/1.
 */

public class UrlConnectionNetWorker implements INetWorker{
    private IPostRequest postRequest;
    private IGetRequest getRequest;
    private String result;
    private NetCallback netCallback=null;
    private boolean haserr=false;
    NetError netError ;
    @Override
    public void postRequest(IPostRequest postRequest) {
        this.postRequest=postRequest;
        if(postRequest!=null)
            netCallback=postRequest.getCallback();

    }

    @Override
    public void getRequest(IGetRequest getRequest) {
        this.getRequest=getRequest;
        if(getRequest!=null)
            netCallback=getRequest.getCallback();
    }

    @Override
    public void work() {
        netError=new NetError();
        if(postRequest!=null)
            workOnPost();
        if(getRequest!=null)
            workOnGet();
    }

    @Override
    public void packResult() {
        if(haserr){
            if(netCallback!=null)
                netCallback.onFail(netError);
            return;
        }
        if(netCallback!=null){
            if(!TextUtils.isEmpty(result))
                netCallback.onSuccess(result);
        }
    }

    private void workOnGet() {

        String url=getRequest.getUrl();
       // Log.e("===>",url);
        HttpURLConnection urlConnection=null;
        InputStream is=null;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader=null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(5000);
            int responseCode = urlConnection.getResponseCode();
            switch (responseCode){
                case HttpURLConnection.HTTP_OK:
                    is=urlConnection.getInputStream();
                    inputStreamReader = new InputStreamReader(is);
                    bufferedReader = new BufferedReader(inputStreamReader);
                    result="";
                    String str="";
                    while ((str=bufferedReader.readLine())!=null){
                        result+=str;
                    }
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    netError.setET(NetError.NOT_FIND_ERROR);
                    netError.setText("资源没有找到");
                    haserr=true;
                    break;
            }
        } catch (MalformedURLException e) {
            netError.setET(NetError.URL_MALFORMED_ERROR);
            netError.setText("url无效");
            haserr=true;
        } catch (IOException e) {
            netError.setET(NetError.IO_ERROR);
            netError.setText("网络错误");
            haserr=true;
        }finally {
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
        }

    }

    private void workOnPost() {
        String url=postRequest.getUrl();
        String params=postRequest.getParams();
        HttpURLConnection urlConnection=null;
        InputStream is=null;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader=null;
        PrintWriter printWriter=null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(5000);

            //写入参数
            printWriter=new PrintWriter(urlConnection.getOutputStream());
            printWriter.write(params);
            printWriter.flush();
            int responseCode = urlConnection.getResponseCode();
            switch (responseCode){
                case HttpURLConnection.HTTP_OK:
                    is=urlConnection.getInputStream();
                    inputStreamReader = new InputStreamReader(is);
                    bufferedReader = new BufferedReader(inputStreamReader);
                    result="";
                    String str="";
                    while ((str=bufferedReader.readLine())!=null){
                        result+=str;
                    }
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    netError.setET(NetError.NOT_FIND_ERROR);
                    netError.setText("资源没有找到");
                    haserr=true;
                    break;
            }
        } catch (MalformedURLException e) {
            netError.setET(NetError.URL_MALFORMED_ERROR);
            netError.setText("url无效");
            haserr=true;

        } catch (IOException e) {
            netError.setET(NetError.IO_ERROR);
            netError.setText("网络错误");
            haserr=true;
        }finally {
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
            if(printWriter!=null){
                printWriter.close();
            }
        }

    }
}
