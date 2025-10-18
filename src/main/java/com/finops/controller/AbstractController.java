/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.controller;

import com.finops.admin.model.LoginBean;
import com.finops.admin.service.AdminService;
import com.finops.bean.AbstractBean;
import com.finops.dao.AbstractDAO;
import com.finops.freight.bean.SOBean;
import com.finops.freight.service.SOService;
import com.finops.partner.model.PartnerBean;
import com.finops.partner.service.PartnerService;
import com.finops.util.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class AbstractController {
    @Autowired
    protected ServletContext servletContext;

    protected static final String PARAM = "param";
    protected static final String QUERY = "query";
    protected static final String INDEX = "index";
    protected static final String POPUP_METADATA= "popupMetaData";
    protected static final String PARAM_LENGTH = "plenth";

    @Autowired
    protected PartnerService partnerService;

    @Autowired
    protected AdminService adminService;

    protected void setMDC(String module, String id, String number, String userId){
        MDC.put(Constants.MODULE, module);
        MDC.put(Constants.ID, id);
        MDC.put(Constants.NUMBER, number);
        MDC.put(Constants.USERID, userId);
    }

    protected void setSessionData(AbstractBean bean, HttpSession session){
        LoginBean sessionBean = (LoginBean) session.getAttribute("loginLst");
        bean.setLoadingAgent(sessionBean.getUserBean().getBranch());
        bean.setYrStartDate(sessionBean.getPeriodBean().getStartDate());
        bean.setYrEndDate(sessionBean.getPeriodBean().getEndDate());
        bean.setUserId(sessionBean.getUserBean().getUserId());
        bean.setAcctYear(sessionBean.getPeriodBean().getAcctYear());
        bean.setPeriodStatus(sessionBean.getPeriodBean().getPeriodStatus());
        bean.setCompanyId(sessionBean.getUserBean().getCompanyId());
        bean.setCompanyName(sessionBean.getUserBean().getCompanyName());
        bean.setCompanyAddress(sessionBean.getUserBean().getCompanyAddress());
        bean.setAdministrator("A".equals(sessionBean.getUserBean().getRole())? true : false);
        bean.setLegacyReportPath("D://Bhaumik/");
        bean.setFileserverRootPath(sessionBean.getFinactProperties().get("FILESERVER_ROOT_PATH"));
        bean.setAzureEndPoint(sessionBean.getFinactProperties().get("AzureEndPoint"));
        bean.setIrnEndpoint(sessionBean.getFinactProperties().get("IRN_Endpoint"));
        bean.setCompanyTel(sessionBean.getUserBean().getCompanyTel());
        bean.setCompanyFax(sessionBean.getUserBean().getCompanyFax());
        bean.setCompanyEmail(sessionBean.getUserBean().getCompanyEmail());
        bean.setCompanyUrl(sessionBean.getUserBean().getCompanyUrl());
        bean.setNote(sessionBean.getUserBean().getNote());
        bean.setCompanyStateCode(sessionBean.getUserBean().getCompanyStateCode());
        bean.setCompanyPan(sessionBean.getPartnerAccount().getPanNo());
        bean.setCompanyTan(sessionBean.getPartnerAccount().getTanNo());
        bean.setCompanyGstinNo(sessionBean.getPartnerAccount().getGstinNo());
        bean.setCutOffDate(sessionBean.getFinactProperties().get("CUT_OFF_DATE"));
    }

    protected List<String> getSearchFieldValues(HttpServletRequest request, int length){
        //columns[4][search][value]
        List<String> fieldValueList = new ArrayList<>();
        for(int i=0; i<length; i++){
            fieldValueList.add(request.getParameter("columns["+i+"][search][value]"));
        }
        return fieldValueList;
    }

    protected boolean validatePartner(String partnerCode, String partnerName, AbstractBean bean, boolean mandatory){
        Map<String, PartnerBean> partnerBeanMap = partnerService.fetchAllPartners(bean.getLoadingAgent());

        if(mandatory || StringUtils.hasText(partnerCode)) {
            if (partnerBeanMap.containsKey(partnerCode)) {
                return partnerBeanMap.get(partnerCode).getDescription1().equals(partnerName);
            }
        }
        else {
            return true;
        }
        return false;
    }

    protected boolean validatePort(String portCode, String portName, SOBean bean){
        Map<String, String> allPorts = adminService.fetchAllPorts("ports");

        if(allPorts.containsKey(portCode)){
            return true;
        }
        return false;
    }

    protected void prePaginate(AbstractBean bean, HttpServletRequest request, int length){
        bean.setStart(Integer.parseInt(request.getParameter("start")));
        bean.setLength(Integer.parseInt(request.getParameter("length")));
        bean.setSearchFieldValueList(getSearchFieldValues(request, length));
    }

    protected String postPaginate(List list){
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    protected String validate(SOBean soBean){
        String fieldName = null;
        if(!validatePartner(soBean.getShipper(), soBean.getShipperName(), soBean, true)){
            fieldName = "SHPR";
        }
        else if(!validatePartner(soBean.getConsignee(), soBean.getConsigneeName(), soBean, false)){
            fieldName = "CNEE";
        }
        else if(!validatePartner(soBean.getSoNotify(), soBean.getSoNotifyName(), soBean, false)){
            fieldName = "NOTIFY";
        }
        else if(!validatePartner(soBean.getAlsoNotify(), soBean.getAlsoNotifyName(), soBean, false)){
            fieldName = "ALSONOTIFY";
        }
        else if(!validatePartner(soBean.getLoadingAgentCode(), soBean.getLoadingAgentName(), soBean, false)){
            fieldName = "LOADINGAGENT";
        }
        else if(!validatePartner(soBean.getDestinationAgentCode(), soBean.getDestinationAgentName(), soBean, false)){
            fieldName = "DESTAGENT";
        }
        else if(!validatePort(soBean.getPod(), soBean.getPodName(), soBean)){
            fieldName = "POD";
        }
        else if(!validatePort(soBean.getPor(), soBean.getPorName(), soBean)){
            fieldName = "POR";
        }

        if(fieldName != null){
            return "Invalid "+fieldName+ " or "+fieldName+ "Name.";
        }
        return null;
    }
}
