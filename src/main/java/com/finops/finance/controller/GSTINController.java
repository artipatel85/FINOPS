package com.finops.finance.controller;

import com.finact.gstin.credit.Data;
import com.finops.controller.AbstractController;
import com.finops.finance.report.GstinCreditXLSReport;
import com.finops.finance.service.GSTINService;
import com.finops.report.AbstractReport;
import com.finops.report.model.ReportBean;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class GSTINController extends AbstractController {

    @Autowired
    private GSTINService gstinService;

    @RequestMapping("/gstincredit.fin")
    public ModelAndView gstJson(@ModelAttribute("reportBean") ReportBean reportBean,
                                HttpSession session) {
        setSessionData(reportBean, session);
        return new ModelAndView("finact/GstinCredit", "command", reportBean);
    }

    @RequestMapping("/gstincreditupload.fin")
    public ModelAndView gstJsonUpload(@ModelAttribute("reportBean") ReportBean reportBean,
                                      @RequestParam("param100") MultipartFile file, HttpServletResponse response,
                                      HttpSession session) throws IOException, Exception {
        setSessionData(reportBean, session);
        String loc = "C:\\Bhaumik\\GST_SUMMARY\\CREDIT\\";
        byte[] bytes = file.getBytes();
        String name = file.getOriginalFilename();
        Path path = Paths.get(loc + name);
        Files.write(path, bytes);
        gstinService.saveCreditDetails(reportBean.getParam4(), loc + name);
        return new ModelAndView("finact/GstinCredit", "command", reportBean);
    }

    @RequestMapping("/gstincreditmatch.fin")
    public ModelAndView gstCreditMatch(@ModelAttribute("reportBean") ReportBean reportBean,
                                       @RequestParam("period") String period, @RequestParam("gstin") String gstin,
                                       HttpServletResponse response,
                                       HttpSession session) throws IOException, Exception {
        setSessionData(reportBean, session);

        gstinService.match(reportBean.getLoadingAgent(), period, gstin);
        return new ModelAndView("finact/GstinCredit", "command", reportBean);
    }

    @RequestMapping(value = "/gstincreditgson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String gstincreditgson(@ModelAttribute("reportBean") ReportBean reportBean, HttpServletRequest request) {
        setSessionData(reportBean, request.getSession());
        List<Data> list = gstinService.listOfPeriods(reportBean.getLoadingAgent());
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping("/gstincreditdisplay.fin")
    public ModelAndView gstCreditMatchDisplay(@ModelAttribute("reportBean") ReportBean reportBean,
                                              @RequestParam("period") String period, @RequestParam("gstin") String gstin,
                                              HttpServletResponse response,
                                              HttpSession session) throws IOException, Exception {
        setSessionData(reportBean, session);
        reportBean.setParam1(period);
        reportBean.setParam2(gstin);
        reportBean.setParam4(DateUtil.startDateFormatMMYYYY(period));
        reportBean.setParam5(DateUtil.endDateFormatMMYYYY(period));
        return new ModelAndView("finact/GstinCreditDisplay", "command", reportBean);
    }

    @RequestMapping(value = "/gstincreditdisplaygson.fin")
    public @ResponseBody
    String gstCreditMatchDisplayGson(@ModelAttribute("reportBean") ReportBean reportBean,
                                     @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                     @RequestParam("gstin") String gstin, HttpServletRequest request) {
        setSessionData(reportBean, request.getSession());
        reportBean.setStart(Integer.parseInt(request.getParameter("start")));
        reportBean.setLength(Integer.parseInt(request.getParameter("length")));
        reportBean.setSearchFieldValueList(getSearchFieldValues(request, 9));
        reportBean.setParam4(startDate);
        reportBean.setParam5(endDate);
        List<ReportBean> list = gstinService.retrieveMappedRecords(gstin, reportBean);
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping("/gstinCreditXLSReport.fin")
    public ModelAndView gstinCreditXLSReport(@ModelAttribute("reportBean") ReportBean reportBean,

                                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        setSessionData(reportBean, request.getSession());
        AbstractReport report = new GstinCreditXLSReport(reportBean, response, gstinService);
        report.generateReport();
        return new ModelAndView("sales", "command", reportBean);
    }
}
