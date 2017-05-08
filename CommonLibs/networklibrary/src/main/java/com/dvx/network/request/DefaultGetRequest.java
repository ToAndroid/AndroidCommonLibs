package com.dvx.network.request;

import android.net.Uri;

import com.dvx.network.interfaces.IGetRequest;

import java.net.URL;

/**
 * Created by andoop on 2016/11/1.
 */

public class DefaultGetRequest extends BaseRequest<DefaultGetRequest> implements IGetRequest<DefaultGetRequest>{

    @Override
    public String getUrl() {
        if(params!=null){
            Uri.Builder builder = Uri.parse(url).buildUpon();
            for(String key:params.keySet()){
               builder.appendQueryParameter(key,params.get(key));
            }
            url=builder.toString();
        }
       return url;
    }
}
