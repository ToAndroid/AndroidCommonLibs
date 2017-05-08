package com.dvx.network;

import android.content.Context;

import com.dvx.network.interfaces.IGetRequest;
import com.dvx.network.interfaces.IPostRequest;
import com.dvx.network.worker.UrlConnectionNetWorker;
import com.dvx.network.interfaces.INetWorker;

/**
 * Created by andoop on 2016/11/1.
 */
public class NetWorkExcutor {
    private static NetWorkExcutor INSTANCE;
    private WorkHouse workHouse;

    public static NetWorkExcutor getInstance() {
        if(INSTANCE==null){
            synchronized (NetWorkExcutor.class){
                if(INSTANCE==null){
                    INSTANCE=new NetWorkExcutor();
                }
            }
        }
        return INSTANCE;
    }

    private NetWorkExcutor() {
        workHouse=new WorkHouse();
    }

    /**
     * 执行post请求
     * @param postRequest
     */
    public void excutePost(IPostRequest postRequest){
        UrlConnectionNetWorker urlConnectionNetWorker = new UrlConnectionNetWorker();
        urlConnectionNetWorker.postRequest(postRequest);
        workHouse.add(urlConnectionNetWorker);
    }

    /**
     * 执行get请求
     * @param getRequest
     */
    public void excuteGet(IGetRequest getRequest){
        UrlConnectionNetWorker urlConnectionNetWorker = new UrlConnectionNetWorker();
        urlConnectionNetWorker.getRequest(getRequest);
        workHouse.add(urlConnectionNetWorker);
    }


}
