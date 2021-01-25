package com.zk.soulierge;

import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class test {
    public String computeSHAHash(String str) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException unused) {
            Log.e("SHA1", "Error initializing SHA1 message digest");
            messageDigest = null;
        }
        try {
            messageDigest.update(str.getBytes("ASCII"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            String convertToHex = convertToHex(messageDigest.digest());
            Log.e("SHA1_RESULT", convertToHex);
            return convertToHex;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    private static String convertToHex(byte[] bArr) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bArr.length; i++) {
            byte b = (byte) ((bArr[i] >>> 4) & 15);
            int i2 = 0;
            while (true) {
                if (b < 0 || b > 9) {
                    stringBuffer.append((char) (97 + (b - 10)));
                } else {
                    stringBuffer.append((char) (48 + b));
                }
                b = (byte) (bArr[i] & 15);
                int i3 = i2 + 1;
                if (i2 >= 1) {
                    break;
                }
                i2 = i3;
            }
        }
        return stringBuffer.toString();
    }
}
