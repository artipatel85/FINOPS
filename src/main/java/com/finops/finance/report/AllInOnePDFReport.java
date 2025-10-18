package com.finops.finance.report;

import com.finops.admin.model.LoginBean;
import com.finops.admin.model.PartnerAccount;
import com.finops.admin.model.UserBean;
import com.finops.finance.bean.BillRow;
import com.finops.finance.bean.InvoiceBean;
import com.finops.finance.service.InvoiceService;
import com.finops.freight.bean.BLBean;
import com.finops.partner.model.PartnerBean;
import com.finops.util.ApplicationUtil;
import com.finops.util.Constants;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AllInOnePDFReport extends PDFJDBCConnectionReport {

    private final InvoiceBean invoiceBean;
    private HttpSession session;
    private InvoiceService service;

    public AllInOnePDFReport(String reportPath, InvoiceBean bean, Map params,
                             HttpServletResponse response, ServletContext context,
                             HttpSession session, InvoiceService service) {
        super(reportPath, params, response, context);
        this.invoiceBean = bean;
        this.session = session;
        this.service = service;
        this.jdbcTemplate = service.getJDBCTemplate();
    }

    public void createData() {

        LoginBean loginBean = (LoginBean) session.getAttribute("loginLst");
        service.findInvoiceById(invoiceBean, true);
        Map<String, PartnerAccount> branchDetails = loginBean.getBranchDetails();
        PartnerAccount branch = branchDetails.get(invoiceBean.getLoadingAgent());
        BLBean blBean = new BLBean();
        if(StringUtils.hasText(invoiceBean.getBlNo()) && "REVENUE".equalsIgnoreCase(invoiceBean.getRevExp())){
            blBean = service.getBlData(invoiceBean);
        }
        UserBean user = loginBean.getUserBean();

        this.parameters.put("company", loginBean.getPartnerAccount().getDescription1());
        this.parameters.put("address", ApplicationUtil.getAddressFormat(branchDetails.get(invoiceBean.getBranch()).getAddress1()));
        this.parameters.put("snTitle", "Invoice Serial No:");
        this.parameters.put("panno", loginBean.getPartnerAccount().getPanNo());
        this.parameters.put("tanno", loginBean.getPartnerAccount().getTanNo());
        this.parameters.put("servicetaxno", "");
        this.parameters.put("billto", invoiceBean.getBilltoName());
        this.parameters.put("billtoaddress", invoiceBean.getBilltoContctDtls());
        this.parameters.put("phone", "");
        this.parameters.put("fax", "");
        this.parameters.put("billtophone", "SHPRPHONE");
        this.parameters.put("billtofax", "SHPRFAX");
        this.parameters.put("email", "");
        this.parameters.put("trxno", invoiceBean.getBillNo());
        this.parameters.put("blno", invoiceBean.getBlNo());
        this.parameters.put("bldate", invoiceBean.getAwbBlDate());
        this.parameters.put("sono", invoiceBean.getSoNo());
        this.parameters.put("cbm", invoiceBean.getActCbm());
        //this.parameters.put("gstinno", loginBean.getPartnerAccount().getGstinNo());
        this.parameters.put("state", ApplicationUtil.getStateName(loginBean.getPartnerAccount().getStateCode()));
        this.parameters.put("statecode", loginBean.getPartnerAccount().getStateCode());
        this.parameters.put("pgstinno", invoiceBean.getPartyGstin());
        this.parameters.put("pstate", ApplicationUtil.getStateName(invoiceBean.getStateCode()));
        this.parameters.put("pstatecode", invoiceBean.getStateCode());
        this.parameters.put("invDate", invoiceBean.getInvDate());
        this.parameters.put("placeOfSupply", invoiceBean.getPlaceOfSupply());
        this.parameters.put("trxid", invoiceBean.getBillId());
        this.parameters.put("trxdate", invoiceBean.getBillDate());
        this.parameters.put("duedate", invoiceBean.getDueDate());
        this.parameters.put("jobno", invoiceBean.getJobNumber());
        this.parameters.put("panno", loginBean.getPartnerAccount().getPanNo());
        this.parameters.put("tanno", loginBean.getPartnerAccount().getTanNo());
        this.parameters.put("gstinno", branchDetails.get(invoiceBean.getBranch()).getGstinNo());
        this.parameters.put("createdby", invoiceBean.getCreatedBy());
        this.parameters.put("salesby", invoiceBean.getSalesBy());
        this.parameters.put("trxid", invoiceBean.getId());
        this.parameters.put("currency", invoiceBean.getCurrencyCode());
        this.parameters.put("shpr", invoiceBean.getShprName());
        this.parameters.put("cnee", invoiceBean.getCneeName());
        this.parameters.put("inwords", new ApplicationUtil().convertDoubleToWord(invoiceBean.getTrxTotal(), invoiceBean.getCurrencyCode(), ""));
        this.parameters.put("invNo", invoiceBean.getInvNo());
        this.parameters.put("localforeign", invoiceBean.getLocalForeign());
        this.parameters.put("nontaxable", new BigDecimal(invoiceBean.getTotalNonTaxable()).setScale(2, BigDecimal.ROUND_HALF_UP));
        this.parameters.put("taxable", new BigDecimal(invoiceBean.getTotalTaxable()).setScale(2, BigDecimal.ROUND_HALF_UP));
        this.parameters.put("grandtotal", new BigDecimal(invoiceBean.getTrxTotal()).setScale(2, BigDecimal.ROUND_HALF_UP));
        this.parameters.put("totalTax", new BigDecimal(invoiceBean.getTotalTax()).setScale(2, BigDecimal.ROUND_HALF_UP));
        this.parameters.put("remarks", invoiceBean.getRemarks());
        this.parameters.put("templateType", invoiceBean.getTemplateType());
        this.parameters.put("imagepath", session.getServletContext().getRealPath("/images/shikharLogo.png"));
        this.parameters.put("signedQRCode", invoiceBean.getSignedQrCode());
        this.parameters.put("irn", invoiceBean.getIrn());
        this.parameters.put("mblno", blBean.getMBlNumber());
        this.parameters.put("containerno", invoiceBean.getContnrNo());
        this.parameters.put("pkgs", invoiceBean.getNoOfPkgs());
        this.parameters.put("weight", invoiceBean.getActGrossWt());
        //this.parameters.put("description", vo.getDescription());
        this.parameters.put("destination", blBean.getPodName());
        this.parameters.put("sbno", invoiceBean.getInvSbNo());
//        this.parameters.put("trxdate", vo.getTrxDate());
//        this.parameters.put("duedate", vo.getDueDate());
        this.parameters.put("jobno", invoiceBean.getJobNumber());
//        this.parameters.put("fileno", vo.getFileNo());
        this.parameters.put("yearStartDate", invoiceBean.getYrStartDate());
        this.parameters.put("yearEndDate", invoiceBean.getYrEndDate());
        this.parameters.put("acct_year", invoiceBean.getAcctYear());

        String signatureFile = invoiceBean.getSignatureFile();
        if (!StringUtils.hasText(signatureFile)) {
            signatureFile = "Blank";
        }

        this.parameters.put("signature", session.getServletContext().getRealPath("/images/" + signatureFile + ".png"));

        if ("REVENUE".equalsIgnoreCase(invoiceBean.getRevExp())) {
            if ("LOCAL".equalsIgnoreCase(invoiceBean.getLocalForeign())) {
                this.parameters.put("bankName", branch.getBankerName());
                this.parameters.put("bankAddress", branch.getBankerAddress());
                this.parameters.put("bankAccount", branch.getBankAcNo());
                this.parameters.put("bankCodeType", "IFSC Code:");
                this.parameters.put("bankCodeValue", branch.getIfscCode());
            } else {
                this.parameters.put("bankName", user.getBankName());
                this.parameters.put("bankAddress", user.getBankAddress());
                this.parameters.put("bankAccount", user.getBankAcNo());
                this.parameters.put("bankCodeType", "SWIFT Code:");
                this.parameters.put("bankCodeValue", user.getSwiftCode());
            }
        }

        if ("SEA".equals(invoiceBean.getSeaAir())) {
            this.parameters.put("blNoLabel", "BL No.");
            this.parameters.put("contlabel", "Cont. No :");
            this.parameters.put("bldatelabel", "BL Date");
            this.parameters.put("mbllabel", "MBL No");
            if ("IMPORT".equals(invoiceBean.getExpImp())) {
                this.parameters.put("mbllabel", "POL");
                this.parameters.put("bldatelabel", "POD");
                this.parameters.put("mblno", invoiceBean.getPol());
                this.parameters.put("bldate", invoiceBean.getPod());
            }
        } else {
            this.parameters.put("blNoLabel", "HAWB No.");
            this.parameters.put("contlabel", "M.AWB No :");
            this.parameters.put("bldatelabel", "HAWB Date");
            this.parameters.put("mbllabel", "M.AWB No");

        }


        List<BillRow> billRows = invoiceBean.getBillTemplateTaxs();
        int i = 1;
        for(BillRow br : billRows){
            this.parameters.put("taxamount" + i, new BigDecimal(br.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
            i++;
        }

        this.parameters.put("InvoiceBeanDS", new JRBeanCollectionDataSource(invoiceBean.getBillTemplateRows()));

        if ("CREDITNOTE".equals(invoiceBean.getBillType())) {
            this.parameters.put("reporttitle", "CREDIT NOTE");
            this.parameters.put("snTitle", "Creditnote Serial No:");
            this.reportTitle = "/reports/Test.jrxml";
            if(StringUtils.hasText(invoiceBean.getIrn())){
                this.reportTitle = "/reports/EInvoiceNew.jrxml";
            }
        } else if ("EXEMPTED".equals(invoiceBean.getTemplateType())) {
            this.parameters.put("reporttitle", "BILL OF SUPPLY");
        } else if ("SEZ".equals(invoiceBean.getTemplateType())) {
            this.parameters.put("reporttitle", "EXPORT INVOICE");
        } else if ("DEBITNOTE".equals(invoiceBean.getBillType())) {
            this.parameters.put("reporttitle", "DEBIT NOTE");
            this.parameters.put("snTitle", "Debitnote Serial No:");
        } else if (Constants.BILL_OF_SUPPLY.equals(invoiceBean.getBillType())) {
            this.parameters.put("reporttitle", "BILL OF SUPPLY");
            this.parameters.put("snTitle", "Bill of Supply Serial No:");
        }
        else if (invoiceBean.isProforma()) {
            this.parameters.put("reporttitle", "PROFORMA INVOICE");
        }
        else {
            this.parameters.put("reporttitle", "TAX INVOICE");
        }


        this.parameters.put("note", invoiceBean.getNote());
    }

}
