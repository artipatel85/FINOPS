/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.finance.report;


import com.finops.finance.bean.InvoiceBean;
import com.finops.finance.service.InvoiceService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chiragkhetani
 */
public class BillingXLSReport extends CommonXLSReport {

    private InvoiceBean bean;
    private InvoiceService invoiceService;
    
    public BillingXLSReport(HttpServletResponse response, InvoiceBean bean, InvoiceService invoiceService) {
        super(response, "Invoice");
        this.bean = bean;
        this.invoiceService = invoiceService;
    }

    @Override
    public List<Object[]> getData() {
        bean.setLength(1000000000);
        List<InvoiceBean> dataList = invoiceService.findInvoicesByPage(bean);
        List<Object[]> arrList = new ArrayList<Object[]>();
        for (InvoiceBean bean : dataList) {
            Object[] lines = new Object[]{bean.getBillNo(), bean.getBillDate(), bean.getDueDate(), bean.getSeaAir(), bean.getExpImp()
                ,bean.getLocalForeign(), ("EXPENSE".equals(bean.getRevExp())) ? bean.getInvSbNo():bean.getInvNo(),
                    bean.getTrxTotal(), bean.getBilltoName()};
            arrList.add(lines);
        }
        return arrList;
    }

    @Override
    public Object[] getHeaders() {
        return new Object[]{};
    }
}
