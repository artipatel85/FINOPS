package com.finops.freight.dao;

import com.finops.aop.MeasureTime;
import com.finops.dao.AbstractDAO;
import com.finops.finance.bean.InvoiceBean;
import com.finops.freight.bean.*;
import com.finops.partner.model.PartnerBean;
import com.finops.report.legacy.AirwayBillPDF;
import com.finops.report.legacy.BLPDFDraft;
import com.finops.report.legacy.BLPDFOriginal;
import com.finops.util.DateUtil;
import com.finops.util.sql.Condition;
import com.finops.util.sql.Query;
import com.finops.util.sql.QueryBuilder;
import com.finops.util.sql.QueryType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HawbDAO extends AbstractDAO {

    /**
     * BL EXPORT and BL IMPORT use the same BL_HDR_F table.
     * @param bean
     * @return
     */
    @MeasureTime
public List<HawbBean> findHawbByPage(HawbBean bean, Map<String, PartnerBean> partnerBeanMap){
        String agent = "LOADNG_AGNT";
        String blAgent = "LOADING_AGNT";
        if("IMPORT".equalsIgnoreCase(bean.getExpImp())){
            agent = "DEST_AGNT";
            blAgent = agent;
        }

        QueryBuilder findBLByPageQuery = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" BLH.HAWB_NUMBER blNumber,BLH.HAWB_AUTO_SEQ_NO ,BLH.JOB_NUMBER ,BLH.SHPR shipper," +
                        "SHPR.DESCRIPTION1 shipperName ,BLH.CNEE consignee,CNEE.DESCRIPTION1 consigneeName ," +
                        "BLH.LOADING_AGNT ,BLH.DEST_AGNT  ,BLH.POL ,POL.DESCRIPTION1 POLDESCRIPTION ,BLH.POD ,POD.DESCRIPTION1 PODDESCRIPTION ," +
                        "BLH.ETD ,BLH.ETA ,BLH.CARGO_RECEIVE_DATE  ,BLH.CREATED_BY  ," +
                        "BLH.CREATION_DATE  ,BLH.AMENDED_BY  ,BLH.AMENDED_DATE  ," +
                        "ASHPR.DESCRIPTION1 actShipperName,BLH.ACT_CNEE,ACNEE.DESCRIPTION1 actConsigneeName, flight_no, convert(flight_date,date) flightDate ")
                .FROM("hawb_hdr_f", "BLH")
                .INNER_JOIN("si_hdr_f", "sih", new Condition("BLH.BKG_REF_NO","SIH.BKG_REF_NO", Query.EQUALS))
                .INNER_JOIN("port_d", "pol", new Condition("POL.PORT_CODE","BLH.POL", Query.EQUALS))
                .INNER_JOIN("port_d", "pod", new Condition("POD.PORT_CODE","BLH.POD", Query.EQUALS))
                .LEFT_JOIN("PARTNER_ACCOUNT_D", "ACNEE",
                        new Condition("BLH.ACT_CNEE", "ACNEE.PARTNER_CODE", Query.EQUALS),
                        new Condition("ACNEE.STATUS", "'A'", Query.EQUALS),
                        new Condition("ACNEE." + agent, "?", Query.EQUALS))
                .LEFT_JOIN("PARTNER_ACCOUNT_D", "ASHPR",
                        new Condition("BLH.ACT_SHPR", "ASHPR.PARTNER_CODE", Query.EQUALS),
                        new Condition("ASHPR.STATUS", "'A'", Query.EQUALS),
                        new Condition("ASHPR." + agent, "?", Query.EQUALS))
                .LEFT_JOIN("PARTNER_ACCOUNT_D", "CNEE",
                        new Condition("BLH.CNEE", "CNEE.PARTNER_CODE", Query.EQUALS),
                        new Condition("CNEE.STATUS", "'A'", Query.EQUALS),
                        new Condition("CNEE." + agent, "?", Query.EQUALS))
                .LEFT_JOIN("PARTNER_ACCOUNT_D", "SHPR",
                        new Condition("BLH.SHPR", "SHPR.PARTNER_CODE", Query.EQUALS),
                        new Condition("SHPR.STATUS", "'A'", Query.EQUALS),
                        new Condition("SHPR." + agent, "?", Query.EQUALS))
                .LEFT_JOIN("PARTNER_ACCOUNT_D", "LA",
                        new Condition("BLH.LOADING_AGNT", "LA.PARTNER_CODE", Query.EQUALS),
                        new Condition("LA.STATUS", "'A'", Query.EQUALS),
                        new Condition("LA." + agent, "?", Query.EQUALS))
                .LEFT_JOIN("PARTNER_ACCOUNT_D", "DA",
                        new Condition("BLH.DEST_AGNT", "DA.PARTNER_CODE", Query.EQUALS),
                        new Condition("DA.STATUS", "'A'", Query.EQUALS),
                        new Condition("DA." + agent, "?", Query.EQUALS))
                .WHERE("BLH."+blAgent, "?");

        addFilter(findBLByPageQuery, "BLH.HAWB_NUMBER", bean.getBlNumber() + "", true);
        addFilter(findBLByPageQuery, "sih.si_number", bean.getSoNumber() + "", false);
        addFilter(findBLByPageQuery, "BLH.JOB_NUMBER", bean.getJobNumber() + "", true);
        addFilter(findBLByPageQuery, "BLH.VSL", bean.getVsl() + "", true);
        addFilter(findBLByPageQuery, "BLH.VOY", bean.getVoy() + "", true);
        addFilter(findBLByPageQuery, "BLH.POL", bean.getPol() + "", true);
        addFilter(findBLByPageQuery, "BLH.POD", bean.getPod() + "", true);
        addFilter(findBLByPageQuery, "SHPR.DESCRIPTION1", bean.getShipperName() + "", true);
        addFilter(findBLByPageQuery, "CNEE.DESCRIPTION1", bean.getConsigneeName() + "", true);
        addFilter(findBLByPageQuery, "ACNEE.DESCRIPTION1", bean.getActConsigneeName()+"", true);
        addFilter(findBLByPageQuery, "LA.DESCRIPTION1", bean.getLoadingAgentName()+"", true);
        addFilter(findBLByPageQuery, "DA.DESCRIPTION1", bean.getDestinationAgentName()+"", true);
        addFilter(findBLByPageQuery, "BLH.M_HAWB_NO", bean.getMBlNumber()+"", true);
        if(org.springframework.util.StringUtils.hasText(bean.getCb().getShippingBillNumber())){
            findBLByPageQuery.FREEWHERECONDITION("AND EXISTS (SELECT 'X' FROM hawb_dtl_f WHERE HAWB_NUMBER = blh.HAWB_NUMBER " +
                    "AND ASBL_NO = '"+bean.getCb().getShippingBillNumber()+"' ) ");
        }

        findBLByPageQuery.ORDER_BY(" HAWB_AUTO_SEQ_NO DESC ")
                .LIMIT(bean.getStart(), bean.getLength());

        return jdbcTemplate.query(findBLByPageQuery.build().toString(),
                new BeanPropertyRowMapper<>(HawbBean.class), bean.getLoadingAgent(),
                bean.getLoadingAgent(),bean.getLoadingAgent(),bean.getLoadingAgent(),
                bean.getLoadingAgent(),bean.getLoadingAgent(),bean.getLoadingAgent());
    }

    public HawbBean generateBLNo(HawbBean blBean){
        String query =  "SELECT CONCAT((SUBSTRING(STR_TO_DATE(SYSDATE(),'%Y-%m-%d'),3,2)),(SUBSTRING(STR_TO_DATE(SYSDATE(),'%Y-%m-%d'),6,2)) ,(MAX(HAWB_AUTO_SEQ_NO) +1 )) blNumber ,\n" +
                "(MAX(HAWB_AUTO_SEQ_NO) +1) blAutoSequence from hawb_hdr_f ";

        return jdbcTemplate.queryForObject(query,
                new BeanPropertyRowMapper<>(HawbBean.class));

    }

    @MeasureTime
    public void save(HawbBean bean){
        String query = "INSERT INTO hawb_hdr_f(HAWB_NUMBER,HAWB_AUTO_SEQ_NO,DELIVERY_DATE,DELIVERY_AT,BKG_REF_NO,BKG_DATE,JOB_NUMBER,CARGO_TYPE,ACT_SHPR,ACT_CNEE," + //10
                "SHPR,SHPR_CONTCT_DTLS,CNEE,CNEE_CONTCT_DTLS,COMM_INV_NUMBER,COMM_INV_DT,HAWB_NOTIFY,HAWB_NOTIFY_CONTCT_DTLS,POL,POL_NAME,POD,POD_NAME,DEST,DEST_NAME," + //24
                "SI_TYPE,FLIGHT_NO,FLIGHT_DATE,FLIGHT_N0_2,SI_TYPE_2,FLIGHT_DATE_2,POL_2,POL_NAME_2,POD_2,POD_NAME_2,FLIGHT_N0_3,SI_TYPE_3,FLIGHT_DATE_3," + //37
                "POL_3,POL_NAME_3,POD_3,POD_NAME_3,ETA_2,ETD_2,ETA_3,ETD_3,CARRIER_CODE,CARRIER_CONTCT_DTLS,ETA,ETD,FREIGHT_PAYABLE_AT,CARGO_RECEIVE_DATE,PT_AIR_FREIGHT," + //52
                "PT_TERMNL_HANDLNG,PT_CARTG,PT_HNDLNG_DOCMNTN,PT_PACKNG,PT_OTHRS,NO_OF_COMM_INV,NO_OF_PACK_LIST,NO_OF_EXP_LIC,NO_OF_CO,NO_OF_FORM_A,NO_OF_OTHERS," + //63
                "CURRENCY_CODE,VAL_CARRAIGE,VAL_CUSTOM,AMT_INSURANCE,LOADING_AGNT,LOADING_AGNT_CONTCT_DTLS,DEST_AGNT,DEST_AGNT_CONTCT_DTLS,ORIGN_WH,ORIGN_WH_CONTCT_DTLS," + //73
                "M_HAWB_NO,HAWB_REMARK,DATE_OF_ISSUE,POI,POI_NAME,CREATED_BY,CREATION_DATE,HAWB_STATUS,PC_AIR_FREIGHT,PC_TAX,PC_DUE_AGENT,PC_DUE_CARRIER,TOTAL_PREPAID," + //86
                "TOTAL_COLLECT,OTHER_CHARGES) VALUES " +
                "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                "?,?,?,?,?,?,?,?,?,SYSDATE(),'A',?,?,?,?,?,?,?)";

        jdbcTemplate.update(query,
                bean.getBlNumber(), bean.getBlAutoSequence(), defaultNull(bean.getDeliveryDate()), bean.getDeliveryAt(),bean.getBkgRefNo(),bean.getBookingRefDate(),
                bean.getJobNumber(), bean.getCargoType(), bean.getActShipper(), bean.getActConsignee(), //10
                bean.getShipper(), bean.getShipperContactDetails(), bean.getConsignee(), bean.getConsigneeContactDetails(), null, null, bean.getSoNotify(), bean.getSoNotifyContactDetails(),
                bean.getPol(), bean.getPolName(), bean.getPod(), bean.getPodName(), bean.getDest(), bean.getDestName(), //24
                bean.getFlightType(), bean.getFlightNo(), defaultNull(bean.getFlightDate()), bean.getFlightNo2(), bean.getFlightType2(), defaultNull(bean.getFlightDate2()), bean.getPol2(),
                bean.getPolName2(), bean.getPod2(), bean.getPodName2(), null, null, null, //37
                null, null, null, null, defaultNull(bean.getEta2()), defaultNull(bean.getEtd2()), null, null, bean.getCarrierCode(), null, defaultNull(bean.getEta()),
                defaultNull(bean.getEtd()),bean.getFreightPayableAt(), defaultNull(bean.getCargoReceivedDate()), bean.getAirFreight(), //52
                bean.getTerminalHandling(), bean.getCartage(), bean.getHandlingDoc(), bean.getPacking(), bean.getOtherPaymentTerms(), bean.getCommercialInvoice(), bean.getPackingList(),
                bean.getExportLicense(), bean.getComInvNumber(), bean.getFormA(), bean.getOthers(), //63
                bean.getCurrencyId(), null, null, null, bean.getLoadingAgentCode(), bean.getLoadingAgentContactDetails(), bean.getDestinationAgentCode(), bean.getDestinationAgentContactDetails(),
                bean.getOriginWareHouse(), bean.getOriginWareHouseContactDetails(), //73
                bean.getMBlNumber(),bean.getRemarks(), defaultNull(bean.getBlIssueDate()), bean.getPoi(), bean.getPoiName(), bean.getUserId(), bean.getPcAirFreight(), bean.getPcTax(),
                bean.getPcDueAgent(), bean.getPcDueCarrier(), bean.getTotalPrepaid(), bean.getTotalCollect(), null);
    }
    
    public HawbBean retrieve(HawbBean bean){
        String companyId = bean.getLoadingAgent();

        String query = "SELECT HAWB.HAWB_NUMBER blNumber,HAWB.BKG_REF_NO,HAWB.JOB_NUMBER,HAWB.CARGO_TYPE,HAWB.ACT_SHPR actShipper,ACNEE.DESCRIPTION1 actConsigneeName," +
                "HAWB.ACT_CNEE actConsignee,ASHPR.DESCRIPTION1 actShipperName,HAWB.SHPR shipper, SHPR.DESCRIPTION1 shipperName," +
                "HAWB.SHPR_CONTCT_DTLS shipperContactDetails,HAWB.CNEE consignee,CNEE.DESCRIPTION1 consigneeName," +
                "HAWB.CNEE_CONTCT_DTLS consigneeContactDetails,HAWB.HAWB_NOTIFY soNotify, NOTIFY.DESCRIPTION1 soNotifyName,HAWB.HAWB_NOTIFY_CONTCT_DTLS soNotifyContactDetails," +
                "HAWB.POL,HAWB.POL_NAME,HAWB.POD,HAWB.POD_NAME,HAWB.DEST,HAWB.DEST_NAME,HAWB.SI_TYPE flightType,HAWB.FLIGHT_NO,CONVERT(HAWB.FLIGHT_DATE,DATE)FLIGHT_DATE,HAWB.CARRIER_CODE," +
                "CARR.DESCRIPTION1 carrierName,HAWB.CARRIER_CONTCT_DTLS carrierContactDetails,convert(HAWB.ETA,DATE) AS ETA,convert(HAWB.ETD,DATE) AS ETD,HAWB.FREIGHT_PAYABLE_AT,CONVERT(HAWB.CARGO_RECEIVE_DATE,DATE) CARGO_RECEIVED_DATE," +
                "HAWB.PT_AIR_FREIGHT airFreight,HAWB.PT_TERMNL_HANDLNG terminalHandling,HAWB.PT_CARTG cartage,HAWB.PT_HNDLNG_DOCMNTN handlingDoc,HAWB.PT_PACKNG packing," +
                "HAWB.PT_OTHRS otherPaymentTerms,HAWB.NO_OF_COMM_INV commercialInvoice,HAWB.NO_OF_PACK_LIST packingList,HAWB.NO_OF_EXP_LIC exportLicense," +
                "HAWB.NO_OF_CO certOfOrigin,HAWB.NO_OF_FORM_A formA,HAWB.NO_OF_OTHERS others,HAWB.CURRENCY_CODE,HAWB.VAL_CARRAIGE,HAWB.VAL_CUSTOM,HAWB.AMT_INSURANCE,HAWB.LOADING_AGNT loadingAgentCode," +
                "LAGNT.DESCRIPTION1 loadingAgentName,HAWB.LOADING_AGNT_CONTCT_DTLS loadingAgentContactDetails,HAWB.DEST_AGNT destinationAgentCode,DAGNT.DESCRIPTION1 destinationAgentName," +
                "HAWB.DEST_AGNT_CONTCT_DTLS destinationAgentContactDetails," +
                "HAWB.ORIGN_WH,OWH.DESCRIPTION1 OWHDESCRIPTION1,HAWB.ORIGN_WH_CONTCT_DTLS,HAWB.M_HAWB_NO mBlNumber,HAWB.HAWB_REMARK,CONVERT(HAWB.DATE_OF_ISSUE,DATE) blIssueDate,HAWB.POI," +
                "HAWB.POI_NAME,HAWB.HAWB_STATUS,HAWB.CREATED_BY,CONVERT(HAWB.CREATION_DATE,DATE) CREATION_DATE,HAWB.AMENDED_BY,CONVERT(HAWB.AMENDED_DATE,DATE) AMENDED_DATE," +
                "HAWB.FLIGHT_N0_2 flightNo2,HAWB.SI_TYPE_2 flightType2,CONVERT(HAWB.FLIGHT_DATE_2,DATE) flightDate2,HAWB.POL_2 pol2,HAWB.POL_NAME_2 polName2," +
                "HAWB.POD_2 pod2,HAWB.POD_NAME_2 podName2,HAWB.FLIGHT_N0_3," +
                "CONVERT(HAWB.FLIGHT_DATE_3,DATE) FLIGHT_DATE_3,HAWB.SI_TYPE_3,HAWB.POL_3,HAWB.POL_NAME_3,HAWB.POD_3,HAWB.POD_NAME_3," +
                "convert(HAWB.ETA_2,DATE) AS eta2,convert(HAWB.ETD_2,DATE) AS etd2,convert(HAWB.ETA_3,DATE) AS ETA_3,convert(HAWB.ETD_3,DATE) AS ETD_3," +
                "PC_AIR_FREIGHT,PC_TAX,PC_DUE_AGENT,PC_DUE_CARRIER,TOTAL_PREPAID,TOTAL_COLLECT,OTHER_CHARGES" +
                " FROM HAWB_HDR_F HAWB "+
                "LEFT OUTER JOIN (SELECT ACNEE.PARTNER_CODE,ACNEE.DESCRIPTION1 FROM PARTNER_ACCOUNT_D ACNEE WHERE ACNEE.LOADNG_AGNT = '" + companyId + "' AND ACNEE.STATUS = 'A') ACNEE ON (HAWB.ACT_CNEE = ACNEE.PARTNER_CODE ) "
                + "LEFT OUTER JOIN (SELECT ASHPR.PARTNER_CODE,ASHPR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D ASHPR WHERE ASHPR.LOADNG_AGNT = '" + companyId + "' AND ASHPR.STATUS = 'A') ASHPR ON (HAWB.ACT_SHPR = ASHPR.PARTNER_CODE ) "
                + "LEFT OUTER JOIN (SELECT CNEE.PARTNER_CODE,CNEE.DESCRIPTION1 FROM PARTNER_ACCOUNT_D CNEE WHERE CNEE.LOADNG_AGNT = '" + companyId + "' AND CNEE.STATUS = 'A') CNEE ON (HAWB.CNEE = CNEE.PARTNER_CODE) "
                + "LEFT OUTER JOIN (SELECT SHPR.PARTNER_CODE,SHPR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D SHPR WHERE SHPR.LOADNG_AGNT = '" + companyId + "' AND SHPR.STATUS = 'A') SHPR ON (HAWB.SHPR = SHPR.PARTNER_CODE ) "
                + "LEFT OUTER JOIN (SELECT NOTIFY.PARTNER_CODE,NOTIFY.DESCRIPTION1 FROM PARTNER_ACCOUNT_D NOTIFY WHERE NOTIFY.LOADNG_AGNT = '" + companyId + "' AND NOTIFY.STATUS = 'A') NOTIFY ON (HAWB.HAWB_NOTIFY = NOTIFY.PARTNER_CODE ) "
                + "LEFT OUTER JOIN (SELECT LAGNT.PARTNER_CODE,LAGNT.DESCRIPTION1 FROM PARTNER_ACCOUNT_D LAGNT WHERE LAGNT.LOADNG_AGNT = '" + companyId + "' AND LAGNT.STATUS = 'A') LAGNT ON (HAWB.LOADING_AGNT = LAGNT.PARTNER_CODE) "
                + "LEFT OUTER JOIN (SELECT DAGNT.PARTNER_CODE,DAGNT.DESCRIPTION1 FROM PARTNER_ACCOUNT_D DAGNT WHERE DAGNT.LOADNG_AGNT = '" + companyId + "' AND DAGNT.STATUS = 'A') DAGNT ON (HAWB.DEST_AGNT = DAGNT.PARTNER_CODE ) "
                + "LEFT OUTER JOIN (SELECT OWH.PARTNER_CODE,OWH.DESCRIPTION1 FROM PARTNER_ACCOUNT_D OWH WHERE OWH.LOADNG_AGNT = '" + companyId + "' AND OWH.STATUS = 'A') OWH ON (HAWB.ORIGN_WH = OWH.PARTNER_CODE) "
                + "LEFT OUTER JOIN (SELECT CARR.PARTNER_CODE,CARR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D CARR WHERE CARR.LOADNG_AGNT = '" + companyId + "' AND CARR.STATUS = 'A') CARR ON (HAWB.CARRIER_CODE = CARR.PARTNER_CODE)"
                + "WHERE HAWB.HAWB_NUMBER = ? ";

        HawbBean blBean = jdbcTemplate.queryForObject(query,
                new BeanPropertyRowMapper<>(HawbBean.class),
                bean.getBlNumber());

        blBean.setReportParams(getColumnHeaderData(bean));

        return  blBean;
    }

    private Map<String, String> getColumnHeaderData(HawbBean bean) {
        String query1 = "SELECT BL_NO,sum(ACT_QTY) actualQty,ACT_UNIT actualUnit From bl_fcl_lcl_f WHERE BL_NO=? Group By BL_NO,ACT_UNIT ";

        String query2 = "SELECT BL_NO,sum(ACT_GROSS_WT) actualWeight,sum(ACT_NET_KGS)ACTUAL_KGS,sum(ACT_CBM) actualMeasurement From bl_fcl_lcl_f "
                + "WHERE BL_NO=? Group By BL_NO";

        String query3 = "SELECT sfl.MARK_NO markNumber,sfl.MARK_DETAILS FROM bl_fcl_lcl_f sfl WHERE sfl.BL_NO = ?";

        StringBuilder packageDetails = new StringBuilder();

        List<ContainerBean> cbList = jdbcTemplate.query(query1,  new BeanPropertyRowMapper<>(ContainerBean.class), bean.getBlNumber());

        cbList.forEach(cb -> packageDetails.append(cb.getActualQty()).append("\n").append(cb.getActualUnit()).append("\n"));

        cbList = jdbcTemplate.query(query2,  new BeanPropertyRowMapper<>(ContainerBean.class), bean.getBlNumber());
        Map<String, String> map = new HashMap<>();
        if(!cbList.isEmpty()){
            map.put("actualGross", cbList.get(0).getActualWeight());
            map.put("actualCBM", cbList.get(0).getActualMeasurement());
        }

        cbList = jdbcTemplate.query(query3,  new BeanPropertyRowMapper<>(ContainerBean.class), bean.getBlNumber());
        if(!cbList.isEmpty()){
            map.put("markNo", cbList.get(0).getMarkNumber());
            map.put("markDetails", cbList.get(0).getMarkDetails());
        }
        map.put("packageDetails", packageDetails.toString());
        map.put("saidToContain", "SAID TO CONTAIN");
        if ("F".equalsIgnoreCase(bean.getBkgType())) {
            map.put("saidToContain", "SAID TO CONTAIN\nSHIPPERS LOAD, WEIGH STOW AND\nCOUNT");
        }

        return map;
    }

    @MeasureTime
    public void update(HawbBean bean){
        String updateQuery = "UPDATE hawb_hdr_f SET BKG_REF_NO=?,BKG_DATE=?,CARGO_TYPE=?,ACT_SHPR=?,ACT_CNEE=?,SHPR=?,SHPR_CONTCT_DTLS=?," +
                "CNEE=?,CNEE_CONTCT_DTLS=?,HAWB_NOTIFY=?,HAWB_NOTIFY_CONTCT_DTLS=?,POL=?,POL_NAME=?,POD=?,POD_NAME=?,DEST=?,DEST_NAME=?,SI_TYPE=?,FLIGHT_NO=?," +
                "FLIGHT_DATE=?,CARRIER_CODE=?,CARRIER_CONTCT_DTLS=?,ETA=?,ETD=?,FREIGHT_PAYABLE_AT=?,CARGO_RECEIVE_DATE=?,PT_AIR_FREIGHT=?,PT_TERMNL_HANDLNG=?," +
                "PT_CARTG=?,PT_HNDLNG_DOCMNTN=?,PT_PACKNG=?,PT_OTHRS=?,NO_OF_COMM_INV=?,NO_OF_PACK_LIST=?,NO_OF_EXP_LIC=?,NO_OF_CO=?,NO_OF_FORM_A=?,NO_OF_OTHERS=?," +
                "CURRENCY_CODE=?,VAL_CARRAIGE=?,VAL_CUSTOM=?,AMT_INSURANCE=?,LOADING_AGNT=?,LOADING_AGNT_CONTCT_DTLS=?,DEST_AGNT=?,DEST_AGNT_CONTCT_DTLS=?,ORIGN_WH=?," +
                "ORIGN_WH_CONTCT_DTLS=?,M_HAWB_NO=?,HAWB_REMARK=?,DATE_OF_ISSUE=?,POI=?,POI_NAME=?,AMENDED_BY=?,AMENDED_DATE=SYSDATE(),FLIGHT_N0_2=?,SI_TYPE_2=?," +
                "FLIGHT_DATE_2=?,POL_2=?,POL_NAME_2=?,POD_2=?,POD_NAME_2=?,FLIGHT_N0_3=?,SI_TYPE_3=?,FLIGHT_DATE_3=?,POL_3=?,POL_NAME_3=?,POD_3=?,POD_NAME_3=?,ETA_2=?," +
                "ETD_2=?,ETA_3=?,ETD_3=?,PC_AIR_FREIGHT=?,PC_TAX=?,PC_DUE_AGENT=?,PC_DUE_CARRIER=?,TOTAL_PREPAID=?,TOTAL_COLLECT=?,OTHER_CHARGES=? " +
                "WHERE HAWB_NUMBER=? ";

            jdbcTemplate.update(updateQuery,
                bean.getBkgRefNo(), bean.getBookingRefDate(), bean.getCargoType(), bean.getActShipper(), bean.getActConsignee(), bean.getShipper(), bean.getShipperContactDetails(),
                bean.getConsignee(), bean.getConsigneeContactDetails(), bean.getSoNotify(), bean.getSoNotifyContactDetails(), bean.getPol(),bean.getPolName(), bean.getPod(), bean.getPodName(),
                bean.getDest(), bean.getDestName(), bean.getFlightType(), bean.getFlightNo(), defaultNull(bean.getFlightDate()), bean.getCarrierCode(),null,
                defaultNull(bean.getEta()), StringUtils.defaultIfEmpty (bean.getEta(),null), bean.getFreightPayableAt(),
                    defaultNull(bean.getCargoReceivedDate()), bean.getAirFreight(), bean.getTerminalHandling(), bean.getCartage(), bean.getHandlingDoc(),
                bean.getPacking(), bean.getOtherPaymentTerms(), bean.getCommercialInvoice(), bean.getPackingList(), bean.getExportLicense(), bean.getCertOfOrigin(), bean.getFormA(),
                bean.getOthers(), bean.getCurrencyId(), null, null, null, bean.getLoadingAgentCode(), bean.getLoadingAgentContactDetails(), bean.getDestinationAgentCode(),
                bean.getDestinationAgentContactDetails(), bean.getOriginWareHouse(), bean.getOriginWareHouseContactDetails(), bean.getMBlNumber(), bean.getRemarks(),
                    defaultNull(bean.getBlIssueDate()), bean.getPoi(), bean.getPoiName(), bean.getUserId(), bean.getFlightNo2(),
                bean.getFlightType2(), defaultNull(bean.getFlightDate2()), bean.getPol2(), bean.getPolName2(), bean.getPod2(), bean.getPodName2(), null, null, null, null, null ,null, null,
                StringUtils.defaultIfEmpty(bean.getEtd2(),null),  StringUtils.defaultIfEmpty(bean.getEta2(),null), null, null, bean.getPcAirFreight(),
                bean.getPcTax(), bean.getPcDueAgent(), bean.getPcDueCarrier(), bean.getTotalPrepaid(), bean.getTotalCollect(), null,bean.getBlNumber());
    }

    public List<ContainerBean> getContainerDetails(HawbBean bean, int lineNo) {
        String query = ("select f.HAWB_NUMBER blNumber,f.BKG_REF_NO bookingRefNumber"
                + ",f.BKG_QTY qty,c.LOADING_AGNT loadingAgentCode,ASBL_NO shippingBillNumber"
                + ",f.BKG_UNIT unit,f.BKG_KGS_GROSS_WT weight,f.BKG_CBM measurement,ASBL_DATE shippingBillDate"
                + ",f.ACT_QTY actualQty,f.ACT_UNIT actualUnit,f.ACT_GROSS_WT actualWeight,f.ACT_CBM actualMeasurement," +
                "f.SI_NUMBER soNumber,f.HAWB_LINE_NO lineNumber,CHARGBL_WT chargeableWeight,VOL_WT volumeWeight"
                + ",f.STATUS,f.MARK_NO markNumber,f.MARK_DETAILS,SI_WH_REMARKS remarks," +
                " f.service_term_code serviceTerm "
                + ",f.CREATED_BY,CONVERT(f.CREATION_DATE,DATE) AS CREATION_DATE,f.AMENDED_BY,CONVERT(f.AMENDED_DATE,DATE) AS AMENDED_DATE "
                + " from (hawb_dtl_f f LEFT OUTER JOIN hawb_hdr_f c on (f.HAWB_NUMBER =c.HAWB_NUMBER))"
               + " WHERE f.HAWB_NUMBER=? ");

        if(lineNo > 0){
            query += " AND f.bkg_ref_no = "+bean.getBkgRefNo()+" AND f.HAWB_LINE_NO = "+lineNo;
        }
        query += " ORDER BY f.HAWB_LINE_NO";

        return jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(ContainerBean.class),
                bean.getBlNumber());
    }

    public void blPDF(HawbBean blBean) {
        BLPDFOriginal.writeUCMpdf(blBean.getBlNumber(), blBean, jdbcTemplate);
    }

    public HawbBean getBlForBilling(InvoiceBean bean) {

        String party = "bl.shpr";
        String theCC = "destcc";
        if("IMPORT".equalsIgnoreCase(bean.getExpImp())){
            party = "bl.cnee";
            theCC = "loadingcc";
        }

        String query = "SELECT " +
                "COALESCE(cntnr.grosswt,0) weight,COALESCE(cntnr.qty,0) qty,COALESCE(cntnr.cbm,0) actualCbm," +
                "bl.pod,bl.pod_name," +
                "bl.pol,bl.pol_name,bl.carrier_code,bl.job_number,DATE_FORMAT(etd,120) etd,bl.shpr shipper,bl.cnee consignee,shpr.description1 shipperName," +
                "cnee.description1 consigneeName,IFNULL(party.code_combination_id,0) partyAcctCode,sal.name salesman,party.credit_period,bl.dest_agnt destinationAgentCode, " +
                "dest.description1 destinationAgentName, bl.loading_agnt loadingAgentCode, loading.description1 loadingAgentName,shpr.address1 shpraddress, " +
                "cnee.address1 consigneeContactDetails,dest.address1 destinationAgentContactDetails, loading.address1 loadingAgentContactDetails,party.gstin_no," +
                "IFNULL("+theCC+".code_combination_id,0) destCCId,party.state_code,party.is_sez," +
                "bl.shpr billTo,  shpr.description1 billToName, shpr.address1 billToAddress,so.invNo,m_hawb_no mBlNumber " +
                "FROM HAWB_HDR_F bl " +
                "LEFT OUTER JOIN (SELECT pa.partner_account_code,pa.partner_code,cc.code_combination_id,pa.credit_period,pa.state_code," +
                "pa.is_sez,pa.gstin_no FROM partner_account_d pa, gl_code_combination_d cc " +
                "WHERE cc.code_combination_id = pa.partner_acct_code AND pa.loadng_agnt = 'SFP' AND pa.status = 'A') party ON (party.partner_code = "+party+") " +
                "LEFT OUTER JOIN (SELECT SUM(chargbl_wt) grosswt,SUM(act_cbm) cbm,SUM(ACT_QTY) qty,hawb_number fclbl FROM hawb_dtl_f WHERE hawb_number=?  " +
                "GROUP BY hawb_number) cntnr ON (cntnr.fclbl = bl.hawb_number) " +
                "LEFT OUTER JOIN (SELECT hawb_no,(GROUP_CONCAT(t2.comm_inv_number SEPARATOR ',')) invNo FROM si_hdr_f t2 " +
                "WHERE hawb_no=? GROUP BY hawb_no) so ON (so.hawb_no = bl.hawb_number) " +
                "LEFT OUTER JOIN partner_account_d shpr ON (shpr.partner_code = bl.shpr AND shpr.loadng_agnt = ? AND shpr.status = 'A') " +
                "LEFT OUTER JOIN partner_account_d cnee ON (cnee.partner_code = bl.cnee AND cnee.loadng_agnt = ? AND cnee.status = 'A') " +
                "LEFT OUTER JOIN partner_account_d dest ON (dest.partner_code = bl.dest_agnt AND dest.loadng_agnt = ? AND dest.status = 'A') " +
                "LEFT OUTER JOIN partner_account_d loading ON (loading.partner_code = bl.loading_agnt AND loading.loadng_agnt = ? AND loading.status = 'A') " +
                "LEFT OUTER JOIN gl_code_combination_d destcc ON (destcc.code_combination_id = dest.partner_acct_code) " +
                "LEFT OUTER JOIN gl_code_combination_d loadingcc ON (loadingcc.code_combination_id = loading.partner_acct_code) " +
                "LEFT OUTER JOIN salesman_f sal ON (sal.code = shpr.salesman_code) WHERE bl.hawb_number = ? ";

        return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(HawbBean.class),bean.getBlNo(),bean.getBlNo(),
                bean.getLoadingAgent(), bean.getLoadingAgent(), bean.getLoadingAgent(), bean.getLoadingAgent(), bean.getBlNo());
    }

    public void blPDFDraft(HawbBean blBean) {
        AirwayBillPDF.writeUCMpdf(blBean.getBlNumber(), blBean, jdbcTemplate);
    }

    public void approve(HawbBean blBean) {
        jdbcTemplate.update("UPDATE bl_hdr_f SET is_approved='Y' WHERE bl_no = ?", blBean.getBlNumber());
    }

    public void saveContainers(HawbBean bean, ContainerBean cb, int counter) {
        String query = "INSERT INTO hawb_dtl_f "
                + "(HAWB_NUMBER,HAWB_LINE_NO,SI_NUMBER,BKG_REF_NO,BKG_QTY,BKG_UNIT,BKG_KGS_GROSS_WT,BKG_CBM,ACT_QTY,ACT_UNIT,ACT_GROSS_WT,ACT_CBM,VOL_WT,CHARGBL_WT,ACT_STUFFING_DATE,ASBL_NO,ASBL_DATE,SI_WH_REMARKS,MARK_NO,MARK_DETAILS,STATUS,CREATED_BY,CREATION_DATE )"
                + " SELECT '" + bean.getBlNumber() + "'," + counter + ",SI_NUMBER,BKG_REF_NO,BKG_QTY,BKG_UNIT,BKG_KGS_GROSS_WT,BKG_CBM,ACT_QTY,ACT_UNIT,ACT_GROSS_WT,ACT_CBM,VOL_WT,CHARGBL_WT"
                + ",ACT_STUFFING_DATE"
                + ",ASBL_NO,ASBL_DATE,SI_WH_REMARKS,MARK_NO,MARK_DETAILS,STATUS,'" + bean.getUserId() + "',SYSDATE() FROM si_dtl_f Where BKG_REF_NO = ? ";

        jdbcTemplate.update(query, cb.getBookingRefNumber());
    }



    public void updateContainer(ContainerBean cb) {
        String query = "UPDATE hawb_dtl_f SET BKG_QTY = ? ,BKG_UNIT = ? ,BKG_CBM = ?, SI_WH_REMARKS=?,AMENDED_BY = ?," +
                "AMENDED_DATE = ?,MARK_NO = ?,MARK_DETAILS = ?,SERVICE_TERM_CODE=? WHERE HAWB_NUMBER=? AND HAWB_LINE_NO=? ";
        jdbcTemplate.update(query, cb.getQty(), cb.getUnit(), cb.getMeasurement(), cb.getContainerRemarks(), cb.getUserId(),
                DateUtil.getSystemDate(), cb.getMarkNumber(), cb.getMarkDetails(),
                cb.getServiceTerm(),cb.getBlNumber(), cb.getLineNumber());
    }

    public void updateJobNumber(JobBean bean) {
        String soNumber = "";
        String soNumberRest = "";

        for(SOBean cb : bean.getSiBeanList()){
            if(org.springframework.util.StringUtils.hasText(cb.getUuid())) {
                soNumber += cb.getSoNumber() + ",";
            }
            else{
                soNumberRest += cb.getSoNumber()+",";
            }
        }
        soNumber+="-1";
        soNumberRest += "-1";

        String blRestUpdateQuery = "UPDATE hawb_hdr_f,si_hdr_f  SET hawb_hdr_f.JOB_NUMBER=NULL  " +
                "WHERE si_hdr_f.SI_NUMBER IN ("+soNumberRest+") and hawb_hdr_f.hawb_number=si_hdr_f.hawb_no  and si_hdr_f.hawb_no is not null ";
        String blUpdateQuery = "UPDATE hawb_hdr_f,si_hdr_f  SET hawb_hdr_f.JOB_NUMBER=?  " +
                "WHERE si_hdr_f.SI_NUMBER IN ("+soNumber+") and hawb_hdr_f.hawb_number=si_hdr_f.hawb_no  and si_hdr_f.hawb_no is not null ";
        jdbcTemplate.update(blRestUpdateQuery);
        jdbcTemplate.update(blUpdateQuery, bean.getJobNumber());
    }

    public void updateBlWithJob(JobBean bean) {
        String blUpdateQuery = "UPDATE HAWB_HDR_F SET VSL=?,VOY=?,CARRIER_CODE=?,ETD=?,ETA=? WHERE JOB_NUMBER = ? ";
        jdbcTemplate.update(blUpdateQuery,bean.getVsl(), bean.getVoy(),bean.getCarrierCode(), bean.getEtd(), bean.getEta(), bean.getJobNumber());
    }

    public void deletHAWB(HawbBean bean) {
        String siUpdateQuery = "UPDATE SI_HDR_F SET HAWB_NO=NULL WHERE HAWB_NO = ? ";
        String hawbDeleteQuery = "delete from hawb_hdr_f where HAWB_NUMBER = ? ";
        String hawbDtlDeleteQuery = "delete from hawb_dtl_f where HAWB_NUMBER = ? ";
        for(String record : bean.getUuids()){
            jdbcTemplate.update(siUpdateQuery, record);
            jdbcTemplate.update(hawbDtlDeleteQuery, record);
            jdbcTemplate.update(hawbDeleteQuery, record);
        }
    }
}
