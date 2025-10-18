package com.finops.util;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

enum BranchState {
    SFP("SFP", "09"),
    SFPM("SFPM", "27"),
    SFPC("SFPC", "33"),
    SFPK("SFPK", "32"),
    SFPG("SFPG", "24");

    private String branch;
    private String stateCode;

    private BranchState(String branch, String stateCode) {
        this.branch = branch;
        this.stateCode = stateCode;
    }

    /**
     * @return the branch
     */
    public String getBranch() {
        return branch;
    }

    /**
     * @param branch the branch to set
     */
    public void setBranch(String branch) {
        this.branch = branch;
    }

    /**
     * @return the stateCode
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * @param stateCode the stateCode to set
     */
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }


}

public class ApplicationUtil {

    static String string;
    static String a[] = {"",
            "ONE",
            "TWO",
            "THREE",
            "FOUR",
            "FIVE",
            "SIX",
            "SEVEN",
            "EIGHT",
            "NINE",};
    static String b[] = {
            "HUNDRED",
            "THOUSAND",
            "LAKH",
            "CRORE"
    };
    static String c[] = {"TEN",
            "ELEVEN",
            "TWELVE",
            "THIRTEEN",
            "FOURTEEN",
            "FIFTEEN",
            "SIXTEEN",
            "SEVENTEEN",
            "EIGHTEEN",
            "NINTEEN",};
    static String d[] = {
            "TWENTY",
            "THIRTY",
            "FORTY",
            "FIFTY",
            "SIXTY",
            "SEVENTY",
            "EIGHTY",
            "NINETY"
    };
    private static Map<String, String> stateMap = new HashMap<String, String>();
    private static Map<String, String> branchToSrNo = new HashMap<String, String>();

    static {
        stateMap.put("01", "Jammu and Kashmir");
        stateMap.put("02", "Himachal Pradesh");
        stateMap.put("03", "Punjab");
        stateMap.put("04", "Chandigarh");
        stateMap.put("05", "Uttarakhand");
        stateMap.put("06", "Haryana");
        stateMap.put("07", "Delhi");
        stateMap.put("08", "Rajasthan");
        stateMap.put("09", "Uttar Pradesh");
        stateMap.put("10", "Bihar");
        stateMap.put("11", "Sikkim");
        stateMap.put("12", "Arunachal Pradesh");
        stateMap.put("13", "Nagaland");
        stateMap.put("14", "Manipur");
        stateMap.put("15", "Mizoram");
        stateMap.put("16", "Tripura");
        stateMap.put("17", "Meghalaya");
        stateMap.put("18", "Assam");
        stateMap.put("19", "West Bengal");
        stateMap.put("20", "Jharkhand");
        stateMap.put("21", "Orissa");
        stateMap.put("22", "Chhattisgarh");
        stateMap.put("23", "Madhya Pradesh");
        stateMap.put("24", "Gujarat");
        stateMap.put("25", "Daman and Diu");
        stateMap.put("26", "Dadra and Nagar Haveli");
        stateMap.put("27", "Maharashtra");
        stateMap.put("28", "Andhra Pradesh");
        stateMap.put("29", "Karnataka");
        stateMap.put("30", "Goa");
        stateMap.put("31", "Lakshadweep");
        stateMap.put("32", "Kerala");
        stateMap.put("33", "Tamil Nadu");
        stateMap.put("34", "Puducherry");
        stateMap.put("36", "Telengana");
        stateMap.put("37", "Andhra Pradesh New");
        stateMap.put("97", "Other Territory");
    }

    static {
        branchToSrNo.put("SFP", "MAX_SERIAL_NO_01");
        branchToSrNo.put("SFPM", "MAX_SERIAL_NO_02");
        branchToSrNo.put("SFPC", "MAX_SERIAL_NO_03");
        branchToSrNo.put("SFPK", "MAX_SERIAL_NO_04");
        branchToSrNo.put("SFPG", "MAX_SERIAL_NO_05");
    }

    public static String getStateName(String stateCode) {
        return stateMap.get(stateCode);
    }

    public static String checkForNull(String fieldname) {
        //String value="";
        if (null != fieldname && !"null".equals(fieldname)) {
            return fieldname;
        } else {
            return "";
        }
    }

    public static String dataTableLabel(String s) {
        if (StringUtils.hasText(s)) {
            return "<u><b>" + s + "</b></u>";
        }
        return s;
    }

    public static Object stringreppipe(String tempstr, char sep) {
        String stringperm = null;
        if (tempstr == null) {
            return tempstr;
        }
        stringperm = tempstr.replace('|', sep);

        return stringperm;
    }

    public static String convertNumToWord(int number) {

        int c = 1;
        int rm;
        string = "";
        while (number != 0) {
            switch (c) {
                case 1:
                    rm = number % 100;
                    pass(rm);
                    if (number > 100 && number % 100 != 0) {
                        display(" ");
                    }
                    number /= 100;

                    break;

                case 2:
                    rm = number % 10;
                    if (rm != 0) {
                        display(" ");
                        display(b[0]);
                        display(" ");
                        pass(rm);
                    }
                    number /= 10;
                    break;

                case 3:
                    rm = number % 100;
                    if (rm != 0) {
                        display(" ");
                        display(b[1]);
                        display(" ");
                        pass(rm);
                    }
                    number /= 100;
                    break;

                case 4:
                    rm = number % 100;
                    if (rm != 0) {
                        display(" ");
                        display(b[2]);
                        display(" ");
                        pass(rm);
                    }
                    number /= 100;
                    break;

                case 5:
                    rm = number % 100;
                    if (rm != 0) {
                        display(" ");
                        display(b[3]);
                        display(" ");
                        pass(rm);
                    }
                    number /= 100;
                    break;

            }
            c++;
        }

        return string;
    }

    public static void pass(int number) {
        int rm, q;
        if (number < 10) {
            display(a[number]);
        }

        if (number > 9 && number < 20) {
            display(c[number - 10]);
        }

        if (number > 19) {
            rm = number % 10;
            if (rm == 0) {
                q = number / 10;
                display(d[q - 2]);
            } else {
                q = number / 10;
                display(a[rm]);
                display(" ");
                display(d[q - 2]);
            }
        }
    }

    public static void display(String s) {
        String t;
        t = string;
        string = s;
        string += t;
    }

    public static String ifNullOrBlankReturnEmpty(String s) {
        if (s == null || s.equals("") || s.equalsIgnoreCase("null")) {
            return "";
        }
        return s;
    }

    public static String getBranchSrNoField(String branch) {
        return branchToSrNo.get(branch);
    }

    public static String getBranchStateCode(String branch) {
        return BranchState.valueOf(branch).getStateCode();
    }

    public static String autoString(String prefix, String postfix) {
        String retString = Long.toString(System.currentTimeMillis() % 1000000);

        if (prefix != null) {
            retString = prefix + retString;
        }
        if (postfix != null) {
            retString = retString + postfix;
        }

        return retString;
    }

    public static String getAddressFormat(String address) {

        StringTokenizer st = new StringTokenizer(address, "|");
        String addressformat = "";
        while (st.hasMoreTokens()) {
            addressformat = addressformat + st.nextToken() + "\n";
        }
        return addressformat;
    }

    public String convertDoubleToWord(Double num, String currency, String numeral) {
        if (currency == null) {
            currency = "";
        }
        if (numeral == null) {
            numeral = "";
        }
        int dollars = (int) Math.abs(num);

        double midval = Math.round((num - dollars) * 100.0) / 100.0;
        int cent = (int) Math.floor((midval * 100.00f));
        String s = "";
        if (cent > 0) {
            s = currency + " " + convertNumToWord(dollars) + " and " + convertNumToWord(cent) + " " + getNumeral(currency) + " only";

        } else {
            s = currency + " " + convertNumToWord(dollars) + " only";
        }
        return s.toUpperCase();
    }

    public String getNumeral(String currency) {
        if ("INR".equals(currency)) {
            return "PAISA";
        } else if ("USD".equals(currency)) {
            return "CENTS";
        }


        return "";
    }

    public String convertFloatToWord(float num, String currency, String numeral) {
        if (currency == null) {
            currency = "";
        }
        if (numeral == null) {
            numeral = "";
        }
        int dollars = (int) Math.abs(num);

        double midval = Math.round((num - dollars) * 100.0) / 100.0;
        int cent = (int) Math.floor((midval * 100.00f));
        String s = "";
        if (cent > 0) {
            s = currency + " " + convertNumToWord(dollars) + " and " + convertNumToWord(cent) + " " + getNumeral(currency) + " only";

        } else {
            s = currency + " " + convertNumToWord(dollars) + " only";
        }
        return s.toUpperCase();
    }
}