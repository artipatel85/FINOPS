/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.finance.report;

import com.finops.freight.bean.TrackingBean;
import com.finops.freight.service.TrackingService;
import com.finops.freight.vo.TrackingVO;
import com.finops.report.XLSReport;
import com.finops.report.model.ReportBean;
import com.finops.report.service.OutstandingService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author BirenDesai
 */
public class TrackingXLSReport extends XLSReport {

    private final TrackingBean bean;
    private TrackingService service;

    public TrackingXLSReport(HttpServletResponse response, TrackingBean vo, TrackingService service) {
        super("Tracking", null, response, "Tracking");
        this.bean = vo;
        this.service = service;
    }

    @Override
    public void createData() {
        List<Object[]> arrList = new ArrayList<>();
        Object[] oar = null;
        List<TrackingVO> dataList = service.getSOTracking(bean);
        for (TrackingVO vo : dataList) {
            oar = new Object[]{vo.getColumn1(), vo.getColumn2(),vo.getColumn3(),vo.getColumn4(),vo.getColumn5(),vo.getColumn6(),
                    vo.getColumn7(),vo.getColumn8(),vo.getColumn9(),vo.getColumn10(),vo.getColumn11(),vo.getColumn12(),vo.getColumn13(),
                    vo.getColumn14(),vo.getColumn15(),vo.getColumn16(),vo.getColumn17(),vo.getColumn18(),vo.getColumn10(),vo.getColumn20(),
                    vo.getColumn21(),vo.getColumn22(),vo.getColumn23(),vo.getColumn24(),vo.getColumn25(),vo.getColumn26(),vo.getColumn27()};
            arrList.add(oar);
        }
        this.data = arrList;

    }

    @Override
    public void createHeader() {
        List<Object[]> arrList = new ArrayList<>();
        Object[] oar = new Object[bean.getHeaderData().size()];
        int index = 0;
        for(TrackingVO header : bean.getHeaderData()) {
            oar[index++] = header.getColumn2();
        }
        arrList.add(oar);
        this.headerData = arrList;
        super.createHeader();
    }



}
