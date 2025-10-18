package com.finops.util;

import java.math.BigDecimal;

public class FinanceUtil {

    public static double modAmount(double amount){
        if(amount >= 0){
            return amount;
        }
        return amount*(-1);
    }

    public static String modAndFormat(double amount){
        double refinedAmount = modAmount(amount);
        return formatBigDecimal(refinedAmount, 2);
    }

    public static String formatBigDecimal(double amount, int scale){
        return new BigDecimal(amount).setScale(scale,BigDecimal.ROUND_HALF_UP).toString();
    }

    public static double getCrValue(double amount){
        return amount * (-1);
    }

    public static String closingBalance(double opening, double dr, double cr){
        double crValue = getCrValue(cr);
        double totalValue = opening+dr+crValue;
        return formatBigDecimal(totalValue, 2);
    }

    public static double closingBalance2(double opening, double dr, double cr){
        double crValue = getCrValue(cr);
        return opening+dr+crValue;
    }

    public static String amountDrCr(double amount){
        if(amount >= 0){
            return FinanceTerm.Dr.name();
        }
        return FinanceTerm.Cr.name();
    }

    public static String formatNegativeAmount(String amount){
        if(amount.startsWith("-")){
            return "("+amount.substring(1)+")";
        }
        return amount;
    }

    public static String formatNegativeAmount2(String amount){
        if(amount.startsWith("-")){
            return amount.substring(1);
        }
        return amount;
    }
}
