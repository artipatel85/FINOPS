package com.finops.freight.controller;

import com.finops.bean.FileUploadBean;
import com.finops.controller.AbstractController;
import com.finops.freight.bean.ContainerBean;
import com.finops.freight.bean.SIBean;
import com.finops.freight.bean.SOBean;
import com.finops.freight.service.SIService;
import com.finops.freight.service.SOService;
import com.finops.partner.model.PartnerBean;
import com.finops.partner.service.PartnerService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
public class SIController extends AbstractController {

    @Autowired
    private SIService siService;

    @Autowired
    private PartnerService partnerService;

    @RequestMapping("/si.fin")
    public ModelAndView si(@ModelAttribute("siBean") SIBean bean,
                           @RequestParam("param") String param,
                           HttpSession session) {
        setSessionData(bean, session);
        bean.setExpImp(param);
        session.setAttribute("SISEARCHBEAN"+param, bean);
        return new ModelAndView("operation/SISearch", "command", bean);
    }

    @RequestMapping(value = "/siGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String siGson(@ModelAttribute("siBean") SIBean bean,
                  HttpServletRequest request, HttpSession session,
                  @RequestParam("param") String param) {
        bean = (SIBean) session.getAttribute("SISEARCHBEAN"+param);
        //setSessionData(bean, session);
        bean.setExpImp(param);
        bean.setStart(Integer.parseInt(request.getParameter("start")));
        bean.setLength(Integer.parseInt(request.getParameter("length")));
        bean.setSearchFieldValueList(getSearchFieldValues(request, 14));
        List list = siService.findSIByPage(bean);

        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        jsonResponse.add("iTotalRecords", gson.toJsonTree(bean.getITotalRecords()));
        jsonResponse.add("iTotalDisplayRecords", gson.toJsonTree(bean.getITotalRecords()));
        return jsonResponse.toString();
    }

    @RequestMapping("/siRetrieve.fin")
    public ModelAndView siRetrieve(@ModelAttribute("siBean") SIBean bean,
                                   @RequestParam("bookingRefNumber") int bookingRefNumber,
                                   @RequestParam("param") String param,
                                   HttpSession session) {
        setSessionData(bean, session);
        bean.setBkgRefNo(bookingRefNumber);
        siService.retrieve(bean, false);
        bean.setSizeMap(adminService.fetchAllSizes("sizes"));
        bean.setUnitMap(adminService.fetchAllUnits("units"));
        bean.setExpImp(param);
        return new ModelAndView("operation/SI", "command", bean);
    }

    @RequestMapping(value = "/siContainerGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String siContainerGson(@ModelAttribute("siBean") SIBean bean, @RequestParam("bkgRefNo") int bkgRefNo,
                       HttpServletRequest request, HttpSession session) {
        setSessionData(bean, session);
        bean.setStart(Integer.parseInt(request.getParameter("start")));
        bean.setLength(Integer.parseInt(request.getParameter("length")));
        bean.setSearchFieldValueList(getSearchFieldValues(request, 14));
        bean.setBkgRefNo(bkgRefNo);
        List<ContainerBean> list = siService.getContainerDetails(bean);

        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping("/siSave.fin")
    public ModelAndView siSave(@ModelAttribute("siBean") SIBean bean,
                               HttpSession session) throws SQLException {
        setSessionData(bean, session);
        String msg = validate(bean);
        if(msg != null){
            bean.setErrMsg(msg);
            return new ModelAndView("operation/SI", "command", bean);
        }
        else {
            siService.saveSI(bean, false);
        }
        return new ModelAndView("operation/SISearch", "command", bean);
    }

    protected String validate(SOBean soBean){
        String fieldName = null;
        if(!validatePartner(soBean.getShipper(), soBean.getShipperName(), soBean, true)){
            fieldName = "SHPR";
        }
        else if(!validatePort(soBean.getPod(), soBean.getPodName(), soBean)){
            fieldName = "POD";
        }

        if(fieldName != null){
            return "Invalid "+fieldName+ " or "+fieldName+ "Name.";
        }
        return null;
    }

    @RequestMapping("/siPDF.fin")
    public StreamingResponseBody siPDF(@ModelAttribute("siBean") SIBean soBean,
                                       @RequestParam("bookingRefNumber") int bookingRefNumber,
                                       HttpSession session, HttpServletResponse response) throws SQLException {
        setSessionData(soBean, session);
        soBean.setBkgRefNo(bookingRefNumber);
        soBean.setLegacyReportPath(soBean.getLegacyReportPath()+"SO/");
        siService.siPDF(soBean);

        response.setContentType("application/pdf");
        try {
            InputStream inputStream = new FileInputStream(new File(soBean.getLegacyReportPath()+soBean.getBkgRefNo()+".pdf"));
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


    @RequestMapping("/siCreate.fin")
    public ModelAndView siCreate(@ModelAttribute("siBean") SIBean bean,
                                 @RequestParam("param") String param,
                                              HttpSession session) {
        setSessionData(bean, session);
        Map<String, PartnerBean> partnerBeanMap = partnerService.fetchAllPartners(bean.getLoadingAgent());
        if("EXPORT".equalsIgnoreCase(param)) {
            bean.setLoadingAgentCode(bean.getLoadingAgent());
            bean.setLoadingAgentName(partnerBeanMap.get(bean.getLoadingAgent()).getDescription1());
            bean.setLoadingAgentContactDetails(bean.getLoadingAgentName()+"\n"+partnerBeanMap.get(bean.getLoadingAgent()).getAddress1());
        }
        else{
            bean.setDestinationAgentCode(bean.getLoadingAgent());
            bean.setDestinationAgentName(partnerBeanMap.get(bean.getLoadingAgent()).getDescription1());
            bean.setDestinationAgentContactDetails(bean.getDestinationAgentName()+"\n"+partnerBeanMap.get(bean.getLoadingAgent()).getAddress1());
        }
        bean.setExpImp(param);
        return new ModelAndView("operation/SI", "command", bean);
    }

    @PostMapping(value = "/saveSIContainer.fin")
    public @ResponseBody
    List<Map<String, String>> saveSIContainer(@ModelAttribute("containerBean") ContainerBean containerBean,
                                        HttpSession session) throws SQLException {
        setSessionData(containerBean, session);
        siService.saveLclFcl(containerBean);
        return new ArrayList<>();
    }
//
//    @RequestMapping(value = "/retrieveSIContainer.fin", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
//    @ResponseBody
//    public String retrieveSIContainer(@ModelAttribute("soBean") SOBean soBean,
//                                      HttpSession session, @RequestParam("bkgRefNo") int bkgRefNo,
//                                      @RequestParam("lineNo") String lineNo) throws SQLException {
//        setSessionData(soBean, session);
//        soBean.setBkgRefNo(bkgRefNo);
//        ContainerBean cb = soService.getContainer(soBean, lineNo);
//        Gson gson = new GsonBuilder().serializeNulls().create();
//        JsonObject jsonResponse = new JsonObject();
//        jsonResponse.add("aaData", gson.toJsonTree(cb));
//        return jsonResponse.toString();
//    }
//
    @RequestMapping(value = "/retrieveSIContainer.fin", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ModelAndView retrieveSIContainer(@ModelAttribute("siBean") SIBean soBean,
                                  HttpSession session, @RequestParam("bkgRefNo") int bkgRefNo,
                                            @RequestParam("soNumber") String soNumber) throws SQLException {
        setSessionData(soBean, session);
        soBean.setBkgRefNo(bkgRefNo);
        ContainerBean cb = siService.getContainer(soBean);
        if(cb == null){
            cb = new ContainerBean();
            cb.setAction("SAVE");
        }
        cb.setSizeMap(adminService.fetchAllSizes("sizes"));
        cb.setUnitMap(adminService.fetchAllUnits("units"));
        cb.setBookingRefNumber(bkgRefNo);
        cb.setSoNumber(soNumber);
        return new ModelAndView("operation/SIContainer", "command", cb);
    }

    @RequestMapping("/siCopy.fin")
    public ModelAndView copySO(@ModelAttribute("siBean") SIBean soBean,
                               HttpSession session) throws SQLException {
        setSessionData(soBean, session);
        String msg = validate(soBean);
        if(msg != null){
            soBean.setErrMsg(msg);
            return new ModelAndView("operation/SO", "command", soBean);
        }
        else {
            siService.saveSI(soBean, true);
        }
        return new ModelAndView("operation/SI", "command", soBean);
    }
//
//    @RequestMapping("/soFilePopup.fin")
//    public ModelAndView soFilePopup(@ModelAttribute("fileUploadBean") FileUploadBean fileUploadBean,
//                                    HttpSession session, @RequestParam("bkgRefNo") String bookingNum,
//                                    @RequestParam("pod") String pod, @RequestParam("soNum") String soNum) throws SQLException {
//        fileUploadBean.setParam1(bookingNum);
//        fileUploadBean.setParam2(soNum);
//        fileUploadBean.setParam3(pod);
//        //soService.retrieveFileUpload(fileUpload);
//        fileUploadBean.setDummy(4 - fileUploadBean.getRowList().size());
//        return new ModelAndView("operation/SoFilePopup", "command", fileUploadBean);
//    }
//
//    @RequestMapping("/uploadSoFile.fin")
//    public ModelAndView uploadSoFile(@ModelAttribute("fileUploadBean") FileUploadBean fileUploadBean,
//                                     HttpSession session) throws SQLException {
//        setSessionData(fileUploadBean, session);
//        soService.uploadFile(fileUploadBean);
//        return new ModelAndView("operation/SoFilePopup", "command", fileUploadBean);
//    }
//
//    @RequestMapping("/soFileDownload.fin")
//    public ModelAndView soFileDownload(@ModelAttribute("fileUpload") FileUploadBean fileUploadBean,
//                                       HttpSession session, @RequestParam("bookingNum") String bookingNum,
//                                       @RequestParam("fileName") String fileName, HttpServletResponse response) throws SQLException {
//        setSessionData(fileUploadBean, session);
//        fileUploadBean.setParam1(bookingNum);
//        //soService.soFileDownload(fileUpload, fileName, response);
//        return new ModelAndView("operation/SoFilePopup", "command", fileUploadBean);
//    }

    @RequestMapping(value = "/siContainerByJobGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String siContainerByJobGson(@ModelAttribute("siBean") SIBean bean, @RequestParam("jobNumber") String jobNumber,
                                HttpServletRequest request, HttpSession session) {
        setSessionData(bean, session);
        bean.setJobNumber(jobNumber);
        return createPaginatedResponse(bean, request, 2);
    }

    private String createPaginatedResponse(SIBean bean, HttpServletRequest request, int type){
        prePaginate(bean, request, 14);
        List<ContainerBean> list = siService.getContainerDetailsForJob(bean);
        return postPaginate(list);
    }
}
