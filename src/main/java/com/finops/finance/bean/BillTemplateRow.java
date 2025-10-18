package com.finops.finance.bean;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BillTemplateRow{

    private String accountname;
    private String accountcode;
    private String des;
    private String sac;
    private String tax;
    private String nontax;
    private String rowDrCr;
    private double tax1PerArr;
    private double tax2PerArr;
    private double tax3PerArr;
    private double tax4PerArr;
    private double tax5PerArr;
    private String chkBox6;

    private String taxDrCrArr;
    private String taxAcctNameArr;
    private int taxAcctCodeArr;
    private String taxAcctDescArr;
    private double taxPercentageArr;
    private double taxOnValueArr;
    private String errMsg;
    private double amount;
    private String additionalDesc;
    private String soNo;
    private String blNo;
}
