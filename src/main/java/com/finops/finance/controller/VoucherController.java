package com.finops.finance.controller;

import com.finops.controller.AbstractController;
import com.finops.finance.bean.VoucherBean;
import com.finops.finance.bean.VoucherRow;
import com.finops.finance.report.ReimbursementPDFReport;
import com.finops.finance.report.VoucherPDFReport;
import com.finops.finance.service.VoucherService;
import com.finops.finance.util.LockFactory;
import com.finops.report.AbstractReport;
import com.finops.util.Constants;
import com.finops.util.DateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class VoucherController extends AbstractController {

    @Autowired
    private VoucherService voucherService;

    @RequestMapping("/voucher.fin")
    public ModelAndView voucherSearch(@ModelAttribute("voucherBean") VoucherBean voucherBean,
                                      @RequestParam("param") String param) {
        voucherBean.setVoucherType(param);
        return new ModelAndView("finact/VoucherSearch", "command", voucherBean);
    }

    @RequestMapping(value = "receiptGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String voucherGSON(@ModelAttribute("voucherBean") VoucherBean voucherBean, HttpServletRequest request,
                       @RequestParam("param") String param) {
        setSessionData(voucherBean, request.getSession());
        voucherBean.setStart(Integer.parseInt(request.getParameter("start")));
        voucherBean.setLength(Integer.parseInt(request.getParameter("length")));
        voucherBean.setSearchFieldValueList(getSearchFieldValues(request, 11));
        List<VoucherBean> list = voucherService.findVouchersByPage(voucherBean, param);

        voucherBean.setAaData(list);
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(voucherBean);
    }

    @RequestMapping(value = "/removeVoucherRow.fin", method = RequestMethod.POST)
    public ModelAndView removeVoucherRow(@ModelAttribute("voucherBean") VoucherBean voucherBean,
                                         @RequestParam("index") int index) {
        List<VoucherRow> voucherRows = voucherBean.getVoucherRows();
        Iterator<VoucherRow> itr = voucherRows.iterator();
        int i = 0;
        while (itr.hasNext()) {
            VoucherRow row = itr.next();
            if (i == (index - 1)) {
                itr.remove();
            }
            i++;
        }
        String url = getReturnURL(voucherBean.getVoucherType());
        return new ModelAndView(url, "command", voucherBean);
    }

    @RequestMapping(value = "/retrieveVoucherRow.fin")
    public ModelAndView retrieve(@ModelAttribute("voucherBean") VoucherBean voucherBean,
                                 @RequestParam("hdrId") int hdrId,
                                 @RequestParam("voucherType") String voucherType, HttpSession session) {
        voucherBean.setJeHdrId(hdrId);
        setSessionData(voucherBean, session);
        boolean hdrOnlyFlag = false;
        VoucherBean vb = null;
        voucherBean.setVoucherType(voucherType);
        if ("JOURNAL".equalsIgnoreCase(voucherBean.getVoucherType())) {
            vb = voucherService.retrieveVoucherRecord(voucherBean);
        } else {
            vb = voucherService.retrieveVoucherRecord(voucherBean, false);
        }
        String uri = getReturnURL(vb.getVoucherType());
        BeanUtils.copyProperties(vb, voucherBean);
        return new ModelAndView(uri, "command", voucherBean);
    }


    @RequestMapping(value = "/saveVoucher.fin", method = RequestMethod.POST)
    public synchronized ModelAndView saveVoucher(@ModelAttribute("voucherBean") VoucherBean voucherBean,
                                    HttpSession session) throws ParseException, SQLException {
        setSessionData(voucherBean, session);
        if (voucherService.validate(voucherBean)) {
            if (voucherService.isVoucherDateValid(voucherBean)
                    && voucherService.shouldSaveVoucher(voucherBean)
                    && voucherService.isVoucherTotalValid(voucherBean)) {

                    voucherService.save(voucherBean);

                return new ModelAndView("finact/VoucherSearch", "command", voucherBean);
            }
        }
        String uri = getReturnURL(voucherBean.getVoucherType());
        return new ModelAndView(uri, "command", voucherBean);
    }

    @RequestMapping(value = "/voucherPDFReport.fin", method = RequestMethod.GET)
    public ModelAndView voucherPDFReport(HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam("voucherType") String voucherType,
                                         @RequestParam("trxId") int trxId) throws Exception {
        VoucherBean voucherBean = new VoucherBean();
        setSessionData(voucherBean, request.getSession());
        voucherBean.setJeHdrId(trxId);
        String fileName = getFileName(voucherType);
        voucherBean.setVoucherType(voucherType);

        AbstractReport report = new VoucherPDFReport(fileName, voucherBean, null,
                response, request.getServletContext(), request.getSession(), voucherService);
        report.generateReport();
        return new ModelAndView("sales", "command", voucherBean);
    }

    private String getFileName(String voucherType) {
        if ("RECEIPT".equals(voucherType)) {
            return "/reports/receipt_voucher.jrxml";
        } else if ("PAYMENT".equals(voucherType)) {
            return "/reports/Payment_Voucher.jrxml";
        } else if ("JOURNAL".equals(voucherType)) {
            return "/reports/journalVoucher.jrxml";
        } else if ("CONTRA".equals(voucherType)) {
            return "/reports/contraVoucher.jrxml";
        } else if ("CREDIT".equals(voucherType)) {
            return "/reports/Credit_note.jrxml";
        }
        return null;
    }


    @RequestMapping("/createVoucher.fin")
    public ModelAndView createVoucher(@ModelAttribute("voucherBean") VoucherBean voucherBean,
                                      @RequestParam("param") String param) {
        voucherBean.setVoucherNo("AUTO");
        voucherBean.setJeDate(DateUtil.getSystemDate());
        int length = 5;
        if("JOURNAL".equalsIgnoreCase(param)){
            length = 2;
        }
        else if(Constants.CONTRA.equalsIgnoreCase(param)){
            length = 1;
        }
        voucherBean.setVoucherRows(dummyVoucherList(length));
        voucherBean.setCurrencyId(1006);
        voucherBean.setCurrencyName("INR");
        voucherBean.setExchangeRate(1.00);
        voucherBean.setVoucherType(param);
        String url = getReturnURL(param);
        return new ModelAndView(url, "command", voucherBean);
    }

    private String getReturnURL(String param) {
        if ("RECEIPT".equalsIgnoreCase(param)) {
            return "finact/ReceiptVoucher";
        } else if ("PAYMENT".equalsIgnoreCase(param)) {
            return "finact/PaymentVoucher";
        } else if ("CONTRA".equalsIgnoreCase(param)) {
            return "finact/ContraVoucher";
        } else if ("JOURNAL".equalsIgnoreCase(param)) {
            return "finact/JournalVoucher";
        }
        return null;
    }

    private List<VoucherRow> dummyVoucherList(int length) {
        List<VoucherRow> voucherRows = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            voucherRows.add(new VoucherRow());
        }
        return voucherRows;
    }

    @RequestMapping(value = "/addJournalRow.fin", method = RequestMethod.POST)
    public ModelAndView addJournalRow(@ModelAttribute("voucherBean") VoucherBean voucherBean,
                                      HttpSession session) throws ParseException, SQLException {
        setSessionData(voucherBean, session);
        List<VoucherRow> voucherRows = voucherBean.getVoucherRows();
        if (validateJ(voucherBean)) {
            voucherRows.add(new VoucherRow());
        }
        return new ModelAndView("finact/JournalVoucher", "command", voucherBean);
    }

    @RequestMapping(value = "/removeJournalRow.fin", method = RequestMethod.POST)
    public ModelAndView removeJournalRow(@ModelAttribute("voucherBean") VoucherBean voucherBean,
                                         @RequestParam("index") int index) {
        List<VoucherRow> voucherRows = voucherBean.getVoucherRows();
        Iterator<VoucherRow> itr = voucherRows.iterator();
        int i = 1;
        while (itr.hasNext()) {
            VoucherRow row = itr.next();
            if (i == (index)) {
                itr.remove();
            }
            i++;
        }
        return new ModelAndView("finact/JournalVoucher", "command", voucherBean);
    }

    private boolean validateJ(VoucherBean voucherBean) throws ParseException, SQLException {
        List<VoucherRow> voucherRows = voucherBean.getVoucherRows();

        if (StringUtils.hasText(voucherBean.getInvNo()) && voucherService.isDuplicateInvNo(voucherBean)) {
            voucherBean.setErrMsg("Duplicate Journal Invoice No.");
            return false;
        }

        for (VoucherRow vr : voucherRows) {
            if (!StringUtils.hasText(vr.getRowAcct()) || vr.getRowAcctId() == 0 || !isSoBlValid(voucherBean, vr)) {
                vr.setErrMsg("Invalid Row.");
                return false;
            }
        }
        return true;
    }

    private boolean isSoBlValid(VoucherBean bean, VoucherRow vr) {
        if (StringUtils.hasText(vr.getSo()) &&
                (!StringUtils.hasText(bean.getSeaAir()) || !StringUtils.hasText(bean.getExpImp()))) {
            bean.setErrMsg("SEA/AIR and EXPORT/IMPORT are required!");
            return false;
        }
//        if(StringUtils.hasText(vr.getSo())){
//            List<ReportBean> result =  autoCompleteService.getSoPopup(vr.getSo(),
//                    bean.getLoadingAgent(), bean.getYrStartDate(), bean.getSeaAir(), bean.getExpImp());
//            if(result == null || result.size() != 1){
//                bean.setErrMsg("Invalid So Number!");
//                return false;
//            }
//            ReportBean rb = result.get(0);
//            if(!vr.getBl().equals(ApplicationUtil.ifNullOrBlankReturnEmpty(rb.getParam2()))){
//                bean.setErrMsg("So Number and Bl Number do not match!");
//                return false;
//            }
//        }
        return true;
    }

    @RequestMapping(value = "/saveJournalVoucher.fin", method = RequestMethod.POST)
    public synchronized ModelAndView saveJournalVoucher(@ModelAttribute("voucherBean") VoucherBean voucherBean,
                                           HttpSession session) throws ParseException, SQLException {
        setSessionData(voucherBean, session);
        if (validateJ(voucherBean)) {
            if (isVoucherDateValid(voucherBean) && shouldSaveVoucher(voucherBean)
                    && isVoucherTotalValid(voucherBean)) {
                voucherService.saveJournalVoucher(voucherBean);

                return new ModelAndView("finact/VoucherSearch", "command", voucherBean);
            }
        }
        return new ModelAndView("finact/JournalVoucher", "command", voucherBean);
    }

    private boolean isVoucherDateValid(VoucherBean voucherBean) throws ParseException {
        if (!DateUtil.isDateInLimit(voucherBean.getJeDate(), voucherBean.getYrStartDate(),
                voucherBean.getYrEndDate())) {
            voucherBean.setErrMsg("Voucher Date is outside the range!");
            return false;
        }
        if (voucherBean.getJeHdrId() > 0 &&
                (!voucherBean.getVoucherNo().contains
                        (voucherBean.getJeDate().replaceAll("-", "").substring(4)))) {
            voucherBean.setErrMsg("Voucher Date and Voucher Number do not match!");
            return false;
        }
        return true;
    }

    private boolean isVoucherTotalValid(VoucherBean voucherBean) throws ParseException {
        if (voucherBean.getHdrTotalAmount() != voucherBean.getLineTotalAmount()) {
            voucherBean.setErrMsg("Invalid Voucher Total!");
            return false;

        }
        return true;
    }

    private boolean shouldSaveVoucher(VoucherBean bean) throws SQLException {
        if (bean.isAdministrator()) {
            return true;
        }
        try {
            if (!DateUtil.isDateInLimit(bean.getJeDate(), bean.getCutOffDate(), bean.getYrEndDate())) {
                bean.setErrMsg("Voucher can not be updated. Cutoffdate " + bean.getCutOffDate() + "!");
                return false;
            }
        } catch (ParseException ex) {
            bean.setErrMsg("Voucher can not be updated. Cutoffdate " + bean.getCutOffDate() + "!");
            return false;
        }

        if (bean.getJeHdrId() > 0) {
            VoucherBean rb = voucherService.getVoucherData(bean);
            String type = bean.getVoucherType();
            if (rb == null) {
                bean.setErrMsg("Voucher can not be updated. Cutoffdate " + bean.getCutOffDate() + "!");
                return false;
            } else if (!rb.getBranch().equals(bean.getLoadingAgent())) {
                bean.setErrMsg("Voucher can not be updated. Voucher Branch " + rb.getLoadingAgent() + "!");
                return false;
            } else if (StringUtils.hasText(rb.getMatchingRefNo())) {
                bean.setErrMsg("Matching is alreday done for this voucher. Matching string is " + rb.getMatchingRefNo() + "!");
                return false;
            }

            if ("PAYMENT".equals(type) || "RECEIPT".equals(type)) {
                if (StringUtils.hasText(rb.getReconcileDate())) {
                    bean.setErrMsg("Voucher can not be updated. Reconcile date " + rb.getReconcileDate() + "!");
                    return false;
                }
            }
        }
        return true;
    }

    @RequestMapping(value = "/addVoucherRow.fin", method = RequestMethod.POST)
    public ModelAndView addVoucherRow(@ModelAttribute("voucherBean") VoucherBean voucherBean) throws ParseException {
        List<VoucherRow> voucherRows = voucherBean.getVoucherRows();
        if (validate(voucherBean)) {
            voucherRows.add(new VoucherRow());
        }
        String url = getReturnURL(voucherBean.getVoucherType());
        return new ModelAndView(url, "command", voucherBean);
    }

    private boolean validate(VoucherBean voucherBean) throws ParseException {
        List<VoucherRow> voucherRows = voucherBean.getVoucherRows();

        for (VoucherRow vr : voucherRows) {
            if (!StringUtils.hasText(vr.getRowAcct()) || vr.getRowAcctId() == 0 || vr.getRowAmt() == 0) {
                vr.setErrMsg("Invalid Row.");
                return false;
            }
        }
        return true;
    }

    @RequestMapping("/reimbursementPDFReport.fin")
    public ModelAndView reimbursementPDFReport(@ModelAttribute("voucherBean") VoucherBean voucherBean,
                                               HttpServletRequest request, HttpServletResponse response,
                                               @RequestParam("jeHdrId") int jeHdrId) throws Exception {
        HttpSession session = request.getSession();
        setSessionData(voucherBean, session);
        String reportPath = "/reports/ReimbursementVoucher.jrxml";
        voucherBean.setJeHdrId(jeHdrId);
        //voucherBean.setAddress1(ApplicationUtil.stringreppipe(branchData.getCompanyAddress(), ','));
        AbstractReport report = new ReimbursementPDFReport(reportPath, null, response, voucherBean,
                servletContext, session,voucherService);
        report.generateReport();
        return new ModelAndView("sales", "command", voucherBean);
    }

    @RequestMapping(value = "/deleteVoucher.fin", method = RequestMethod.POST)
    public ModelAndView deleteVoucher(@ModelAttribute("voucherBean") VoucherBean voucherBean,
                                      HttpSession session, @RequestParam("param") String param) throws ParseException {
        setSessionData(voucherBean, session);
        voucherBean.setVoucherType(param);
        voucherService.deleteVoucher(voucherBean);
        return new ModelAndView("finact/VoucherSearch", "command", voucherBean);
    }
}
