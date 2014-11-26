
package com.android.lib.core.util;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import java.util.UUID;

public class DeviceUtil {

    /**
     * 
     */
    public DeviceUtil() {

    }

    /**
     * 
     * Generate device UUID
     * 
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * @return
     */
    public static String getDeviceModels() {
        return getDeviceName();
    }

    /**
     * @return
     */
    public static String getDeviceOsName() {
        return "Android API " + Build.VERSION.SDK_INT;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /**
     * @return
     */
    public static String getAppVersion(Activity activity) {
        PackageInfo pInfo;
        try {
            pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            String version = pInfo.versionName;
            return version;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return "";
    }
}
