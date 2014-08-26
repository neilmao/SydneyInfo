package com.neilmao.tool;

/**
 * Created with IntelliJ IDEA.
 * User: neilmao
 * Date: 27/08/2014
 * Time: 12:04 AM
 */
public class HTMLUtils {

    /* Extract content between given two strings
     *
     *  todo: now the algorithm is very inefficient, need to change to KMP to make it in O(1) rather than O(3)
     */
    public static String extractBetween(String str ,String startStr, boolean includeStart,
                                        String endStr, boolean includeEnd) {
        int start = str.indexOf(startStr);
        if (!includeStart)
            startStr.length();

        int end = str.indexOf(endStr, start) - 1;
        if (includeEnd)
            end += endStr.length();

        if (start != -1 && end != -1) {
            if (start < end) {
                return str.substring(start, end);
            }
        }
        return "";
    }
}
