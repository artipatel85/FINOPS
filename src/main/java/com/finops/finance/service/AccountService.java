package com.finops.finance.service;

import com.finops.finance.bean.LedgerBean;
import com.finops.finance.dao.AccountDAO;
import com.finops.freight.bean.FinactPropertiesBean;
import com.finops.report.model.ReportBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountDAO accountDAO;

    public List<LedgerBean> ledgerView(LedgerBean ledgerBean) {
        return accountDAO.ledgerView(ledgerBean);
    }

    public LedgerBean retrieveLedger(LedgerBean ledgerBean) {
        LedgerBean lb = accountDAO.retrieveLedger(ledgerBean);
        String lineDrCr = "DR";
        if(lb.getOpeningBalance() < 0){
            lb.setOpeningBalance(lb.getOpeningBalance() * -1);
            lineDrCr = "CR";
        }
        lb.setLineDtrCtr(lineDrCr);
        return lb;
    }

    public List<LedgerBean> taxMasterView(LedgerBean ledgerBean) {
        return accountDAO.taxMasterView(ledgerBean);
    }

    public ReportBean getCurrentBalance(String toString, String param3, String dayMonth) {
        return accountDAO.getCurrentBalance(toString, param3, dayMonth);
    }

    @Transactional
    public void saveLedger(LedgerBean ledgerBean) {
        accountDAO.saveLedger(ledgerBean);
    }

    public LedgerBean retrieveLedgerByName(LedgerBean ledgerBean) {
        return accountDAO.retrieveLedgerByName(ledgerBean);
    }

    @Transactional
    public void updateLedger(LedgerBean ledgerBean) {
        accountDAO.updateParty(ledgerBean);
        accountDAO.balanceMove(ledgerBean);
    }

    @Transactional
    public void balanceTransfer(FinactPropertiesBean finactPropertiesBean) {
        accountDAO.balanceTransfer(finactPropertiesBean);
    }

    public void updateFinactProperties(FinactPropertiesBean finactPropertiesBean) {
        accountDAO.updateFinactProperties(finactPropertiesBean);
    }
}
