package cn.domob.android.deviceinfolib;

/* * * * * * * * * * * * * * * * * * *
* author :andoop　　　　　　　　　　　
* time   :2017/2/6
* explain：硬件信息处理者
* * * * * * * * * * * * * * * * * * */

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class HardWareInfoManager {


    /**
     * 获取设备基板名称
     *
     * @return
     */
    public  String getBoard() {
        String result = "";
        try {
            result = android.os.Build.BOARD;
        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getBoard:" + e.toString());
        }
        return result;
    }

    /**
     * 获取设备引导程序版本号
     *
     * @return
     */
    public  String getBootLoader() {
        String result = "";
        try {
            result = Build.BOOTLOADER;
        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getBootLoader:" + e.toString());
        }
        return result;
    }

    /**
     * 获取设备品牌
     *
     * @return
     */
    public  String getBrand() {
        String result = "";
        try {
            result = Build.BRAND;
        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getBrand:" + e.toString());
        }
        return result;
    }

    /**
     * 获取设备指令集名称（CPU的类型）,多个以 ， 分割
     *
     * @return
     */
    public  String getCPU_ABI() {
        String result = "";
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                String[] abis = Build.SUPPORTED_ABIS;
                for (int i = 0; i < abis.length; i++) {
                    result += abis[i];
                    if (i < (abis.length - 1)) {
                        result += ",";
                    }
                }
            } else {
                result = Build.CPU_ABI + "," + Build.CPU_ABI2;
            }

        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getCPU_ABI:" + e.toString());
        }
        return result;
    }


    /**
     * 获取设备驱动名称
     *
     * @return
     */
    public  String getDevice() {
        String result = "";
        try {
            result = Build.DEVICE;
        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getDevice:" + e.toString());
        }
        return result;
    }

    /**
     * 获取设备显示的版本包（在系统设置中显示为版本号）和ID一样
     *
     * @return
     */
    public  String getDisplay() {
        String result = "";
        try {
            result = Build.DISPLAY;
        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getDisplay:" + e.toString());
        }
        return result;
    }

    /**
     * 设备的唯一标识。由设备的多个信息拼接合成。
     *
     * @return
     */
    public  String getFingerPrint() {
        String result = "";
        try {
            result = Build.FINGERPRINT;
        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getFingerPrint:" + e.toString());
        }
        return result;
    }

    /**
     * 设备硬件名称,一般和基板名称一样（BOARD）
     *
     * @return
     */
    public  String getHardWare() {
        String result = "";
        try {
            result = Build.HARDWARE;
        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getHardWare:" + e.toString());
        }
        return result;
    }

    /**
     * 设备主机地址
     *
     * @return
     */
    public  String getHost() {
        String result = "";
        try {
            result = Build.HOST;
        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getHost:" + e.toString());
        }
        return result;
    }

    /**
     * :设备版本号。
     *
     * @return
     */
    public  String getID() {
        String result = "";
        try {
            result = Build.ID;
        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getID:" + e.toString());
        }
        return result;
    }

    /**
     * 获取手机的型号 设备名称
     *
     * @return
     */
    public  String getModel() {
        String result = "";
        try {
            result = Build.MODEL;
        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getModel:" + e.toString());
        }
        return result;
    }

    /**
     * 获取设备制造商
     *
     * @return
     */
    public  String getManufacturer() {
        String result = "";
        try {
            result = Build.MANUFACTURER;
        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getManufacturer:" + e.toString());
        }
        return result;
    }

    /**
     * 整个产品的名称
     *
     * @return
     */
    public  String getProduct() {
        String result = "";
        try {
            result = Build.PRODUCT;
        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getProduct:" + e.toString());
        }
        return result;
    }

    /**
     * 无线电固件版本号，通常是不可用的 显示unknown
     *
     * @return
     */
    public  String getRadio() {
        String result = "";
        try {
            result = Build.getRadioVersion();

        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getProduct:" + e.toString());
        }
        return result;
    }

    /**
     * 设备标签。如release-keys 或测试的 test-keys
     *
     * @return
     */
    public  String getTags() {
        String result = "";
        try {
            result = Build.TAGS;

        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getTags:" + e.toString());
        }
        return result;
    }

    /**
     * 时间
     *
     * @return
     */
    public  String getTime() {
        String result = "";
        try {
            result = Build.TIME + "";

        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getTime:" + e.toString());
        }
        return result;
    }

    /**
     * 设备版本类型 主要为”user” 或”eng”.
     *
     * @return
     */
    public  String getType() {
        String result = "";
        try {
            result = Build.TYPE;

        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getType:" + e.toString());
        }
        return result;
    }

    /**
     * 设备用户名 基本上都为android-build
     *
     * @return
     */
    public  String getUser() {
        String result = "";
        try {
            result = Build.USER;

        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getUser:" + e.toString());
        }
        return result;
    }

    /**
     * 获取系统版本字符串。如4.1.2 或2.2 或2.3等
     *
     * @return
     */
    public  String getVersionRelease() {
        String result = "";
        try {
            result = Build.VERSION.RELEASE;

        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getVersionRelease:" + e.toString());
        }
        return result;
    }

    /**
     * 设备当前的系统开发代号，一般使用REL代替
     *
     * @return
     */
    public  String getVersionCodeName() {
        String result = "";
        try {
            result = Build.VERSION.CODENAME;

        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getVersionCodeName:" + e.toString());
        }
        return result;
    }

    /**
     * 系统源代码控制值，一个数字或者git hash值
     *
     * @return
     */
    public  String getVersionIncreamental() {
        String result = "";
        try {
            result = Build.VERSION.INCREMENTAL;

        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getVersionIncreamental:" + e.toString());
        }
        return result;
    }

    /**
     * 系统的API级别 一般使用下面大的SDK_INT 来查看
     *
     * @return
     */
    public  String getSDK_INT() {
        String result = "";
        try {
            result = Build.VERSION.SDK_INT + "";

        } catch (Exception e) {
            Log.e("----->" + "HWInfoManager", "getSDK_INT:" + e.toString());
        }
        return result;
    }

    /**
     * 获取进程名称
     * @param context
     * @return
     */
    public  String getCurProcessName(Context context) {
        try {
            int pid = android.os.Process.myPid();
            ActivityManager mActivityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                    .getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        } catch (Exception e) {
            Log.e("----->" + "HWareInfoManager", "getCurProcessName:" + e.toString());
        }
        return "";
    }

    /**
     * 获取pid
     * @return
     */
    public  String getPid(){
        return android.os.Process.myPid()+"";
    }

    /**
     * 获取屏幕亮度
     * @param context
     * @return
     */
    public  int getScreenBrightness(Context context) {
        int value = -1;
        ContentResolver cr = context.getContentResolver();
        try {
            value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            Log.e("----->" + "HWareInfoManager", "getScreenBrightness:" + e.toString());
        }
        return value;
    }

    /**
     * 获取音量 格式如：系统音量,通话音量,铃声音量,音乐音量,提示音音量
     * @param context
     * @return
     */
    public  String getVolume(Context context){
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            StringBuilder stringBuilder = new StringBuilder();
            //系统音量
            stringBuilder.append(audioManager.getStreamVolume( AudioManager.STREAM_SYSTEM ));
            stringBuilder.append(",");
            //通话音量
            stringBuilder.append(audioManager.getStreamVolume( AudioManager.STREAM_VOICE_CALL ));
            stringBuilder.append(",");
            //铃声音量
            stringBuilder.append(audioManager.getStreamVolume( AudioManager.STREAM_RING ));
            stringBuilder.append(",");
            //音乐音量
            stringBuilder.append(audioManager.getStreamVolume( AudioManager.STREAM_MUSIC ));
            stringBuilder.append(",");
            //提示声音音量
            stringBuilder.append(audioManager.getStreamVolume( AudioManager.STREAM_ALARM ));
            return stringBuilder.toString();
        } catch (Exception e) {
            Log.e("----->" + "HWareInfoManager", "getVolume:" + e.toString());
        }

        return "";
    }

}
