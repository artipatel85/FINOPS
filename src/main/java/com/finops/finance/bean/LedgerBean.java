package com.finops.finance.bean;

import com.finops.bean.AbstractBean;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class LedgerBean extends AbstractBean {
    private String acctName;
    private String parentName;
    private String acctTypeName;
    private int parentId;
    private String acctTypeId;
    private String costFlag;
    private String billFlag;
    private String depRate;
    private double openDr;
    private double openCr;
    private String ccFlag;
    private String bwFlag;

    // party details attributes
    private int codeCombinationId;
    private String prtAddress;
    private String prtPinCode;
    private String prtPhone;
    private String prtFax;
    private String prtEmail;
    private String prtWebsite;
    private String prtContactPerson;
    private String bankerName;
    private String bankerAddress;
    private String currencyId;
    private String creditDays;
    private String itNo;
    private String stNo;
    private String gstinNo;
    private String stateCode;
    private String stateName;
    private String currencyName;
    private String convRate;
    private String agentName;
    private String agent;
    private String attribute1;

    private String telCC;
    private String telAC;
    private String telNo;
    private String faxCC;
    private String faxAC;
    private String faxNo;
    private String city;
    private String country;
    private String prtAddress2;
    private String tanNo;
    private String panNo;
    private double exchangeRate;
    private int acctID;
    private double openingBalance;
    private String lineDtrCtr;
    private String zipcode;
    private String countrycode;
    private List<LedgerBean> aaData;
    private String acctFlag;
    private String plFlag;
    private String slFlag;
    private String taxFlag;

    private String isPartition;
    private double percentage;
    private String sub1Desc;
    private String sub2Desc;
    private String sub3Desc;
    private double sub1Per;
    private double sub2Per;
    private double sub3Per;
    private double defaultPercentage;
    private String taxHead;
    private String creditorType;

}
