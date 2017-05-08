package cn.domob.android.deviceinfolib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

/* * * * * * * * * * * * * * * * * * *
* author :andoop　　　　　　　　　　　
* time   :2017/2/7
* explain：电量变化receiver
* * * * * * * * * * * * * * * * * * */
public class BatteryChangeReceiver extends BroadcastReceiver {

    private int level=-1;
    private int scale=-1;
    private int status=-1;
    private int health=-1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null){
             level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
             scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
             status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
             health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
           /* Log.e("----->" + "BCReceiver", "onReceive:" + "level:" + level + ";" +
                    "scale:" + scale + ";" +"status:"+status+";health:"+health);*/
        }
    }

    public int getLevel() {
        return level;
    }

    public int getScale() {
        return scale;
    }

    public int getStatus() {
        return status;
    }

    public int getHealth() {
        return health;
    }
}
