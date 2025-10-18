package com.finops.freight.controller;

import com.finops.controller.AbstractController;
import com.finops.freight.bean.*;
import com.finops.freight.service.HawbService;
import com.finops.freight.service.SIService;
import com.finops.partner.model.PartnerBean;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class HawbController extends AbstractController {

    @Autowired
    private HawbService hawbService;

    @Autowired
    private SIService siService;

    @RequestMapping("/hawb.fin")
    public ModelAndView hawb(@ModelAttribute("hawbBean") HawbBean bean,
                           @RequestParam("param") String param,
                             HttpSession session) {
        setSessionData(bean, session);
        bean.setExpImp(param);
        session.setAttribute("HAWBSEARCHBEAN"+param, bean);
        return new ModelAndView("operation/HawbSearch", "command", bean);
    }

    @RequestMapping(value = "/hawbGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String blGson(@ModelAttribute("hawbBean") HawbBean bean,
                  HttpServletRequest request, HttpSession session,
                  @RequestParam("param") String param) {
        bean = (HawbBean) session.getAttribute("HAWBSEARCHBEAN"+param);
        //setSessionData(bean, session);
        bean.setExpImp(param);
        bean.setStart(Integer.parseInt(request.getParameter("start")));
        bean.setLength(Integer.parseInt(request.getParameter("length")));
        bean.setSearchFieldValueList(getSearchFieldValues(request, 14));
        Map<String, PartnerBean> partnerBeanMap = partnerService.fetchAllPartners(bean.getLoadingAgent());
        List<HawbBean> list = hawbService.findHawbByPage(bean, partnerBeanMap);

        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping("/hawbRetrieve.fin")
    public ModelAndView blRetrieve(@ModelAttribute("hawbBean") HawbBean bean,
                                   @RequestParam("blNumber") String blNumber,
                                   @RequestParam("param") String param,
                                   HttpSession session) {
        setSessionData(bean, session);
        bean.setBlNumber(blNumber);
        BLBean returnedBean = hawbService.retrieve(bean);
        returnedBean.setExpImp(param);
        returnedBean.setAction("UPDATE");
        returnedBean.setSignatureMap(adminService.getSignatures(bean.getLoadingAgent()+"BL"));
        return new ModelAndView("operation/HAWB", "command", returnedBean);
    }

    @RequestMapping(value = "/hawbContainerGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String hawbContainerGson(@ModelAttribute("hawbBean") HawbBean bean, @RequestParam("blNo") String blNo,
                           HttpServletRequest request, HttpSession session) {
        setSessionData(bean, session);
        prePaginate(bean, request, 14);
        bean.setBlNumber(blNo);
        List<ContainerBean> list = null;
        if(StringUtils.hasText(blNo)) {
            list = hawbService.getContainerDetails(bean);
        }
        else{
            list = (List<ContainerBean>)session.getAttribute("HAWB_CREATE_CONTAINERS");
        }
        return postPaginate(list);
    }

    @RequestMapping(value = "/retrieveHawbContainer.fin", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ModelAndView retrieveHawbContainer(@ModelAttribute("hawbBean") HawbBean bean,
                                            HttpSession session, @RequestParam("bkgRefNo") int bkgRefNo,
                                            @RequestParam("blNo") String blNo,
                                            @RequestParam("lineNo") int lineNo) throws SQLException {
        setSessionData(bean, session);
        bean.setBkgRefNo(bkgRefNo);
        bean.setBlNumber(blNo);
        ContainerBean cb = hawbService.getHawbContainer(bean, lineNo);
        //cb.setSizeMap(adminService.fetchAllSizes("sizes"));
        cb.setUnitMap(adminService.fetchAllUnits("units"));
        return new ModelAndView("operation/HawbContainer", "command", cb);
    }

    @RequestMapping("/hawbSave.fin")
    public ModelAndView hawbSave(@ModelAttribute("hawbBean") HawbBean bean,
                               HttpSession session) throws SQLException {
        setSessionData(bean, session);
        String msg = validate(bean);
        if(msg != null){
            bean.setErrMsg(msg);
            return new ModelAndView("operation/HAWB", "command", bean);
        }
        else {
            int[] ids = (int[])session.getAttribute("SI_BKG_REF_IDS");
            bean.setCbList((List<ContainerBean>) session.getAttribute("HAWB_CREATE_CONTAINERS"));
            bean.setIds(ids);
            hawbService.save(bean);
            session.removeAttribute("SI_BKG_REF_IDS");
            session.removeAttribute("BL_CREATE_CONTAINERS");
        }
        bean.setAction("UPDATE");
        return new ModelAndView("operation/HAWB", "command", bean);
    }

    protected String validate(SOBean soBean){
        String fieldName = null;
        if(!validatePartner(soBean.getShipper(), soBean.getShipperName(), soBean, true)){
            fieldName = "SHPR";
        }


        if(fieldName != null){
            return "Invalid "+fieldName+ " or "+fieldName+ "Name.";
        }
        return null;
    }

    @PostMapping(value = "/saveHawbContainer.fin", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public List<Map<String, String>> saveBLContainer(@ModelAttribute("containerBean") ContainerBean containerBean,
                                                     HttpSession session) throws SQLException {
        setSessionData(containerBean, session);
        hawbService.updateContainer(containerBean);
        return new ArrayList<>();
    }

    @RequestMapping("/hawbCreate.fin")
    public ModelAndView hawb(@ModelAttribute("blBean") BLBean bean,
                             @RequestParam("param") String param) {
        bean.setExpImp(param);
        bean.setAction("SAVE");

        return new ModelAndView("operation/HawbCreate", "command", bean);
    }

    @RequestMapping(value = "/hawbSiSearchGSON.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String hawbSiSearchGSON(@ModelAttribute("blBean") BLBean bean,
                          HttpServletRequest request, HttpSession session,
                          @RequestParam("param") String param) {
        SOBean soBean = (SOBean) session.getAttribute("SIINSEARCHFORHAWB");
        List<SIBean> listOfSO = new ArrayList<SIBean>();
        if(soBean != null) {
            setSessionData(soBean, session);
            soBean.setExpImp(param);
            soBean.setStart(Integer.parseInt(request.getParameter("start")));
            soBean.setLength(20);
            soBean.setAction("HAWB_CREATE");
            listOfSO = siService.findSIByPage(soBean);
        }

        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(listOfSO));

        return jsonResponse.toString();
    }


    @RequestMapping("/hawbSISearch.fin")
    public ModelAndView hawbSISearch(@ModelAttribute("hawbBean") HawbBean bean,
                                   HttpSession session) {
        SOBean soBean = new SOBean();
        setSessionData(soBean, session);
        soBean.setShipper(bean.getShipper()); //"VM004"
        soBean.setConsignee(bean.getConsignee());//"BAP010"
        soBean.setPol(bean.getPol());//"INNDH"
        soBean.setPod(bean.getPod());//"AMS"
        soBean.setDest(bean.getDest());//"VLC"
        soBean.setBkgType(bean.getBkgType());
        session.setAttribute("SIINSEARCHFORHAWB", soBean);
        return new ModelAndView("operation/HawbCreate", "command", bean);
    }

    @RequestMapping(value = "/generateHawb.fin", method = RequestMethod.POST)
    public ModelAndView generateHawb(@ModelAttribute("hawbBean") HawbBean bean,
                                   HttpSession session) {
        setSessionData(bean, session);

        int[] bkgRefNos = bean.getIds();
        if (bkgRefNos == null || bkgRefNos.length == 0) {
            bean.setErrMsg("Please select a record");
            return new ModelAndView("operation/HawbCreate", "command", bean);
        }


        SIBean sb = siService.retrieve(bean, true);
        if(sb == null){
            bean.setErrMsg("Records do not match");
            return new ModelAndView("operation/HawbCreate", "command", bean);
        }
        session.setAttribute("SI_BKG_REF_IDS", bkgRefNos);
        session.setAttribute("HAWB_CREATE_CONTAINERS", bean.getCbList());
        session.removeAttribute("SIINSEARCHFORHAWB");
        bean.setBlReleaseDate(DateUtil.getSystemDate());
        return new ModelAndView("operation/HAWB", "command", bean);
    }

    @RequestMapping(value = "/hawbDelete.fin", method = RequestMethod.POST)
    public String hawbDelete(@ModelAttribute("hawbBean") HawbBean bean,
                                     HttpSession session) {
        hawbService.deletHAWB(bean);
        return "forward:/hawb.fin";
    }

    @RequestMapping("/hawbPDF.fin")
    public StreamingResponseBody hawbPDF(@ModelAttribute("blBean") HawbBean blBean,
                                       @RequestParam("blNo") String blNo,
                                       @RequestParam("type") String printType,
                                       HttpSession session, HttpServletResponse response) throws SQLException {
        setSessionData(blBean, session);
        blBean.setBlNumber(blNo);
        blBean.setLegacyReportPath(blBean.getLegacyReportPath()+"BL/");
        hawbService.blPDFDraft(blBean);

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
}
