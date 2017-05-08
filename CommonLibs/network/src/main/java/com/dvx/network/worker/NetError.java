package com.dvx.network.worker;

/**
 * Created by andoop on 2016/11/7.
 * 网络错误
 */

public class NetError {
    //解析错误
    public final static int PARSE_ERROR=0x0001;
    //请求超时错误
    public final static int REQUEST_TIME_OUT_ERROR=0x0002;
    //读取超时错误
    public final static int READ_TIME_OUT_ERROR=0x0003;
    //资源没找到
    public final static int NOT_FIND_ERROR=0x0004;
    //url有误
    public final static int URL_MALFORMED_ERROR=0x0005;
    //io错误
    public final static int IO_ERROR=0x0006;
    public NetError(){}

    private int error_type;
    private String text;
    public void setET(int ey){
        this.error_type=ey;
    }

    public int getET() {
        return error_type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
