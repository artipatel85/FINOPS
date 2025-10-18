package com.finops.report.controller;

import com.finops.controller.AbstractController;
import com.finops.finance.report.ReconPDFReport;
import com.finops.finance.report.ReconXLSReport;
import com.finops.finance.service.AccountService;
import com.finops.report.AbstractReport;
import com.finops.report.model.ReportBean;
import com.finops.report.service.ReconciliationService;
import com.finops.util.DateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ReconciliationController extends AbstractController {

    @Autowired
    private ReconciliationService reconciliationService;

    @Autowired
    private AccountService accountService;

    @RequestMapping("/reconciliation.fin")
    public ModelAndView reconciliation(@ModelAttribute("reportBean") ReportBean reportBean,
                                       HttpSession session) {
        session.removeAttribute("recon");
        String endDate = reportBean.getParam1();
        if (!StringUtils.hasText(endDate)) {
            endDate = DateUtil.getSystemDate();
            reportBean.setParam1(endDate);
        }
        session.setAttribute("recon", reportBean);
        return new ModelAndView("finact/Reconciliation", "command", reportBean);
    }

    @RequestMapping(value = "/reconGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String reconGson(@ModelAttribute("reportBean") ReportBean reportBean, HttpSession session,
                     HttpServletRequest request) {
        reportBean = (ReportBean)session.getAttribute("recon");
        setSessionData(reportBean, session);
        reportBean.setStart(Integer.parseInt(request.getParameter("start")));
        reportBean.setLength(Integer.parseInt(request.getParameter("length")));
        reportBean.setSearchFieldValueList(getSearchFieldValues(request, 11));
        List<ReportBean> list = reconciliationService.reconciliationView(reportBean);
        reportBean.setAaData(list);
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(reportBean);
    }

    @RequestMapping("/reconcile.fin")
    public ModelAndView reconcile(@ModelAttribute("reportBean") ReportBean reportBean,
                                  HttpSession session, Model model) {
        if (reportBean.getUuids() == null || reportBean.getUuids().length == 0) {
            model.addAttribute("errMsg", "Please select a record.");
            return new ModelAndView("finact/Reconciliation", "command", reportBean);
        }
        setSessionData(reportBean, session);
        int updatedCount = reconciliationService.reconcile(reportBean);
        return new ModelAndView("finact/Reconciliation", "command", reportBean);
    }

    @RequestMapping("/reconPDFReport.fin")
    public ModelAndView reconPDFReport(@ModelAttribute("reportBean") ReportBean reportBean,
                                       HttpServletRequest request, HttpServletResponse response) throws Exception {
        setSessionData(reportBean, request.getSession());
        String reportPath = "/reports/ReconUnmatched.jrxml";
        AbstractReport report = new ReconPDFReport(reportPath, null, response, reportBean,
                servletContext, reconciliationService,accountService);
        report.generateReport();
        return new ModelAndView("sales", "command", reportBean);
    }

    @RequestMapping("/reconXLSReport.fin")
    public ModelAndView reconXLSReport(@ModelAttribute("reportBean") ReportBean reportBean,
                                       HttpServletRequest request, HttpServletResponse response) throws Exception {
        setSessionData(reportBean, request.getSession());
        AbstractReport report = new ReconXLSReport(reportBean,response, reconciliationService,accountService);
        report.generateReport();
        return new ModelAndView("sales", "command", reportBean);
    }
}
