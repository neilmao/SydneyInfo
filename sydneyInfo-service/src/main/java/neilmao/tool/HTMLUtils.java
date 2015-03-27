package neilmao.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: neilmao
 * Date: 27/08/2014
 * Time: 12:04 AM
 */
public class HTMLUtils {

    /** Extract content between given two strings
     *
     *  todo: now the algorithm is very inefficient, need to change to KMP to make it in O(1) rather than O(3)
     *  todo: or it can change to use regex but need to handle reserved characters
     */
    public static String extractBetween(String str ,String startStr, boolean includeStart,
                                        String endStr, boolean includeEnd) {
        int start = str.indexOf(startStr);
        if (!includeStart)
            start += startStr.length();

        int end = str.indexOf(endStr, start);
        if (includeEnd)
            end += endStr.length();

        if (start != -1 && end != -1) {
            if (start < end) {
                return str.substring(start, end);
            }
        }
        return null;
    }

    /** String search by Regex
     *
     * @param str text to be searched
     * @param regex
     * @return first search result, inclusive with start and end of regex
     */

    public static String searchByPattern(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return str.substring(matcher.start(), matcher.end());
        }
        return null;
    }
}
