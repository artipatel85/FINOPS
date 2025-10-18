package com.finops.finance.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

@lombok.Getter
@lombok.Setter
public class EInvoiceRequest {

    private String loadingAgent;
    private int customerTrxId;
    private int companyId;
    private int acctYear;
    private String userId;
    private String startDate;
    private String endDate;
    private String documentType;
    private String irn;
    private String cancelRemarks;
    private String gstin;
    private String typeOfSupply;
    @JsonIgnore
    private String message;
}
