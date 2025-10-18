package com.finops.report.service;

import com.finops.report.dao.ReconciliationDAO;
import com.finops.report.model.ReportBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReconciliationService {

    @Autowired
    private ReconciliationDAO reconciliationDAO;

    public List<ReportBean> reconciliationView(ReportBean bean){
        return reconciliationDAO.reconciliationView(bean);
    }

    public List<ReportBean> reconciliationReportView(ReportBean bean){
        return reconciliationDAO.reconciliationReportView(bean);
    }

    @Transactional
    public int reconcile(ReportBean bean){
        return reconciliationDAO.reconcile(bean);
    }
}
