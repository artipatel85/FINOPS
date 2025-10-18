package com.finops.report.controller;

import com.finops.controller.AbstractController;
import com.finops.finance.report.ProfitabilityXLSReport;
import com.finops.report.AbstractReport;
import com.finops.report.model.ReportBean;
import com.finops.report.service.ProfitabilityService;
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

import java.util.List;
import java.util.Map;

@Controller
public class ProfitabilityController extends AbstractController {

    @Autowired
    private ProfitabilityService profitabilityService;

    @RequestMapping("/profitabilitySearch.fin")
    public ModelAndView profitabilitySearch(@ModelAttribute("reportBean") ReportBean reportBean,
                                            HttpSession session) {
        setSessionData(reportBean, session);
        reportBean.setParam4(reportBean.getYrStartDate());
        reportBean.setParam5(reportBean.getYrEndDate());
        session.setAttribute("profitability",reportBean);
        return new ModelAndView("finact/ProfitabilitySearch", "command", reportBean);
    }

    @RequestMapping("/profitability.fin")
    public ModelAndView profitability(@ModelAttribute("reportBean") ReportBean reportBean,
                                      HttpSession session) {
        setSessionData(reportBean, session);
        //trialDAO.setupMonthlyData(reportBean);

        session.setAttribute("profitability",reportBean);
        return new ModelAndView("finact/Profitability", "command", reportBean);
    }

    @RequestMapping(value = "/profitabilityGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String profitabilityGson(@ModelAttribute("reportBean") ReportBean reportBean, HttpSession session,
                             HttpServletRequest request) {
        reportBean = (ReportBean)session.getAttribute("profitability");
        setSessionData(reportBean, session);
        Map<String, String> salesmanMap = adminService.fetchSalesMan("salesman");
        reportBean.setStart(Integer.parseInt(request.getParameter("start")));
        reportBean.setLength(Integer.parseInt(request.getParameter("length")));
        reportBean.setSearchFieldValueList(getSearchFieldValues(request, 11));
        List<ReportBean> list = profitabilityService.summary(reportBean, salesmanMap);
        reportBean.setAaData(list);
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(reportBean);
    }

    @RequestMapping("/profitabilityDetail.fin")
    public ModelAndView profitabilityDetail(@ModelAttribute("reportBean") ReportBean reportBean,
                                            HttpSession session, @RequestParam("blNo") String blNo, @RequestParam("jobNo") String jobNo) {
        reportBean.setParam1(blNo);
        reportBean.setParam2(jobNo);
        session.setAttribute("profitDetail", reportBean);
        return new ModelAndView("finact/ProfitabilityDetail", "command", reportBean);
    }

    @RequestMapping(value = "/profitabilityDetailGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String profitabilityDetailGson(@ModelAttribute("reportBean") ReportBean reportBean, HttpSession session,
                                   HttpServletRequest request) {
        reportBean = (ReportBean)session.getAttribute("profitDetail");
        setSessionData(reportBean, session);
        List<ReportBean> list = profitabilityService.detail(reportBean);

        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping("/profitabilityXLSReport.fin")
    public ModelAndView profitabilityXLSReport(@ModelAttribute("reportBean") ReportBean reportBean,
                                               HttpServletRequest request, HttpServletResponse response) throws Exception {
        setSessionData(reportBean, request.getSession());
        Map<String, String> salesmanMap = adminService.fetchSalesMan("salesman");
        AbstractReport report = new ProfitabilityXLSReport(reportBean,response, profitabilityService, salesmanMap);
        report.generateReport();
        return new ModelAndView("sales", "command", reportBean);
    }


}
