package com.finops.finance.service;

import com.finops.finance.dao.FinanceAccountCronDAO;
import com.finops.freight.dao.FreightCronDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinanceAccountCronService {

    @Autowired
    private FinanceAccountCronDAO financeAccountCronDAO;

    public void financeCron(String trxDate){
        financeAccountCronDAO.financeCron(trxDate);
    }
}
