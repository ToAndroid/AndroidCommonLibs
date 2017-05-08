//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.domob.android.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import cn.domob.android.utils.Logger;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

public class NetUtils {
    private static Logger mLogger = new Logger(NetUtils.class.getSimpleName());
    private static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

    public NetUtils() {
    }

    public static boolean checkNetWork(Context context) {
        return isWIFI(context)?true:isMobile(context);
    }

    public static Proxy readAPN(Context context) {
        Cursor query = null;

        try {
            ContentResolver e = context.getContentResolver();
            query = e.query(PREFERRED_APN_URI, (String[])null, (String)null, (String[])null, (String)null);
            if(query != null && query.moveToFirst()) {
                String proxy = query.getString(query.getColumnIndex("proxy"));
                int port = query.getInt(query.getColumnIndex("port"));
                if(!TextUtils.isEmpty(proxy) && port > 0) {
                    mLogger.debugLog("setProxy -- proxy:" + proxy + "| port:" + port);
                    Proxy var5 = new Proxy(Type.HTTP, new InetSocketAddress(proxy, port));
                    return var5;
                }
            }
        } catch (Exception var9) {
            mLogger.debugLog("readAPN error");
            mLogger.printStackTrace(var9);
        } finally {
            if(query != null) {
                query.close();
                query = null;
            }

        }

        return null;
    }

    public static boolean isWIFI(Context context) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService("connectivity");
        NetworkInfo networkInfo = manager.getNetworkInfo(1);
        return networkInfo != null?networkInfo.isConnected():false;
    }

    public static boolean isMobile(Context context) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService("connectivity");
        NetworkInfo networkInfo = manager.getNetworkInfo(0);
        return networkInfo != null?networkInfo.isConnected():false;
    }
}
