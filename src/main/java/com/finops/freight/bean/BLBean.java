package com.finops.freight.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public class BLBean extends SOBean{

    private String totalInWords;
    private String signature;
    private int blAutoSequence;
    private String blIssueDate;
    private String poi;
    private String poiName;
    private String blReleaseDate;
    private String isApproved;
    private Map<String, String> signatureMap;
    private String stateCode;
    private String isSez;
    private String billTo;
    private String billToName;
    private String billToAddress;
    private int partyAcctCode;
    private int destCCId;
    private String invSbNo;
    private String invNo;
    private String actualCbm;
    private String containerList;
    private String weight;
    private String shippingBillNo;
    private String qty;
    private String mblDate;
    private String itemNumber;
    private String doNumber;
    private String icdFactory;
    private String describedDate;
    private Integer freeDays;
    private String igmNo;
    private String igmDate;
    private String theManager;
    private String surveyors;
    private String emptyReturnLocation;
}
