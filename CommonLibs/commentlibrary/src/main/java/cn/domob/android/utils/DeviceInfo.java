//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.domob.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Rect;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.os.Vibrator;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import cn.domob.android.utils.AppInfo;
import cn.domob.android.utils.Logger;
import cn.domob.android.utils.Utility;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;

public class DeviceInfo {
    private static Logger mLogger = new Logger(DeviceInfo.class.getSimpleName());
    private static String mPkgName;
    private static int mVersionCode;
    private static String mVersionName;
    private static String mAppName;
    private static String mOsVersion;
    private static String mDeviceModel;
    private static String mCarrier;
    private static String mUserAgentOfWebView;
    private static Boolean mIsTablet;
    private static float mRealDensity;
    private static float mCurrentDensity;
    private static int mCurrentWidth;
    private static int mCurrentHeight;
    private static String mOrientation;
    private static String mUserAgent;
    private static final String NETWORK_UNKNOWN = "unknown";
    private static final String NETWORK_GPRS = "gprs";
    private static final String NETWORK_WIFI = "wifi";
    private static final String DEFAULT_BROWSER_PACKAGE_NAME = "com.android.browser";
    private static final String DEFAULT_BROWSER_PACKAGE_NAME_GOOGLE = "com.google.android.browser";
    private static final String DEFAULT_BROWSER_ACTIVITY_NAME = "com.android.browser.BrowserActivity";
    private static String mDeviceID;
    private static ArrayList<String> disableParamsList = new ArrayList();
    private static String mAndroidID;
    private static String mIMSI;
    private static String mIMEI;
    private static String mAdvertisingID;
    private static final String EMULATOR_SIGN = "sdk";
    private static final String PKG_NAME = "pkgname";
    private static final String VERSION_CODE = "vc";
    private static final String VERSION_NAME = "vn";
    private static final String APP_NAME = "appname";
    private static final String USERAGENT_WEBVIEW = "useragent";
    private static final String USERAGENT = "ua";
    private static final String ISINSTALL = "install";
    private static final String IDV = "idv";
    private static final String IMEI = "imei";
    private static final String IMSI = "imsi";
    private static final String ANDROIDID = "andoidid";
    private static final String OSVERSION = "osv";
    private static final String DEVICEMODEL = "devicemodel";
    private static final String NETWORKTYPE = "networktype";
    private static final String NETWORKAVAILABLE = "networkavailable";
    private static final String IP = "ip";
    private static final String TIMEZONE = "timezone";
    private static final String CARRIER = "carrier";
    private static final String ORIENTATION = "orientation";
    private static final String ISEMULATOR = "isemulator";
    private static final String REALDENSITY = "rsd";
    private static final String CURRENTDENSITY = "csd";
    private static final String REALSCREENWIDTH = "rsw";
    private static final String REALSCREENHEIGHT = "rsh";
    private static final String CURRENTSCREENWIDTH = "csw";
    private static final String CURRENTSCREENHEIGHT = "csh";
    private static final String LOCATIONINFO = "locinfo";
    private static final String LOCATIONACCURACY = "locacc";
    private static final String LOCATIONACCURACYMETERS = "locaccmeters";
    private static final String LOCATIONSTATUS = "locstatus";
    private static final String LOCATIONTIME = "loctime";
    private static final String APMACADDRESS = "ama";
    private static final String APSSID = "ssid";
    private static final String MACADDRESS = "ma";
    private static final String AREACODE = "areacode";
    private static final String CELLID = "cellid";
    private static final String LANGUAGE = "language";
    private static final String SCANED_AP_MAC = "scan";
    private static final String ISTABLET = "istab";
    private static final String ADVERTISING_ID = "aaid";
    private static final String EDITABLE_SCREEN_HEIGHT = "esh";

    public DeviceInfo() {
    }

    public static void setDisableParamsList(ArrayList<String> disableParamsList) {
        disableParamsList = disableParamsList;
    }

    public static String getMacAddress(Context context) {
        if(disableParamsList.contains("ma")) {
            return "";
        } else if(AppInfo.isAccessWifiStateAuthorized(context)) {
            WifiManager wifiMgr = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            return wifiInfo.getMacAddress();
        } else {
            return null;
        }
    }

    public static String getAdvertisingID(Context context) {
        if(disableParamsList.contains("aaid")) {
            return "";
        } else {
            if(mAdvertisingID == null && isAndroidVersionLargerThan(9, true)) {
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

    private static void initAppInfo(Context context) {
        mLogger.debugLog(DeviceInfo.class.getSimpleName(), "Start to get app info.");

        try {
            PackageManager e = context.getPackageManager();
            PackageInfo packageInfo;
            if(e != null && (packageInfo = e.getPackageInfo(context.getPackageName(), 0)) != null) {
                mPkgName = packageInfo.packageName;
                mVersionCode = packageInfo.versionCode;
                mVersionName = packageInfo.versionName;
            }

            ApplicationInfo appInfo = e.getApplicationInfo(context.getPackageName(), 128);
            if(appInfo != null) {
                int labelRes = appInfo.labelRes;
                if(labelRes != 0) {
                    mAppName = context.getResources().getString(appInfo.labelRes);
                } else {
                    mAppName = appInfo.nonLocalizedLabel == null?null:appInfo.nonLocalizedLabel.toString();
                }
            }
        } catch (Exception var5) {
            mLogger.errorLog(DeviceInfo.class.getSimpleName(), "Failed in getting app info.");
            mLogger.printStackTrace(var5);
        }

    }

    public static String getApnName(Context context) {
        Cursor currApn = null;
        currApn = getCurrentApn(context);
        if(currApn != null && currApn.getCount() > 0) {
            currApn.moveToFirst();
            String apnType = currApn.getString(currApn.getColumnIndexOrThrow("apn"));
            currApn.close();
            return apnType;
        } else {
            return "";
        }
    }

    public static Cursor getCurrentApn(Context context) {
        try {
            String e = getNetworkType(context);
            if(e != null && e.equals("wifi")) {
                mLogger.debugLog("network is wifi, don\'t read apn.");
                return null;
            } else {
                String currApn = "content://telephony/carriers/preferapn";
                Uri uri = Uri.parse(currApn);
                Cursor cr = context.getContentResolver().query(uri, (String[])null, (String)null, (String[])null, (String)null);
                return cr;
            }
        } catch (Exception var5) {
            mLogger.printStackTrace(var5);
            return null;
        } catch (Error var6) {
            mLogger.printStackTrace(var6);
            return null;
        }
    }

    public static String getDeviceId(Context context) {
        if(disableParamsList.contains("idv")) {
            return "";
        } else {
            if(mDeviceID == null) {
                if(isEmulator(context)) {
                    mLogger.debugLog("Use emulator id");
                    mDeviceID = "-1,-1,emulator";
                } else {
                    mLogger.debugLog("Generate device id");
                    mDeviceID = generateDeviceId(context);
                }
            }

            return mDeviceID;
        }
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

    public static String getIMEI(Context context) {
        if(disableParamsList.contains("imei")) {
            return "";
        } else {
            try {
                if(mIMEI == null) {
                    TelephonyManager e = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                    mIMEI = e.getDeviceId();
                }
            } catch (Exception var2) {
                mLogger.errorLog("Failed to get IMEI.");
                mLogger.printStackTrace(var2);
            }

            return mIMEI;
        }
    }

    public static String getIMSI(Context context) {
        if(disableParamsList.contains("imsi")) {
            return "";
        } else {
            try {
                if(mIMSI == null) {
                    TelephonyManager e = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                    mIMSI = e.getSubscriberId();
                }
            } catch (Exception var2) {
                mLogger.errorLog("Failed to get IMSI.");
                mLogger.printStackTrace(var2);
            }

            return mIMSI;
        }
    }

    public static String getAndroidID(Context context) {
        if(disableParamsList.contains("andoidid")) {
            return "";
        } else {
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
    }

    public static boolean isEmulator(Context context) {
        if(disableParamsList.contains("isemulator")) {
            return false;
        } else {
            if(mAndroidID == null) {
                mAndroidID = getAndroidID(context);
            }

            return mAndroidID == null && isImeiAllZero(context) && "sdk".equalsIgnoreCase(Build.MODEL);
        }
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

    public static String getAppPkgName(Context context) {
        if(disableParamsList.contains("pkgname")) {
            return "";
        } else {
            if(mPkgName == null) {
                initAppInfo(context);
                if(!Utility.isStringNullOrEmpty(mPkgName)) {
                    Log.i(Logger.getLogTag(), "Current package name is " + mPkgName);
                }
            }

            return mPkgName;
        }
    }

    public static int getAppVersionCode(Context context) {
        if(disableParamsList.contains("vc")) {
            return -1;
        } else {
            if(mPkgName == null) {
                initAppInfo(context);
            }

            return mVersionCode;
        }
    }

    public static String getAppVersionName(Context context) {
        if(disableParamsList.contains("vn")) {
            return "";
        } else {
            if(mPkgName == null) {
                initAppInfo(context);
            }

            return mVersionName;
        }
    }

    public static String getAppName(Context context) {
        if(disableParamsList.contains("appname")) {
            return "";
        } else {
            if(mPkgName == null) {
                initAppInfo(context);
            }

            return mAppName;
        }
    }

    public static String getUserAgentOfWebView(Context context) {
        if(disableParamsList.contains("useragent")) {
            return "";
        } else {
            if(mUserAgentOfWebView == null) {
                mUserAgentOfWebView = (new WebView(context)).getSettings().getUserAgentString();
            }

            return mUserAgentOfWebView;
        }
    }

    public static boolean isInstalled(Context context, String pkgName) {
        if(disableParamsList.contains("install")) {
            return false;
        } else {
            if(null != pkgName && !pkgName.equals("")) {
                try {
                    PackageInfo e = context.getPackageManager().getPackageInfo(pkgName, 1);
                    if(null != e) {
                        mLogger.verboseLog("Already insalled pkgName = " + pkgName);
                        return true;
                    }
                } catch (NameNotFoundException var3) {
                    ;
                }
            }

            return false;
        }
    }

    public static String getOSVersion(Context context) {
        if(disableParamsList.contains("osv")) {
            return "";
        } else {
            if(mOsVersion == null) {
                if(VERSION.RELEASE.length() > 0) {
                    mOsVersion = VERSION.RELEASE.replace(",", "_");
                } else {
                    mOsVersion = "1.5";
                }
            }

            return mOsVersion;
        }
    }

    public static String getDeviceModel(Context context) {
        if(disableParamsList.contains("devicemodel")) {
            return "";
        } else {
            if(mDeviceModel == null && Build.MODEL.length() > 0) {
                mDeviceModel = Build.MODEL.replace(",", "_");
            }

            return mDeviceModel;
        }
    }

    public static String getNetworkType(Context context) {
        if(disableParamsList.contains("networktype")) {
            return "";
        } else if(context.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") ==PackageManager.PERMISSION_DENIED) {
            mLogger.errorLog(DeviceInfo.class.getSimpleName(), "Cannot access user\'s network type.  Permissions are not set.");
            return "unknown";
        } else {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if(info != null) {
                int type = info.getType();
                if(type == 0) {
                    String subTypeName = info.getSubtypeName();
                    if(subTypeName != null) {
                        return subTypeName;
                    }

                    return "gprs";
                }

                if(type == 1) {
                    return "wifi";
                }
            }

            return "unknown";
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        if(disableParamsList.contains("networkavailable")) {
            return false;
        } else {
            try {
                ConnectivityManager e = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = e.getActiveNetworkInfo();
                return info != null && info.isConnected();
            } catch (Exception var3) {
                mLogger.printStackTrace(var3);
                return false;
            }
        }
    }

    public static String getIPv4(Context context) {
        if(disableParamsList.contains("ip")) {
            return "";
        } else {
            try {
                WifiManager e = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(e.getConnectionInfo().getIpAddress());
                return ip;
            } catch (Exception var3) {
                mLogger.printStackTrace(var3);
                return null;
            }
        }
    }

    public static String getIPv6() {
        if(disableParamsList.contains("ip")) {
            return "";
        } else {
            try {
                Enumeration e = NetworkInterface.getNetworkInterfaces();

                while(e.hasMoreElements()) {
                    NetworkInterface intf = (NetworkInterface)e.nextElement();
                    Enumeration enumIpAddr = intf.getInetAddresses();

                    while(enumIpAddr.hasMoreElements()) {
                        InetAddress inetAddress = (InetAddress)enumIpAddr.nextElement();
                        if(!inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress().toString();
                        }
                    }
                }
            } catch (Exception var4) {
                mLogger.printStackTrace(var4);
            }

            return null;
        }
    }

    public static String getTimeZoneStr() {
        if(disableParamsList.contains("timezone")) {
            return "";
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("Z");
            return simpleDateFormat.format(new Date());
        }
    }

    public static String getCarrier(Context context) {
        if(disableParamsList.contains("carrier")) {
            return "";
        } else {
            try {
                if(mCarrier == null) {
                    TelephonyManager e = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                    mCarrier = e.getNetworkOperatorName();
                }
            } catch (Exception var2) {
                mLogger.printStackTrace(var2);
            }

            return mCarrier;
        }
    }

    public static String getOrientation(Context context) {
        if(disableParamsList.contains("orientation")) {
            return "";
        } else {
            mOrientation = "v";
            Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            if(display.getOrientation() == 1 || display.getOrientation() == 3) {
                mOrientation = "h";
            }

            return mOrientation;
        }
    }

    public static boolean isSupportVibration(Context context) {
        if(context.checkCallingOrSelfPermission("android.permission.VIBRATE") == -1) {
            return false;
        } else {
            if(isAndroidVersionLargerThan(11, true)) {
                try {
                    Vibrator e = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
                    Class mVibratorRefection = e.getClass();
                    Method method = mVibratorRefection.getMethod("hasVibrator", new Class[0]);
                    Object object = method.invoke(mVibratorRefection.newInstance(), new Object[0]);
                    if(String.valueOf(object).equals("false")) {
                        return false;
                    }
                } catch (Exception var5) {
                    mLogger.debugLog("Android version of the device is less than 3.0, the interface is no mapping");
                    mLogger.printStackTrace(var5);
                }
            }

            return true;
        }
    }

    public static float getRealDensity(Context context) {
        if(disableParamsList.contains("rsd")) {
            return -1.0F;
        } else {
            try {
                if(mRealDensity == 0.0F) {
                    Display e = ((WindowManager)context.getSystemService("window")).getDefaultDisplay();
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    e.getMetrics(displayMetrics);
                    mRealDensity = displayMetrics.density;
                }
            } catch (Exception var3) {
                mLogger.printStackTrace(var3);
            }

            return mRealDensity;
        }
    }

    public static float getCurrentDensity(Context context) {
        if(disableParamsList.contains("csd")) {
            return -1.0F;
        } else {
            try {
                if(mCurrentDensity == 0.0F) {
                    DisplayMetrics e = context.getResources().getDisplayMetrics();
                    mCurrentDensity = e.density;
                }
            } catch (Exception var2) {
                mLogger.printStackTrace(var2);
            }

            return mCurrentDensity;
        }
    }

    public static int getRealScreenWidth(Context context) {
        if(disableParamsList.contains("rsw")) {
            return -1;
        } else {
            int mRealWidth = Math.round((float)getCurrentScreenWidth(context) * (getRealDensity(context) / getCurrentDensity(context)));
            return mRealWidth;
        }
    }

    public static int getRealScreenHeight(Context context) {
        if(disableParamsList.contains("rsh")) {
            return -1;
        } else {
            int mRealHeight = Math.round((float)getCurrentScreenHeight(context) * (getRealDensity(context) / getCurrentDensity(context)));
            return mRealHeight;
        }
    }

    public static int getCurrentScreenWidth(Context context) {
        if(disableParamsList.contains("csw")) {
            return -1;
        } else {
            Display display = ((WindowManager)context.getSystemService("window")).getDefaultDisplay();
            if(display != null) {
                mCurrentWidth = display.getWidth();
            }

            return mCurrentWidth;
        }
    }

    public static int getCurrentScreenHeight(Context context) {
        if(disableParamsList.contains("csh")) {
            return -1;
        } else {
            Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            if(display != null) {
                mCurrentHeight = display.getHeight();
            }

            return mCurrentHeight;
        }
    }

    public static String getLocationInfo(Context context) {
        if(disableParamsList.contains("locinfo")) {
            return "";
        } else {
            DeviceInfo.LmLocationManager lmLocationManager = DeviceInfo.LmLocationManager.lmLocationManager;
            Location location = lmLocationManager.getLocation(context);
            return location != null?lmLocationManager.getLocationStr(location):null;
        }
    }

    public static int getLocationAccuracyMeters() {
        return disableParamsList.contains("locaccmeters")?-1:DeviceInfo.LmLocationManager.lmLocationManager.getAccuracyMeters();
    }

    public static int getLocationAccuracy() {
        return disableParamsList.contains("locacc")?-1:DeviceInfo.LmLocationManager.lmLocationManager.getAccuracy();
    }

    public static int getLocationStatus() {
        return disableParamsList.contains("locstatus")?-1:DeviceInfo.LmLocationManager.lmLocationManager.getStatus();
    }

    public static long getLocationTime() {
        return disableParamsList.contains("loctime")?-1L:DeviceInfo.LmLocationManager.lmLocationManager.getTime();
    }

    public static boolean isAndroidVersionLargerThan(int _version, boolean _isEqual) {
        return _isEqual?VERSION.SDK_INT >= _version:VERSION.SDK_INT > _version;
    }

    public static String getAPMacAddress(Context context) {
        if(disableParamsList.contains("ama")) {
            return "";
        } else if(AppInfo.isAccessWifiStateAuthorized(context) && isWifiEnabled(context)) {
            WifiManager wifiMgr = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            return wifiInfo.getBSSID();
        } else {
            return null;
        }
    }

    public static String getAPSSID(Context context) {
        if(disableParamsList.contains("ssid")) {
            return "";
        } else if(AppInfo.isAccessWifiStateAuthorized(context) && isWifiEnabled(context)) {
            WifiManager wifiMgr = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            return wifiInfo.getSSID();
        } else {
            return null;
        }
    }

    public static String getScanedAPMacAddress(Context context) {
        if(!disableParamsList.contains("scan")) {
            return "";
        } else {
            String scaned = "";

            try {
                if(AppInfo.isAccessWifiStateAuthorized(context) && isWifiEnabled(context)) {
                    WifiManager e = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                    List scanResults = e.getScanResults();
                    ScanResult[] scanResultsArray = new ScanResult[scanResults.size()];

                    for(int scanJsonObject = 0; scanJsonObject < scanResults.size(); ++scanJsonObject) {
                        scanResultsArray[scanJsonObject] = (ScanResult)scanResults.get(scanJsonObject);
                    }

                    Arrays.sort(scanResultsArray, new Comparator<ScanResult>() {
                        public int compare(ScanResult o1, ScanResult o2) {
                            int diff = o2.level - o1.level;
                            byte result = 0;
                            if(diff > 0) {
                                result = 1;
                            } else if(diff < 0) {
                                result = -1;
                            }

                            return result;
                        }
                    });
                    JSONObject var12 = new JSONObject();
                    int maxLimitedNumber = scanResultsArray.length <= 20?scanResultsArray.length:20;

                    for(int i = 0; i < maxLimitedNumber; ++i) {
                        ScanResult result = scanResultsArray[i];
                        String bssid = result.BSSID;
                        String ssid = result.SSID;
                        if(!Utility.isStringNullOrEmpty(bssid)) {
                            if(Utility.isStringNullOrEmpty(bssid)) {
                                ssid = "#";
                            }

                            if(ssid.length() > 16) {
                                ssid = ssid.substring(0, 16);
                            }

                            var12.put(bssid, ssid);
                        }
                    }

                    scaned = var12.toString();
                }
            } catch (Exception var11) {
                mLogger.printStackTrace(var11);
            }

            return scaned;
        }
    }

    public static String[] getLocationBaseInfo(Context context) {
        if(!disableParamsList.contains("areacode") && !disableParamsList.contains("cellid")) {
            mLogger.verboseLog(DeviceInfo.class.getSimpleName(), "getLocationBasedService");
            String[] baseInfo = new String[]{"-1", "-1", "-1", "-1"};
            if(!AppInfo.isPermissionsAuthorized(context, "android.permission.ACCESS_COARSE_LOCATION") && !AppInfo.isPermissionsAuthorized(context, "android.permission.ACCESS_FINE_LOCATION")) {
                mLogger.warnLog("No permission to access locationBaseInfo");
                return baseInfo;
            } else {
                try {
                    TelephonyManager e = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                    if(e != null) {
                        mLogger.verboseLog(DeviceInfo.class.getSimpleName(), "tManager is not null");
                        mLogger.verboseLog(DeviceInfo.class.getSimpleName(), "Network Operator: " + e.getNetworkOperator());
                        int cid = -1;
                        int lac = -1;
                        int phoneType = e.getPhoneType();
                        CellLocation cellLocation = e.getCellLocation();
                        if(cellLocation != null) {
                            switch(phoneType) {
                            case 0:
                            default:
                                mLogger.debugLog("无法获取基站信息");
                                break;
                            case 1:
                                GsmCellLocation cellLocation2 = (GsmCellLocation)cellLocation;
                                if(cellLocation2 != null) {
                                    cid = ((GsmCellLocation)cellLocation2).getCid();
                                    lac = ((GsmCellLocation)cellLocation2).getLac();
                                }
                                break;
                            case 2:
                                CdmaCellLocation cellLocation1 = (CdmaCellLocation)cellLocation;
                                if(cellLocation1 != null) {
                                    cid = ((CdmaCellLocation)cellLocation1).getBaseStationId();
                                    lac = ((CdmaCellLocation)cellLocation1).getNetworkId();
                                }
                            }

                            baseInfo[0] = String.valueOf(cid);
                            baseInfo[1] = String.valueOf(lac);
                        }

                        if(e.getNetworkOperator() != null && e.getNetworkOperator().length() >= 5) {
                            int mcc = Integer.valueOf(e.getNetworkOperator().substring(0, 3)).intValue();
                            int mnc = Integer.valueOf(e.getNetworkOperator().substring(3, 5)).intValue();
                            baseInfo[2] = String.valueOf(mcc);
                            baseInfo[3] = String.valueOf(mnc);
                        }
                    }
                } catch (Exception var9) {
                    mLogger.printStackTrace(var9);
                }

                return baseInfo;
            }
        } else {
            return new String[]{"-1", "-1", "-1", "-1"};
        }
    }

    public static String getLocalLanguage() {
        return disableParamsList.contains("language")?"":Locale.getDefault().getLanguage();
    }

    public static Boolean isTabletDevice(Context context) {
        if(disableParamsList.contains("istab")) {
            return Boolean.valueOf(false);
        } else if(mIsTablet != null) {
            return mIsTablet;
        } else {
            try {
                if(VERSION.SDK_INT >= 11) {
                    Configuration e = context.getResources().getConfiguration();
                    Method mIsLayoutSizeAtLeast = e.getClass().getMethod("isLayoutSizeAtLeast", new Class[]{Integer.TYPE});
                    Boolean isTablet = (Boolean)mIsLayoutSizeAtLeast.invoke(e, new Object[]{Integer.valueOf(4)});
                    mIsTablet = isTablet;
                }
            } catch (Exception var4) {
                mLogger.printStackTrace(var4);
            }

            if(mIsTablet == null) {
                mIsTablet = Boolean.valueOf(false);
            }

            return mIsTablet;
        }
    }

    public static int getEditableScreenHeight(Context context) {
        if(disableParamsList.contains("esh")) {
            return 0;
        } else if(context instanceof Activity) {
            Activity activity = (Activity)context;
            Window window = activity.getWindow();
            Rect rect = new Rect();
            window.getDecorView().getWindowVisibleDisplayFrame(rect);
            int statusBarHeight = rect.top;
            int contentViewTop = window.findViewById(16908290).getTop();
            int titleBarHeight = contentViewTop - statusBarHeight;
            return rect.height() - titleBarHeight;
        } else {
            return 0;
        }
    }

    public static boolean isSdPresent() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static boolean isScreenOn(Context context) {
        try {
            PowerManager e = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = e.isScreenOn();
            return isScreenOn;
        } catch (Exception var3) {
            mLogger.printStackTrace(var3);
            return true;
        }
    }

    public static Intent getDefaultBrowserLaunchIntent(Context context, Uri uri) {
        Intent intent = null;
        if(context != null && uri != null) {
            if(isInstalled(context, "com.android.browser")) {
                intent = new Intent("android.intent.action.VIEW", uri);
                intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
            } else if(isInstalled(context, "com.google.android.browser")) {
                intent = new Intent("android.intent.action.VIEW", uri);
                intent.setClassName("com.google.android.browser", "com.android.browser.BrowserActivity");
            }

            if(context.getPackageManager().resolveActivity(intent, 65536) == null) {
                intent = null;
            }
        }

        return intent;
    }

    public static boolean isWifiEnabled(Context context) {
        WifiManager wifiMgr = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        return wifiMgr.getWifiState() == 3;
    }

    public static String getUserAgent(Context context) {
        if(disableParamsList.contains("ua")) {
            return "";
        } else {
            String replaceString = "_";
            if(null == mUserAgent) {
                StringBuffer stringbuffer = new StringBuffer();
                stringbuffer.append("android");
                stringbuffer.append(",");
                stringbuffer.append(",");
                if(VERSION.RELEASE.length() > 0) {
                    stringbuffer.append(VERSION.RELEASE.replaceAll(",", "_"));
                } else {
                    stringbuffer.append("1.5");
                }

                stringbuffer.append(",");
                stringbuffer.append(",");
                String s = Build.MODEL;
                if(Build.MODEL.length() > 0) {
                    stringbuffer.append(s.replaceAll(",", "_"));
                }

                stringbuffer.append(",");
                String carrier = getCarrier(context);
                if(carrier != null) {
                    stringbuffer.append(carrier.replaceAll(",", "_"));
                }

                stringbuffer.append(",");
                stringbuffer.append(",");
                stringbuffer.append(",");
                mUserAgent = stringbuffer.toString();
                mLogger.debugLog("getUserAgent:" + mUserAgent);
            }

            return mUserAgent;
        }
    }

    public static long getSdAvailableSize() {
        long sdAvailableSize = 0L;
        if(isSdPresent()) {
            StatFs statfs = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long blocSize = (long)statfs.getBlockSize();
            long availaBlock = (long)statfs.getAvailableBlocks();
            sdAvailableSize = availaBlock * blocSize;
        }

        return sdAvailableSize;
    }

    public static long getSdTotalSize() {
        long sdTotalSize = 0L;
        if(isSdPresent()) {
            StatFs statfs = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long blocSize = (long)statfs.getBlockSize();
            long blockCount = (long)statfs.getBlockCount();
            sdTotalSize = blockCount * blocSize;
        }

        return sdTotalSize;
    }

    private static class LmLocationManager {
        private static final DeviceInfo.LmLocationManager lmLocationManager = new DeviceInfo.LmLocationManager();
        private Location location;
        private int status = -1;
        private final boolean isLocationAllowed = true;
        private static final long COORD_MIN_INTERVAL = 600000L;
        private static final int TWO_MINUTES = 120000;
        private static final int NETWORK_DURATION = 1200000;
        private static final int GPS_DURATION = 120000;

        private LmLocationManager() {
        }

        private static DeviceInfo.LmLocationManager getInstance() {
            return lmLocationManager;
        }

        private Location getLocation(Context context) {
            context = context.getApplicationContext();
            this.status = 2;

            try {
                if(context == null) {
                    return null;
                }

                boolean e = AppInfo.isPermissionsAuthorized(context, "android.permission.ACCESS_FINE_LOCATION");
                if(!e && !AppInfo.isPermissionsAuthorized(context, "android.permission.ACCESS_COARSE_LOCATION")) {
                    this.status = 1;
                } else {
                    LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
                    if(locationManager != null) {
                        List isProviderEnabled = locationManager.getProviders(true);
                        Iterator localIterator = isProviderEnabled.iterator();

                        while(localIterator.hasNext()) {
                            String provider = (String)localIterator.next();
                            Location localLocation = locationManager.getLastKnownLocation(provider);
                            if(localLocation != null && this.isBetterLocation(localLocation, this.location)) {
                                this.location = localLocation;
                            }
                        }

                        if(this.location == null || System.currentTimeMillis() > this.location.getTime() + 300000L) {
                            this.startRecording(locationManager, context);
                        }
                    }

                    if(this.location == null) {
                        boolean isProviderEnabled1 = locationManager.isProviderEnabled("network");
                        if(locationManager == null || !isProviderEnabled1 && !e || !isProviderEnabled1 && e && !locationManager.isProviderEnabled("gps")) {
                            this.status = 0;
                        }
                    }
                }
            } catch (Exception var8) {
                DeviceInfo.mLogger.printStackTrace(var8);
            }

            return this.location;
        }

        private synchronized void startRecording(LocationManager locationManager, Context context) {
            if(locationManager != null) {
                try {
                    Criteria e = new Criteria();
                    e.setAltitudeRequired(false);
                    e.setBearingRequired(false);
                    e.setSpeedRequired(false);
                    e.setCostAllowed(false);
                    e.setAccuracy(2);
                    Iterator i$ = locationManager.getProviders(e, true).iterator();

                    while(i$.hasNext()) {
                        String provider = (String)i$.next();
                        DeviceInfo.LmLocationManager.LlLocationListener mLocationListener = new DeviceInfo.LmLocationManager.LlLocationListener(locationManager);
                        DeviceInfo.mLogger.debugLog(provider + " start to listener position");
                        locationManager.requestLocationUpdates(provider, 0L, 0.0F, mLocationListener, context.getMainLooper());
                        if(provider.equals("network")) {
                            this.removeUpdates(locationManager, mLocationListener, 1200000, provider);
                        } else if(provider.equals("gps")) {
                            this.removeUpdates(locationManager, mLocationListener, 120000, provider);
                        }
                    }
                } catch (Exception var7) {
                    DeviceInfo.mLogger.printStackTrace(var7);
                }

            }
        }

        private void removeUpdates(final LocationManager locationManager, final LocationListener listener, int duration, final String provider) {
            Timer mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                public void run() {
                    locationManager.removeUpdates(listener);
                    DeviceInfo.mLogger.debugLog(provider + " stop listening position");
                }
            }, (long)duration);
        }

        private boolean isBetterLocation(Location location, Location currentBestLocation) {
            if(currentBestLocation == null) {
                return true;
            } else {
                long locationInterval = System.currentTimeMillis() - location.getTime();
                long currentBestLocationInterval = System.currentTimeMillis() - currentBestLocation.getTime();
                if(locationInterval <= 600000L && currentBestLocationInterval > 600000L) {
                    return true;
                } else if(locationInterval > 600000L && currentBestLocationInterval <= 600000L) {
                    return false;
                } else {
                    long timeDelta = location.getTime() - currentBestLocation.getTime();
                    boolean isSignificantlyNewer = timeDelta > 120000L;
                    boolean isSignificantlyOlder = timeDelta < -120000L;
                    boolean isNewer = timeDelta > 0L;
                    if(isSignificantlyNewer) {
                        return true;
                    } else if(isSignificantlyOlder) {
                        return false;
                    } else {
                        int accuracyDelta = (int)(location.getAccuracy() - currentBestLocation.getAccuracy());
                        boolean isLessAccurate = accuracyDelta > 0;
                        boolean isMoreAccurate = accuracyDelta < 0;
                        boolean isSignificantlyLessAccurate = accuracyDelta > 200;
                        boolean isFromSameProvider = this.isSameProvider(location.getProvider(), currentBestLocation.getProvider());
                        return isMoreAccurate?true:(isNewer && !isLessAccurate?true:isNewer && !isSignificantlyLessAccurate && isFromSameProvider);
                    }
                }
            }
        }

        private boolean isSameProvider(String provider1, String provider2) {
            return provider1 == null?provider2 == null:(provider2 != null?provider1.equals(provider2):true);
        }

        private int getAccuracy() {
            if(this.location != null) {
                String provider = this.location.getProvider();
                DeviceInfo.mLogger.debugLog("This location is obtained via " + provider);
                if(provider != null) {
                    if(provider.equals("network")) {
                        return 1;
                    }

                    if(provider.equals("gps")) {
                        return 0;
                    }

                    if(provider.equals("passive")) {
                        return 2;
                    }
                }
            }

            return 3;
        }

        private int getAccuracyMeters() {
            int accruacy;
            if(this.location == null) {
                accruacy = 0;
            } else {
                accruacy = (int)this.location.getAccuracy();
            }

            DeviceInfo.mLogger.debugLog("location accuracy is " + accruacy + " meters");
            return accruacy;
        }

        private int getStatus() {
            switch(this.status) {
            case 0:
                DeviceInfo.mLogger.debugLog("Location can not be obtained due to USER_CLOSE");
                break;
            case 1:
                DeviceInfo.mLogger.debugLog("Location can not be obtained due to NO_PERSSION");
                break;
            case 2:
                DeviceInfo.mLogger.debugLog("Location can not be obtained due to NO_AVAILABLE_LOCATION");
            }

            return this.status;
        }

        private long getTime() {
            if(this.location != null) {
                long LocTimeStamp = this.location.getTime();
                long timeDelta = (System.currentTimeMillis() - LocTimeStamp) / 1000L;
                DeviceInfo.mLogger.debugLog(DeviceInfo.class.getSimpleName(), String.format("The location is %s minutes %s seconds ago acquired", new Object[]{String.valueOf(timeDelta / 60L), String.valueOf(timeDelta % 60L)}));
                return LocTimeStamp;
            } else {
                return 0L;
            }
        }

        private String getLocationStr(Location _location) {
            String s = null;
            if(_location != null) {
                s = _location.getLatitude() + "," + _location.getLongitude();
                DeviceInfo.mLogger.debugLog(DeviceInfo.class.getSimpleName(), "User coordinates are " + s);
            }

            return s;
        }

        private class LlLocationListener implements LocationListener {
            public LocationManager locmgr;

            LlLocationListener(LocationManager mgr) {
                this.locmgr = mgr;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }

            public void onLocationChanged(Location location) {
                String provider = location.getProvider();
                if(provider != null && !provider.equals("network")) {
                    DeviceInfo.mLogger.debugLog(provider + " get location successfully, and remove the listener");
                    this.locmgr.removeUpdates(this);
                } else {
                    DeviceInfo.mLogger.debugLog(provider + " get location successfully, do not remove the listener");
                }

            }
        }

        private class Status {
            static final int USER_CLOSE = 0;
            static final int NO_PERSSION = 1;
            static final int NO_AVAILABLE_LOCATION = 2;

            private Status() {
            }
        }

        private class Accuracy {
            static final int GPS = 0;
            static final int NETWORK = 1;
            static final int PASSIVE = 2;
            static final int OTHERS = 3;

            private Accuracy() {
            }
        }
    }
}
