package com.finops.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AbstractBean {

    private String action;
    private String loadingAgent;
    private String yrStartDate;
    private String yrEndDate;
    private int acctYear;
    private int companyId;
    private String companyName;
    private String companyAddress;
    private String companyStateCode;
    private String companyState;
    private boolean administrator;
    private String cutOffDate;
    private String periodStatus;

    private String errMsg;
    private long start;
    private long end;
    private int length;
    private List<String> searchFieldValueList;
    private String createdBy;
    private String userId;
    private String fileserverRootPath;
    private int id;
    private String[] uuids;
    private String uuid;
    private int[] ids;
    private String description;
    private String status;
    private String creationDate;
    private String amendedBy;
    private String amendedDate;
    private String errorMsg;
    private boolean reportRequest;
    private String filterValue;
    private String seaAir;
    private String localForeign;
    private String expImp;
    private String legacyReportPath;
    private String azureEndPoint;
    private String irnEndpoint;
    private String companyGstinNo;
    private String companyTel;
    private String companyFax;
    private String companyEmail;
    private String companyUrl;
    private String companyPan;
    private String companyTan;
    private String note;
    private int iTotalRecords = 10000;
    private int iTotalDisplayRecords = 10000;
}
