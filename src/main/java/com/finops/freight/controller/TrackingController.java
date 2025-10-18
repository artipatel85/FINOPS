package com.finops.freight.controller;

import com.finops.controller.AbstractController;
import com.finops.finance.report.AgeingXLSReport;
import com.finops.finance.report.TrackingXLSReport;
import com.finops.freight.bean.TrackingBean;
import com.finops.freight.service.TrackingService;
import com.finops.freight.vo.TrackingVO;
import com.finops.report.AbstractReport;
import com.finops.report.model.ReportBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Controller
public class TrackingController extends AbstractController {

    @Autowired
    private TrackingService trackingService;

    @RequestMapping("/seaExportTracking.fin")
    public ModelAndView seaExportTracking(@ModelAttribute("trackingBean") TrackingBean trackingBean,
                                          HttpSession session, @RequestParam("formId") String formId) throws SQLException {
        setSessionData(trackingBean, session);
        trackingBean.setFormId(formId);
        trackingBean.setTrackingVoList(trackingService.getCustomGridDataForUser(trackingBean));
        trackingBean.setGridSeq(trackingBean.getTrackingVoList().size()+1);
        return new ModelAndView("tracking/SeaExportTracking", "command", trackingBean);
    }

    @RequestMapping("/seaExportTrackingUpdateGrid.fin")
    public ModelAndView seaExportTrackingUpdateGrid(@ModelAttribute("trackingBean") TrackingBean trackingBean,
                                                    HttpSession session) throws SQLException {
        setSessionData(trackingBean, session);
        int gridSeq = 1;
        for (TrackingVO vo : trackingBean.getTrackingVoList()) {
            if ("Y".equalsIgnoreCase(vo.getColumn3())) {
                trackingBean.setGridSeq(gridSeq++);
                trackingBean.setGridName(vo.getColumn2());
                break;
            } else {
                gridSeq++;
            }
        }
        trackingBean.setTrackingVoList(trackingService.getCustomGridData(trackingBean));
        return new ModelAndView("tracking/CustomizeGrid", "command", trackingBean);
    }

    @RequestMapping("/seaExportTrackingGridSave.fin")
    public ModelAndView seaExportTrackingGridSave(@ModelAttribute("trackingBean") TrackingBean trackingBean,
                                                  HttpSession session) throws SQLException {
        setSessionData(trackingBean, session);
        if(trackingBean.getGridSeq() > 5){
            trackingBean.setErrorMsg("Can not create more that 5 grids per user.");
        }
        else{
            trackingService.saveGrid(trackingBean);
        }
        return new ModelAndView("tracking/CustomizeGrid", "command", trackingBean);
    }

    @RequestMapping("/seaExportTrackingResult.fin")
    public ModelAndView seaExportTrackingResult(@ModelAttribute("trackingBean") TrackingBean trackingBean,
                                                HttpSession session) throws SQLException {
        setSessionData(trackingBean, session);
        //trackingBean.setFormId("1029");
        trackingService.prepareGSONData(trackingBean);
        session.setAttribute("seaExportTrackingBean", trackingBean);
        return new ModelAndView("tracking/SOTrackingResult", "command", trackingBean);
    }

    @RequestMapping(value = "/seaExportTrackingGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String seaExportTrackingGson(@ModelAttribute("trackingBean") TrackingBean trackingBean,
                                 HttpServletRequest request,
                                 @RequestParam("param") String formId) throws SQLException {
        HttpSession session = request.getSession();
        TrackingBean sessionBean = (TrackingBean)session.getAttribute("seaExportTrackingBean");
        setSessionData(trackingBean, session);
        trackingBean.setFormId(sessionBean.getFormId());
        trackingBean.setColumnString(sessionBean.getColumnString());
        trackingBean.setQuery(sessionBean.getQuery());
        trackingBean.setStart(Integer.parseInt(request.getParameter("start")));
        trackingBean.setLength(Integer.parseInt(request.getParameter("length")));
        trackingBean.setSearchFieldValueList(getSearchFieldValues(request, 11));
        List<TrackingVO> list = trackingService.getSOTracking(trackingBean);

        trackingBean.setAaData(list);
        Gson gson = new GsonBuilder().serializeNulls().create();

        //session.removeAttribute("seaExportTrackingBean");
        return gson.toJson(trackingBean);
    }

    @RequestMapping("/seaExportTrackingXLSReport.fin")
    public ModelAndView partywiseAuditSummary2(@ModelAttribute("trackingBean") TrackingBean trackingBean,
                                               @RequestParam Map<String, String> requestParams, HttpSession session,
                                               HttpServletResponse response) throws Exception {
        TrackingBean sessionBean = (TrackingBean)session.getAttribute("seaExportTrackingBean");
        setSessionData(trackingBean, session);
        trackingBean.setFormId(sessionBean.getFormId());
        trackingBean.setColumnString(sessionBean.getColumnString());
        trackingBean.setQuery(sessionBean.getQuery());
        trackingBean.setLength(20000);
        trackingBean.setHeaderData(sessionBean.getHeaderData());
        AbstractReport report = new TrackingXLSReport(response, trackingBean, trackingService);
        report.generateReport();
        return new ModelAndView("sales", "command", trackingBean);
    }
}
