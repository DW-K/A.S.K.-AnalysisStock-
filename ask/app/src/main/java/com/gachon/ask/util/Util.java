package com.gachon.ask.util;

import java.text.DecimalFormat;

public class Util {
    // Google Login Request Code
    public static final int RC_SIGN_IN = 1000;

    /**
     * 숫자에 천단위마다 콤마 넣기
     * @param int
     * @return String
     * */
    public static String toNumFormat(int num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }

}
