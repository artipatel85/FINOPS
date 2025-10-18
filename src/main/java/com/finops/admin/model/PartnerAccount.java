package com.finops.admin.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PartnerAccount {

    private int companyId;
    private String panNo;
    private String tanNo;
    private String gstinNo;
    private String stateCode;
    private String bankAcNo;
    private String bankName;
    private String bankerAddress;
    private String bankerName;
    private String ifscCode;
    private String zipCode;
    private String description1;
    private String address1;
    private String partnerCode;
    private String swiftCode;
}
