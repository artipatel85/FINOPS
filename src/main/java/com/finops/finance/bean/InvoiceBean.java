package com.finops.finance.bean;

import com.finops.freight.bean.BLBean;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter @Getter
public class InvoiceBean extends BillingBean{

    private String blNo;
    private String billTo;
    private String billtoName;
    private String billtoContctDtls;
    private BLBean blBean;
    private double totalTaxable;
    private double totalNonTaxable;
    private String remarks;
    private String salesBy;
    private String saveToken;
    private double totalTax;
    private double discountAmount;
    private double otherChargesAmount;
    private int discountAcctCode;
    private String discountAcctName;
    private String discountDesc;
    private double discountPercentage;
    private double discountOnValue;

    private int otherChargesAcctCode;
    private String otherChargesAcctName;
    private String otherChargesDesc;
    private double otherChargesPercentage;
    private double otherChargesOnValue;
    private int templateId;
    private String templateName;
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

    private List<BillRow> billTemplateRows = new ArrayList<>();
    private List<BillRow> billTemplateTaxs = new ArrayList<>();
    private List<BillRow> billTemplateTDS = new ArrayList<>();
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
    private int codeCombinationId;
    private String preparedBy;
    private String branch;
}
