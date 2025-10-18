package com.finops.freight.bean;

import com.finops.bean.AbstractBean;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class SailingScheduleBean extends AbstractBean {
    private int salingScheduleId;
    private int salingScheduleSeqId;
    private String connectSailNumber;
    private String oldVesselCode;
    private String vesselCode;
    private String vesselCodeCB;
    private String oldVoyageCode;
    private String voyageCode;
    private String voyageCodeCB;
    private String operatorCode;
    private String operatorName;
    private String partnerCode;
    private String description;
    private String remark;
    private boolean publicFlag;
    private String createdBy;
    private String creationDate;
    private String amendedBy;
    private String amendedDate;
    private List<FreightRow> polList = new ArrayList<>();
    private List<FreightRow> podList = new ArrayList<>();
    private String connectByPod;
    private String connectByETA;
    private String connectByPol;
    private String connectByETD;
    private String connectById;
    private String connectByValue;
}
