package com.android.lib.core.util;

/**
 * Created by nguyenxuan on 11/25/2014.
 */
public class StringUtil {

    public static boolean isEmpty(String string){
        if("".equals(string)){
            return false;
        }
        return true;
    }

    public static boolean isEmpty(CharSequence text) {
        if(text!=null)
        return isEmpty(text.toString());
        return true;
    }
}
