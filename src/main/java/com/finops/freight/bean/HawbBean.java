package com.finops.freight.bean;

import com.finops.bean.AbstractBean;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HawbBean extends SIBean {
    private String flightNo;
    private String pcAirFreight;
    private String pcTax;
    private String pcDueAgent;
    private String pcDueCarrier;
    private String totalPrepaid;
    private String totalCollect;
    private String flightNo2;
    private String flightDate2;
    private String flightType2;
    private String pol2;
    private String polName2;
    private String pod2;
    private String podName2;
    private String eta2;
    private String etd2;
    private String flightDate;
    private String otherCharges;
}
