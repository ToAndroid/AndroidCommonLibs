//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.domob.android.utils;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import cn.domob.android.utils.AppInfo;
import cn.domob.android.utils.DeviceInfo;
import cn.domob.android.utils.Logger;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocationInfo {
    private static Logger mLogger = new Logger(LocationInfo.class.getSimpleName());

    public LocationInfo() {
    }

    public static String getLocationInfo(Context context) {
        LocationInfo.DLocationManager dLocationManager = LocationInfo.DLocationManager.dLocationManager;
        Location location = dLocationManager.getLocation(context);
        return location != null?dLocationManager.getLocationStr(location):null;
    }

    public static int getLocationAccuracyMeters() {
        return LocationInfo.DLocationManager.dLocationManager.getAccuracyMeters();
    }

    public static int getLocationAccuracy() {
        return LocationInfo.DLocationManager.dLocationManager.getAccuracy();
    }

    public static int getLocationStatus() {
        return LocationInfo.DLocationManager.dLocationManager.getStatus();
    }

    public static long getLocationTime() {
        return LocationInfo.DLocationManager.dLocationManager.getTime();
    }

    public static String[] getLocationBaseInfo(Context context) {
        mLogger.verboseLog(DeviceInfo.class.getSimpleName(), "getLocationBasedService");
        String[] baseInfo = new String[]{"-1", "-1", "-1", "-1"};
        if(!AppInfo.isPermissionsAuthorized(context, "android.permission.ACCESS_COARSE_LOCATION") && !AppInfo.isPermissionsAuthorized(context, "android.permission.ACCESS_FINE_LOCATION")) {
            mLogger.warnLog("No permission to access locationBaseInfo");
            return baseInfo;
        } else {
            try {
                TelephonyManager e = (TelephonyManager)context.getSystemService("phone");
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
    }

    private static class DLocationManager {
        private static final LocationInfo.DLocationManager dLocationManager = new LocationInfo.DLocationManager();
        private static final int MAX_GPS_LOCATE_FAILED_TIMES = 3;
        private static final int MIN_INTERVAL_OF_GPS_LOCATE = 600000;
        private static volatile int mGPSLocateFailedTimes = 0;
        private static volatile long mPreviousGPSLocateTimestamp;
        private static final long COORD_MIN_INTERVAL = 600000L;
        private static final int TWO_MINUTES = 120000;
        private static final int NETWORK_DURATION = 1200000;
        private static final int GPS_DURATION = 90000;
        private Location location;
        private int status = -1;

        private DLocationManager() {
        }

        private static LocationInfo.DLocationManager getInstance() {
            return dLocationManager;
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
                    LocationManager locationManager = (LocationManager)context.getSystemService("location");
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
                LocationInfo.mLogger.printStackTrace(var8);
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

                    while(true) {
                        while(i$.hasNext()) {
                            String provider = (String)i$.next();
                            if(provider.equals("gps")) {
                                if(this.canStartGPSLocate()) {
                                    mPreviousGPSLocateTimestamp = System.currentTimeMillis();
                                    this.requestLocationByProvider(context, locationManager, provider);
                                }
                            } else if(!provider.equals("network") && !provider.equals("passive")) {
                                LocationInfo.mLogger.debugLog(String.format("Detected an unknown location provider %s, but taking into account security, there is no use.", new Object[]{provider}));
                            } else {
                                this.requestLocationByProvider(context, locationManager, provider);
                            }
                        }

                        return;
                    }
                } catch (Exception var6) {
                    LocationInfo.mLogger.printStackTrace(var6);
                }
            }
        }

        private boolean canStartGPSLocate() {
            if(3 <= mGPSLocateFailedTimes) {
                LocationInfo.mLogger.debugLog(String.format("gps positioning has failed %d times, has reached or exceeded the maximum allowed number(%d) of failures", new Object[]{Integer.valueOf(mGPSLocateFailedTimes), Integer.valueOf(3)}));
                return false;
            } else {
                long currentTimestamp = System.currentTimeMillis();
                if(currentTimestamp < mPreviousGPSLocateTimestamp + 600000L) {
                    LocationInfo.mLogger.debugLog("Now can not be gps positioning, because just evoke gps positioning time");
                    return false;
                } else {
                    return true;
                }
            }
        }

        private void requestLocationByProvider(Context context, LocationManager locationManager, String provider) {
            LocationInfo.DLocationManager.DLocationListener mLocationListener = new LocationInfo.DLocationManager.DLocationListener(locationManager);
            LocationInfo.mLogger.debugLog(provider + " start to listener position");
            locationManager.requestLocationUpdates(provider, 0L, 0.0F, mLocationListener, context.getMainLooper());
            if(provider.equals("network")) {
                this.handleWhenLocationTimeout(locationManager, mLocationListener, 1200000, provider);
            } else if(provider.equals("gps")) {
                this.handleWhenLocationTimeout(locationManager, mLocationListener, 90000, provider);
            }

        }

        private void handleWhenLocationTimeout(final LocationManager locationManager, final LocationInfo.DLocationManager.DLocationListener listener, int duration, final String provider) {
            Timer mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                public void run() {
                    if(provider.equals("gps")) {
                        DLocationManager.this.updateGPSLocateFailedTimesIfNeed(listener);
                    }

                    locationManager.removeUpdates(listener);
                    LocationInfo.mLogger.debugLog("remove the listener of " + provider);
                }
            }, (long)duration);
        }

        private void updateGPSLocateFailedTimesIfNeed(LocationInfo.DLocationManager.DLocationListener listener) {
            if(!listener.isLocateSuccess()) {
                ++mGPSLocateFailedTimes;
                LocationInfo.mLogger.debugLog("gps locate failed, and the current total failed times is " + mGPSLocateFailedTimes);
            }

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
                LocationInfo.mLogger.debugLog("This location is obtained via " + provider);
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

            LocationInfo.mLogger.debugLog("location accuracy is " + accruacy + " meters");
            return accruacy;
        }

        private int getStatus() {
            switch(this.status) {
            case 0:
                LocationInfo.mLogger.debugLog("Location can not be obtained due to USER_CLOSE");
                break;
            case 1:
                LocationInfo.mLogger.debugLog("Location can not be obtained due to NO_PERSSION");
                break;
            case 2:
                LocationInfo.mLogger.debugLog("Location can not be obtained due to NO_AVAILABLE_LOCATION");
            }

            return this.status;
        }

        private long getTime() {
            if(this.location != null) {
                long LocTimeStamp = this.location.getTime();
                long timeDelta = (System.currentTimeMillis() - LocTimeStamp) / 1000L;
                LocationInfo.mLogger.debugLog(DeviceInfo.class.getSimpleName(), String.format("The location is %s minutes %s seconds ago acquired", new Object[]{String.valueOf(timeDelta / 60L), String.valueOf(timeDelta % 60L)}));
                return LocTimeStamp;
            } else {
                return 0L;
            }
        }

        private String getLocationStr(Location _location) {
            String s = null;
            if(_location != null) {
                s = _location.getLatitude() + "," + _location.getLongitude();
                LocationInfo.mLogger.debugLog(DeviceInfo.class.getSimpleName(), "User coordinates are " + s);
            }

            return s;
        }

        private class DLocationListener implements LocationListener {
            public LocationManager locmgr;
            private boolean mIsLocateSuccess = false;

            public boolean isLocateSuccess() {
                return this.mIsLocateSuccess;
            }

            DLocationListener(LocationManager mgr) {
                this.locmgr = mgr;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }

            public void onLocationChanged(Location location) {
                if(location != null) {
                    this.mIsLocateSuccess = true;
                    String provider = location.getProvider();
                    if(provider != null && !provider.equals("network")) {
                        LocationInfo.mLogger.debugLog("remove the listener of " + provider);
                        this.locmgr.removeUpdates(this);
                    }
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
