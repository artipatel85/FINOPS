package com.finops.freight.controller;

import com.finops.bean.FileUploadBean;
import com.finops.controller.AbstractController;
import com.finops.freight.bean.BLBean;
import com.finops.freight.bean.ContainerBean;
import com.finops.freight.bean.SOBean;
import com.finops.freight.service.SOService;
import com.finops.partner.model.PartnerBean;
import com.finops.partner.service.PartnerService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SOController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(SOController.class);

    @Autowired
    private SOService soService;

    @Autowired
    private PartnerService partnerService;

    @RequestMapping("/so.fin")
    public ModelAndView so(@ModelAttribute("soBean") SOBean bean,
                           @RequestParam("param") String param,
                           HttpSession session) {
        setSessionData(bean, session);
        bean.setExpImp(param);
        session.setAttribute("SOSEARCHBEAN"+param, bean);
        return new ModelAndView("operation/SOSearch", "command", bean);
    }

    @RequestMapping(value = "/soGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String soGson(@ModelAttribute("soBean") SOBean bean,
                  HttpServletRequest request, HttpSession session,
                  @RequestParam("param") String param) {
        bean = (SOBean) session.getAttribute("SOSEARCHBEAN"+param);
        //setSessionData(bean, session);
        bean.setExpImp(param);
        bean.setStart(Integer.parseInt(request.getParameter("start")));
        bean.setLength(Integer.parseInt(request.getParameter("length")));
        bean.setSearchFieldValueList(getSearchFieldValues(request, 14));
        List list = soService.searchSo(bean);

        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        jsonResponse.add("iTotalRecords", gson.toJsonTree(bean.getITotalRecords()));
        jsonResponse.add("iTotalDisplayRecords", gson.toJsonTree(bean.getITotalRecords()));
        //session.removeAttribute("SOSEARCHBEAN");
        return jsonResponse.toString();
    }

    @RequestMapping("/soRetrieve.fin")
    public ModelAndView soRetrieve(@ModelAttribute("soBean") SOBean soBean,
                                   @RequestParam("bookingRefNumber") int bookingRefNumber,
                                   @RequestParam("param") String param,
                                   HttpSession session) {
        setSessionData(soBean, session);
        soBean.setBkgRefNo(bookingRefNumber);
        soService.retrieve(soBean, false);
        soBean.setSizeMap(adminService.fetchAllSizes("sizes"));
        soBean.setUnitMap(adminService.fetchAllUnits("units"));
        soBean.setExpImp(param);
        return new ModelAndView("operation/SO", "command", soBean);
    }

    @RequestMapping("/soFLList.fin")
    public ModelAndView soFLList(@ModelAttribute("soBean") SOBean soBean,
                                   @RequestParam("bookingRefNumber") int bookingRefNumber,
                                   @RequestParam("param") String param,
                                   @RequestParam("soNo") String soNo,
                                   @RequestParam("bkgType") String bkgType,
                                   HttpSession session) {
        setSessionData(soBean, session);
        soBean.setBkgRefNo(bookingRefNumber);
        soService.retrieve(soBean, false);
        //soBean.setSizeMap(adminService.fetchAllSizes("sizes"));
        //soBean.setUnitMap(adminService.fetchAllUnits("units"));
        soBean.setSoNumber(soNo);
        soBean.setBookingType(bkgType);
        if("F".equalsIgnoreCase(bkgType)){
            soBean.setBkgType("FCL");
        }
        else{
            soBean.setBkgType("LCL");
        }
        soBean.setExpImp(param);
        return new ModelAndView("operation/SOBKGList", "command", soBean);
    }

    @RequestMapping(value = "/soContainerGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String soContainerGson(@ModelAttribute("soBean") SOBean bean, @RequestParam("bkgRefNo") int bkgRefNo,
                           HttpServletRequest request, HttpSession session) {
        setSessionData(bean, session);
        bean.setBkgRefNo(bkgRefNo);
        return createPaginatedResponse(bean, request, 0);
    }

    @RequestMapping(value = "/soContainerByJobGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String soContainerByJobGson(@ModelAttribute("soBean") SOBean bean, @RequestParam("jobNumber") String jobNumber,
                                HttpServletRequest request, HttpSession session) {
        setSessionData(bean, session);
        bean.setJobNumber(jobNumber);
        return createPaginatedResponse(bean, request, 2);
    }

    private String createPaginatedResponse(SOBean bean, HttpServletRequest request, int type) {
        prePaginate(bean, request, 14);
        List<ContainerBean> list = soService.getContainerDetails(bean, type);
        return postPaginate(list);
    }

    @RequestMapping("/soSave.fin")
    public ModelAndView soSave(@ModelAttribute("soBean") SOBean soBean,
                               HttpSession session) throws SQLException {
        setSessionData(soBean, session);
        String msg = validate(soBean);
        if (msg != null) {
            soBean.setErrMsg(msg);
            return new ModelAndView("operation/SO", "command", soBean);
        } else {
            soService.saveSO(soBean, false);
        }
        return new ModelAndView("operation/SO", "command", soBean);
    }

    @RequestMapping("/soPDF.fin")
    public StreamingResponseBody soPDF(@ModelAttribute("soBean") SOBean soBean,
                                       @RequestParam("bookingRefNumber") int bookingRefNumber,
                                       HttpSession session, HttpServletResponse response) throws SQLException {
        setSessionData(soBean, session);
        soBean.setBkgRefNo(bookingRefNumber);
        soBean.setLegacyReportPath(soBean.getLegacyReportPath() + "SO/");
        soService.soPDF(soBean);

        response.setContentType("application/pdf");
        try {
            InputStream inputStream = new FileInputStream(new File(soBean.getLegacyReportPath() + soBean.getBkgRefNo() + ".pdf"));
            return outputStream -> {
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    outputStream.write(data, 0, nRead);
                }
                inputStream.close();
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/soCreate.fin")
    public ModelAndView soCreate(@ModelAttribute("soBean") SOBean soBean,
                                 @RequestParam("param") String param,
                                 HttpSession session) {
        setSessionData(soBean, session);
        soBean.setExpImp(param);
        Map<String, PartnerBean> partnerBeanMap = partnerService.fetchAllPartners(soBean.getLoadingAgent());
        if ("EXPORT".equalsIgnoreCase(param)) {
            soBean.setLoadingAgentCode(soBean.getLoadingAgent());
            soBean.setLoadingAgentName(partnerBeanMap.get(soBean.getLoadingAgent()).getDescription1());
            soBean.setLoadingAgentContactDetails(soBean.getLoadingAgentName() + "\n" + partnerBeanMap.get(soBean.getLoadingAgent()).getAddress1());
        } else {
            soBean.setDestinationAgentCode(soBean.getLoadingAgent());
            soBean.setDestinationAgentName(partnerBeanMap.get(soBean.getLoadingAgent()).getDescription1());
            soBean.setDestinationAgentContactDetails(soBean.getDestinationAgentName() + "\n" + partnerBeanMap.get(soBean.getLoadingAgent()).getAddress1());
        }
        return new ModelAndView("operation/SO", "command", soBean);
    }

    @PostMapping(value = "/saveSOContainer.fin")
    public @ResponseBody
    List<Map<String, String>> saveSOContainer(@ModelAttribute("containerBean") ContainerBean containerBean,
                                              HttpSession session) throws SQLException {
        setSessionData(containerBean, session);
        soService.saveLclFcl(containerBean);
        return new ArrayList<>();
    }

    @RequestMapping(value = "/retrieveSOContainer.fin", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String retrieveSOContainer(@ModelAttribute("soBean") SOBean soBean,
                                      HttpSession session,
                                      @RequestParam("bkgRefNo") int bkgRefNo,
                                      @RequestParam("lineNo") int lineNo) throws SQLException {
        setSessionData(soBean, session);
        soBean.setBkgRefNo(bkgRefNo);
        ContainerBean cb = soService.getContainer(soBean, lineNo, 1);
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(cb));
        return jsonResponse.toString();
    }

    @RequestMapping(value = "/retrieveContainer.fin", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ModelAndView retrieveContainer(@ModelAttribute("soBean") SOBean soBean,
                                          HttpSession session, @RequestParam("bkgRefNo") int bkgRefNo,
                                          @RequestParam("soNumber") String soNumber,
                                          @RequestParam("lineNo") int lineNo, @RequestParam("type") String type,
                                          @RequestParam("param") String param) {
        setSessionData(soBean, session);
        soBean.setBkgRefNo(bkgRefNo);
        ContainerBean cb = soService.getContainer(soBean, lineNo, 1);
        if (cb == null) {
            cb = new ContainerBean();
        }
        cb.setBookingType(type);
        cb.setSizeMap(adminService.fetchAllSizes("sizes"));
        cb.setUnitMap(adminService.fetchAllUnits("units"));
        cb.setBookingRefNumber(bkgRefNo);
        cb.setSoNumber(soNumber);
        cb.setBookingType(type);
        cb.setExpImp(param);
        return new ModelAndView("operation/SOContainer", "command", cb);
    }

    @RequestMapping("/soCopy.fin")
    public ModelAndView copySO(@ModelAttribute("soBean") SOBean soBean,
                               HttpSession session) throws SQLException {
        setSessionData(soBean, session);
        String msg = validate(soBean);
        if (msg != null) {
            soBean.setErrMsg(msg);
            return new ModelAndView("operation/SO", "command", soBean);
        } else {
            soService.saveSO(soBean, true);
        }
        return new ModelAndView("operation/SO", "command", soBean);
    }

    @RequestMapping("/soFilePopup.fin")
    public ModelAndView soFilePopup(@ModelAttribute("fileUploadBean") FileUploadBean fileUploadBean,
                                    HttpSession session, @RequestParam("bkgRefNo") String bookingNum,
                                    @RequestParam("pod") String pod, @RequestParam("soNum") String soNum) {
        fileUploadBean.setParam1(bookingNum);
        fileUploadBean.setParam2(soNum);
        fileUploadBean.setParam3(pod);
        soService.retrieveFileUpload(fileUploadBean);
        fileUploadBean.setDummy(4 - fileUploadBean.getRowList().size());
        return new ModelAndView("operation/SoFilePopup", "command", fileUploadBean);
    }

    @RequestMapping("/uploadSoFile.fin")
    public ModelAndView uploadSoFile(@ModelAttribute("fileUploadBean") FileUploadBean fileUploadBean,
                                     HttpSession session) {
        setSessionData(fileUploadBean, session);
        soService.uploadFile(fileUploadBean);
        return new ModelAndView("operation/SOSearch", "command", fileUploadBean);
    }

    @RequestMapping("/soFileDownload.fin")
    public ModelAndView soFileDownload(@ModelAttribute("fileUpload") FileUploadBean fileUploadBean,
                                       HttpSession session, @RequestParam("bookingNum") String bookingNum,
                                       @RequestParam("fileName") String fileName, HttpServletResponse response) {
        setSessionData(fileUploadBean, session);
        fileUploadBean.setParam1(bookingNum);
        //soService.soFileDownload(fileUpload, fileName, response);
        return new ModelAndView("operation/SoFilePopup", "command", fileUploadBean);
    }

    @RequestMapping("/soDelete.fin")
    public String soDelete(@ModelAttribute("SOBean") SOBean soBean,
                                 HttpSession session, @RequestParam("bookingRefNumber") String bkgRefNo,
                                 @RequestParam("lineNo") String lineNo,
                                 @RequestParam("param") String param) {
        setSessionData(soBean, session);
        soService.deleteFclLcl(bkgRefNo, lineNo);
        logger.info("SO Container with Bkg ref no {} and line no {} deleted successfully.", bkgRefNo, lineNo);
        return "forward:/so.fin?param="+param;
    }

    @RequestMapping("/deleteSO.fin")
    public String deleteSO(@ModelAttribute("SOBean") SOBean bean,
                                 @RequestParam("param") String param){
        soService.deleteSO(bean);
        bean.setExpImp(param);
        return "forward:/so.fin";
    }
}
