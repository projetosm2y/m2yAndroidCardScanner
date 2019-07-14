package br.com.m2yandroidcardscanner.utils.validations;

/**
 * Created by mobile2you on 12/08/16.
 */
public class IsExactText {

    public static boolean isValid(String text, String otherText) {
        return text.equals(otherText);
    }

}
