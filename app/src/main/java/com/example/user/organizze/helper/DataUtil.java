package com.example.user.organizze.helper;

import java.text.SimpleDateFormat;

public class DataUtil {
    public static String dataAtual(){
        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        return new String (simpleDateFormat.format(data));
    }

    public static String mesAnoDataEscolhida(String data){
        String retornoData[] = data.split("/");
        String mesAno = retornoData[1] + retornoData[2];

        return mesAno;
    }
}
