package com.leeky.myapplication.utils;

import android.util.Base64;

import java.math.BigInteger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Leeky on 2019/5/23.
 */
public class AES {
    public static final String CharSet = "UTF-8";
    private static String Key = "";
    private static Cipher Encoder = null;
    private static Cipher Decoder = null;

    public AES() {
    }

    public static String getKey() {
        return Key;
    }

    public static void setKey(String key) {
        Key = key;
    }

    public static String encode(String plain) {
        try {
            Cipher cipher = getEncoder();
            byte[] res = cipher.doFinal(plain.getBytes("UTF-8"));
            printHexString(res);
            res = Base64.encode(res, 2);
            String rtn = new String(res, 0, res.length, "UTF-8");
            return rtn;
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static void printHexString(byte[] b) {
        for(int i = 0; i < b.length; ++i) {
            String hex = Integer.toHexString(b[i] & 255);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            BigInteger bi = new BigInteger(hex, 16);
            Long il = bi.longValue();
            System.out.print(hex.toUpperCase());
        }

    }

    public static String decode(String cryp) {
        try {
            byte[] res = Base64.decode(cryp.getBytes("UTF-8"), 2);
            System.out.println(res);
            Cipher cipher = getDecoder();
            res = cipher.doFinal(res);
            return new String(res, 0, res.length, "UTF-8");
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    private static Cipher getEncoder() throws Exception {
        if (Encoder == null) {
            String key = getKey();
            byte[] bs = key.getBytes("UTF-8");
            SecretKeySpec skeySpec = new SecretKeySpec(bs, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(1, skeySpec);
            Encoder = cipher;
        }

        return Encoder;
    }

    private static Cipher getDecoder() throws Exception {
        if (Decoder == null) {
            String key = getKey();
            byte[] bs = key.getBytes("UTF-8");
            SecretKeySpec skeySpec = new SecretKeySpec(bs, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(2, skeySpec);
            Decoder = cipher;
        }

        return Decoder;
    }
}
