package com.finops.finance.controller;

import com.finact.Inv;
import com.finops.admin.cache.AdminCache;
import com.finops.admin.service.AdminService;
import com.finops.controller.AbstractController;
import com.finops.finance.bean.*;
import com.finops.finance.report.AllInOnePDFReport;
import com.finops.finance.report.BillingXLSReport;
import com.finops.finance.report.PendingBLXLSReport;
import com.finops.finance.service.BillTemplateService;
import com.finops.finance.service.InvoiceService;
import com.finops.finance.service.VoucherService;
import com.finops.finance.util.FinanceConstants;
import com.finops.finance.util.LockFactory;
import com.finops.finance.util.RestClient;
import com.finops.freight.bean.BLBean;
import com.finops.freight.service.BLService;
import com.finops.freight.service.HawbService;
import com.finops.freight.service.SIService;
import com.finops.freight.service.SOService;
import com.finops.report.AbstractReport;
import com.finops.report.model.ReportBean;
import com.finops.util.ApplicationUtil;
import com.finops.util.Constants;
import com.finops.util.DateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class InvoiceController extends AbstractController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private BLService blService;

    @Autowired
    private SOService soService;

    @Autowired
    private SIService siService;

    @Autowired
    private HawbService hawbService;

    @Autowired
    private BillTemplateService billTemplateService;

    @Autowired
    private VoucherService voucherService;

    @RequestMapping(value = "/viewBillList.fin")
    public ModelAndView view(@ModelAttribute("billingBean") InvoiceBean billingBean,
                             @RequestParam("PARAM") String revExp,
                             HttpSession session) {
        billingBean.setRevExp(revExp);
        if (Constants.PROFORMA.equals(revExp)) {
            billingBean.setRevExp(Constants.REVENUE);
            billingBean.setProforma(true);
        }
        if (Constants.BILL_OF_SUPPLY.equals(revExp)) {
            billingBean.setBillType(revExp);
        }
        session.setAttribute("viewBillList", billingBean);
        return new ModelAndView("billing/BillInvoiceSearch", "command", billingBean);
    }

    @RequestMapping(value = "/viewBillListGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String viewBillList(@ModelAttribute("billingBean") InvoiceBean billingBean,
                        @RequestParam("PARAM") String revExp,
                        HttpServletRequest request) {
        billingBean = (InvoiceBean) request.getSession().getAttribute("viewBillList");
        setSessionData(billingBean, request.getSession());
        billingBean.setStart(Integer.parseInt(request.getParameter("start")));
        billingBean.setLength(Integer.parseInt(request.getParameter("length")));
        billingBean.setSearchFieldValueList(getSearchFieldValues(request, 11));
        billingBean.setRevExp(revExp);
        List<InvoiceBean> list = invoiceService.findInvoicesByPage(billingBean);

        billingBean.setAaData(list);
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(billingBean);
    }

    @RequestMapping(value = "/retrieveBill.fin")
    public ModelAndView retrieve(@ModelAttribute("billingBean") InvoiceBean billingBean,
                                 HttpSession session, @RequestParam("billId") int billId,
                                 @RequestParam("param") String param) {
        setSessionData(billingBean, session);
        billingBean.setBillId(billId);
        billingBean.setBillType(param);
        billingBean = invoiceService.findInvoiceById(billingBean, false);
        session.setAttribute("billingInvoice", billingBean);
        billingBean.setSignatureMap(adminService.getSignatures(billingBean.getLoadingAgent()+"INVOICE"));

        return modelAndView(billingBean);
    }

    @RequestMapping(value = "/saveBill.fin")
    public ModelAndView save(@ModelAttribute("billingBean") InvoiceBean billingBean,
                             HttpSession session) throws SQLException {
        InvoiceBean sessionBean = (InvoiceBean) session.getAttribute("billingInvoice");

        setBillingBean(sessionBean, billingBean);
        setSessionData(billingBean, session);
        String saveTokenInSession = (String) session.getAttribute("saveTokenInSession");
        billingBean.setSignatureMap(adminService.getSignatures(billingBean.getLoadingAgent()+ "INVOICE"));


        if ("REVENUE".equalsIgnoreCase(sessionBean.getRevExp())) {
            if(!StringUtils.hasText(billingBean.getCurrencyCode())){
                billingBean.setErrMsg("Invalid Currency Code!");
                return new ModelAndView("billing/BillInvoiceDetail", "command", billingBean);
            }
            billingBean.setBlBean(sessionBean.getBlBean());
            billingBean.setBlNo(sessionBean.getBlNo());
        } else if (invoiceService.isDuplicateInvNo(billingBean)) {
            billingBean.setErrMsg("Duplicate Creditor Invoice No.");
            return new ModelAndView("billing/BillExpenseDetail", "command", billingBean);
        }

        if (shouldSaveVoucher(billingBean)) {
            if ("AUTO".equalsIgnoreCase(billingBean.getBillNo()) && !ApplicationUtil.ifNullOrBlankReturnEmpty(saveTokenInSession).equals(billingBean.getSaveToken())) {
                billingBean.setErrMsg("Duplicate Entry. Record Already created.");
                return modelAndView(billingBean);
            }
            synchronized (LockFactory.getLockObject(billingBean.getLoadingAgent())) {
                invoiceService.save(billingBean);
                session.removeAttribute("saveTokenInSession");
            }
        }

        return modelAndView(billingBean);

    }

    private ModelAndView modelAndView(InvoiceBean bean){
        if ("REVENUE".equalsIgnoreCase(bean.getRevExp())) {
            if(bean.isProforma()){
                return new ModelAndView("billing/BillProformaDetail", "command", bean);
            }
            return new ModelAndView("billing/BillInvoiceDetail", "command", bean);
        } else {
            return new ModelAndView("billing/BillExpenseDetail", "command", bean);
        }
    }

    private boolean isSoBlValid(InvoiceBean bean, BillRow vr) {
        if (StringUtils.hasText(vr.getSoNo()) &&
                (!StringUtils.hasText(bean.getSeaAir()) || !StringUtils.hasText(bean.getExpImp()))) {
            bean.setErrMsg("SEA/AIR and EXPORT/IMPORT are required!");
            return false;
        }
        if (StringUtils.hasText(vr.getSoNo()) && vr.getAmount() != 0) {
            List<ReportBean> result = null;
            if("SEA".equalsIgnoreCase(bean.getSeaAir())) {
                result = soService.retrieve(vr.getSoNo(),
                        bean.getLoadingAgent(), bean.getYrStartDate(), bean.getSeaAir(), bean.getExpImp());
            }
            else{
                result = siService.retrieve(vr.getSoNo(),
                        bean.getLoadingAgent(), bean.getYrStartDate(), bean.getSeaAir(), bean.getExpImp());
            }
            if (result == null || result.size() != 1) {
                bean.setErrMsg("Invalid So Number!");
                return false;
            }
            ReportBean rb = result.get(0);
            if (!vr.getBlNo().equals(ApplicationUtil.ifNullOrBlankReturnEmpty(rb.getParam2()))) {
                bean.setErrMsg("So Number and Bl Number do not match!");
                return false;
            }
        }
        return true;
    }



    private boolean shouldSaveVoucher(InvoiceBean bean) throws SQLException {
        if(!"O".equalsIgnoreCase(bean.getPeriodStatus())){
            bean.setErrMsg("Account Year is closed!!");
            return false;
        }
        if (bean.getRevExp().equalsIgnoreCase("EXPENSE")) {
            for (BillRow btr : bean.getBillTemplateRows()) {
                if (!isSoBlValid(bean, btr)) {
                    btr.setErrMsg("expenseErrRow");
                    return false;
                }
            }
        }
        if (bean.isAdministrator()) {
            return true;
        }
        try {
            if (!DateUtil.isDateInLimit(bean.getBillDate(), bean.getYrStartDate(),
                    bean.getYrEndDate())) {
                bean.setErrMsg(bean.getBillType() + " Date is outside the range!");
                return false;
            }
            if (!DateUtil.isDateInLimit(bean.getBillDate(), bean.getCutOffDate(), bean.getYrEndDate())) {
                bean.setErrMsg(bean.getBillType() + " can not be updated. Cutoffdate " + bean.getCutOffDate() + "!");
                return false;
            }
            ReportBean rb = voucherService.getVoucherDataForBill(bean);
            if (rb != null) {
                if (rb.getParam3() != null && !rb.getParam3().equals(bean.getLoadingAgent())) {
                    bean.setErrMsg(bean.getBillType() + " can not be updated. Voucher Branch " + rb.getParam3() + "!");
                    return false;
                } else if (StringUtils.hasText(rb.getParam4())) {
                    bean.setErrMsg("Matching is alreday done for this " + bean.getBillType() + ". Matching string is " + rb.getParam4() + "!");
                    return false;
                }
            }


        } catch (ParseException ex) {
            bean.setErrMsg("Voucher can not be updated. Cutoffdate " + bean.getCutOffDate() + "!");
            return false;
        }
        return true;
    }

    public void deleteBillingVoucher(String trxIds, InvoiceBean bean){

    }

    @RequestMapping(value = "/billingPDFReport.fin", method = RequestMethod.GET)
    public ModelAndView billingPDFReport(HttpServletRequest request, HttpServletResponse response,
                                       @RequestParam("PARAM") String param,
                                       @RequestParam("trxid") int trxid,
                                         @RequestParam("irn") String irn) throws Exception {
        InvoiceBean invoiceBean = new InvoiceBean();
        setSessionData(invoiceBean, request.getSession());
        String reportPath = "/reports/Billing.jrxml";
        if("EXPENSE".equals(param)){
            reportPath = "/reports/journalVoucherExpenseNew.jrxml";
        }
        else if(StringUtils.hasText(irn)){
            reportPath = "/reports/Billing.jrxml";
        }
        invoiceBean.setBillId(trxid);
        AbstractReport report = new AllInOnePDFReport(reportPath, invoiceBean, null,
                response, request.getServletContext(), request.getSession(), invoiceService);
        report.generateReport();
        return new ModelAndView("sales", "command", invoiceBean);
    }

    @RequestMapping(value = "/expensePDFReport.fin", method = RequestMethod.GET)
    public ModelAndView expensePDFReport(HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam("PARAM") String param,
                                         @RequestParam("trxid") int trxid) throws Exception {
        InvoiceBean invoiceBean = new InvoiceBean();
        setSessionData(invoiceBean, request.getSession());
        String reportPath = "/reports/journalVoucherExpenseNew.jrxml";
        invoiceBean.setBillId(trxid);
        AbstractReport report = new AllInOnePDFReport(reportPath, invoiceBean, null,
                response, request.getServletContext(), request.getSession(), invoiceService);
        report.generateReport();
        return new ModelAndView("sales", "command", invoiceBean);
    }


    private void setBillingBean(InvoiceBean sessionBean, InvoiceBean billingBean) {
        billingBean.setExpImp(sessionBean.getExpImp());
        billingBean.setRevExp(sessionBean.getRevExp());
        billingBean.setBillType(sessionBean.getBillType());
        billingBean.setSeaAir(sessionBean.getSeaAir());
        billingBean.setStateCode(sessionBean.getStateCode());
        billingBean.setLocalForeign(sessionBean.getLocalForeign());
        billingBean.setTemplateId(sessionBean.getTemplateId());
        billingBean.setPartyAcctCode(sessionBean.getPartyAcctCode());
        billingBean.setTemplateType(sessionBean.getTemplateType());
        billingBean.setBillTo(sessionBean.getBillTo());
        billingBean.setBilltoName(sessionBean.getBilltoName());
        billingBean.setBilltoContctDtls(sessionBean.getBilltoContctDtls());
        billingBean.setBlBean(sessionBean.getBlBean());
    }

    @RequestMapping(value = "/loadBillingDataA.fin")
    public ModelAndView loadBillingDataA(@ModelAttribute("billingBean") InvoiceBean billingBean,
                                          HttpSession session) {
        //billingBean.setBillType("INVOICE");
        setSessionData(billingBean, session);
        session.setAttribute("billingInvoice", billingBean);
        return new ModelAndView("billing/BillInvoiceLF", "command", billingBean);
    }

    @RequestMapping(value = "/loadBillingData1.fin")
    public ModelAndView loadBillingData1(@ModelAttribute("invoiceBean") InvoiceBean billingBean,
                                         HttpSession session, HttpServletRequest request) throws IllegalAccessException, InvocationTargetException {
        String tOrN = billingBean.getTaxableNonTaxable();
        String param = request.getParameter("action");
        InvoiceBean sessionBean = (InvoiceBean) session.getAttribute("billingInvoice");
        sessionBean.setLocalForeign(param);
        setSessionData(sessionBean, session);
        BLBean blData = sessionBean.getBlBean();
        if (Constants.LOCAL.equals(param) && blData != null) {
            if (sessionBean.getCompanyStateCode() != null && blData.getStateCode() != null) {
                if (Constants.YES.equalsIgnoreCase(blData.getIsSez())) {
                    sessionBean.setTemplateType(Constants.SEZ);
                } else if (Constants.NO.equals(tOrN) || Constants.BILL_OF_SUPPLY.equals(sessionBean.getBillType())) {
                    sessionBean.setTemplateType(Constants.EXEMPTED);
                } else if (sessionBean.getCompanyStateCode().equalsIgnoreCase(blData.getStateCode())) {
                    sessionBean.setTemplateType(Constants.SGST);
                } else {
                    sessionBean.setTemplateType(Constants.IGST);
                }
            }
            sessionBean.setBillTo(blData.getBillTo());
            sessionBean.setBilltoName(blData.getBillToName());
            sessionBean.setBilltoContctDtls(blData.getBillToAddress());
            sessionBean.setPartyAcctCode(blData.getPartyAcctCode());
            sessionBean.setStateCode(blData.getStateCode());
        } else if (blData != null) {
            if (Constants.EXPORT.equals(sessionBean.getExpImp())) {
                sessionBean.setBillTo(blData.getDestinationAgentCode());
                sessionBean.setBilltoName(blData.getDestinationAgentName());
                sessionBean.setBilltoContctDtls(blData.getDestinationAgentContactDetails());
            } else {
                sessionBean.setBillTo(blData.getLoadingAgentCode());
                sessionBean.setBilltoName(blData.getLoadingAgentName());
                sessionBean.setBilltoContctDtls(blData.getLoadingAgentContactDetails());
            }
            sessionBean.setPartyAcctCode(blData.getDestCCId());
            if ("T".equals(tOrN)) {
                sessionBean.setTemplateType(Constants.IGST);
            } else {
                sessionBean.setTemplateType(Constants.SEZ);
            }
        }
        BeanUtils.copyProperties(billingBean, sessionBean);
        session.setAttribute("billingInvoice", billingBean);
        if (Constants.REVENUE.equals(billingBean.getRevExp())) {
            return new ModelAndView("billing/BillInvoice", "command", billingBean);
        } else {
            return new ModelAndView("billing/BillExpense", "command", billingBean);
        }
    }

    @RequestMapping(value = "/loadBillingData.fin")
    public ModelAndView loadBillingData(@ModelAttribute("invoiceBean") InvoiceBean invoiceBean,
                                        @RequestParam("seaAir") String seaAir, @RequestParam("expImp") String expImp,
                                        @RequestParam("blNo") String blNo, @RequestParam("PARAM") String revExp,
                                        @RequestParam("proforma") boolean proforma, @RequestParam("billType") String billType,
                                        HttpSession session) {
        invoiceBean.setSeaAir(seaAir);
        invoiceBean.setExpImp(expImp);
        invoiceBean.setRevExp(revExp);
        invoiceBean.setBillType(billType);
        invoiceBean.setProforma(proforma);
        invoiceBean.setBlNo(blNo);
        setSessionData(invoiceBean, session);
        BLBean blData = invoiceService.getBlData(invoiceBean);

        invoiceBean.setBlBean(blData);
        session.setAttribute("billingInvoice", invoiceBean);
        return new ModelAndView("billing/BillInvoiceLF", "command", invoiceBean);
    }

    @RequestMapping(value = "/bulkCreateIRN.fin")
    public ModelAndView bulkCreateIRN(@ModelAttribute("eInvoiceRequest") EInvoiceRequest eInvoiceRequest,
                                      HttpSession session) {
        return new ModelAndView("billing/BulkCreate", "command", eInvoiceRequest);
    }

    @RequestMapping("/performBulkCreateIRN.fin")
    public ModelAndView performBulkCreateIRN(@ModelAttribute("eInvoiceRequest") EInvoiceRequest eInvoiceRequest,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        InvoiceBean bean = new InvoiceBean(); //Dummy to populate session data
        setSessionData(bean, session);

        if (!DateUtil.isDateInLimit(eInvoiceRequest.getStartDate(), bean.getYrStartDate(), bean.getYrEndDate())
                || !DateUtil.isDateInLimit(eInvoiceRequest.getEndDate(), bean.getYrStartDate(), bean.getYrEndDate())) {
            eInvoiceRequest.setMessage("Start Date and End Date must be in the current financial year.");
            return new ModelAndView("billing/BulkCreate", "command", eInvoiceRequest);
        }

        RestClient<EInvoiceRequest, EInvoiceResponse> restClient = new RestClient<>(bean.getIrnEndpoint()+"/irn/create/bulk", HttpMethod.POST);
        //eInvoiceRequest.setLoadingAgent(bean.getLoadingAgent());
        eInvoiceRequest.setAcctYear(bean.getAcctYear());
        eInvoiceRequest.setCompanyId(bean.getCompanyId());
        eInvoiceRequest.setUserId(bean.getUserId());
        eInvoiceRequest.setGstin(bean.getCompanyGstinNo());
        ResponseEntity<EInvoiceResponse> responseEntity = restClient.execute(eInvoiceRequest, EInvoiceResponse.class, getHeaders());
        EInvoiceResponse eInvoiceResponse = responseEntity.getBody();
        eInvoiceRequest.setMessage(eInvoiceResponse.getMessage());
        return new ModelAndView("billing/BulkCreate", "command", eInvoiceRequest);
    }

    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @RequestMapping("/pendingBL.fin")
    public ModelAndView pendingBL(@ModelAttribute("reportbean") ReportBean reportbean) {
        //billingDAO.pendingBL(reportbean);
        return new ModelAndView("finact/PendingBL", "command", reportbean);
    }

    @RequestMapping("/pendingBLXLS.fin")
    public ModelAndView pendingBLXLS(@ModelAttribute("reportbean") ReportBean reportbean,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        AbstractReport report = new PendingBLXLSReport(reportbean, response, invoiceService);
        report.generateReport();
        return new ModelAndView("sales", "command", reportbean);
    }

    @RequestMapping(value = "/billListGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String billList(@ModelAttribute("invoiceBean") InvoiceBean billingBean,
                    HttpServletRequest request, @RequestParam("blNo") String blNo, @RequestParam("revExp") String revExp,
                    @RequestParam("proforma") boolean proforma, @RequestParam("billType") String billType) {
        //billingBean = (BillingBean)request.getSession().getAttribute("viewBillList");
        setSessionData(billingBean, request.getSession());
        billingBean.setStart(Integer.parseInt(request.getParameter("start")));
        billingBean.setLength(Integer.parseInt(request.getParameter("length")));
        billingBean.setSearchFieldValueList(getSearchFieldValues(request, 11));
        billingBean.setRevExp(revExp);
        billingBean.setBlNo(blNo);
        billingBean.setBillType(billType);
        billingBean.setProforma(proforma);
        List<InvoiceBean> list = invoiceService.findInvoicesByPage(billingBean);

        billingBean.setAaData(list);
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(billingBean);
    }

    @RequestMapping(value = "/loadBillingData2.fin")
    public ModelAndView loadBillingData2(@ModelAttribute("billingBean") InvoiceBean billingBean,
                                         HttpSession session) {
        super.setSessionData(billingBean, session);
        InvoiceBean sessionBean = (InvoiceBean) session.getAttribute("billingInvoice");

        if("REVENUE".equals(billingBean.getRevExp()) &&
                !validatePartner(billingBean.getBillTo(), billingBean.getBilltoName(), billingBean, true)){
            billingBean.setErrMsg("Invalid Billing Party Data!");
            return new ModelAndView("billing/BillInvoice", "command", billingBean);
        }

        if (sessionBean.getBlBean() != null && "REVENUE".equalsIgnoreCase(sessionBean.getRevExp())) {
            BLBean blData = invoiceService.getBlData(billingBean);
            sessionBean.setBlBean(blData);
            billingBean.setBlBean(sessionBean.getBlBean());
            billingBean.setInvSbNo(sessionBean.getBlBean().getInvSbNo());
            billingBean.setInvNo(sessionBean.getBlBean().getInvNo());
            billingBean.setActCbm(sessionBean.getBlBean().getActualCbm());
            billingBean.setActGrossWt(sessionBean.getBlBean().getWeight());
            billingBean.setContnrNo(sessionBean.getBlBean().getContainerList());
            billingBean.setShpr(sessionBean.getBlBean().getShipper());
            billingBean.setShprName(sessionBean.getBlBean().getShipperName());
            billingBean.setCnee(sessionBean.getBlBean().getConsignee());
            billingBean.setCneeName(sessionBean.getBlBean().getConsigneeName());
            billingBean.setAwbBlDate(sessionBean.getBlBean().getBlIssueDate());
            billingBean.setNoOfPkgs(sessionBean.getBlBean().getQty());
            billingBean.setInvSbNo(sessionBean.getBlBean().getShippingBillNo());
            billingBean.setPol(sessionBean.getBlBean().getPol());
            billingBean.setPod(sessionBean.getBlBean().getPod());
            billingBean.setJobNumber(sessionBean.getBlBean().getJobNumber());
            billingBean.setCarrierCode(sessionBean.getBlBean().getCarrierCode());
            billingBean.setSalesBy(sessionBean.getBlBean().getSalesBy());
        }

        billingBean.setRevExp(sessionBean.getRevExp());

        billingBean.setPreparedBy(billingBean.getUserId());
        billingBean.setStateCode(sessionBean.getStateCode());
        billingBean.setProforma(sessionBean.isProforma());
        billingBean.setBillDate(DateUtil.getSystemDate());
        billingBean.setBillNo("AUTO");
        if ("LOCAL".equalsIgnoreCase(billingBean.getLocalForeign())) {
            billingBean.setCurrencyId(1006);
            billingBean.setCurrencyCode("INR");
            billingBean.setExchangeRate(1.00);
        }
        //billingBean = sessionBean;
        setSessionData(billingBean, session);
        billTemplateService.fetchBillTemplateByParams(billingBean);
        String saveTokenValue = String.valueOf(System.currentTimeMillis());
        billingBean.setSaveToken(saveTokenValue);
        session.setAttribute("billingInvoice", billingBean);
        session.setAttribute("saveTokenInSession", saveTokenValue);
        if ("REVENUE".equals(billingBean.getRevExp())) {
            billingBean.setSignatureMap(adminService.getSignatures(billingBean.getLoadingAgent()+ "INVOICE"));
            return new ModelAndView(getRevenueURI(billingBean.isProforma()), "command", billingBean);
        } else {
            List<BillRow> billTemplateTDS = new ArrayList<>();
            for (int k = 0; k < 3; k++) {
                BillRow row = new BillRow();
                billTemplateTDS.add(row);
            }
            billingBean.setBillTemplateTDS(billTemplateTDS);
            return new ModelAndView("billing/BillExpenseDetail", "command", billingBean);
        }
    }

    private String getRevenueURI(boolean isProforma) {
        return isProforma ? "billing/BillProformaDetail" : "billing/BillInvoiceDetail";
    }

    @RequestMapping(value="/createIRN.fin")
    public ModelAndView createIRN(@ModelAttribute("billingBean") InvoiceBean billingBean,
                                  HttpSession session) throws Exception {
        setSessionData(billingBean, session);
        RestClient<EInvoiceRequest, EInvoiceResponse> restClient = new RestClient<>(billingBean.getIrnEndpoint()+"/irn/create", HttpMethod.POST);
        EInvoiceRequest eInvoiceRequest = new EInvoiceRequest();
        eInvoiceRequest.setCustomerTrxId(billingBean.getBillId());
        eInvoiceRequest.setLoadingAgent(billingBean.getLoadingAgent());
        eInvoiceRequest.setAcctYear(billingBean.getAcctYear());
        eInvoiceRequest.setCompanyId(billingBean.getCompanyId());
        eInvoiceRequest.setUserId(billingBean.getUserId());
        eInvoiceRequest.setGstin(billingBean.getCompanyGstinNo());
        ResponseEntity<EInvoiceResponse> responseEntity = restClient.execute(eInvoiceRequest, EInvoiceResponse.class, getHeaders());
        billingBean.setSignatureMap(adminService.getSignatures(billingBean.getLoadingAgent()+"INVOICE"));

        InvoiceBean sessionBean = (InvoiceBean) session.getAttribute("billingInvoice");
        billingBean.setBlBean(sessionBean.getBlBean());
        billingBean.setBlNo(sessionBean.getBlNo());
        setBillingBean(sessionBean, billingBean);
        setSessionData(billingBean, session);
        EInvoiceResponse eInvoiceResponse = responseEntity.getBody();

        billingBean.setErrMsg(eInvoiceResponse.getMessage());
        if (StringUtils.hasText(eInvoiceResponse.getIrn())) {
            System.out.println("IRN Transaction Successful");
            billingBean.setIrn(eInvoiceResponse.getIrn());
        }

        return new ModelAndView("billing/BillInvoiceDetail", "command", billingBean);
    }

    @RequestMapping(value = "/deleteInvoice.fin", method = RequestMethod.POST)
    public ModelAndView deleteVoucher(@ModelAttribute("billingBean") InvoiceBean billingBean,
                                      HttpSession session,
                                      @RequestParam("PARAM") String param) {
        setSessionData(billingBean, session);
        invoiceService.deleteInvoice(billingBean);
        billingBean.setRevExp(param);
        return new ModelAndView("billing/BillInvoiceSearch", "command", billingBean);
    }

    @RequestMapping("/billingReportXLS.fin")
    public ModelAndView gstinReport(@ModelAttribute("billingBean") InvoiceBean billingBean,
                                    HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam("param") String param) throws Exception {
        billingBean.setRevExp(param);
        setSessionData(billingBean, request.getSession());
        AbstractReport report = new BillingXLSReport(response, billingBean, invoiceService);
        report.generateReport();
        return new ModelAndView("sales", "command", billingBean);
    }
}
