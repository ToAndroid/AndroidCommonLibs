//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.domob.android.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import cn.domob.android.utils.AppInfo;
import cn.domob.android.utils.DeviceInfo;
import cn.domob.android.utils.Logger;
import java.lang.reflect.Method;

public class DeviceId {
    private static Logger mLogger = new Logger(DeviceId.class.getSimpleName());
    private static final String EMULATOR_SIGN = "sdk";
    private static String mIMEI;
    private static String mIMSI;
    private static String mAndroidID;
    private static String mAdvertisingID;

    public DeviceId() {
    }

    public static String getDeviceId(Context context) {
        String mDeviceID = null;
        if(isEmulator(context)) {
            mLogger.debugLog("Use emulator id");
            mDeviceID = "-1,-1,emulator";
        } else {
            mLogger.debugLog("Generate device id");
            mDeviceID = generateDeviceId(context);
        }

        return mDeviceID;
    }

    public static boolean isEmulator(Context context) {
        String mAndroidID = getAndroidID(context);
        return mAndroidID == null && isImeiAllZero(context) && "sdk".equalsIgnoreCase(Build.MODEL);
    }

    private static boolean isImeiAllZero(Context context) {
        String imei = getIMEI(context);
        boolean imeiIsAllZero = false;
        if(imei == null) {
            imeiIsAllZero = true;
        } else {
            imeiIsAllZero = imei.replaceAll("0", "").equals("");
        }

        return imeiIsAllZero;
    }

    private static String generateDeviceId(Context context) {
        mLogger.debugLog("Start to generate device id");
        StringBuffer deviceid = new StringBuffer();

        String androidID;
        try {
            androidID = getIMEI(context);
            if(androidID != null) {
                deviceid.append(androidID);
            } else {
                deviceid.append("-1");
            }

            deviceid.append(",");
            String imsi = getIMSI(context);
            if(imsi != null) {
                deviceid.append(imsi);
            } else {
                deviceid.append("-1");
            }

            deviceid.append(",");
        } catch (SecurityException var4) {
            mLogger.printStackTrace(var4);
            Log.e(Logger.getLogTag(), "you must set READ_PHONE_STATE permisson in AndroidManifest.xml");
        } catch (Exception var5) {
            mLogger.printStackTrace(var5);
        }

        androidID = getAndroidID(context);
        if(androidID != null) {
            deviceid.append(androidID);
        } else {
            mLogger.verboseLog("Android ID is null, use -1 instead");
            deviceid.append("-1");
        }

        mLogger.debugLog("Generated device id: " + deviceid.toString());
        return deviceid.toString();
    }

    protected static String getIMSI(Context context) {
        try {
            if(mIMSI == null) {
                TelephonyManager e = (TelephonyManager)context.getSystemService("phone");
                mIMSI = e.getSubscriberId();
            }
        } catch (Exception var2) {
            mLogger.errorLog("Failed to get IMSI.");
            mLogger.printStackTrace(var2);
        }

        return mIMSI;
    }

    protected static String getIMEI(Context context) {
        try {
            if(mIMEI == null) {
                TelephonyManager e = (TelephonyManager)context.getSystemService("phone");
                mIMEI = e.getDeviceId();
            }
        } catch (Exception var2) {
            mLogger.errorLog("Failed to get IMEI.");
            mLogger.printStackTrace(var2);
        }

        return mIMEI;
    }

    protected static String getAndroidID(Context context) {
        try {
            if(mAndroidID == null) {
                mAndroidID = Secure.getString(context.getContentResolver(), "android_id");
            }
        } catch (Exception var2) {
            mLogger.errorLog("Failed to get android ID.");
            mLogger.printStackTrace(var2);
        }

        return mAndroidID;
    }

    public static String getMacAddress(Context context) {
        if(AppInfo.isAccessWifiStateAuthorized(context)) {
            WifiManager wifiMgr = (WifiManager)context.getSystemService("wifi");
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            return wifiInfo.getMacAddress();
        } else {
            return null;
        }
    }

    public static String getAdvertisingID(Context context) {
        if(mAdvertisingID == null && DeviceInfo.isAndroidVersionLargerThan(9, true)) {
            try {
                Class e = Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient");
                Method getAdvertisingIdInfoMethod = e.getMethod("getAdvertisingIdInfo", new Class[]{Context.class});
                Object infoObject = getAdvertisingIdInfoMethod.invoke(e.newInstance(), new Object[]{context});
                Class AdvertisingIdClientInfoClass = infoObject.getClass();
                Method getIdMthod = AdvertisingIdClientInfoClass.getMethod("getId", new Class[0]);
                String advertisingIdStr = String.valueOf(getIdMthod.invoke(infoObject, new Object[0]));
                mAdvertisingID = advertisingIdStr;
            } catch (Exception var7) {
                ;
            }
        }

        return mAdvertisingID;
    }
}
