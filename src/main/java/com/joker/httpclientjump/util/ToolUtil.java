package com.joker.httpclientjump.util;

import com.joker.httpclientjump.constant.Common;


public class ToolUtil {

    /**
     * 格式化url绝对路径,不以'/'开头,以'/'结尾
     *
     * @param param Url path absolute
     * @return java.lang.String
     */
    public static String formatPath(String param) {
        while (param.startsWith(Common.DIAGONAL)) {
            param = param.substring(1);
        }
        if (!param.endsWith(Common.DIAGONAL)) {
            param = param.concat(Common.DIAGONAL);
        }
        return param;
    }

    public static String removeFrontAndRearDiagonal(String param) {
        while (param.startsWith(Common.DIAGONAL)) {
            param = param.substring(1);
        }
        while (param.endsWith(Common.DIAGONAL)) {
            param = param.substring(0, param.length() - 1);
        }
        return param;
    }
}
