package com.finops.finance.service;

import com.finops.finance.dao.DigitalCopyDAO;
import com.finops.report.model.ReportBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DigitizeService {

    @Autowired
    private DigitalCopyDAO digitalCopyDAO;

    public void saveDigitalCopyRecord(ReportBean reportBean) {
        digitalCopyDAO.saveDigitalCopyRecord(reportBean);
    }

    public List<ReportBean> viewDigitalCopyRecords(ReportBean reportBean) {
        return digitalCopyDAO.viewDigitalCopyRecords(reportBean);
    }

    public void deleteDigitalCopy(ReportBean reportBean) {
        digitalCopyDAO.deleteDigitalCopy(reportBean);
    }
}
