package com.tima.platform.service.encoding;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    public static String hash512(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }catch (NoSuchAlgorithmException s){
            return "";
        }
    }
}
