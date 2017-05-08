package com.dvx.network.request;

import com.dvx.network.NetCallback;
import com.dvx.network.interfaces.IBaseRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by domob on 2016/11/1.
 */

public class BaseRequest <T> implements IBaseRequest<T> {
    protected String url;
    protected Map<String,String> params;
    protected NetCallback callback;
    public BaseRequest(){
        params=new HashMap<>();
    }
    @Override
    public String getUrl() {
        //参数拼接，加密

        return url;
    }

    @Override
    public T addParam(String key, String value) {
        if(params==null)
            params=new HashMap<>();
        params.put(key,value);
        return (T) this;
    }

    @Override
    public T addParams(Map<String, String> params) {
        if(params==null)
            params=new HashMap<>();
        this.params.putAll(params);
        return (T) this;
    }

    @Override
    public T addCallback(NetCallback callback) {
        this.callback=callback;
        return (T) this;
    }

    @Override
    public T url(String url) {
        this.url=url;
        return (T) this;
    }

    @Override
    public NetCallback getCallback() {
        return callback;
    }
}
