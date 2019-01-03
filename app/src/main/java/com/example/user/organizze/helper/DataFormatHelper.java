package com.example.user.organizze.helper;

import java.text.DecimalFormat;

public class DataFormatHelper {

    public static String fomatoDecimal(Double number){
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String retorno = decimalFormat.format(number);

        return retorno;
    }
}
