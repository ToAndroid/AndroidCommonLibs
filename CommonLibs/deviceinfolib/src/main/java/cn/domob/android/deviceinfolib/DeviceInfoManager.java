package cn.domob.android.deviceinfolib;

/* * * * * * * * * * * * * * * * * * *
* author :andoop　　　　　　　　　　　
* time   :2017/2/7
* explain：
* * * * * * * * * * * * * * * * * * */

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class DeviceInfoManager {

    private Context context;
    private static DeviceInfoManager INSTANCE;
    //电池变化监听
    private BatteryChangeReceiver batteryChangeReceiver;
    //设备信息管理
    private HardWareInfoManager hardWareInfoManager;
    //网络信息管理
    private NetInfoManager netInfoManager;
    //phone 信息管理
    private PhoneInfoManager phoneInfoManager;
    //是否是模拟器
    private boolean isEmulator;

    private DeviceInfoManager(Context context) {
        this.context = context;
        batteryChangeReceiver=new BatteryChangeReceiver();
        hardWareInfoManager=new HardWareInfoManager();
        netInfoManager=new NetInfoManager();
        phoneInfoManager=new PhoneInfoManager();

    }

    public static DeviceInfoManager newInstance(Context context) {

        if (INSTANCE == null) {
            synchronized (DeviceInfoManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DeviceInfoManager(context);
                }
            }

        }
        return INSTANCE;
    }

    /**
     * 初始化
     */
    public void init(){
        //注册电量变化监听
        try {
            IntentFilter filter=new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            context.registerReceiver(batteryChangeReceiver,filter);
        } catch (Exception e) {
            Log.e("----->" + "DeviceInfoManager", "init:" + e.toString());
        }

        //初始化模拟器检测
        InitAntiEmul initAntiEmul = new InitAntiEmul(context);
        initAntiEmul.isEmulator(new InitAntiEmul.callback() {
            @Override
            public void result(boolean b) {
                isEmulator=b;
            }
        });

    }
    public void onExit(){
        try {
            context.unregisterReceiver(batteryChangeReceiver);
        } catch (Exception e) {
            Log.e("----->" + "DeviceInfoManager", "onExit:" + e.toString());
        }
    }

    /**
     * 返回电池信息 level,scale,status,health 用 ',' 隔开 默认都是-1
     * @return
     */
    public String getBatteryInfo(){
        if(batteryChangeReceiver==null)
            return "-1,-1,-1,-1";
        String result="";
        result=batteryChangeReceiver.getLevel()+","+batteryChangeReceiver.getScale()+
                ","+batteryChangeReceiver.getStatus()+","+batteryChangeReceiver.getHealth();
        return result;
    }

    /**
     * 判断是否是虚拟机
     * @return
     */
    public boolean isEmulator(){
        return isEmulator;
    }


    public HardWareInfoManager hardWareInfoManager(){
        if(hardWareInfoManager==null)
            hardWareInfoManager=new HardWareInfoManager();
        return hardWareInfoManager;
    }

    public NetInfoManager netInfoManager(){
        if(netInfoManager==null)
            netInfoManager=new NetInfoManager();
        return netInfoManager;
    }

    public PhoneInfoManager phoneInfoManager(){
        if(phoneInfoManager==null)
            phoneInfoManager=new PhoneInfoManager();
        return phoneInfoManager;
    }





}
