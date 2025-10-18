package com.finops.freight.service;

import com.finops.aop.MeasureTime;
import com.finops.freight.bean.TrackingBean;
import com.finops.freight.dao.*;
import com.finops.freight.vo.TrackingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrackingService {

    @Autowired
    private TrackingDAO trackingDAO;

    @Autowired
    private SOExportTrackingDAO soExportTrackingDAO;

    @Autowired
    private SOImportTrackingDAO soImportTrackingDAO;

    @Autowired
    private AirExportTrackingDAO airExportTrackingDAO;

    @Autowired
    private AirImportTrackingDAO airImportTrackingDAO;


    public List<TrackingVO> getCustomGridDataForUser(TrackingBean bean){
        return getDAOInstance(bean.getFormId()).getCustomGridDataForUser(bean);
    }

    public List<TrackingVO> getCustomGridData(TrackingBean bean){
        return trackingDAO.getCustomGridData(bean);
    }

    private TrackingDAO getDAOInstance(String type){
        switch(type){
            case "1029":
                return soExportTrackingDAO;
            case "1033":
                return soImportTrackingDAO;
            case "1031":
                return airExportTrackingDAO;
            case "1035":
                return airImportTrackingDAO;
        }
        return trackingDAO;
    }

    @MeasureTime
    @Transactional
    public void saveGrid(TrackingBean bean) {
        trackingDAO.saveGrid(bean);
    }

    @MeasureTime
    public void prepareGSONData(TrackingBean bean){
        getDAOInstance(bean.getFormId()).prepareGSONData(bean);
    }

    @MeasureTime
    public List<TrackingVO> getSOTracking(TrackingBean bean){
        return getDAOInstance(bean.getFormId()).getSOTracking(bean);
    }
}
