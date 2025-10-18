package com.finops.report.service;

import com.finops.report.dao.OutstandingDAO;
import com.finops.report.dao.OutstandingNewDAO;
import com.finops.report.model.ReportBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutstandingService{

    @Autowired
    private OutstandingDAO outstandingDAO;

    @Autowired
    private OutstandingNewDAO outstandingNewDAO;

    public List<ReportBean> receivable(ReportBean bean) {
        return outstandingDAO.receivable(bean);
    }

    public List<ReportBean> receivable(ReportBean bean, int id) {
        return outstandingNewDAO.receivable(bean);
    }

    public List<ReportBean> getAgeingInvoicewise(ReportBean bean) {
        return outstandingDAO.getAgeingInvoicewise(bean);
    }

    public List<ReportBean> receivable2(ReportBean bean) {
        return outstandingDAO.receivable2(bean);
    }

    public List<ReportBean> receivable3(ReportBean bean) {
        return outstandingDAO.receivable3(bean);
    }
}
