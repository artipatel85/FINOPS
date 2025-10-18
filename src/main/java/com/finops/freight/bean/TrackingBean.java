package com.finops.freight.bean;

import com.finops.bean.AbstractBean;
import com.finops.freight.vo.TrackingVO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
public class TrackingBean extends JobBean {
    private String formId;
    private String hiddenColumns;
    private List<String> headers;
    private List<TrackingVO> aaData;
    private int columnLength;
    private String doiStartDate;
    private String doiEndDate;
    private String bkgStartDate;
    private String bkgEndDate;
    private String query;
    private int gridSeq;
    private String gridName;
    private String display;
    private String originalDesc;
    private int dataLength;
    private String userDesc;
    private int seq;
    private List<TrackingVO> trackingVoList;
    private String columnString;
    private List<TrackingVO> headerData;
}
