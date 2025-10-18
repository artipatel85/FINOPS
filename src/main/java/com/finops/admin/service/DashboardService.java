package com.finops.admin.service;

import com.finops.admin.dao.DashboardDAO;
import com.finops.finance.bean.InvoiceBean;
import com.finops.finance.dao.InvoiceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private DashboardDAO dashboardDAO;

    public List<Integer> getInvoiceList(InvoiceBean invoiceBean) {
        return dashboardDAO.getMonthlyInvoiceData(invoiceBean, "", "");
    }
}
