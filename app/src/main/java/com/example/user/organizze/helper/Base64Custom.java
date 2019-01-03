package com.example.user.organizze.helper;

import android.util.Base64;

public class Base64Custom {

    public static String codificarBase64(String s){

        return Base64.encodeToString(s.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");

    }

    public static String decodificarBase64(String s){

        return new String(Base64.decode(s, Base64.DEFAULT));
    }
}
