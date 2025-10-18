/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.freight.report;

import com.finops.finance.report.PDFReport;
import com.finops.freight.bean.BLBean;
import com.finops.freight.service.BLService;
import com.finops.util.DateUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bhaumik
 */
public class BLDOImportReport extends PDFReport {

    private BLBean bean;
    private BLService blService;

    public BLDOImportReport(String reportTitle, Map params,
                            HttpServletResponse response, BLBean bean, ServletContext context,
                            BLService blService) {
        super(reportTitle, params, response, context);
        this.bean = bean;
        this.blService = blService;
    }

    public BLDOImportReport(Map params, BLBean bean){
        super(null, params, null,null);
        this.bean = bean;
    }

    @Override
    public void createData() {
        this.data = blService.getContainerDetails(bean);
        this.parameters.putAll(getParameters());
    }
    
    public Map getParameters(){
        Map params = new HashMap();
        if (bean.getStart() != 3) {
            bean.setReportRequest(true);

            try {
                BLBean blBean = blService.retrieve(bean);
                params.put("consignor", bean.getShipperContactDetails());
                params.put("consignee", blBean.getConsigneeName()+"\n"+blBean.getConsigneeContactDetails());
                params.put("notify", blBean.getSoNotifyName()+"\n"+blBean.getSoNotifyContactDetails());
                params.put("por", blBean.getPorName());
                params.put("pol", blBean.getPolName());
                params.put("pod", blBean.getPodName());
                params.put("destination", blBean.getDestName());
                params.put("precarriage", blBean.getPreCarriage());
                params.put("vsl", blBean.getVsl());
                params.put("voy", blBean.getVoy());
                params.put("imagepath", context.getRealPath("/images/shikhar_logo_bl.png"));
                params.put("backimage", context.getRealPath("/images/backimage.png"));
                params.put("wmark", context.getRealPath("/images/wmark-original.png"));
                params.putAll(blBean.getReportParams());
                params.put("blNo", bean.getBlNumber());
                params.put("freightPayableAt", blBean.getFreightPayableAt());
                String signature = blBean.getSignature();
                if(!StringUtils.hasText(signature)){
                    signature = "Blank";
                }
                params.put("signature", context.getRealPath("/images/"+signature+".png"));
                String noOfOriginal = blBean.getNoOfOriginal();
                if ("1".equals(noOfOriginal)) {
                    noOfOriginal = "ONE";
                } else if ("2".equals(noOfOriginal)) {
                    noOfOriginal = "TWO";
                } else if ("3".equals(noOfOriginal)) {
                    noOfOriginal = "THREE";
                } else {
                    noOfOriginal = "SEAWAYBILL";
                    params.put("wmark", context.getRealPath("/images/wmark-nonnegotiable.png"));
                }
                if (bean.getStart() == 2) {
                    params.put("wmark", context.getRealPath("/images/wmark-nonnegotiable.png"));
                }
                params.put("noOfOriginal", noOfOriginal);
                params.put("placeOfIssue", blBean.getPoiName());
                params.put("issueDate", blBean.getBlIssueDate());
                params.put("destAddress", blBean.getDestinationAgentContactDetails());
                params.put("original", "ORIGINAL");
                params.put("igmNo", blBean.getIgmNo());
                params.put("igmDate", blBean.getIgmDate());
                params.put("itemNo", blBean.getItemNumber());
                Integer freeDays = blBean.getFreeDays();
                if(blBean.getFreeDays() == null || blBean.getFreeDays() == 0){
                    freeDays = 1;
                }
                params.put("doValidityDate", DateUtil.addDaysToGivenDate(blBean.getDescribedDate(),freeDays-1));
                params.put("icdFactory", blBean.getIcdFactory());
                params.put("doNumber", blBean.getDoNumber());
                params.put("doDate", DateUtil.getBillDueDate(0));
                params.put("mBlNumber", blBean.getMBlNumber());
                params.put("toTheManager", blBean.getTheManager());
                params.put("remarks", blBean.getRemarks());
                params.put("surveyors", blBean.getSurveyors());
                params.put("emptyRetLoc", blBean.getEmptyReturnLocation());
                params.put("mblDate", blBean.getMblDate());
                params.put("companyDescription", bean.getCompanyName());
                params.put("companyAddress", bean.getCompanyAddress());
                params.put("terminalCode", blBean.getTerminalCode());
                params.put("terminalName", blBean.getTerminalName());
                params.put("releaseDate", blBean.getBlReleaseDate());

                String content = "Deliver Order issued against a suitable Bank Guarantee / Seaway Bill / Original Bill of Lading duly endorced in possession" +
                        " and is subject to all Clause / Terms and condition of TRANSLINER Bill of Ladding. "+"\n\n"+
                        "**This is an electronically generated Delivery Order which requires no signature."+"\n"+
                        "P.S. THIS DELIVERY ORDER VALID SUBJECT TO DISCHARGE OF CONTAINER/SHIPMENT"+"\n\n"+
                        "By virtue of obtaining this Delivery Order the importer/consignee of his representative confirms that he is responsible for " +
                        "payment of appropriate custom duty at the value of the first assessment or subsequent reassessment of value/duty if any";


                params.put("content", content);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else{
            params.put("backimage", context.getRealPath("/images/backimage.png"));
        }
        return params;
    }
}
