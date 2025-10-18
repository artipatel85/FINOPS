/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.freight.controller;

import com.finops.controller.AbstractController;
import com.finops.freight.bean.*;
import com.finops.freight.service.ContainerService;
import com.finops.freight.service.SOService;
import com.finops.freight.service.SailingScheduleService;
import com.finops.partner.model.PartnerBean;
import com.finops.report.model.ReportBean;
import com.finops.util.DateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rtradadia
 */
@Controller
public class FreightController extends AbstractController implements InitializingBean {

    @Autowired
    private SailingScheduleService sailingScheduleService;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private SOService soService;

    private Map<String, String> conditionMap;

    @RequestMapping("/sailingSchedule.fin")
    public ModelAndView sailingSchedule(@ModelAttribute("sailingScheduleBean") SailingScheduleBean sailingScheduleBean) {
        return new ModelAndView("operation/SailingScheduleSearch", "command", sailingScheduleBean);
    }

    @RequestMapping(value = "/sailingScheduleGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String partnerAccountGson(@ModelAttribute("sailingScheduleBean") SailingScheduleBean sailingScheduleBean,
                              HttpServletRequest request, HttpSession session) {

        setSessionData(sailingScheduleBean, session);
        sailingScheduleBean.setStart(Integer.parseInt(request.getParameter("start")));
        sailingScheduleBean.setLength(Integer.parseInt(request.getParameter("length")));
        sailingScheduleBean.setSearchFieldValueList(getSearchFieldValues(request, 14));
        List list = sailingScheduleService.fetchList(sailingScheduleBean);

        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }


    @RequestMapping("/sailingScheduleRetrieve.fin")
    public ModelAndView sailingScheduleRetrieve(@ModelAttribute("sailingScheduleBean") SailingScheduleBean sailingScheduleBean,
                                                @RequestParam("scheduleId") int scheduleId, @RequestParam("scheduleSeqId") int scheduleSeqId,
                                                HttpSession session) {
        setSessionData(sailingScheduleBean, session);
        sailingScheduleBean.setSalingScheduleId(scheduleId);
        sailingScheduleBean.setSalingScheduleSeqId(scheduleSeqId);
        sailingScheduleService.retrieveSS(sailingScheduleBean);

        return new ModelAndView("operation/SailingSchedule", "command", sailingScheduleBean);
    }

    @RequestMapping("/sailingScheduleCreate.fin")
    public ModelAndView sailingScheduleCreate(@ModelAttribute("sailingScheduleBean") SailingScheduleBean sailingScheduleBean,
                                              HttpSession session) {
        setSessionData(sailingScheduleBean, session);
        sailingScheduleService.createSS(sailingScheduleBean);
        return new ModelAndView("operation/SailingSchedule", "command", sailingScheduleBean);
    }

    @RequestMapping("/sailingScheduleSave.fin")
    public ModelAndView sailingScheduleSave(@ModelAttribute("sailingScheduleBean") SailingScheduleBean sailingScheduleBean,
                                            HttpSession session) {
        setSessionData(sailingScheduleBean, session);
        if (!validateSS(sailingScheduleBean)) {
            return new ModelAndView("operation/SailingSchedule", "command", sailingScheduleBean);
        }
        sailingScheduleService.save(sailingScheduleBean);
        return new ModelAndView("operation/SailingScheduleSearch", "command", sailingScheduleBean);
    }

    private boolean validateSS(SailingScheduleBean bean) {

        List<FreightRow> polList = bean.getPolList();
        List<FreightRow> podList = bean.getPodList();
        int i = 0;

        for (FreightRow row : polList) {
            if (!StringUtils.hasText(row.getValue1()) && i > 0) {
                break;
            }
            try {
                FreightRow pod = podList.get(i);
                if (i == 0 && !StringUtils.hasText(row.getValue1())) {
                    bean.setErrorMsg("Atleast one POL entry is required.");
                    return false;
                }
                if (i == 0 && !StringUtils.hasText(pod.getValue1())) {
                    bean.setErrorMsg("Atleast one POD entry is required.");
                    return false;
                }
                if (!DateUtil.compareDates(row.getValue7(), pod.getValue3())) {
                    bean.setErrorMsg("ETD can not be greater than ETA.");
                    return false;
                }

                i++;
            } catch (ParseException ex) {
                bean.setErrorMsg("Unknown error occurred.");
                return false;
            }
        }


        return true;
    }

    @RequestMapping("/connectBySSPopup.fin")
    public ModelAndView popup3View(@ModelAttribute("reportbean") ReportBean reportbean,
                                   @RequestParam(PARAM) String param, @RequestParam("ETD") String etd, HttpSession session, @RequestParam("POD") String pod) {
        Map<String, String> map = new HashMap<>();
        String connectBySSQuery = " SELECT SS_SEQ_NUMBER, SS_NUMBER, VSL, VOY, CARRIER,"
                + "STR_TO_DATE(CFS_DATE,'%Y-%m-%d') CFS_DATE, STR_TO_DATE(CY_DATE ,'%Y-%m-%d') CY_DATE, POL"
                + ", STR_TO_DATE(ETD ,'%Y-%m-%d') ETD, POD"
                + ", STR_TO_DATE(ETA ,'%Y-%m-%d') ETA,  CT_VSL, CT_VOY, CT_POL, CT_POD"
                + ", STR_TO_DATE(CT_ETD ,'%Y-%m-%d') CT_ETD"
                + ", STR_TO_DATE(CT_ETA ,'%Y-%m-%d') CT_ETA"
                + ",CREATED_BY, STR_TO_DATE(CREATION_DATE,'%Y-%m-%d') CREATION_DATE, AMENDED_BY"
                + ", STR_TO_DATE(AMENDED_DATE,'%Y-%m-%d') AMENDED_DATE,ROW_NUMBER() OVER (ORDER BY SS_NUMBER DESC) as row "
                + " FROM SAILING_SCHEDULE_D WHERE ETD >  '" + etd + "' AND POL = '" + pod + "' "
                + " AND VSL LIKE ? AND VOY LIKE ? AND CONVERT(VARCHAR(25), ETD, 126) LIKE ?";
        map.put(PARAM, param);
        map.put(QUERY, connectBySSQuery);
        map.put(INDEX, "11");
        map.put(PARAM_LENGTH, "3");
        session.setAttribute(POPUP_METADATA, map);
        return new ModelAndView("operation/ConnectBySS", "command", reportbean);
    }

    @RequestMapping("/SOSSPopup.fin")
    public ModelAndView SOSSPopup(@ModelAttribute("reportbean") ReportBean reportbean,
                                  @RequestParam(PARAM) String param, @RequestParam("POL") String pol,
                                  @RequestParam("POD") String pod, HttpSession session) {
        Map<String, String> map = new HashMap<>();
        String connectBySSQuery = " SELECT SS_SEQ_NUMBER param1, SS_NUMBER param2, VSL param3, VOY param4, CARRIER param5,"
                + "STR_TO_DATE(CFS_DATE,'%Y-%m-%d') param6, STR_TO_DATE(CY_DATE ,'%Y-%m-%d') param7, POL param8"
                + ", STR_TO_DATE(ETD ,'%Y-%m-%d') param9, POD param10"
                + ", STR_TO_DATE(ETA ,'%Y-%m-%d') param11,  CT_VSL, CT_VOY, CT_POL, CT_POD"
                + ", STR_TO_DATE(CT_ETD ,'%Y-%m-%d') CT_ETD"
                + ", STR_TO_DATE(CT_ETA ,'%Y-%m-%d') CT_ETA"
                + ",CREATED_BY, STR_TO_DATE(CREATION_DATE,'%Y-%m-%d') CREATION_DATE, AMENDED_BY"
                + ", STR_TO_DATE(AMENDED_DATE,'%Y-%m-%d') AMENDED_DATE "
                + " FROM SAILING_SCHEDULE_D WHERE POL = '" + pol + "' AND POD='" + pod + "' "
                + "  ORDER BY SS_NUMBER DESC";
        map.put(PARAM, param);
        map.put(QUERY, connectBySSQuery);
        map.put(INDEX, "11");
        map.put(PARAM_LENGTH, "3");
        session.setAttribute(POPUP_METADATA, map);
        return new ModelAndView("operation/ConnectBySS", "command", reportbean);
    }
//
//
//    @RequestMapping("/soPopupPartner1.fin")
//    public ModelAndView soPopupPartner1(@ModelAttribute("reportbean") ReportBean reportbean,
//                                        @RequestParam(PARAM) String param, HttpSession session) {
//        setSessionData(reportbean, session);
//        String query = "SELECT TOP 20 pa.partner_code CODE,pa.description1 PARTNERNAME,"
//                + "((pa.description1)+(coalesce(case when Ltrim(pa.address1) <> '' then +('|'+ltrim(pa.address1)) end,''))+c.description) ADDRESS,"
//                + "'" + param + "' "
//                + "FROM partner_account_d pa LEFT OUTER JOIN country_d c ON (pa.country_code = c.country_code) "
//                + "WHERE pa.status = 'A' AND pa.display='Y' AND pa.loadng_agnt = '" + reportbean.getLoadingAgent() + "' "
//                + "AND pa.partner_code LIKE ? AND pa.description1 LIKE ?";
//
//        query += getPartnerCondition(param);
//
//        Map<String, String> map = new HashMap<>();
//        map.put(PARAM, param);
//        map.put(QUERY, query);
//        map.put(INDEX, "3");
//        map.put(PARAM_LENGTH, "2");
//        session.setAttribute(POPUP_METADATA, map);
//        return new ModelAndView("operation/SoPopupPartner", "command", reportbean);
//    }

    private String getPartnerCondition(String param) {
        return conditionMap.get(param);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        conditionMap = new HashMap<>();
        conditionMap.put("SHIPPER", " AND SHPR='Y' ");
        conditionMap.put("ACTSHIPPER", " AND SHPR='Y' ");
        conditionMap.put("CNEE", " AND CNEE='Y' ");
        conditionMap.put("ACTCNEE", " AND CNEE='Y' ");
        conditionMap.put("NOTIFY", " AND CNEE='Y' ");
        conditionMap.put("ALSONOTIFY", " AND CNEE='Y' ");
        conditionMap.put("LOADINGAGENT", " AND AGNT='Y' ");
        conditionMap.put("DESTAGENT", " AND AGNT='Y' ");
        conditionMap.put("ORIGINWH", " AND SUB_CONTRCTR='Y' ");
        conditionMap.put("CARRIER", " AND (CARRIER='Y' or CO_LOADER='Y') ");
    }

    @RequestMapping("/clp.fin")
    public ModelAndView clp(@ModelAttribute("containerBean") ContainerBean containerBean) {
        return new ModelAndView("operation/ContainerLoadPlanSearch", "command", containerBean);
    }

    @RequestMapping(value = "/clpGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String clpGson(@ModelAttribute("containerBean") ContainerBean containerBean,
                              HttpServletRequest request, HttpSession session) {

        setSessionData(containerBean, session);
        containerBean.setStart(Integer.parseInt(request.getParameter("start")));
        containerBean.setLength(Integer.parseInt(request.getParameter("length")));
        containerBean.setSearchFieldValueList(getSearchFieldValues(request, 14));
        List list = containerService.findCLPByPage(containerBean);

        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping("/retrieveCLP.fin")
    public ModelAndView retrieveCLP(@ModelAttribute("containerBean") ContainerBean containerBean,
                                                @RequestParam("loadPlanNo") String loadPlanNo,
                                                HttpSession session, HttpServletRequest request) {
        setSessionData(containerBean, session);
        containerBean.setNumber(loadPlanNo);
        containerBean = containerService.findCLPByPlanNo(containerBean, partnerService, adminService);
        SOBean soBean = new SOBean();
        setSessionData(soBean, session);
        soBean.setLoadPlanNo(loadPlanNo);
        soBean.setPol(containerBean.getPol());
        soBean.setPod(containerBean.getPod());
        soBean.setOriginWareHouse(containerBean.getWarehouse());
        soBean.setAction("CLP");
        soBean.setCb(containerBean);
        containerBean.setSizeMap(adminService.fetchAllSizes("sizes"));
        List<ContainerBean> containerBeanList = soService.getContainerDetails(soBean, 5);
        containerBean.setContainerBeanList(containerBeanList);
        return new ModelAndView("operation/CLP", "command", containerBean);
    }

    @RequestMapping("/refreshCLP.fin")
    public ModelAndView refreshCLP(@ModelAttribute("containerBean") ContainerBean containerBean,
                                    @RequestParam("loadPlanNo") String loadPlanNo,
                                    HttpSession session, HttpServletRequest request) {
        setSessionData(containerBean, session);
        SOBean soBean = new SOBean();
        setSessionData(soBean, session);
        soBean.setLoadPlanNo(containerBean.getNumber());
        soBean.setPol(containerBean.getPol());
        soBean.setPod(containerBean.getPod());
        soBean.setOriginWareHouse(containerBean.getWarehouse());
        soBean.setAction("CLP");
        soBean.setCb(containerBean);
        containerBean.setSizeMap(adminService.fetchAllSizes("sizes"));
        List<ContainerBean> containerBeanList = soService.getContainerDetails(soBean, 6);
        containerBean.setContainerBeanList(containerBeanList);
        return new ModelAndView("operation/CLP", "command", containerBean);
    }

    @RequestMapping("/createCLP.fin")
    public ModelAndView createCLP(@ModelAttribute("containerBean") ContainerBean containerBean,
                                    HttpSession session, HttpServletRequest request) {
        setSessionData(containerBean, session);
        containerBean.setNumber("AUTO");
        containerBean.setSizeMap(adminService.fetchAllSizes("sizes"));
        return new ModelAndView("operation/CLP", "command", containerBean);
    }

    @RequestMapping("/saveCLP.fin")
    public ModelAndView saveCLP(@ModelAttribute("containerBean") ContainerBean containerBean,
                                HttpSession session){
        setSessionData(containerBean, session);
        containerService.saveCLP(containerBean, soService);
        containerBean.setSizeMap(adminService.fetchAllSizes("sizes"));
        return new ModelAndView("operation/ContainerLoadPlanSearch", "command", containerBean);
    }

    @RequestMapping("/CLPPDF.fin")
    public StreamingResponseBody jobPDF(@ModelAttribute("containerBean") ContainerBean containerBean,
                                        @RequestParam("loadPlanNumber") String loadPlanNumber,
                                        HttpSession session, HttpServletResponse response) throws SQLException {
        setSessionData(containerBean, session);
        containerBean.setNumber(loadPlanNumber);
        containerBean.setLegacyReportPath(containerBean.getLegacyReportPath()+"SO/");
        containerService.clpPDFReport(containerBean);
        response.setContentType("application/pdf");
        try {
            InputStream inputStream = new FileInputStream(new File(containerBean.getLegacyReportPath()+containerBean.getNumber()+".pdf"));
            return outputStream -> {
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    outputStream.write(data, 0, nRead);
                }
                inputStream.close();
            };
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
