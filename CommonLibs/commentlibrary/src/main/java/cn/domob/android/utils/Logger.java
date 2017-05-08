//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.domob.android.utils;

import android.util.Log;
import cn.domob.android.utils.Utility;

public class Logger {
    private String mClassSimpleName;
    public static String mLogTag = "DB";
    private static boolean mIsLoggable = false;
    private static final int PT_LEVEL = 7;
    private static long OldTime = 0L;
    private static long NewTime = 0L;

    public Logger(String classSimpleName) {
        this.mClassSimpleName = classSimpleName;
    }

    public Logger(String classSimpleName, String logTag) {
        this.mClassSimpleName = classSimpleName;
        if(!Utility.isStringNullOrEmpty(logTag)) {
            mLogTag = logTag;
        }

    }

    private void log(String content, int level) {
        String logContent = this.mClassSimpleName + "::" + content;
        this.printLog(logContent, level);
    }

    public void printLog(String logContent, int level) {
        if(mIsLoggable) {
            switch(level) {
            case 2:
                Log.v(mLogTag, logContent);
                break;
            case 3:
                Log.d(mLogTag, logContent);
                break;
            case 4:
                Log.i(mLogTag, logContent);
                break;
            case 5:
                Log.w(mLogTag, logContent);
                break;
            case 6:
                Log.e(mLogTag, logContent);
                break;
            case 7:
                NewTime = System.currentTimeMillis();
                Log.d(mLogTag, logContent + "spend time is:" + (float)(NewTime - OldTime) / 1000.0F);
                OldTime = NewTime;
            }
        }

    }

    public void verboseLog(Object obj, String content) {
        this.verboseLog(content);
    }

    public void debugLog(Object obj, String content) {
        this.debugLog(content);
    }

    public void infoLog(Object obj, String content) {
        this.infoLog(content);
    }

    public void warnLog(Object obj, String content) {
        this.warnLog(content);
    }

    public void errorLog(Object obj, String content) {
        this.errorLog(content);
    }

    public void ptLog(Object obj, String content) {
        this.debugLog(content);
    }

    public void verboseLog(String content) {
        this.log(content, 2);
    }

    public void debugLog(String content) {
        this.log(content, 3);
    }

    public void infoLog(String content) {
        this.log(content, 4);
    }

    public void warnLog(String content) {
        this.log(content, 5);
    }

    public void errorLog(String content) {
        this.log(content, 6);
    }

    public void ptLog(String content) {
        this.log(content, 7);
    }

    public void printStackTrace(Throwable e) {
        if(mIsLoggable) {
            e.printStackTrace();
        }

    }

    public boolean isLoggable() {
        return mIsLoggable;
    }

    public static void setLoggable(boolean isLoggable) {
        mIsLoggable = isLoggable;
    }

    public static String getLogTag() {
        return mLogTag;
    }
}
