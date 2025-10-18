package com.finops.report.service;

import com.finops.admin.service.AdminService;
import com.finops.aop.MeasureTime;
import com.finops.partner.model.PartnerBean;
import com.finops.partner.service.PartnerService;
import com.finops.report.dao.SalesRegisterDAO;
import com.finops.report.model.ReportBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SalesRegisterService {

    @Autowired
    private SalesRegisterDAO salesRegisterDAO;

    @Autowired
    private PartnerService ps;

    @Autowired
    protected AdminService adminService;

    @MeasureTime
    public List<ReportBean> gstinReport(ReportBean bean, String type) {

        List<ReportBean> reportBeanList = salesRegisterDAO.gstinReport(bean, type);
        Map<String, String> stateList = adminService.fetchAllStates("states");
//        for(ReportBean rb : reportBeanList){
//            PartnerBean pb = ps.getPartnerData(rb.getParam21(),
//                    bean.getParam3(), ps);
//            rb.setParam2(pb.getGstinNo());
//            rb.setParam6(pb.getStateCode()+"-"+stateList.get(pb.getStateCode()));
//        }
        return reportBeanList;
    }

    public List<ReportBean> expense(ReportBean vo) {
        return salesRegisterDAO.expense(vo);
    }

    public List<ReportBean> invoice(ReportBean vo) {
        return salesRegisterDAO.invoice(vo);
    }
}
