package com.finops.finance.util;

public class LockFactory {

    private static final Object sfp = new Object();
    private static final Object sfpm = new Object();
    private static final Object sfpk = new Object();

    public static Object getLockObject(String branch){
        if("SFP".equals(branch)){
            return sfp;
        }
        else if("SFPM".equals(branch)){
            return sfpm;
        }
        else{
            return sfpk;
        }
    }
}
