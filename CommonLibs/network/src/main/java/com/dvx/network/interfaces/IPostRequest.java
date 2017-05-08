package com.dvx.network.interfaces;

/**
 * Created by andoop on 2016/11/1.
 */

public interface IPostRequest<T> extends IBaseRequest<T>{
    //获取参数
    String getParams();

}
