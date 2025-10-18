package com.finops.freight.bean;

import com.finops.bean.AbstractBean;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SIBean extends BLBean {
    private String commercialInvoice;
    private String packingList;
    private String exportLicense;
    private String certOfOrigin;
    private String formA;
    private String others;
    private String airFreight;
    private String terminalHandling;
    private String cartage;
    private String handlingDoc;
    private String packing;
    private String otherPaymentTerms;
    private String flightType;
    private String currencyId;
    private String currencyCode;
    private String valueForCustom;
    private String valueForCarriage;
    private String amountOfInsurance;
}
