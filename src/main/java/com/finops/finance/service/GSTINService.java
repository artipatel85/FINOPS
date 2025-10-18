package com.finops.finance.service;

import com.finact.gstin.credit.Data;
import com.finops.finance.dao.GSTINDAO;
import com.finops.report.model.ReportBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GSTINService {

    @Autowired
    private GSTINDAO gstindao;


    @Transactional
    public void saveCreditDetails(String param4, String s) {
        gstindao.saveCreditDetails(param4, s);
    }

    @Transactional
    public void match(String loadingAgent, String period, String gstin) {
        gstindao.match(loadingAgent, period, gstin);
    }

    public List<Data> listOfPeriods(String loadingAgent) {
        return gstindao.listOfPeriods(loadingAgent);
    }

    public List<ReportBean> retrieveMappedRecords(String gstin, ReportBean reportBean) {
        return gstindao.retrieveMappedRecords(gstin, reportBean);
    }
}
