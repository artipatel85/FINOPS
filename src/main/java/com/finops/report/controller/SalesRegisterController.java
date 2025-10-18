package com.finops.report.controller;

import com.finops.controller.AbstractController;
import com.finops.finance.report.SalesRegisterXLSReport;
import com.finops.report.AbstractReport;
import com.finops.report.model.ReportBean;
import com.finops.report.service.SalesRegisterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SalesRegisterController extends AbstractController {

    @Autowired
    private SalesRegisterService service;

    @RequestMapping("/SalesRegister.fin")
    public ModelAndView salesform(@ModelAttribute("reportbean") ReportBean reportbean) {
        return new ModelAndView("finact/SalesRegister", "command", reportbean);
    }

    @RequestMapping("/gstinReport.fin")
    public ModelAndView gstinReport(@ModelAttribute("reportbean") ReportBean reportbean,
                                    HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam("param") int param) throws Exception {
        reportbean.setIntparam1(param);
        setSessionData(reportbean, request.getSession());
        AbstractReport report = new SalesRegisterXLSReport(reportbean, response, service);
        report.generateReport();
        return new ModelAndView("sales", "command", reportbean);
    }
}
