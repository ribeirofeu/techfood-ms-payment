package com.fiap.techfood.payment.infrastructure.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Utils {

    private Utils(){}

    public static String convertToBase64(String info) {
        return Base64.getEncoder().encodeToString(info.getBytes(StandardCharsets.UTF_8));
    }

}
