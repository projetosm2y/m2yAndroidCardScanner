package br.com.m2yandroidcardscanner.utils.validations;

/**
 * Created by mobile2you on 12/08/16.
 */
public class IsLengthValid {

    public static boolean isValid(String text, int size, boolean exactSameSize) {
        return exactSameSize ? text.length() == size : text.length() >= size;
    }

}
