package com.finops.finance.bean;

import com.finops.bean.AbstractBean;
import com.finops.report.model.ReportBean;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter
public class VoucherBean extends FinanceBean {
    private int jeHdrId;
    private String voucherNo;
    private String jeDate;
    private String hdrAcctName;
    private String lineAcctName;
    private int lineCodeCombinationId;
    private int codeCombinationId;
    private double hdrTotalAmount;
    private int currencyId;
    private String currencyName;
    private String currencyNumeral;
    private double exchangeRate;
    private double lineTotalAmount;
    private String voucherType;
    private List<VoucherBean> aaData;
    private List<VoucherRow> voucherRows = new ArrayList<>();
    private String blNo;
    private String soNo;
    private String invNo;
    private String invDate;
    private String[] hdrIds;
    private String remarks;
    private String address1;
    private String gstinNo;
    private String hdrLineFlag;
    private String chqNo;
    private String chqDate;
    private double enteredDr;
    private double enteredCr;
    private String acctName;
    private String reconcileDate;
    private String branch;
    private String matchingRefNo;
    private int seqNo;
}
