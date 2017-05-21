package com.apin.common.utils;

import org.apache.commons.lang3.StringUtils;

public class ConvertUtil {

    public static String getColumnByPropertyName(String propertyName) {
        if (StringUtils.isBlank(propertyName)) {
            return null;
        }
        char[] car = propertyName.trim().toCharArray();
        StringBuilder cname = new StringBuilder();
        for (char c : car) {
            cname.append((c >= 'A' && c <= 'Z') ? ("_" + (char) (c + 32)) : c);
        }
        return cname.toString();
    }


}
