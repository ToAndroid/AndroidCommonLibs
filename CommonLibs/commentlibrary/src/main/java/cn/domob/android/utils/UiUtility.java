//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.domob.android.utils;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Method;

public class UiUtility {
    private static Logger mLogger = new Logger(UiUtility.class.getSimpleName());

    public UiUtility() {
    }

    public static int getPixel(Context context, int dip) {
        return (int)(DeviceInfo.getCurrentDensity(context) * (float)dip);
    }

    public static int getDip(Context context, int pixel) {
        return (int)(Double.parseDouble(String.valueOf(pixel)) / (double)DeviceInfo.getCurrentDensity(context));
    }

    public static void closeHardware(View view) {
        if(view != null && DeviceInfo.isAndroidVersionLargerThan(11, true)) {
            try {
                Class e = view.getClass();
                Method method = e.getMethod("setLayerType", new Class[]{Integer.TYPE, Paint.class});
                method.invoke(view, new Object[]{Integer.valueOf(1), null});
            } catch (Exception var3) {
                mLogger.printStackTrace(var3);
            }
        }

    }

    public static boolean isMeetConditionToSendImpReport(Context context, ViewGroup viewGroup) {
        return viewGroup != null && viewGroup.isShown() && viewGroup.hasWindowFocus() && viewGroup.getWindowVisibility() == 0 && DeviceInfo.isScreenOn(context);
    }

    public static void showConfirmDialog(final Context context, String title, String msg) {
        (new Builder(context)).setTitle(title).setMessage(msg).setNegativeButton("确定", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((Activity)context).finish();
            }
        }).show();
    }
}
