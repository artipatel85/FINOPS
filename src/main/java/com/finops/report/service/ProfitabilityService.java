package com.finops.report.service;

import com.finops.partner.service.PartnerService;
import com.finops.report.dao.ProfitabilityDAO;
import com.finops.report.model.ReportBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProfitabilityService {

    @Autowired
    private ProfitabilityDAO profitabilityDAO;

    @Autowired
    private PartnerService partnerService;

    public List<ReportBean> summary(ReportBean bean, Map<String, String> salesmanMap) {
        return profitabilityDAO.summary(bean, partnerService, salesmanMap);
    }

    public List<ReportBean> detail(ReportBean bean) {
        return profitabilityDAO.detail(bean);
    }
}
