package com.leeky.myapplication.utils;

import android.text.TextUtils;

/**
 * Created by Leeky on 2019/5/23.
 */
public class AESEncryption {
    public static String AESKEY = "nCbf67awoApYVlSB";
    public static String getAESEncrption(String content){
        if(TextUtils.isEmpty(AES.getKey())){
            AES.setKey(AESKEY);
        }

        String AESEncrptionStr = AES.encode(content);
        return AESEncrptionStr;
    }
}
