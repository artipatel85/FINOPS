package com.finops.freight.controller;

import com.finops.controller.AbstractController;
import com.finops.freight.bean.BLBean;
import com.finops.freight.bean.ContainerBean;
import com.finops.freight.bean.SOBean;
import com.finops.freight.report.BLDOImportReport;
import com.finops.freight.report.BLPDFReport;
import com.finops.freight.service.BLService;
import com.finops.freight.service.SOService;
import com.finops.partner.model.PartnerBean;
import com.finops.partner.service.PartnerService;
import com.finops.report.AbstractReport;
import com.finops.util.DateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class BLController extends AbstractController {

    @Autowired
    private BLService blService;

    @Autowired
    private SOService soService;

    @RequestMapping("/bl.fin")
    public ModelAndView bl(@ModelAttribute("blBean") BLBean bean,
                           @RequestParam("param") String param,
                           HttpSession session) {
        setSessionData(bean, session);
        bean.setExpImp(param);
        session.setAttribute("BLSEARCHBEAN"+param, bean);
        return new ModelAndView("operation/BLSearch", "command", bean);
    }

    @RequestMapping(value = "/blGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String blGson(@ModelAttribute("blBean") BLBean bean,
                  HttpServletRequest request, HttpSession session,
                  @RequestParam("param") String param) {
        bean = (BLBean) session.getAttribute("BLSEARCHBEAN"+param);
        //setSessionData(bean, session);
        bean.setExpImp(param);
        bean.setStart(Integer.parseInt(request.getParameter("start")));
        bean.setLength(Integer.parseInt(request.getParameter("length")));
        bean.setSearchFieldValueList(getSearchFieldValues(request, 14));
        Map<String, PartnerBean> partnerBeanMap = partnerService.fetchAllPartners(bean.getLoadingAgent());
        List<BLBean> list = blService.findBLByPage(bean, partnerBeanMap);

        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping("/blCreate.fin")
    public ModelAndView blCreate(@ModelAttribute("blBean") BLBean bean,
                           @RequestParam("param") String param) {
        bean.setExpImp(param);
        return new ModelAndView("operation/BLCreate", "command", bean);
    }

    @RequestMapping("/blSoSearch.fin")
    public ModelAndView blSoSearch(@ModelAttribute("blBean") BLBean bean,
                                   HttpSession session) {
        SOBean soBean = new SOBean();
        setSessionData(soBean, session);
        soBean.setShipper(bean.getShipper()); //"VM004"
        soBean.setConsignee(bean.getConsignee());//"BAP010"
        soBean.setPol(bean.getPol());//"MNU"
        soBean.setPod(bean.getPod());//"VLC"
        soBean.setDest(bean.getDest());//"VLC"
        soBean.setBkgType(bean.getBkgType());
        soBean.setExpImp(bean.getExpImp());
        session.setAttribute("SOINSEARCHFORBL", soBean);
        return new ModelAndView("operation/BLCreate", "command", bean);
    }

    @RequestMapping(value = "/blSoSearchGSON.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String blSoSearchGSON(@ModelAttribute("blBean") BLBean bean,
                  HttpServletRequest request, HttpSession session,
                  @RequestParam("param") String param) {
        SOBean soBean = (SOBean) session.getAttribute("SOINSEARCHFORBL");
        List<SOBean> listOfSO = new ArrayList<SOBean>();
        if(soBean != null) {
            setSessionData(soBean, session);
            soBean.setExpImp(param);
            soBean.setStart(Integer.parseInt(request.getParameter("start")));
            soBean.setLength(20);
            soBean.setAction("BL_CREATE");
            listOfSO = soService.findSOByPage(soBean);
        }

        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(listOfSO));

        return jsonResponse.toString();
    }

    @RequestMapping(value = "/generateBL.fin", method = RequestMethod.POST)
    public ModelAndView generateBL(@ModelAttribute("blBean") BLBean blBean,
                                   @RequestParam("param") String param,
                                      HttpSession session) {
        setSessionData(blBean, session);

        int[] bkgRefNos = blBean.getIds();
        if (bkgRefNos == null || bkgRefNos.length == 0) {
            blBean.setErrMsg("Please select a record");
            return new ModelAndView("operation/BLCreate", "command", blBean);
        }

        SOBean sb = soService.retrieve(blBean, true);
        if(sb == null){
            blBean.setErrMsg("Records do not match");
            return new ModelAndView("operation/BLCreate", "command", blBean);
        }
        session.setAttribute("SO_BKG_REF_IDS", bkgRefNos);
        session.setAttribute("BL_CREATE_CONTAINERS", blBean.getCbList());
        session.removeAttribute("SOINSEARCHFORBL");
        blBean.setBlReleaseDate(DateUtil.getSystemDate());
        blBean.setExpImp(param);
        blBean.setAction("SAVE");
        return new ModelAndView("operation/BL", "command", blBean);
    }

    @RequestMapping("/blRetrieve.fin")
    public ModelAndView blRetrieve(@ModelAttribute("blBean") BLBean blBean,
                                   @RequestParam("blNumber") String blNumber,
                                   @RequestParam("param") String param,
                                   HttpSession session) {
        setSessionData(blBean, session);
        blBean.setBlNumber(blNumber);
        BLBean returnedBean = blService.retrieve(blBean);
        returnedBean.setExpImp(param);
        returnedBean.setAction("UPDATE");
        returnedBean.setSignatureMap(adminService.getSignatures(blBean.getLoadingAgent()+"BL"));
        return new ModelAndView("operation/BL", "command", returnedBean);
    }

    @RequestMapping("/blSave.fin")
    public ModelAndView blSave(@ModelAttribute("blBean") BLBean blBean,
                               HttpSession session) throws SQLException {
        setSessionData(blBean, session);
        String msg = validate(blBean);
        if(msg != null){
            blBean.setErrMsg(msg);
            return new ModelAndView("operation/BL", "command", blBean);
        }
        else {
            int[] ids = (int[])session.getAttribute("SO_BKG_REF_IDS");
            blBean.setCbList((List<ContainerBean>) session.getAttribute("BL_CREATE_CONTAINERS"));
            blBean.setIds(ids);
            blService.save(blBean);
            session.removeAttribute("SO_BKG_REF_IDS");
            session.removeAttribute("BL_CREATE_CONTAINERS");
            blBean.setSignatureMap(adminService.getSignatures(blBean.getLoadingAgent()+"BL"));
        }
        blBean.setAction("UPDATE");
        return new ModelAndView("operation/BL", "command", blBean);
    }

    @PostMapping(value = "/saveBLContainer.fin", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public List<Map<String, String>> saveBLContainer(@ModelAttribute("containerBean") ContainerBean containerBean,
                                        HttpSession session) throws SQLException {
        setSessionData(containerBean, session);
        blService.updateContainer(containerBean);
        return new ArrayList<>();
    }

    @RequestMapping(value = "/retrieveBLContainer.fin", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ModelAndView retrieveBLContainer(@ModelAttribute("blBean") BLBean blBean,
                                      HttpSession session, @RequestParam("bkgRefNo") int bkgRefNo,
                                      @RequestParam("blNo") String blNo,
                                      @RequestParam("lineNo") int lineNo) throws SQLException {
        setSessionData(blBean, session);
        blBean.setBkgRefNo(bkgRefNo);
        blBean.setBlNumber(blNo);
        ContainerBean cb = blService.getBlContainer(blBean, lineNo);
        cb.setSizeMap(adminService.fetchAllSizes("sizes"));
        cb.setUnitMap(adminService.fetchAllUnits("units"));
        return new ModelAndView("operation/BLContainer", "command", cb);
    }



    @RequestMapping(value = "/blContainerGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String blContainerGson(@ModelAttribute("blBean") BLBean bean, @RequestParam("blNo") String blNo,
                           HttpServletRequest request, HttpSession session) {
        setSessionData(bean, session);
        prePaginate(bean, request, 14);
        bean.setBlNumber(blNo);
        List<ContainerBean> list = null;
        if(StringUtils.hasText(blNo)) {
            list = blService.getContainerDetails(bean);
        }
        else{
            list = (List<ContainerBean>)session.getAttribute("BL_CREATE_CONTAINERS");
        }
        return postPaginate(list);
    }

    @RequestMapping("/blPDF.fin")
    public StreamingResponseBody blPDF(@ModelAttribute("blBean") BLBean blBean,
                                       @RequestParam("blNo") String blNo,
                                       @RequestParam("type") String printType,
                                       HttpSession session, HttpServletResponse response) throws SQLException {
        setSessionData(blBean, session);
        blBean.setBlNumber(blNo);
        blBean.setLegacyReportPath(blBean.getLegacyReportPath()+"BL/");
        if("DRAFT".equalsIgnoreCase(printType)){
            blService.blPDFDraft(blBean);
        }
        else {
            blService.blPDF(blBean);
        }

        response.setContentType("application/pdf");
        try {
            InputStream inputStream = new FileInputStream(new File(blBean.getLegacyReportPath()+blBean.getBlNumber()+".pdf"));
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

    @PostMapping(value = "/approveBL.fin", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ModelAndView approveBL(@ModelAttribute("blBean") BLBean blBean,
                                      HttpSession session,
                                  @RequestParam("param") String param) throws SQLException {
        setSessionData(blBean, session);
        blService.approve(blBean);
        BLBean returnedBean = blService.retrieve(blBean);
        returnedBean.setAction("UPDATE");
        returnedBean.setExpImp(param);
        returnedBean.setAction("UPDATE");
        returnedBean.setSignatureMap(adminService.getSignatures(blBean.getLoadingAgent()+"BL"));
        return new ModelAndView("operation/BL", "command", returnedBean);
    }

    @RequestMapping("/blPrint.fin")
    public ModelAndView blPrint(@ModelAttribute("blBean") BLBean blBean,
                                HttpServletRequest request, HttpServletResponse response,
                                @RequestParam("blNo") String blNo, @RequestParam("type") int type) throws Exception {
        setSessionData(blBean, request.getSession());
        blBean.setBlNumber(blNo);
        blBean.setStart(type);

//        if (!"A".equals(blBean.getLoggedInUserRole())) {
//            int creditPeriod = getCreditPeriod(request, loadingAgent, partnerCode);
//            if(creditPeriod > 0){
//                evaluateOutstanding(request,creditPeriod, blBean.getAcctYear());
//            }
//            else{
//                System.out.println("Credit Period is not set for the partner "+request.getParameter("code"));
//            }
//        }
        return new ModelAndView("operation/BLPrint", "command", blBean);
    }

    @RequestMapping("/blSysGeneratedPDF.fin")
    public ModelAndView blSysGenReport(@ModelAttribute("blBean") BLBean blBean,
                                       HttpServletRequest request, HttpServletResponse response,
                                       @RequestParam("blNo") String blNo, @RequestParam("type") int type,
                                       @RequestParam("author") String author) throws Exception {
        setSessionData(blBean, request.getSession());
        blBean.setBlNumber(blNo);
        blBean.setStart(type);
        String reportPath = "/reports/bl_system_gen_report.jrxml";
        if(type == 3){
            reportPath = "/reports/tnc.jrxml";
        }
        AbstractReport report = new BLPDFReport(reportPath, null, response, blBean, request.getServletContext(), blService);
        report.generateReport();
        return new ModelAndView("sales", "command", blBean);
    }

    @RequestMapping("/blSysGeneratedPDFUpload.fin")
    public ModelAndView blSysGenReportUpload(@ModelAttribute("blBean") BLBean blBean,
                                             HttpServletRequest request, HttpServletResponse response,
                                             @RequestParam("blNo") String blNo, @RequestParam("type") int type,
                                             @RequestParam("author") String author) throws Exception {

        setSessionData(blBean, request.getSession());
        blBean.setBlNumber(blNo);
        blBean.setStart(type);
        String reportPath = "/reports/bl_system_gen_report.jrxml";
        String fileName = blNo+"/"+blNo+"_BL.pdf";
        if(type == 3){
            reportPath = "/reports/tnc.jrxml";
            fileName = blNo+"/"+blNo+"_TNC.pdf";
        }
        AbstractReport report = new BLPDFReport(reportPath, null, response, blBean, request.getServletContext(),blService);
        report.generateReportStream();
        //ByteArrayOutputStream out = report.getByteArrayOutputStream();
        byte[] bytes = report.getBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        //AzureBlobUtil.uploadFile(fileName, in, bytes.length, "shik2022/patel");
        return new ModelAndView("sales", "command", blBean);
    }

    @RequestMapping("/doPDF.fin")
    public ModelAndView doPdf(@ModelAttribute("blBean") BLBean blBean,
                              HttpServletRequest request, HttpServletResponse response,
                              @RequestParam("blNo") String blNo, @RequestParam("type") int type) throws Exception {
        setSessionData(blBean, request.getSession());
        blBean.setBlNumber(blNo);
        blBean.setStart(type);
        String reportPath = "/reports/doReport.jrxml";

        AbstractReport report = new BLDOImportReport(reportPath, null, response, blBean,servletContext, blService);
        report.generateReport();
        return new ModelAndView("sales", "command", blBean);
    }

    @RequestMapping("/deleteBL.fin")
    public ModelAndView deleteBL(@ModelAttribute("blBean") BLBean blBean,
                              @RequestParam("param") String param){
        blService.deleteBL(blBean);
        blBean.setExpImp(param);
        return new ModelAndView("operation/BLSearch", "command", blBean);
    }
}
