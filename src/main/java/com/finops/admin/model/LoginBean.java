package com.finops.admin.model;

import com.finops.finance.bean.PeriodBean;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class LoginBean {

    private String userId;
    private String password;
    private UserBean userBean;
    private PartnerAccount partnerAccount;
    private Map<Integer, FormBean> formBeanMap;
    private PeriodBean periodBean;
    private Map<String, PartnerAccount> branchDetails;
    private Map<String, String> finactProperties;

}
