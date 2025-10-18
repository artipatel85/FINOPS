package com.finops.finance.bean;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class VoucherRow {
    private String rowDrCr;
    private String rowAcct;
    private String rowParentId;
    private String rowRem;
    private String rowChq;
    private String rowChqDt;
    private double  rowAmt;
    private String errMsg;
    private int rowAcctId;
    private String so;
    private String bl;
    private String sac;
    private double debit;
    private double credit;
    private int srNo;
}
