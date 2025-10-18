package com.finops.partner.model;

import com.finops.bean.AbstractBean;
import com.finops.finance.bean.InvoiceBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter @Getter
public class PartnerBean extends AbstractBean {

    private List<PartnerBean> aaData;
    private String partnerCode;
    private String partnerAccountCode;
    private int partnerAcctCode;
    private String description1;
    private String description2;
    private String address1;
    private String address2;
    private String panNo;
    private String stateCode;
    private String stateName;
    private String gstinNo;
    private String country;
    private String acctName;
    private double creditLimit;
    private int creditPeriod;
    private String city;
    private String tanNo;
    private String cinNo;

    private String zipCode;
    private String countryName;
    private String countryCode;
    private String tel;
    private String telCc;
    private String telAc;
    private String telNo;
    private String fax;
    private String faxCc;
    private String faxAc;
    private String faxNo;
    private String contactPerson;
    private String email;
    private String bankerName;
    private String bankerTelCC;
    private String bankerTelAC;
    private String bankerTelNo;
    private String bankAddress;
    private String bankCity;
    private String bankStateCode;
    private String bankStateName;
    private String bankZipCode;
    private String bankAccountNo;
    private String ifscCode;
    private String swiftCode;
    private String iecNo;
    private String salesManCode;
    private String salesManName;
    private String shipper;
    private String consignee;
    private String coLoader;
    private String carrier;
    private String agent;
    private String creditor;
    private String debitor;
    private String branch;
    private String fullName1;
    private String fullName2;
    private String web;
    private String loadingAgentName;
    private String loadingAgentCode;
    private String destAgentName;
    private String destAgentCode;
    private String display;
    private String clientType;
    private String bpType;
    private String shprType;
    private String cneeType;
    private String agntType;
    private String coloadrType;
    private String cntrctrType;
    private String carrierType;
    private String creditorType;
    private String debtorType;
    private String currencyId;
    private String currencyCode;
    private String serviceTax;
    private String taxNo;
    private String accountNo;
    private String attachment1FileName;
    private String attachment2FileName;
    private String attachment3FileName;
    private String attachment4FileName;
    private MultipartFile attachment1File;
    private MultipartFile attachment2File;
    private MultipartFile attachment3File;
    private MultipartFile attachment4File;
    private String updateAttachments;
    private String codeNo;
    private String serialNo;;
    private boolean replicate;
    private String disablePrint;
    private String kycNote;

    public void setAttachment1FileName(String attachment1FileName){
        if(StringUtils.hasText(attachment1FileName)){
            this.attachment1FileName = attachment1FileName;
        }
    }

}
