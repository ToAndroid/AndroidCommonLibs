package com.dvx.network.request;

import android.util.Log;

import com.dvx.network.interfaces.IPostRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by andoop on 2016/11/1.
 */

public class DefaultPostRequest extends BaseRequest<DefaultPostRequest> implements IPostRequest<DefaultPostRequest>{

    @Override
    public String getParams() {
        StringBuilder stringBuilder = new StringBuilder();
        if(params!=null){
            for(String key:params.keySet()){
                try {
                    stringBuilder.append(key).append("=").append(URLEncoder.encode(params.get(key),"utf-8")).append("&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e("net","urlecode err:"+e.getMessage());
                }
            }
        }else {
           // Log.e("net","postrequeest: params ="+params);
        }
        return stringBuilder.toString();
    }
}
