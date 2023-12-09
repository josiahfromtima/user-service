package com.tima.platform.service.encoding;

import java.util.Base64;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/7/23
 */
public class MessageEncoding {
    private MessageEncoding(){}
    public static String base64Encoding(String plainText) {
        if(plainText == null || plainText.isEmpty()) return "";
        return Base64.getEncoder().encodeToString(plainText.getBytes());
    }

    public static String base64Decoding(String encodedString) {
        if(encodedString == null || encodedString.isEmpty()) return "";
        return new String(Base64.getDecoder().decode(encodedString));
    }

    public static String base64EncodingNoPadding(String plainText) {
        if(plainText == null || plainText.isEmpty()) return "";
        return Base64.getEncoder().withoutPadding().encodeToString(plainText.getBytes());
    }

    public static String base64UrlEncoding(String url) {
        if(url == null || url.isEmpty()) return "";
        return Base64.getUrlEncoder().encodeToString(url.getBytes());
    }

    public static String base64UrlDecoding(String encodedUrl) {
        if(encodedUrl == null || encodedUrl.isEmpty()) return "";
        return new String(Base64.getUrlDecoder().decode(encodedUrl));
    }
}
