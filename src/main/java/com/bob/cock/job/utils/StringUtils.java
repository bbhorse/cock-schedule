package com.bob.cock.job.utils;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {

    public static int compare(String text1, String text2) {
        if (text1 == null && text2 == null)
            return 0;
        if (text1 != null && text2 == null) {
            return 1;
        }
        if (text1 == null) {
            return -1;
        }
        return text1.compareTo(text2);
    }
    
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasText(String text) {
        if (text == null)
            return false;
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i)))
                return true;
        }
        return false;
    }

    public static boolean equals(String text1, String text2) {
        if (text1 == null)
            return text2 == null;

        return text1.equals(text2);
    }

    public static String truncate(String text, int maxLength) {
        if (text == null)
            return null;
        if (text.length() <= maxLength)
            return text;
        return text.substring(0, maxLength);
    }

    public static String trim(String text) {
        if (text == null)
            return null;
        return text.trim();
    }

    public static int getAbsoluteLength(String text) {
        int charlength = text.length();
        return (text.getBytes(Charset.forName("UTF-8")).length - charlength)
                / 2 + charlength;
    }

    public static boolean isServerAddress(String serverAddress) {
        if (!StringUtils.hasText(serverAddress)) return false;
        Pattern pattern = Pattern.compile("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])-http-\\d{0,5}-tcp-\\d{0,5}$");
        Matcher matcher = pattern.matcher(serverAddress);
        return matcher.matches();   
    }
    
    public static void main(String ...strings ) {
        System.out.println(isServerAddress("192.168.10.100-http-8080-tcp-8090"));
    }
    
    private StringUtils() {
    }
}