package com.finops.service;

import com.finops.cron.CronBean;
import com.finops.dao.CronJobDAO;
import com.finops.report.model.ReportBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CronService {

    @Autowired
    private CronJobDAO cronJobDAO;

    @Transactional
    public void performCron(ReportBean reportBean){
        cronJobDAO.updateOpenBalancesToZero(reportBean);
        cronJobDAO.resetDiffInOpenBalance(reportBean);
        cronJobDAO.updateOpenDayBalancesToZero(reportBean);
        cronJobDAO.reApproveAllTheVouchers(reportBean);
    }
}
