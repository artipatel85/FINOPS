package com.finops.freight.bean;

import com.finops.bean.AbstractBean;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter @Setter
public class ContainerBean extends AbstractBean {

    private String number;
    private String size;
    private String netWeight;
    private String qty;
    private String unit;
    private String weight;
    private String measurement;
    private String shippingBillNumber;
    private String shippingBillDate;
    private String customSealNumber;
    private String lineSealNumber;
    private String markNumber;
    private String markDetails;
    private String tmpMarkNumber;
    private String tmpMarkDetails;
    private int lineNumber;
    private String actualQty;
    private String actualWeight;
    private String actualUnit;
    private String actualNetWeight;
    private String actualMeasurement;
    private String containerRemarks;
    private String loadingAgentCode;
    private String soNumber;
    private String blNumber;
    private int blLineNumber;
    private int bookingRefNumber;
    private String bookingType;
    private String serviceTerm;
    private String containerNo;
    private String loadPlanDate;
    private String voy;
    private String vsl;
    private String carrierCode;
    private String carrierName;
    private String actualLoadingDate;
    private String pod;
    private String podName;
    private String pol;
    private String polName;
    private String warehouse;
    private String warehouseName;
    private String etd;
    private String eta;
    private Map<String, String> unitMap;
    private Map<String, String> sizeMap;
    private String shipper;
    private String consignee;
    private String shipperName;
    private String consigneeName;
    private String dest;
    private String loadMethod;
    private List<ContainerBean> containerBeanList;
    private String chargeableWeight;
    private String volumeWeight;
    private String actualStuffingDate;
}
