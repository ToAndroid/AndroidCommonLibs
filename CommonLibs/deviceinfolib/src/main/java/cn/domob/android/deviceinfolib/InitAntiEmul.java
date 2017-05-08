package cn.domob.android.deviceinfolib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

/**
 * 判断是否是模拟器
 */
public class InitAntiEmul {
    private int batteryV = 0;
    private double batteryT = 0;

    public InitAntiEmul(Context context) {
        BatteryBroadcastReceiver batteryBroadcastReceiver = new BatteryBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        context.registerReceiver(batteryBroadcastReceiver, intentFilter);
    }

    public void isEmulator(final callback callback) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ((batteryT == 0 && batteryV == 0) || (batteryT == 0 && hasBaseband()) || (batteryV == 0 && hasBaseband())) {
                    callback.result(true);
                } else {
                    callback.result(false);
                }
            }
        }, 1000L);

    }

    //判断是否存在基带信息
    private static boolean hasBaseband() {
        try {
            Class localClass = Class.forName("android.os.SystemProperties");
            Object localObject = localClass.newInstance();
            boolean bool = localClass.getMethod("get", new Class[]{String.class, String.class}).invoke(localObject, "gsm.version.baseband", "no message").equals("no message");
            return bool;
        } catch (Exception localException) {
        }
        return false;
    }

    //监听电量变化广播
    class BatteryBroadcastReceiver extends BroadcastReceiver {
        BatteryBroadcastReceiver() {
        }

        public void onReceive(Context paramContext, Intent paramIntent) {
            if (paramIntent.getAction().equals("android.intent.action.BATTERY_CHANGED")) {
                InitAntiEmul.this.batteryV = paramIntent.getIntExtra("voltage", 0);
                InitAntiEmul.this.batteryT = (0.1D * paramIntent.getIntExtra("temperature", 0));
            }
        }
    }

    public interface callback {

        void result(boolean b);
    }


}
