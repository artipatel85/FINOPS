/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.finance.report;

import com.finops.admin.model.LoginBean;
import com.finops.admin.model.PartnerAccount;
import com.finops.finance.bean.VoucherBean;
import com.finops.finance.service.VoucherService;
import com.finops.util.ApplicationUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

/**
 * @author bhaumik
 */
public class ReimbursementPDFReport extends PDFReport {
    private final VoucherBean bean;
    private final HttpSession session;
    private VoucherService voucherService;

    public ReimbursementPDFReport(String reportTitle, Map params, HttpServletResponse response,
                                  VoucherBean bean, ServletContext context, HttpSession session,
                                  VoucherService voucherService) {
        super(reportTitle, params, response, context);
        this.bean = bean;
        this.session = session;
        this.voucherService = voucherService;
    }

    @Override
    public void createData() {
        voucherService.reimbursementVoucher(bean);
        LoginBean loginBean = (LoginBean) session.getAttribute("loginLst");
        Map<String, PartnerAccount> branchDetails = loginBean.getBranchDetails();
        PartnerAccount branch = branchDetails.get(bean.getLoadingAgent());
        this.data = bean.getVoucherRows();
        this.parameters.put("address", ApplicationUtil.getAddressFormat(bean.getAddress1()));
        this.parameters.put("acctName", bean.getHdrAcctName());
        this.parameters.put("companyName", bean.getCompanyName());
        this.parameters.put("companyAddress",
                ApplicationUtil.stringreppipe(branch.getAddress1(), ','));
        this.parameters.put("totalAmount", bean.getHdrTotalAmount());
        this.parameters.put("voucherNo", bean.getVoucherNo());
        this.parameters.put("jeDate", bean.getJeDate());
        this.parameters.put("remarks", bean.getRemarks());
        this.parameters.put("gstinNo", bean.getGstinNo());
        this.parameters.put("companyGstinNo", bean.getCompanyGstinNo());
        this.parameters.put("shipperInvoiceNo", bean.getInvNo());
        this.parameters.put("imagepath", context.getRealPath("/images/shikharLogo.png"));
        this.parameters.put("note", "Company Note");
        this.parameters.put("createdby", bean.getUserId());
    }

}
