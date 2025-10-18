package com.finops.report.controller;

import com.finops.controller.AbstractController;
import com.finops.finance.bean.LedgerBean;
import com.finops.finance.report.*;
import com.finops.finance.service.AccountService;
import com.finops.report.AbstractReport;
import com.finops.report.model.ReportBean;
import com.finops.report.service.TrialService;
import com.finops.util.DateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TrialController extends AbstractController {

    @Autowired
    private TrialService trialService;

    @Autowired
    private AccountService accountService;

    @RequestMapping("/Trial.fin")
    public ModelAndView trialBalance(@ModelAttribute("reportBean") ReportBean reportBean,
                                  HttpSession session) {
        setSessionData(reportBean, session);
        reportBean.setParam1(reportBean.getYrEndDate());
        return new ModelAndView("finact/Trial", "command", reportBean);
    }

    @RequestMapping(value = "/TrialGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String trialGson(@ModelAttribute("reportBean") ReportBean reportBean, HttpSession session,
                     HttpServletRequest request) {
        reportBean.setStart(Integer.parseInt(request.getParameter("start")));
        reportBean.setLength(Integer.parseInt(request.getParameter("length")));
        reportBean.setSearchFieldValueList(getSearchFieldValues(request, 9));
        setSessionData(reportBean, session);
        reportBean.setParam1(reportBean.getYrEndDate());
        List<ReportBean> list = trialService.findTrialRecordsByPage(reportBean);
        reportBean.setAaData(list);
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(reportBean);
    }

    @RequestMapping("/Monthly.fin")
    public ModelAndView monthly(@ModelAttribute("reportBean") ReportBean reportBean,
                                @RequestParam("id") String id, HttpSession session,
                                @RequestParam("opn") String opn, @RequestParam("acctName") String acctName) {
        reportBean.setParam1(id);
        reportBean.setParam2(opn);
        LedgerBean lb = new LedgerBean();
        lb.setCodeCombinationId(Integer.parseInt(id));
        setSessionData(lb, session);
        lb = accountService.retrieveLedger(lb);
        reportBean.setParam3(lb.getAcctName());
        //reportBean.setParam3(acctName);
        session.setAttribute("monthly", reportBean);
        return new ModelAndView("finact/Monthly", "command", reportBean);
    }

    @RequestMapping(value = "/MonthlyGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody String monthlyGson(@ModelAttribute("reportBean") ReportBean reportBean, HttpSession session) {
        reportBean = (ReportBean)session.getAttribute("monthly");
        setSessionData(reportBean, session);
        List<ReportBean> list = trialService.monthlySummaryData(reportBean);
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }


    @RequestMapping("/Trasactions.fin")
    public ModelAndView transactionData(@ModelAttribute("reportBean") ReportBean reportBean,
                                        HttpSession session, @RequestParam("month") int month,
            @RequestParam("openingBalance") String openingBalance){
        ReportBean sessionBean = (ReportBean)session.getAttribute("monthly");
        if(sessionBean != null){
            reportBean.setParam3(sessionBean.getParam3());
            sessionBean.setParam4(reportBean.getParam4());
            sessionBean.setParam5(reportBean.getParam5());
            sessionBean.setParam6(reportBean.getParam6());
        }
        reportBean = sessionBean;
        reportBean.setIntparam1(month);
        reportBean.setParam10(openingBalance);
        setSessionData(reportBean, session);
        trialService.setupMonthlyData(reportBean);
        session.setAttribute("monthly",reportBean);
        //reportBean.setParam7(Long.toString(System.currentTimeMillis()));
        return new ModelAndView("finact/Transactions", "command", reportBean);
    }

    @RequestMapping(value = "/TransGSON.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody String transactionGson(@ModelAttribute("reportBean") ReportBean reportBean,
                                                HttpSession session, HttpServletRequest request) {
        reportBean = (ReportBean)session.getAttribute("monthly");
        reportBean.setStart(Integer.parseInt(request.getParameter("start")));
        reportBean.setLength(Integer.parseInt(request.getParameter("length")));
        reportBean.setSearchFieldValueList(getSearchFieldValues(request, 11));
        setSessionData(reportBean, session);
        List<ReportBean> list = trialService.findTransactionsByPage(reportBean);
        reportBean.setAaData(list);
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(reportBean);
    }

    @RequestMapping("/TransactionsPDFReport.fin")
    public ModelAndView transactionsPDFReport(@ModelAttribute("reportBean") ReportBean reportBean,
                                              HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        reportBean = (ReportBean)session.getAttribute("monthly");
        setSessionData(reportBean, request.getSession());
        String reportPath = "/reports/Transactions.jrxml";
        AbstractReport report = new TransactionsPDFReport(reportPath, null, response, reportBean,
                servletContext, trialService);
        report.generateReport();
        return new ModelAndView("sales", "command", reportBean);
    }

    @RequestMapping("/TransactionsXLSReport.fin")
    public ModelAndView transactionsXLSReport(@ModelAttribute("reportBean") ReportBean reportBean,
                                              HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        ReportBean sessionBean = (ReportBean)session.getAttribute("monthly");
        reportBean.setParam9(sessionBean.getParam9());
        reportBean.setParam1(sessionBean.getParam1());
        setSessionData(reportBean, request.getSession());
        AbstractReport report = new TransactionsXLSReport(reportBean,response, trialService);
        report.generateReport();
        return new ModelAndView("sales", "command", reportBean);
    }

    @RequestMapping("/LedgerMatch.fin")
    public ModelAndView transactionData(@ModelAttribute("reportBean") ReportBean reportBean,
                                        HttpSession session) {
        setSessionData(reportBean, session);
        trialService.ledgerMatch(reportBean);
        reportBean.setParam7(Long.toString(System.currentTimeMillis()));
        return new ModelAndView("finact/Transactions", "command", reportBean);
    }

    @RequestMapping("/PaymentAdvisePDFReport.fin")
    public ModelAndView paymentAdvisePDFReport(@ModelAttribute("reportBean") ReportBean reportBean,
                                               HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        ReportBean sessionBean = (ReportBean)session.getAttribute("monthly");
        reportBean.setParam1(sessionBean.getParam1());
        setSessionData(reportBean, request.getSession());
        String reportPath = "/reports/PaymentAdvise.jrxml";
        AbstractReport report = new PaymentAdvisePDFReport(reportPath, null, response, reportBean,
                servletContext, trialService);
        report.generateReport();
        return new ModelAndView("sales", "command", reportBean);
    }

    @RequestMapping("/trialXLS.fin")
    public ModelAndView partywiseSummary(@ModelAttribute("reportBean") ReportBean reportBean, HttpSession session, HttpServletResponse response) throws Exception {
        setSessionData(reportBean, session);
        AbstractReport report = new TrialXLSReport(reportBean, response, trialService);
        report.generateReport();
        return new ModelAndView("sales", "command", reportBean);
    }

    @RequestMapping("/trialPDF.fin")
    public ModelAndView partywiseSummaryPDF(@ModelAttribute("reportBean") ReportBean reportBean, HttpSession session,
                                            HttpServletResponse response, HttpServletRequest request) throws Exception {
        //reportBean = (ReportBean)session.getAttribute("monthly");
        setSessionData(reportBean, request.getSession());
        String reportPath = "/reports/TrialBalance.jrxml";
        Map paramMap = new HashMap();
        paramMap.put("asOnDate", reportBean.getParam1());
        paramMap.put("printDate", DateUtil.getSystemDate());
        AbstractReport report = new TrialPDFReport(reportPath, paramMap, response, reportBean,
                servletContext, trialService);
        report.generateReport();
        return new ModelAndView("sales", "command", reportBean);
    }

    @RequestMapping("/TDS.fin")
    public ModelAndView tds(@ModelAttribute("reportBean") ReportBean reportBean,
                            HttpSession session, @RequestParam("id") int id) {
        setSessionData(reportBean, session);
        trialService.setupMonthlyData(reportBean);
        reportBean.setIntparam2(id);
        session.setAttribute("tdsRegister",reportBean);
        return new ModelAndView("finact/TDSRegister", "command", reportBean);
    }

    @RequestMapping(value = "/TDSGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String tdsGson(@ModelAttribute("reportBean") ReportBean reportBean, HttpSession session,
                   HttpServletRequest request) {
        reportBean = (ReportBean)session.getAttribute("tdsRegister");
        reportBean.setStart(Integer.parseInt(request.getParameter("start")));
        reportBean.setLength(Integer.parseInt(request.getParameter("length")));
        reportBean.setSearchFieldValueList(getSearchFieldValues(request, 9));
        int id = reportBean.getIntparam2();
        boolean isPayable = (id == 1);
        List<ReportBean> list = trialService.tdsRegisterData(reportBean, isPayable);
        reportBean.setAaData(list);
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(reportBean);
    }

    @RequestMapping("/TDSXLSReport.fin")
    public ModelAndView tdsXLSReport(@ModelAttribute("reportBean") ReportBean reportBean,
                                     HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                     @RequestParam("id") int id) throws Exception {
        setSessionData(reportBean, request.getSession());
        reportBean.setIntparam2(id);
        AbstractReport report = new TDSRegisterXLSReport(reportBean,response, trialService);
        report.generateReport();
        return new ModelAndView("sales", "command", reportBean);
    }

    @RequestMapping("/SettleOpeningBalance.fin")
    public ModelAndView settleOpeningBalance(@ModelAttribute("reportBean") ReportBean reportBean,
                                             HttpSession session) {
        ReportBean sessionBean = (ReportBean)session.getAttribute("monthly");
        reportBean.setParam1(sessionBean.getParam1());
        setSessionData(reportBean, session);
        trialService.settleOpeningBalance(reportBean);
        reportBean.setParam7(Long.toString(System.currentTimeMillis()));
        return new ModelAndView("finact/Transactions", "command", reportBean);
    }

    @RequestMapping("/bclPDF.fin")
    public ModelAndView bclPDF(@ModelAttribute("reportBean") ReportBean reportBean, HttpSession session,
                               HttpServletResponse response, HttpServletRequest request,
                               @RequestParam("id") String id, @RequestParam("endDate") String endDate) throws Exception {
        //reportBean = (ReportBean)session.getAttribute("monthly");
        setSessionData(reportBean, request.getSession());
        String reportPath = "/reports/BCL.jrxml";
        Map paramMap = new HashMap();
        reportBean.setParam11(id);
        reportBean.setParam12(endDate);
        paramMap.put("asOnDate", reportBean.getParam1());
        paramMap.put("printDate", DateUtil.getSystemDate());
        AbstractReport report = new BCLPDFReport(reportPath, paramMap, trialService, response, reportBean,
                servletContext);
        report.generateReport();
        return new ModelAndView("sales", "command", reportBean);
    }
}
