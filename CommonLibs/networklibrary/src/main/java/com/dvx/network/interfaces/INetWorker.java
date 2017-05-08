package com.dvx.network.interfaces;

/**
 * Created by domob on 2016/11/1.
 */

public interface INetWorker {
    void postRequest(IPostRequest postRequest);

    void getRequest(IGetRequest getRequest);

    void work();

    void packResult();
}
