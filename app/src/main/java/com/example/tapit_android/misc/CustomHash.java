package com.example.tapit_android.misc;

import com.example.tapit_android.models.Users;
import com.firebase.ui.auth.data.model.User;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CustomHash {
    private static String makeSHA1Hash(String input)
            throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.reset();
        byte[] buffer = input.getBytes("UTF-8");
        md.update(buffer);
        byte[] digest = md.digest();

        String hexStr = "";
        for (int i = 0; i < digest.length; i++) {
            hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return hexStr;
    }

    public static String getUserHash(String email, String name) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String hashString = makeSHA1Hash(name+email);
        return hashString;
    }


}
