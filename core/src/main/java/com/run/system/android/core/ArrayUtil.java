package com.run.system.android.core;

import java.util.Collection;

/**
 * Created by nguyenxuan on 11/25/2014.
 */
public class ArrayUtil {

    public static boolean isSubsetStringOf(Collection<String> subset, Collection<String> superset) {
        if(subset!=null && superset!=null){
            for (String string : subset) {
                if (!superset.contains(string)) {
                    return false;
                }
            }
        }
        return true;
    }
}
