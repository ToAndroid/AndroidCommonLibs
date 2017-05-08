package cn.domob.android.deviceinfolib;

/* * * * * * * * * * * * * * * * * * *
* author :andoop　　　　　　　　　　　
* time   :2017/2/6
* explain：电话相关信息处理者
* * * * * * * * * * * * * * * * * * */

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

 public class PhoneInfoManager {
    /**
     * 获取电话号码
     * @param context
     * @return
     */
    public  String getPhoneNumber(Context context){
        TelephonyManager phoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            String line1Number = phoneManager.getLine1Number();
            if(TextUtils.isEmpty(line1Number)){
                return "";
            }else {
                return line1Number;
            }
        } catch (Exception e) {
            Log.e("----->" + "PhoneInfoManager", "getPhoneNumber:" + e.toString());
        }
        return "";
    }


}
