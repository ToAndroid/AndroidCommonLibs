//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.domob.android.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import cn.domob.android.utils.DeviceInfo;
import cn.domob.android.utils.Logger;
import cn.domob.android.utils.UiUtility;
import cn.domob.android.utils.Utility;

public class AppInfo {
    private static Logger mLogger = new Logger(AppInfo.class.getSimpleName());
    private static String mPkgName;
    private static int mVersionCode;
    private static String mVersionName;
    private static String mAppName;

    public AppInfo() {
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

    protected static String getAppPkgName(Context context) {
        if(mPkgName == null) {
            initAppInfo(context);
            if(!Utility.isStringNullOrEmpty(mPkgName)) {
                mLogger.infoLog("Current package name is " + mPkgName);
            }
        }

        return mPkgName;
    }

    protected static int getAppVersionCode(Context context) {
        if(mPkgName == null) {
            initAppInfo(context);
        }

        return mVersionCode;
    }

    protected static String getAppVersionName(Context context) {
        if(mPkgName == null) {
            initAppInfo(context);
        }

        return mVersionName;
    }

    protected static String getAppName(Context context) {
        if(mPkgName == null) {
            initAppInfo(context);
        }

        return mAppName;
    }

    public static boolean isPermissionsAuthorized(Context context, String permission) {
        try {
            if(-1 == context.checkCallingOrSelfPermission(permission)) {
                return false;
            }
        } catch (Exception var3) {
            mLogger.printStackTrace(var3);
        }

        return true;
    }

    public static boolean isPermissionsAuthorized(Context context, String[] permissions, boolean isLocationSwitchOn) {
        boolean shouldFinish = false;
        StringBuilder messageBuilder = new StringBuilder(Logger.getLogTag() + " 缺少权限：\n");

        for(int i = 0; i < permissions.length; ++i) {
            String permission = permissions[i];
            boolean isPermission = true;
            if(!Utility.isStringNullOrEmpty(permission)) {
                if(permission.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                    if(DeviceInfo.isAndroidVersionLargerThan(3, false)) {
                        isPermission = isPermissionsAuthorized(context, permission);
                    }
                } else if(permission.equals("android.permission.ACCESS_COARSE_LOCATION")) {
                    if(isLocationSwitchOn) {
                        isPermission = isPermissionsAuthorized(context, permission);
                    }
                } else {
                    isPermission = isPermissionsAuthorized(context, permission);
                }
            }

            if(!isPermission) {
                Log.e(Logger.getLogTag(), String.format("you must have %s permission !", new Object[]{permission}));
                messageBuilder.append(permission + " \n");
                shouldFinish = true;
            }
        }

        if(shouldFinish) {
            UiUtility.showConfirmDialog(context, Logger.getLogTag(), messageBuilder.toString());
        }

        return !shouldFinish;
    }

    public static boolean isAccessWifiStateAuthorized(Context context) {
        if(0 == context.checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE")) {
            mLogger.infoLog("access wifi state is enabled");
            return true;
        } else {
            return false;
        }
    }
}
