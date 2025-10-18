/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.freight.report;

import com.finops.finance.report.PDFReport;
import com.finops.freight.bean.BLBean;
import com.finops.freight.bean.ContainerBean;
import com.finops.freight.service.BLService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Bhaumik
 */
public class BLPDFReport extends PDFReport {

    private BLBean bean;
    private BLService blService;

    public BLPDFReport(String reportTitle, Map params, HttpServletResponse response, BLBean bean, ServletContext context,
                       BLService service) {
        super(reportTitle, params, response, context);
        this.bean = bean;
        this.blService = service;
    }
    
    public BLPDFReport(Map params, BLBean bean){
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

            BLBean blBean = blService.retrieve(bean);
            params.put("consignor", blBean.getShipperContactDetails());
            params.put("consignee", blBean.getConsigneeContactDetails());
            params.put("notify", blBean.getSoNotifyContactDetails());
            params.put("por", blBean.getPorName());
            params.put("pol", blBean.getPolName());
            params.put("pod", blBean.getPodName());
            params.put("destination", blBean.getDestName());
            params.put("precarriage", blBean.getPreCarriage());
            params.put("vsl", blBean.getVsl());
            params.put("voy", blBean.getVoy());
            params.put("imagepath", context.getRealPath("/images/shikhar_logo_bl.jpg"));
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
            String titleheader = "";
            String attachString = "";
            String newline = "";
            String markDetails = (String) params.get("markDetails");
            if (((List<ContainerBean>) this.data).size() > 2 || markDetails.length() > 500) {
                newline = "\n\n\n\n\n";
                attachString = "\n*** FULL PARTICULARS AS PER ATTACHED SHEET ***";
                titleheader = "Attachment No :: 1\t\t\t\t\t\t\t\t"
                        + "MTD Number :: " + bean.getBlNumber();
            }
            params.put("attach", attachString);
            params.put("nlpop", newline);
            params.put("titleheader", titleheader);
        }
        else{
            params.put("backimage", context.getRealPath("/images/backimage.png"));
        }
        return params;
    }
}
