package com.finops.finance.bean;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class BillingBean extends FinanceBean{

    private int billId;
    private String billNo;
    private String billDate;
    private String currency;
    private double exchangeRate;
    private int currencyId;
    private String currencyCode;
    private double trxTotal;
    private String taxType;
    private String salesManCode;
}
