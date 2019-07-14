package br.com.m2yandroidcardscanner.utils.validations;

/**
 * Created by azul on 11/04/17.
 */

public class IsCnpj {
    private static final int[] weightCNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private static int resolveDigit(String str, int[] weight) {
        int sum = 0;
        for (int index = str.length() - 1, digito; index >= 0; index--) {
            digito = Integer.parseInt(str.substring(index, index + 1));
            sum += digito * weight[weight.length - str.length() + index];
        }
        sum = 11 - sum % 11;
        return sum > 9 ? 0 : sum;
    }


    public static boolean isValid(String cnpj) {
        if ((cnpj == null) || (cnpj.length() != 14)) return false;

        Integer digit1 = resolveDigit(cnpj.substring(0, 12), weightCNPJ);
        Integer digit2 = resolveDigit(cnpj.substring(0, 12) + digit1, weightCNPJ);
        return cnpj.equals(cnpj.substring(0, 12) + digit1.toString() + digit2.toString());
    }
}
