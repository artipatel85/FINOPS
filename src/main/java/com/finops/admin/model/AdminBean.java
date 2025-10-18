package com.finops.admin.model;

import com.finops.bean.AbstractBean;
import com.finops.report.model.ReportBean;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class AdminBean extends AbstractBean {

    private String password;
    private String branch;
    private String role;
    private String cityName;
    private String portCode;
    private String description2;
    private String countryName;
    private String countryCode;
    private String display;
    private String sizeCode;
    private String sizeName;
    private String unitCode;
    private String unitName;
    private String isFinUser;
    private String currencyId;
    private String currencyCode;
    private double exchangeRate;
    private String code;
    private String name;
    private String designation;
    private String emailId;

    //user
    private String userProfileId;
    private String confirmPassword;
    private String remarks;
    private String isFinanceUser;
    private String userRole;
    private String telCc;
    private String telAc;
    private String telNo;
    private String faxCc;
    private String faxAc;
    private String faxNo;
    private String zone;

    private List<ReportBean> reconList;
    private Map<String,String> roleList;
    private List<FormBean> priviledgeList = new ArrayList<>();
}
