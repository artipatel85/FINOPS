package com.finops.finance.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class BillRow{

    private String seaAir;
    private String localForeign;
    private String expImp;
    private String revExp;
    private String billType;
    private String blNo;
    private String billTo;
    private String billtoName;
    private String billtoContctDtls;
    private double totalTaxable;
    private double totalNonTaxable;
    private String remarks;
    private String salesBy;
    private String createdBy;
    private int billId;
    private String billNo;
    private String billDate;
    private String currency;
    private double exchangeRate;
    private int currencyId;
    private String currencyCode;
    private double trxTotal;
    private double totalTax;
    private double discountAmount;
    private double otherChargesAmount;
    private double totalGST;
    private String taxType;

    private String dueDate;

    private String invSbNo;
    private String invNo;
    private String invDate;
    private String rcm;
    private String mainInvoiceNo;

    private int jeHdrId;
    private String stateCode;
    private List<InvoiceBean> aaData;
    private int partyAcctCode;
    private String taxableNonTaxable;
    private int partyTDSAcctCode;
    private String partyTDSAcctName;
    private String partyTDSDesc;
    private double partyTDSValue;
    private String partyTDSDrCr;

    private double totalTDS;
    private String isSez;
    private String[] trxIds;
    private String soNo;
    private String partyGstin;
    private String placeOfSupply;
    private Map<String, String> signatureMap;
    private String signatureFile;
    private String irn;
    private String signedQrCode;
    private boolean proforma;
    private boolean generateInvoice;
    private String irnAction;
    private String posCode;
    private String jobNumber;
    private String acctName;
    private String accountCode;
    private String codeDesc;
    private String sacCode;
    private String taxable;
    private String nonTax;
    private String rowDrCr;
    private double tax1Per;
    private double tax2Per;
    private double tax3Per;
    private double tax4Per;
    private double tax5Per;
    private String chkBox6;
    private String category;
    private int codeCombinationId;

    private String taxDrCrArr;
    private String taxAcctNameArr;
    private int taxAcctCodeArr;
    private String taxAcctDescArr;
    private double taxPercentageArr;
    private double taxOnValueArr;
    private String errMsg;
    private double amount;
    private String additionalDesc;

    private String actGrossWt;
    private String noOfPkgs;
    private String actUnit;
    private String pod;
    private String pol;
    private String actCbm;
    private String cnee;
    private String shpr;
    private String awbBlDate;
    private String shprName;
    private String cneeName;
    private String contnrNo;
    private String carrierCode;
    private String templateType;

    private double accountedCR;
    private double accountedDR;
    private double amountCR;
    private double amountDR;
    private int cashBank;
    private String headerBlNO;
    private String branch;

}
