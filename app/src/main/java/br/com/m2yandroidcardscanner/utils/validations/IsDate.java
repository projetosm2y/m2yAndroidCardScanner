package br.com.m2yandroidcardscanner.utils.validations;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by azul on 07/11/17.
 */

public class IsDate {
    public static boolean isValid(String text, String dateFormat) {
        SimpleDateFormat sdf =  new SimpleDateFormat(dateFormat);
        try {
            sdf.parse(text);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
