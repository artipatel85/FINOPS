package com.finops.report.controller;

import com.finops.controller.AbstractController;
import com.finops.finance.report.AgeingXLSNewReport;
import com.finops.finance.report.AgeingXLSReport;
import com.finops.report.AbstractReport;
import com.finops.report.model.ReportBean;
import com.finops.report.service.OutstandingService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
public class OutstandingController extends AbstractController {

    @Autowired
    private OutstandingService outstandingService;

    @RequestMapping("/Outstanding.fin")
    public ModelAndView outstanding(@ModelAttribute("reportbean") ReportBean reportbean,
                                        HttpSession session) {
        session.removeAttribute("outstandingParty");
        return new ModelAndView("finact/Partywise", "command", reportbean);
    }

    @RequestMapping("/outstandingPartyView.fin")
    public ModelAndView outstandingPartyView(@ModelAttribute("reportbean") ReportBean reportbean,
                                             @RequestParam Map<String, String> requestParams, HttpSession session) {
        if (requestParams.size() > 1) {
            setOutstandingSession(reportbean, requestParams, session);
        } else {
            reportbean = (ReportBean) session.getAttribute("outstandingParty");
        }
        return new ModelAndView("finact/OutstandingPartyView", "command", reportbean);
    }

    @RequestMapping("/outstandingPartyViewNew.fin")
    public ModelAndView outstandingPartyViewNew(@ModelAttribute("reportbean") ReportBean reportbean,
                                             @RequestParam Map<String, String> requestParams, HttpSession session) {
        if (requestParams.size() > 1) {
            setOutstandingSession(reportbean, requestParams, session);
        } else {
            reportbean = (ReportBean) session.getAttribute("outstandingParty");
        }
        return new ModelAndView("finact/OutstandingPartyViewNew", "command", reportbean);
    }

    private void setOutstandingSession(ReportBean reportbean, Map<String, String> requestParams,
                                       HttpSession session) {
        reportbean.setParam1(requestParams.get("p1"));
        reportbean.setParam2(requestParams.get("p2"));
        reportbean.setParam3(requestParams.get("p3"));
        reportbean.setParam4(requestParams.get("p4"));
        reportbean.setParam5(requestParams.get("p5"));
        reportbean.setParam6(requestParams.get("p6"));
        reportbean.setParam7(requestParams.get("p7"));
        reportbean.setParam8(requestParams.get("p8"));
        reportbean.setParam9(requestParams.get("p9"));
        session.setAttribute("outstandingParty", reportbean);
    }

    @RequestMapping(value = "/outstandingPartyGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String outstandingPartyGson(@ModelAttribute("reportbean") ReportBean reportBean, HttpSession session) {
        reportBean = (ReportBean) session.getAttribute("outstandingParty");
        setSessionData(reportBean, session);
        List<ReportBean> list = outstandingService.receivable(reportBean);
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping(value = "/outstandingPartyNewGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String outstandingPartyNewGson(@ModelAttribute("reportbean") ReportBean reportBean, HttpSession session) {
        reportBean = (ReportBean) session.getAttribute("outstandingParty");
        setSessionData(reportBean, session);
        List<ReportBean> list = outstandingService.receivable(reportBean, 1);
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping("/partyViewDetail.fin")
    public ModelAndView partyViewDetail(@ModelAttribute("reportbean") ReportBean reportBean,
                                        @RequestParam("id") String id, HttpSession session) {
        reportBean = (ReportBean) session.getAttribute("outstandingParty");
        if ("1".equals(reportBean.getParam9())) {
            reportBean.setParam8(id);
        } else {
            reportBean.setParam7(id);
        }
        return new ModelAndView("finact/PartyViewDetail", "command", reportBean);
    }

    @RequestMapping(value = "/partyViewDetailGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String partyViewDetailGson(@ModelAttribute("reportbean") ReportBean reportBean, HttpSession session) {
        reportBean = (ReportBean) session.getAttribute("outstandingParty");
        setSessionData(reportBean, session);
        List<ReportBean> list = outstandingService.getAgeingInvoicewise(reportBean);
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping("/partywiseSummary.fin")
    public ModelAndView partywiseSummary(@ModelAttribute("reportbean") ReportBean reportBean,
                                         @RequestParam Map<String, String> requestParams, HttpSession session,
                                         HttpServletResponse response) throws Exception {

        if (!StringUtils.hasText(reportBean.getParam9())) {
            ReportBean sessionBean = (ReportBean) session.getAttribute("outstandingParty");
            if (sessionBean != null) {
                reportBean = sessionBean;
            }
        }

        if ("1".equals(reportBean.getParam9())) {
            reportBean.setParam10("PARTYSUMMARY");
        } else {
            reportBean.setParam10("SALESMANSUMMARY");
        }

        setSessionData(reportBean, session);
        AbstractReport report = new AgeingXLSReport(response, reportBean, outstandingService);
        report.generateReport();
        return new ModelAndView("sales", "command", reportBean);
    }

    @RequestMapping("/partywiseSummaryNew.fin")
    public ModelAndView partywiseSummaryNew(@ModelAttribute("reportbean") ReportBean reportBean,
                                         @RequestParam Map<String, String> requestParams, HttpSession session,
                                         HttpServletResponse response) throws Exception {

        if (!StringUtils.hasText(reportBean.getParam9())) {
            ReportBean sessionBean = (ReportBean) session.getAttribute("outstandingParty");
            if (sessionBean != null) {
                reportBean = sessionBean;
            }
        }

        if ("1".equals(reportBean.getParam9())) {
            reportBean.setParam10("PARTYSUMMARY");
        } else {
            reportBean.setParam10("SALESMANSUMMARY");
        }

        setSessionData(reportBean, session);
        AbstractReport report = new AgeingXLSNewReport(response, reportBean, outstandingService);
        report.generateReport();
        return new ModelAndView("sales", "command", reportBean);
    }

    @RequestMapping("/partywiseAuditSummary.fin")
    public ModelAndView partywiseAuditSummary(@ModelAttribute("reportbean") ReportBean reportBean,
                                              @RequestParam Map<String, String> requestParams, HttpSession session,
                                              HttpServletResponse response) throws Exception {

        if (!StringUtils.hasText(reportBean.getParam9())) {
            ReportBean sessionBean = (ReportBean) session.getAttribute("outstandingParty");
            if (sessionBean != null) {
                reportBean = sessionBean;
            }
        }

        if ("1".equals(reportBean.getParam9())) {
            reportBean.setParam10("PARTYSUMMARY2");
        } else {
            reportBean.setParam10("SALESMANSUMMARY");
        }

        setSessionData(reportBean, session);
        AbstractReport report = new AgeingXLSReport(response, reportBean, outstandingService);
        report.generateReport();
        return new ModelAndView("sales", "command", reportBean);
    }

    @RequestMapping("/partywiseAuditSummary2.fin")
    public ModelAndView partywiseAuditSummary2(@ModelAttribute("reportbean") ReportBean reportBean,
                                               @RequestParam Map<String, String> requestParams, HttpSession session,
                                               HttpServletResponse response) throws Exception {

        if (!StringUtils.hasText(reportBean.getParam9())) {
            ReportBean sessionBean = (ReportBean) session.getAttribute("outstandingParty");
            if (sessionBean != null) {
                reportBean = sessionBean;
            }
        }

        if ("1".equals(reportBean.getParam9())) {
            reportBean.setParam10("PARTYSUMMARY3");
        } else {
            reportBean.setParam10("SALESMANSUMMARY");
        }

        setSessionData(reportBean, session);
        AbstractReport report = new AgeingXLSReport(response, reportBean, outstandingService);
        report.generateReport();
        return new ModelAndView("sales", "command", reportBean);
    }

    @RequestMapping("/partywiseDetail.fin")
    public ModelAndView partywiseDetail(@ModelAttribute("reportbean") ReportBean reportBean,
                                        @RequestParam Map<String, String> requestParams, HttpSession session,
                                        HttpServletResponse response) throws Exception {

        if (!StringUtils.hasText(reportBean.getParam9())) {
            ReportBean sessionBean = (ReportBean) session.getAttribute("outstandingParty");
            if (sessionBean != null) {
                reportBean = sessionBean;
            }
        }

        if ("1".equals(reportBean.getParam9())) {
            reportBean.setParam10("PARTYDETAIL");
        } else {
            reportBean.setParam10("SALESMANDETAIL");
        }

        setSessionData(reportBean, session);
        AbstractReport report = new AgeingXLSReport(response, reportBean, outstandingService);
        report.generateReport();
        return new ModelAndView("sales", "command", reportBean);
    }
}
