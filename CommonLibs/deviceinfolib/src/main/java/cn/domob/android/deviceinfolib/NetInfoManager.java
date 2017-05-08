package cn.domob.android.deviceinfolib;

/* * * * * * * * * * * * * * * * * * *
* author :andoop　　　　　　　　　　　
* time   :2017/2/6
* explain：网络信息处理者
* * * * * * * * * * * * * * * * * * */

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.List;

public class NetInfoManager {


    /**
     * 判断网络是否连通
     * @param context
     * @return
     */
    public  boolean isNetAvailable(Context context){
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();

            return (info != null && info.isConnected());
        } catch (Exception e) {
            Log.e("----->" + "NetInfoManager", "isNetAvailable:" + e.toString());
            return false;
        }
    }

    /**
     * 返回网络类型字符串 如：wifi  3G 4G  no
     * @param context
     * @return
     */
    public  String getNetTypeString(Context context){
        String strNetworkType = "";

        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
            {
                strNetworkType = "wifi";
            }
            else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                String _strSubTypeName = networkInfo.getSubtypeName();

              //  Log.e("----->", "Network getSubtypeName : " + _strSubTypeName);

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000"))
                        {
                            strNetworkType = "3G";
                        }
                        else
                        {
                            strNetworkType = _strSubTypeName;
                        }

                        break;
                }

              //  Log.e("----->", "Network getSubtype : " + Integer.valueOf(networkType).toString());
            }
        }

       // Log.e("----->", "Network Type : " + strNetworkType);

        return strNetworkType;
    }


    private  String getMacAddress0(Context context) {
        if (isAccessWifiStateAuthorized(context)) {
            WifiManager wifiMgr = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = null;
            try {
                wifiInfo = wifiMgr.getConnectionInfo();
                return wifiInfo.getMacAddress();
            } catch (Exception e) {
                Log.e("----->" + "NetInfoManager", "getMacAddress0:" + e.toString());
            }

        }
            return "";

    }

    /**
     * Check whether accessing wifi state is permitted
     *
     * @param context
     * @return
     */
    private  boolean isAccessWifiStateAuthorized(Context context) {
        if (PackageManager.PERMISSION_GRANTED == context
                .checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE")) {
            Log.e("----->" + "NetInfoManager", "isAccessWifiStateAuthorized:" + "access wifi state is enabled");
            return true;
        } else
            return false;
    }

    /**
     * 获取mac地址
     * @param context
     * @return
     */
    public  String getMacAddress(Context context){

        //如果是6.0以下，直接通过wifimanager获取
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            String macAddress0 = getMacAddress0(context);
            if(!TextUtils.isEmpty(macAddress0)){
                return macAddress0;
            }
        }

        String str="";
        String macSerial="";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            Log.e("----->" + "NetInfoManager", "getMacAddress:" + ex.toString());
        }
        if (macSerial == null || "".equals(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("----->" + "NetInfoManager", "getMacAddress:" + e.toString());
            }

        }
        return macSerial;
    }

    private  String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }
    private  String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

    /**
     * 获取附近wifi名称 用 , 分割
     * @param context
     * @return
     */
    public  String getNearbyWifiName(Context context){

        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        if(wifimanager.getWifiState()==WifiManager.WIFI_STATE_ENABLED){

            StringBuilder stringBuilder = new StringBuilder();
            try {
                List<ScanResult> scanResults = wifimanager.getScanResults();
                for (int i = 0; i <scanResults.size(); i++) {
                    stringBuilder.append(scanResults.get(i).SSID);
                    if(i<(scanResults.size()-1)){
                        stringBuilder.append(",");
                    }
                }

                return stringBuilder.toString();
            }catch (Exception e){
                Log.e("----->" + "NetInfoManager", "getNearbyWifiName:" + e.toString());
            }

        }else {
            return "";
        }

        return "";
    }

    /**
     * 获取当前wifi信息  格式： wifi名称,网络id,wifi强度,wifi接入点mac
     * @param context
     * @return
     */
    public  String getCurrentWifiInfo(Context context){

        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        try {
            StringBuilder stringBuilder = new StringBuilder();
            WifiInfo connectionInfo = wifimanager.getConnectionInfo();

            stringBuilder.append(connectionInfo.getSSID());
            stringBuilder.append(",");
            stringBuilder.append(connectionInfo.getNetworkId());
            stringBuilder.append(",");
            List<ScanResult> scanResults = wifimanager.getScanResults();
            if(scanResults!=null&&scanResults.size()>0){
                stringBuilder.append(WifiManager.calculateSignalLevel(scanResults.get(0).level,1001));
            }else {
                stringBuilder.append("-1");
            }
            stringBuilder.append(",");
            stringBuilder.append(connectionInfo.getBSSID());
            return stringBuilder.toString();
        } catch (Exception e) {
            Log.e("----->" + "NetInfoManager", "getCurrentWifiInfo:" + e.toString());
        }
        return "";
    }



}
