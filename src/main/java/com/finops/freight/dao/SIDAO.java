package com.finops.freight.dao;

import com.finops.aop.MeasureTime;
import com.finops.bean.FileUploadBean;
import com.finops.bean.FileUploadRowBean;
import com.finops.dao.AbstractDAO;
import com.finops.freight.bean.*;
import com.finops.report.legacy.SIPDF;
import com.finops.report.legacy.SOPDF;
import com.finops.report.model.ReportBean;
import com.finops.util.DateUtil;
import com.finops.util.sql.Condition;
import com.finops.util.sql.Query;
import com.finops.util.sql.QueryBuilder;
import com.finops.util.sql.QueryType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SIDAO extends AbstractDAO {

    /**
     * SO EXPORT and SO IMPORT use the same SO_HDR_F table.
     * @param bean
     * @return
     */
    @MeasureTime
    public List<SIBean> findSIByPage(SOBean bean){
        String agent = "LOADNG_AGNT";
        String soAgent = "LOADING_AGNT";
        if("IMPORT".equalsIgnoreCase(bean.getExpImp())){
            agent = "DEST_AGNT";
            soAgent = agent;
        }

        QueryBuilder findSOByPageQuery = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" SI.SI_NUMBER soNumber,SI.BKG_REF_NO,CONVERT(SI.BKG_DATE,DATE) bookingRefDate" +
                        ",SI.ACT_SHPR,ASHPR.DESCRIPTION1 ASHPRDESC ,SI.ACT_CNEE" +
                        ",ACNEE.DESCRIPTION1 ACNEEDESC,SI.SHPR shipper,SHPR.DESCRIPTION1 shipperName,SI.SHPR_CONTCT_DTLS" +
                        ",SI.CNEE consignee,CNEE.DESCRIPTION1 consigneeName,SI.CNEE_CONTCT_DTLS,SI.COMM_INV_NUMBER,SI.SI_STATUS" +
                        ",SI.POL,SI.POL_NAME,SI.POD,SI.POD_NAME,SI.JOB_NUMBER,SI.HAWB_NO blNumber,SI.LOADING_AGNT loadingAgentCode," +
                        "SI.CREATED_BY,SI.CREATION_DATE,SI.AMENDED_BY,SI.AMENDED_DATE ")
                .FROM("SI_HDR_F", "SI")
                .LEFT_JOIN("PARTNER_ACCOUNT_D", "CNEE",
                        new Condition("SI.CNEE","CNEE.PARTNER_CODE", Query.EQUALS),
                        new Condition("CNEE.STATUS","'A'", Query.EQUALS),
                        new Condition("CNEE.LOADNG_AGNT","?", Query.EQUALS))
                .LEFT_JOIN("PARTNER_ACCOUNT_D", "SHPR",
                        new Condition("SI.SHPR","SHPR.PARTNER_CODE", Query.EQUALS),
                        new Condition("SHPR.STATUS","'A'", Query.EQUALS),
                        new Condition("SHPR.LOADNG_AGNT","?", Query.EQUALS))
                .LEFT_JOIN("PARTNER_ACCOUNT_D", "ACNEE",
                        new Condition("SI.ACT_CNEE","ACNEE.PARTNER_CODE", Query.EQUALS),
                        new Condition("ACNEE.STATUS","'A'", Query.EQUALS),
                        new Condition("ACNEE.LOADNG_AGNT","?", Query.EQUALS))
                .LEFT_JOIN("PARTNER_ACCOUNT_D", "ASHPR",
                        new Condition("SI.ACT_SHPR","ASHPR.PARTNER_CODE", Query.EQUALS),
                        new Condition("ASHPR.STATUS","'A'", Query.EQUALS),
                        new Condition("ASHPR.LOADNG_AGNT","?", Query.EQUALS))
                .WHERE("SI."+soAgent, "?");
                if("HAWB_CREATE".equalsIgnoreCase(bean.getAction())){
                    if(StringUtils.hasText(bean.getShipper())){
                        findSOByPageQuery.AND("si.shpr", "'"+bean.getShipper()+"'");
                    }
                    if(StringUtils.hasText(bean.getConsignee())){
                        findSOByPageQuery.AND("si.cnee", "'"+bean.getConsignee()+"'");
                    }

                    findSOByPageQuery.AND("si.pol", "'"+bean.getPol()+"'")
                            .AND("si.pod", "'"+bean.getPod()+"'");
                    if(StringUtils.hasText(bean.getDest())) {
                        findSOByPageQuery.AND("si.dest", "'" + bean.getDest() + "'");
                    }
                    //findSOByPageQuery.ANDNULL("si.HAWB_NO");

                }
                else if("JOBSI".equalsIgnoreCase(bean.getAction())){
                    addFilter(findSOByPageQuery, "SI.POL", bean.getPol()+"", true);
                    addFilter(findSOByPageQuery, "SI.POD", bean.getPod()+"", true);
                    findSOByPageQuery.AND("(si.JOB_NUMBER", " '"+bean.getJobNumber()+"' OR si.JOB_NUMBER IS NULL) ");
                }
                else{
                    addFilter(findSOByPageQuery, "SI.HAWB_NO", bean.getBlNumber()+"", true);
                    addFilter(findSOByPageQuery, "SI.BKG_REF_NO", bean.getBkgRefNo()+"", true);
                    addFilter(findSOByPageQuery, "SI.SI_NUMBER", bean.getSoNumber()+"", true);
                    addFilter(findSOByPageQuery, "SI.POL", bean.getPol()+"", true);
                    addFilter(findSOByPageQuery, "SI.POD", bean.getPod()+"", true);
                    addFilter(findSOByPageQuery, "SHPR.DESCRIPTION1", bean.getShipperName()+"", true);
                    addFilter(findSOByPageQuery, "CNEE.DESCRIPTION1", bean.getConsigneeName()+"", true);
                    addFilter(findSOByPageQuery, "SI.JOB_NUMBER", bean.getJobNumber()+"", true);
                    addFilter(findSOByPageQuery, "SI.COMM_INV_NUMBER", bean.getComInvNumber()+"", true);
                    //addFilter(findSOByPageQuery, "SI.BKG_DATE", bean.getBookingRefDate()+"", false);
                    if(StringUtils.hasText(bean.getBookingRefDate()) && StringUtils.hasText(bean.getBookingRefDateTo())) {
                        findSOByPageQuery.AND_BETWEEN("SI.BKG_DATE", "'"+bean.getBookingRefDate()+"'", "'"+bean.getBookingRefDateTo()+"'");
                    }
                    if(StringUtils.hasText(bean.getCb().getShippingBillNumber())){
                        findSOByPageQuery.FREEWHERECONDITION("AND EXISTS (SELECT 'X' FROM si_dtl_f WHERE si_number = si.si_number " +
                                "AND ASBL_NO = '"+bean.getCb().getShippingBillNumber()+"' ) ");
                    }
                }

                findSOByPageQuery.ORDER_BY(" BKG_REF_NO DESC ")
                .LIMIT(bean.getStart(), bean.getLength());

                String query = findSOByPageQuery.build().toString();

        return jdbcTemplate.query(findSOByPageQuery.build().toString(),
                new BeanPropertyRowMapper<>(SIBean.class), bean.getLoadingAgent(),
                bean.getLoadingAgent(), bean.getLoadingAgent(), bean.getLoadingAgent(), bean.getLoadingAgent());
    }

    public List<SIBean> retrieve(SIBean bean) {
        String company = bean.getLoadingAgent();
        String bkgNo = bean.getBkgRefNo() + "";
        if (bean.getIds() != null && bean.getIds().length > 0) {
            bkgNo = "'" + Arrays.stream(bean.getIds())
                    .mapToObj(String::valueOf) // convert each int to a string
                    .collect(Collectors.joining("','")) + "'";
        }
        String query = ("SELECT SI.SI_NUMBER soNumber,convert(SI.DELIVERY_DATE,DATE) DELIVERY_DATE,SI.DELIVERY_AT,SI.BKG_REF_NO ,convert(SI.BKG_DATE,DATE) AS bookingRefDate" +
                ",SI.JOB_NUMBER,SI.CARGO_TYPE,SI.ACT_SHPR actShipper,ACNEE.DESCRIPTION1 actConsigneeName,SI.ACT_CNEE actConsignee,ASHPR.DESCRIPTION1 actShipperName," +
                "SI.SHPR shipper,SHPR.DESCRIPTION1 shipperName,SI.SHPR_CONTCT_DTLS shipperContactDetails" +
                ",SI.CNEE consignee,CNEE.DESCRIPTION1 consigneeName,SI.CNEE_CONTCT_DTLS consigneeContactDetails,SI.COMM_INV_NUMBER comInvNumber,convert(SI.COMM_INV_DT,DATE) comInvDate," +
                "SI.SI_NOTIFY soNotify,NOTIFY.DESCRIPTION1 soNotifyName,SI.SI_NOTIFY_CONTCT_DTLS soNotifyContactDetails,SI.POL,SI.POL_NAME,SI.POD,SI.POD_NAME,SI.DEST," +
                "SI.DEST_NAME,ATPORT.DESCRIPTION1 ATPORTDESC,SI.SI_TYPE,SI.CARRIER_CODE,CARR.DESCRIPTION1 carrierName" +
                ",SI.CARRIER_CONTCT_DTLS,SI.FREIGHT_PAYABLE_AT,convert(SI.CARGO_RECEIVE_DATE,DATE) cargoReceivedDate,SI.PT_AIR_FREIGHT airFreight," +
                "SI.PT_TERMNL_HANDLNG terminalHandling,SI.PT_CARTG cartage,SI.PT_HNDLNG_DOCMNTN handlingDoc,SI.PT_PACKNG packing,SI.PT_OTHRS otherPaymentTerms," +
                "SI.NO_OF_COMM_INV commercialInvoice,SI.NO_OF_PACK_LIST packingList,SI.NO_OF_EXP_LIC exportLicense,SI.NO_OF_CO certOfOrigin,SI.NO_OF_FORM_A formA,SI.NO_OF_OTHERS others,SI.CURRENCY_CODE currencyId," +
                "SI.VAL_CARRAIGE,SI.VAL_CUSTOM,SI.AMT_INSURANCE,SI.LOADING_AGNT loadingAgentCode,LAGNT.DESCRIPTION1 loadingAgentName,SI.LOADING_AGNT_CONTCT_DTLS loadingAgentContactDetails,SI.DEST_AGNT destinationAgentCode,DAGNT.DESCRIPTION1 destinationAgentName," +
                "SI.DEST_AGNT_CONTCT_DTLS destinationAgentContactDetails,SI.ORIGN_WH originWareHouse,OWH.DESCRIPTION1 originWareHouseName,SI.ORIGN_WH_CONTCT_DTLS originWareHouseContactDetails,SI.SI_REMARK,SI.SI_STATUS,SI.HAWB_NO,SI.CREATED_BY," +
                "convert(SI.CREATION_DATE,DATE) AS CREATION_DATE,SI.AMENDED_BY,convert(SI.AMENDED_DATE,DATE) AS AMENDED_DATE "
                + "FROM ((((((((((SI_HDR_F SI "
                + "LEFT OUTER JOIN (SELECT ACNEE.PARTNER_CODE,ACNEE.DESCRIPTION1 FROM PARTNER_ACCOUNT_D ACNEE WHERE ACNEE.LOADNG_AGNT = '" + company + "' AND ACNEE.STATUS = 'A') ACNEE ON (SI.ACT_CNEE = ACNEE.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT ASHPR.PARTNER_CODE,ASHPR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D ASHPR WHERE ASHPR.LOADNG_AGNT = '" + company + "' AND ASHPR.STATUS = 'A') ASHPR ON (SI.ACT_SHPR = ASHPR.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT CNEE.PARTNER_CODE,CNEE.DESCRIPTION1 FROM PARTNER_ACCOUNT_D CNEE WHERE CNEE.LOADNG_AGNT = '" + company + "' AND CNEE.STATUS = 'A') CNEE ON (SI.CNEE = CNEE.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT SHPR.PARTNER_CODE,SHPR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D SHPR WHERE SHPR.LOADNG_AGNT = '" + company + "' AND SHPR.STATUS = 'A') SHPR ON (SI.SHPR = SHPR.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT NOTIFY.PARTNER_CODE,NOTIFY.DESCRIPTION1 FROM PARTNER_ACCOUNT_D NOTIFY WHERE NOTIFY.LOADNG_AGNT = '" + company + "' AND NOTIFY.STATUS = 'A') NOTIFY ON (SI.SI_NOTIFY = NOTIFY.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT LAGNT.PARTNER_CODE,LAGNT.DESCRIPTION1 FROM PARTNER_ACCOUNT_D LAGNT WHERE LAGNT.LOADNG_AGNT = '" + company + "' AND LAGNT.STATUS = 'A') LAGNT ON (SI.LOADING_AGNT = LAGNT.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT DAGNT.PARTNER_CODE,DAGNT.DESCRIPTION1 FROM PARTNER_ACCOUNT_D DAGNT WHERE DAGNT.LOADNG_AGNT = '" + company + "' AND DAGNT.STATUS = 'A') DAGNT ON (SI.DEST_AGNT = DAGNT.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT OWH.PARTNER_CODE,OWH.DESCRIPTION1 FROM PARTNER_ACCOUNT_D OWH WHERE OWH.LOADNG_AGNT = '" + company + "' AND OWH.STATUS = 'A') OWH ON (SI.ORIGN_WH = OWH.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT CARR.PARTNER_CODE,CARR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D CARR WHERE CARR.LOADNG_AGNT = '" + company + "' AND CARR.STATUS = 'A') CARR ON (SI.CARRIER_CODE = CARR.PARTNER_CODE ))"
                + "LEFT OUTER JOIN PORT_D ATPORT ON (SI.DELIVERY_AT = ATPORT.PORT_CODE))  WHERE SI.BKG_REF_NO IN (" + bkgNo + ") ");

        return jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(SIBean.class));
    }

    public List<ContainerBean> getContainerDetails(SIBean bean) {
        String bkgNo = bean.getBkgRefNo() + "";
        if (bean.getIds() != null && bean.getIds().length > 0) {
            bkgNo = "'" + Arrays.stream(bean.getIds())
                    .mapToObj(String::valueOf) // convert each int to a string
                    .collect(Collectors.joining("','")) + "'";
        }
        String query = ("SELECT BD.SI_NUMBER soNumber,BD.BKG_REF_NO bookingRefNumber,BD.BKG_QTY qty,BD.ASBL_NO shippingBillNumber,convert(BD.ASBL_DATE,DATE) shippingBillDate"
                + ",BD.BKG_UNIT unit,BD.BKG_KGS_GROSS_WT weight,BD.BKG_CBM measurement,BD.ACT_QTY actualQty"
                + ",BD.ACT_UNIT actualUnit,BD.ACT_GROSS_WT actualWeight,BD.ACT_CBM actualMeasurement,BD.VOL_WT volumeWeight,BD.CHARGBL_WT chargeableWeight"
                + ",convert(BD.ACT_STUFFING_DATE,DATE) AS ACT_STUFFING_DATE,BD.SI_WH_REMARKS,BD.MARK_NO markNumber,BD.MARK_DETAILS"
                + ",BD.CREATED_BY,convert(BD.CREATION_DATE,DATE) AS CREATION_DATE,BD.AMENDED_BY,convert(BD.AMENDED_DATE,DATE) AS AMENDED_DATE,L.LOADING_AGNT loadingAgentCode"
                + " FROM SI_DTL_F BD,SI_HDR_F L WHERE BD.BKG_REF_NO =L.BKG_REF_NO"
                + " AND BD.BKG_REF_NO=?");

        return jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(ContainerBean.class),
                bean.getBkgRefNo());
    }

    public List<ContainerBean> getContainerDetailsForJob(SIBean bean) {

        String query = ("SELECT BD.SI_NUMBER soNumber,BD.BKG_REF_NO bookingRefNumber,BD.BKG_QTY qty,BD.ASBL_NO,convert(BD.ASBL_DATE,DATE) AS ASBL_DATE"
                + ",BD.BKG_UNIT unit,BD.BKG_KGS_GROSS_WT weight,BD.BKG_CBM measurement,BD.ACT_QTY actualQty"
                + ",BD.ACT_UNIT actualUnit,BD.ACT_GROSS_WT actualWeight,BD.ACT_CBM actualMeasurement,BD.VOL_WT volumeWeight,BD.CHARGBL_WT chargeableWeight"
                + ",convert(BD.ACT_STUFFING_DATE,DATE) AS ACT_STUFFING_DATE,BD.SI_WH_REMARKS,BD.MARK_NO markNumber,BD.MARK_DETAILS"
                + ",BD.CREATED_BY,convert(BD.CREATION_DATE,DATE) AS CREATION_DATE,BD.AMENDED_BY,convert(BD.AMENDED_DATE,DATE) AS AMENDED_DATE,L.LOADING_AGNT loadingAgentCode"
                + " FROM SI_DTL_F BD,SI_HDR_F L WHERE BD.BKG_REF_NO =L.BKG_REF_NO"
                + " AND L.JOB_NUMBER=?");

        return jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(ContainerBean.class),
                bean.getJobNumber());
    }


    public void saveSI(SIBean bean) {
        String query = ("INSERT INTO si_hdr_f(BKG_REF_NO,SI_NUMBER,DELIVERY_DATE,DELIVERY_AT,BKG_DATE,CARGO_TYPE,ACT_SHPR,ACT_CNEE," + //8
                "SHPR,SHPR_CONTCT_DTLS,CNEE,CNEE_CONTCT_DTLS,COMM_INV_NUMBER,COMM_INV_DT,SI_NOTIFY," + //15
                "SI_NOTIFY_CONTCT_DTLS,POL,POL_NAME,POD,POD_NAME,DEST,DEST_NAME,SI_TYPE,CARRIER_CODE,CARRIER_CONTCT_DTLS," + //25
                "FREIGHT_PAYABLE_AT,CARGO_RECEIVE_DATE,PT_AIR_FREIGHT,PT_TERMNL_HANDLNG,PT_CARTG,PT_HNDLNG_DOCMNTN,PT_PACKNG,PT_OTHRS,NO_OF_COMM_INV, " + //34
                "NO_OF_PACK_LIST,NO_OF_EXP_LIC,NO_OF_CO,NO_OF_FORM_A,NO_OF_OTHERS,CURRENCY_CODE,VAL_CARRAIGE, VAL_CUSTOM,AMT_INSURANCE, LOADING_AGNT, " + //44
                "LOADING_AGNT_CONTCT_DTLS, DEST_AGNT, DEST_AGNT_CONTCT_DTLS, ORIGN_WH,ORIGN_WH_CONTCT_DTLS,SI_REMARK,CREATED_BY, CREATION_DATE, SI_STATUS) " + //53
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                        "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                        "?,?,?,?,?,?,?,?,?,?,?,SYSDATE(),'A')");

        jdbcTemplate.update(query,
                bean.getBkgRefNo(), bean.getSoNumber(), bean.getDeliveryDate(), bean.getDeliveryAt(), bean.getBookingRefDate(), bean.getCargoType(), bean.getActShipper(), //7
                bean.getActConsignee(), bean.getShipper(), bean.getShipperContactDetails(), bean.getConsignee(), bean.getConsigneeContactDetails(),bean.getComInvNumber(), //13
                org.apache.commons.lang3.StringUtils.defaultIfEmpty(bean.getComInvDate(),null), bean.getSoNotify(),bean.getSoNotifyContactDetails(), bean.getPol(), //17
                bean.getPolName(), bean.getPod(), bean.getPodName(), bean.getDest(), bean.getDestName(), bean.getFlightType(), bean.getCarrierCode(),"", //25
                bean.getFreightPayableAt(), org.apache.commons.lang3.StringUtils.defaultIfEmpty(bean.getCargoReceivedDate(),null) , //27
                bean.getAirFreight(), bean.getTerminalHandling(), bean.getCartage(), bean.getHandlingDoc(), bean.getPacking(), bean.getOtherPaymentTerms(), //33
                bean.getCommercialInvoice(), bean.getPackingList(), bean.getExportLicense(), bean.getCertOfOrigin(),bean.getFormA(), bean.getOthers(), bean.getCurrencyId(), //40
                null, null, null, bean.getLoadingAgentCode(), bean.getLoadingAgentContactDetails(),bean.getDestinationAgentCode(), bean.getDestinationAgentContactDetails(), //47
                bean.getOriginWareHouse(), bean.getOriginWareHouseContactDetails(), bean.getRemarks(), bean.getUserId());
    }

    public void updateSI(HawbBean bean) {
        String bkgNo = null;
        if (bean.getIds() != null && bean.getIds().length > 0) {
            bkgNo = "'" + Arrays.stream(bean.getIds())
                    .mapToObj(String::valueOf) // convert each int to a string
                    .collect(Collectors.joining("','")) + "'";
        }
        String query = ("UPDATE si_hdr_f SET hawb_no=?, DATE_OF_ISSUE=? WHERE BKG_REF_NO IN (" + bkgNo + ") ");
        jdbcTemplate.update(query, bean.getBlNumber(), defaultNull(bean.getBlIssueDate()));
    }

    public void updateSI(SIBean bean) {
        String query = ("UPDATE si_hdr_f SET SI_NUMBER=?,DELIVERY_DATE=?" +
                ",DELIVERY_AT=?,BKG_DATE=?,CARGO_TYPE=?,ACT_SHPR=?,ACT_CNEE=?,SHPR=?,SHPR_CONTCT_DTLS=?" +
                ",CNEE=?,CNEE_CONTCT_DTLS=?,COMM_INV_DT=?,SI_NOTIFY=?,SI_NOTIFY_CONTCT_DTLS=?,POL=?,POD=?" +
                ",DEST=?,SI_TYPE=?,CARRIER_CODE=?,CARRIER_CONTCT_DTLS=?,FREIGHT_PAYABLE_AT=?" +
                ",CARGO_RECEIVE_DATE=?,PT_AIR_FREIGHT=?,PT_TERMNL_HANDLNG=?,PT_CARTG=?,PT_HNDLNG_DOCMNTN=?" +
                ",PT_PACKNG=?,PT_OTHRS=?,CURRENCY_CODE=?,VAL_CARRAIGE=?,VAL_CUSTOM=?,AMT_INSURANCE=?" +
                ",LOADING_AGNT=?,LOADING_AGNT_CONTCT_DTLS=?,DEST_AGNT=?,DEST_AGNT_CONTCT_DTLS=?,ORIGN_WH=?" +
                ",ORIGN_WH_CONTCT_DTLS=?,SI_REMARK=?,SI_STATUS=?" +
                ",COMM_INV_NUMBER=?,NO_OF_COMM_INV=?" +
                ",NO_OF_PACK_LIST=?,NO_OF_EXP_LIC=?,NO_OF_CO=?,NO_OF_FORM_A=?" +
                ",NO_OF_OTHERS=?,POL_NAME=?,POD_NAME=?,DEST_NAME=?,AMENDED_BY=?,AMENDED_DATE=? " +
                "where BKG_REF_NO = ? ");

        jdbcTemplate.update(query, bean.getSoNumber(),
                bean.getDeliveryDate(), bean.getDeliveryAt(),bean.getBookingRefDate(), bean.getCargoType(),  bean.getActShipper(), bean.getActConsignee(), bean.getShipper(),
                bean.getShipperContactDetails(),bean.getConsignee(), bean.getConsigneeContactDetails(), org.apache.commons.lang3.StringUtils.defaultIfEmpty(bean.getComInvDate(),null),
                bean.getSoNotify(), bean.getSoNotifyContactDetails(), bean.getPol(), bean.getPod(), bean.getDest(), bean.getFlightType(),
                bean.getCarrierCode(), null,bean.getFreightPayableAt(), org.apache.commons.lang3.StringUtils.defaultIfEmpty(bean.getCargoReceivedDate(),null), bean.getAirFreight(),
                bean.getTerminalHandling(), bean.getCartage(), bean.getHandlingDoc(), bean.getPacking(), bean.getOtherPaymentTerms(), bean.getCurrencyId(), null, null, null,
                bean.getLoadingAgentCode(), bean.getLoadingAgentContactDetails(), bean.getDestinationAgentCode(), bean.getDestinationAgentContactDetails(), bean.getOriginWareHouse(),
                bean.getOriginWareHouseContactDetails(), bean.getRemarks(), "A", bean.getComInvNumber(), bean.getCommercialInvoice(), bean.getPackingList(), bean.getExportLicense(),
                bean.getCertOfOrigin(), bean.getFormA(), bean.getOthers(), bean.getPolName(), bean.getPodName(),bean.getDestName(), bean.getCreatedBy(),
                DateUtil.getSystemDate(), bean.getBkgRefNo());
    }

    public void siPDF(SOBean soBean) {
        SIPDF.writeUCMpdf(soBean.getBkgRefNo(), soBean, jdbcTemplate);
    }

    public void saveLclFcl(ContainerBean cb) {
        String query = "INSERT INTO si_dtl_f ( SI_NUMBER,BKG_REF_NO,BKG_QTY,BKG_UNIT,BKG_KGS_GROSS_WT,BKG_CBM,"
                + "ACT_QTY,ACT_UNIT,ACT_GROSS_WT,ACT_CBM,VOL_WT,CHARGBL_WT,ACT_STUFFING_DATE,SI_WH_REMARKS,"
                + "MARK_NO,MARK_DETAILS,CREATED_BY,CREATION_DATE,AMENDED_BY,AMENDED_DATE,ASBL_NO,ASBL_DATE) " +
                "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        jdbcTemplate.update(query,
                cb.getSoNumber(), cb.getBookingRefNumber(), cb.getQty(), cb.getUnit(), cb.getWeight(), cb.getMeasurement(), default0(cb.getActualQty()), default0(cb.getActualUnit()),
                default0(cb.getActualWeight()), default0(cb.getActualMeasurement()), default0(cb.getVolumeWeight()), default0(cb.getChargeableWeight()),
                null, cb.getContainerRemarks(),cb.getMarkNumber(), cb.getMarkDetails(), cb.getUserId(),
                DateUtil.getSystemDate(), cb.getUserId(), DateUtil.getSystemDate(), cb.getShippingBillNumber(),defaultNull(cb.getShippingBillDate()));
    }


    public void updateLclFcl(ContainerBean cb) {
        String query = "UPDATE si_dtl_f SET BKG_QTY=?,BKG_UNIT=?,BKG_KGS_GROSS_WT=?, BKG_CBM=?,ACT_QTY=?,ACT_UNIT=?,ACT_GROSS_WT=?,ACT_CBM=?," +
                "ACT_STUFFING_DATE=?,ASBL_NO=?,ASBL_DATE=?,SI_WH_REMARKS=?,AMENDED_BY=?,AMENDED_DATE=?,MARK_NO=?,MARK_DETAILS=?,VOL_WT=?,CHARGBL_WT=? " +
                "WHERE BKG_REF_NO = ? ";

        jdbcTemplate.update(query,
                cb.getQty(), cb.getUnit(), cb.getWeight(), cb.getMeasurement(), cb.getActualQty(), cb.getActualUnit(), cb.getActualWeight(), cb.getActualMeasurement(),
                null, cb.getShippingBillNumber(), defaultNull(cb.getShippingBillDate()), cb.getContainerRemarks(),
                cb.getUserId(), DateUtil.getSystemDate(), cb.getMarkNumber(), cb.getMarkDetails(),cb.getVolumeWeight(),cb.getChargeableWeight(), cb.getBookingRefNumber());
    }

    public void uploadFile(FileUploadBean fileUploadBean) {

        String query = "INSERT INTO so_file (BKG_REF_NO,SO_NUMBER,LOADING_AGNT,POD,FILE_NAME,FILE_,DESCRIPTION,CREATED_BY,CREATION_DATE) "
                + "VALUES (?,?,?,?,?,?,?,?,getdate())";

        for (FileUploadRowBean row : fileUploadBean.getRowList()) {
            try {
                if (row.getDocument() != null && row.getDocument().getInputStream() != null) {
                    jdbcTemplate.update(query, fileUploadBean.getParam1(), fileUploadBean.getParam2(), fileUploadBean.getLoadingAgent(),
                            fileUploadBean.getParam3(), row.getFileName(), null, fileUploadBean.getCreatedBy());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateSI(JobBean bean) {
        String soNumber = "";
        String soNumberRest = "";

        for(SOBean cb : bean.getSiBeanList()){
            if(StringUtils.hasText(cb.getUuid())) {
                soNumber += cb.getSoNumber() + ",";
            }
            else{
                soNumberRest += cb.getSoNumber()+",";
            }
        }
        soNumber+="-1";
        soNumberRest += "-1";

        String soRestUpdateQuery = "UPDATE SI_HDR_F SET JOB_NUMBER =NULL WHERE SI_NUMBER IN ("+soNumberRest+") ";
        String soUpdateQuery = "UPDATE SI_HDR_F SET JOB_NUMBER =? WHERE SI_NUMBER IN ("+soNumber+") ";
        jdbcTemplate.update(soRestUpdateQuery);
        jdbcTemplate.update(soUpdateQuery, bean.getJobNumber());
    }

    public void updateSIWithJob(JobBean bean) {
        String soUpdateQuery = "UPDATE SI_HDR_F SET VSL=?,VOY=?,CARRIER_CODE=?,ETD=?,ETA=? WHERE JOB_NUMBER = ? ";
        jdbcTemplate.update(soUpdateQuery,bean.getVsl(), bean.getVoy(),bean.getCarrierCode(), bean.getEtd(), bean.getEta(), bean.getJobNumber());
    }

    public List<ReportBean> retrieve(String soNo, String loadingAgent, String yrStartDate, String seaAir, String expImp) {
        String soAgent = "LOADING_AGNT";
        if ("IMPORT".equalsIgnoreCase(expImp)) {
            //agent = "DEST_AGNT";
            soAgent = "DEST_AGNT";
        }
        String query = "SELECT si_number param1,hawb_no param2 FROM si_hdr_f WHERE "+soAgent+"=? AND si_number LIKE ? AND BKG_DATE > DATE_SUB(?, INTERVAL 400 DAY) limit 10";

        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ReportBean.class), loadingAgent, soNo, yrStartDate);
    }

    public List<SIBean> retrieveBySI(SIBean bean) {
        String company = bean.getLoadingAgent();
        String bkgNo = bean.getSoNumber() + "";
        if (bean.getIds() != null && bean.getIds().length > 0) {
            bkgNo = "'" + Arrays.stream(bean.getIds())
                    .mapToObj(String::valueOf) // convert each int to a string
                    .collect(Collectors.joining("','")) + "'";
        }
        String query = ("SELECT SI.SI_NUMBER soNumber,convert(SI.DELIVERY_DATE,DATE) DELIVERY_DATE,SI.DELIVERY_AT,SI.BKG_REF_NO ,convert(SI.BKG_DATE,DATE) AS bookingRefDate" +
                ",SI.JOB_NUMBER,SI.CARGO_TYPE,SI.ACT_SHPR actShipper,ACNEE.DESCRIPTION1 actConsigneeName,SI.ACT_CNEE actConsignee,ASHPR.DESCRIPTION1 actShipperName," +
                "SI.SHPR shipper,SHPR.DESCRIPTION1 shipperName,SI.SHPR_CONTCT_DTLS shipperContactDetails" +
                ",SI.CNEE consignee,CNEE.DESCRIPTION1 consigneeName,SI.CNEE_CONTCT_DTLS consigneeContactDetails,SI.COMM_INV_NUMBER comInvNumber,convert(SI.COMM_INV_DT,DATE) comInvDate," +
                "SI.SI_NOTIFY soNotify,NOTIFY.DESCRIPTION1 soNotifyName,SI.SI_NOTIFY_CONTCT_DTLS soNotifyContactDetails,SI.POL,SI.POL_NAME,SI.POD,SI.POD_NAME,SI.DEST," +
                "SI.DEST_NAME,ATPORT.DESCRIPTION1 ATPORTDESC,SI.SI_TYPE,SI.CARRIER_CODE,CARR.DESCRIPTION1 carrierName" +
                ",SI.CARRIER_CONTCT_DTLS,SI.FREIGHT_PAYABLE_AT,convert(SI.CARGO_RECEIVE_DATE,DATE) cargoReceivedDate,SI.PT_AIR_FREIGHT airFreight," +
                "SI.PT_TERMNL_HANDLNG terminalHandling,SI.PT_CARTG cartage,SI.PT_HNDLNG_DOCMNTN handlingDoc,SI.PT_PACKNG packing,SI.PT_OTHRS otherPaymentTerms," +
                "SI.NO_OF_COMM_INV commercialInvoice,SI.NO_OF_PACK_LIST packingList,SI.NO_OF_EXP_LIC exportLicense,SI.NO_OF_CO certOfOrigin,SI.NO_OF_FORM_A formA,SI.NO_OF_OTHERS others,SI.CURRENCY_CODE currencyId," +
                "SI.VAL_CARRAIGE,SI.VAL_CUSTOM,SI.AMT_INSURANCE,SI.LOADING_AGNT loadingAgentCode,LAGNT.DESCRIPTION1 loadingAgentName,SI.LOADING_AGNT_CONTCT_DTLS loadingAgentContactDetails,SI.DEST_AGNT destinationAgentCode,DAGNT.DESCRIPTION1 destinationAgentName," +
                "SI.DEST_AGNT_CONTCT_DTLS destinationAgentContactDetails,SI.ORIGN_WH originWareHouse,OWH.DESCRIPTION1 originWareHouseName,SI.ORIGN_WH_CONTCT_DTLS originWareHouseContactDetails,SI.SI_REMARK,SI.SI_STATUS,SI.HAWB_NO blNumber,SI.CREATED_BY," +
                "convert(SI.CREATION_DATE,DATE) AS CREATION_DATE,SI.AMENDED_BY,convert(SI.AMENDED_DATE,DATE) AS AMENDED_DATE "
                + "FROM ((((((((((SI_HDR_F SI "
                + "LEFT OUTER JOIN (SELECT ACNEE.PARTNER_CODE,ACNEE.DESCRIPTION1 FROM PARTNER_ACCOUNT_D ACNEE WHERE ACNEE.LOADNG_AGNT = '" + company + "' AND ACNEE.STATUS = 'A') ACNEE ON (SI.ACT_CNEE = ACNEE.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT ASHPR.PARTNER_CODE,ASHPR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D ASHPR WHERE ASHPR.LOADNG_AGNT = '" + company + "' AND ASHPR.STATUS = 'A') ASHPR ON (SI.ACT_SHPR = ASHPR.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT CNEE.PARTNER_CODE,CNEE.DESCRIPTION1 FROM PARTNER_ACCOUNT_D CNEE WHERE CNEE.LOADNG_AGNT = '" + company + "' AND CNEE.STATUS = 'A') CNEE ON (SI.CNEE = CNEE.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT SHPR.PARTNER_CODE,SHPR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D SHPR WHERE SHPR.LOADNG_AGNT = '" + company + "' AND SHPR.STATUS = 'A') SHPR ON (SI.SHPR = SHPR.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT NOTIFY.PARTNER_CODE,NOTIFY.DESCRIPTION1 FROM PARTNER_ACCOUNT_D NOTIFY WHERE NOTIFY.LOADNG_AGNT = '" + company + "' AND NOTIFY.STATUS = 'A') NOTIFY ON (SI.SI_NOTIFY = NOTIFY.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT LAGNT.PARTNER_CODE,LAGNT.DESCRIPTION1 FROM PARTNER_ACCOUNT_D LAGNT WHERE LAGNT.LOADNG_AGNT = '" + company + "' AND LAGNT.STATUS = 'A') LAGNT ON (SI.LOADING_AGNT = LAGNT.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT DAGNT.PARTNER_CODE,DAGNT.DESCRIPTION1 FROM PARTNER_ACCOUNT_D DAGNT WHERE DAGNT.LOADNG_AGNT = '" + company + "' AND DAGNT.STATUS = 'A') DAGNT ON (SI.DEST_AGNT = DAGNT.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT OWH.PARTNER_CODE,OWH.DESCRIPTION1 FROM PARTNER_ACCOUNT_D OWH WHERE OWH.LOADNG_AGNT = '" + company + "' AND OWH.STATUS = 'A') OWH ON (SI.ORIGN_WH = OWH.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT CARR.PARTNER_CODE,CARR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D CARR WHERE CARR.LOADNG_AGNT = '" + company + "' AND CARR.STATUS = 'A') CARR ON (SI.CARRIER_CODE = CARR.PARTNER_CODE ))"
                + "LEFT OUTER JOIN PORT_D ATPORT ON (SI.DELIVERY_AT = ATPORT.PORT_CODE))  WHERE SI.SI_NUMBER IN (" + bkgNo + ") ");

        return jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(SIBean.class));
    }
}
