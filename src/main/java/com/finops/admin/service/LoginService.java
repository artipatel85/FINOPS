package com.finops.admin.service;

import com.finops.admin.dao.PartnerAccountDAO;
import com.finops.admin.dao.UserDAO;
import com.finops.admin.dao.RoleFormAccessDAO;
import com.finops.admin.model.FormBean;
import com.finops.admin.model.PartnerAccount;
import com.finops.admin.model.UserBean;
import com.finops.finance.bean.PeriodBean;
import com.finops.finance.dao.PeriodDAO;
import com.finops.freight.bean.FinactPropertiesBean;
import com.finops.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service("loginService")
public class LoginService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PartnerAccountDAO partnerAccountDAO;

    @Autowired
    private RoleFormAccessDAO roleFormAccessDAO;

    @Autowired
    private PeriodDAO periodDAO;

    public UserBean login(UserBean bean){
        return userDAO.getUser(bean);
    }

    public List<PartnerAccount> getBranchDetails(String branch){
        return partnerAccountDAO.getBranchDetails(branch);
    }

    public List<FormBean> getRoleFormAccess(String roleName){
        return roleFormAccessDAO.fetchUserPrivilegeMap(roleName, true);
    }

    public Map<Integer, FormBean> getRoleFormAccessMap(String roleName){
        List<FormBean> formBeans = this.getRoleFormAccess(roleName);
        return roleFormAccessDAO.fetchUserPrivilegeMap(formBeans);
    }

    public PeriodBean getCurrentPeriod(int companyId){
        int currentAcctYear = DateUtil.getCurrentAcctYear();
        return periodDAO.getPeriod(currentAcctYear, companyId);
    }

    public void retrieveFinactProperties(FinactPropertiesBean bean ) {
        Properties props = userDAO.getFinactCache();
        bean.setReportPath(props.getProperty("ReportPath"));
        bean.setCutOffDate(props.getProperty("CUT_OFF_DATE"));
    }


}
