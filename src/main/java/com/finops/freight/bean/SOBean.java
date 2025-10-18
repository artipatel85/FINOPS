package com.finops.freight.bean;

import com.finops.bean.AbstractBean;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SOBean extends AbstractBean {
    private String loadingAgentCode;
    private String destinationAgentCode;
    private String loadingAgentName;
    private String destinationAgentName;
    private int bkgRefNo;
    private String bookingRefDate;
    private String bookingType;
    private String soNumber;
    private String pol;
    private String pod;
    private String polName;
    private String podName;
    private String por;
    private String porName;
    private String dest;
    private String destName;
    private String vsl;
    private String voy;
    private String actShipper;
    private String actShipperName;
    private String actConsignee;
    private String actConsigneeName;
    private String shipper;
    private String shipperName;
    private String consignee;
    private String consigneeName;
    private String remarks;
    private String jobNumber;
    private String blNumber;

    private String shipperContactDetails;
    private String consigneeContactDetails;
    private String soNotify;
    private String soNotifyName;

    public String getOriginWareHouseContactDetails() {
        return originWareHouseContactDetails==null?originWareHouseContactDetails:originWareHouseContactDetails.toUpperCase();
    }

    public String getDestinationAgentContactDetails() {
        return destinationAgentContactDetails == null? destinationAgentContactDetails: destinationAgentContactDetails.toUpperCase();
    }

    public String getLoadingAgentContactDetails() {
        return loadingAgentContactDetails == null?loadingAgentContactDetails:loadingAgentContactDetails.toUpperCase();
    }

    public String getAlsoNotifyContactDetails() {
        return alsoNotifyContactDetails == null?alsoNotifyContactDetails:alsoNotifyContactDetails.toUpperCase();
    }

    public String getSoNotifyContactDetails() {
        return soNotifyContactDetails == null? soNotifyContactDetails:soNotifyContactDetails.toUpperCase();
    }

    public String getShipperContactDetails() {
        return shipperContactDetails == null? shipperContactDetails:shipperContactDetails.toUpperCase();
    }

    public String getConsigneeContactDetails() {
        return consigneeContactDetails == null? consigneeContactDetails : consigneeContactDetails.toUpperCase();
    }

    private String soNotifyContactDetails;
    private String alsoNotify;
    private String alsoNotifyName;
    private String alsoNotifyContactDetails;
    private String fromFreightCode;
    private String fromFreightName;
    private String upToFreightCode;
    private String upToFreightName;
    private String noOfOriginal;
    private String deliveryDate;
    private String deliveryAt;
    private String atPortDesc;
    private String etd;
    private String eta;
    private String cfsDate;
    private String cfsTime;
    private String cyDate;
    private String cyTime;
    private String loadingAgentContactDetails;
    private String destinationAgentContactDetails;
    private String originWareHouse;
    private String originWareHouseName;
    private String originWareHouseContactDetails;
    private String carrierCode;
    private String carrierName;
    private String cargoReceivedDate;
    private String freightType;
    private String freightPayableAt;
    private String freightPayableAtValue;
    private String preCarriage;
    private String preVoyage;
    private String cargoType;
    private String porInd;
    private String podInd;
    private String polInd;
    private String destInd;
    private String comInvNumber;
    private String comInvDate;
    private String bkgType;
    private ContainerBean cb = new ContainerBean();
    private List<ContainerBean> cbList = new ArrayList<ContainerBean>();
    private Map<String, String> unitMap;
    private Map<String, String> sizeMap;
    private String terminalCode;
    private String terminalName;
    private String mBlNumber;
    private String salesBy;
    private String gatewayCutOffDate;
    private Map<String, String> reportParams = new HashMap<>();
    private String loadPlanNo;
    private String bookingRefDateTo;
    private String deliveryDateTo;
    private String shipmentFHAN;
}
