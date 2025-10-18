package com.finops.freight.dao;

import com.finops.aop.MeasureTime;
import com.finops.bean.FileUploadBean;
import com.finops.bean.FileUploadRowBean;
import com.finops.dao.AbstractDAO;
import com.finops.freight.bean.BLBean;
import com.finops.freight.bean.ContainerBean;
import com.finops.freight.bean.JobBean;
import com.finops.freight.bean.SOBean;
import com.finops.report.legacy.SOPDF;
import com.finops.report.model.ReportBean;
import com.finops.util.ApplicationUtil;
import com.finops.util.DateUtil;
import com.finops.util.sql.Condition;
import com.finops.util.sql.Query;
import com.finops.util.sql.QueryBuilder;
import com.finops.util.sql.QueryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SODAO extends AbstractDAO {

    private static final Logger logger = LoggerFactory.getLogger(SODAO.class);

    /**
     * SO EXPORT and SO IMPORT use the same SO_HDR_F table.
     *
     * @param bean
     * @return
     */
    @MeasureTime
    public List<SOBean> findSOByPage(SOBean bean) {
        String agent = "LOADNG_AGNT";
        String soAgent = "LOADING_AGNT";
        if ("IMPORT".equalsIgnoreCase(bean.getExpImp())) {
            agent = "DEST_AGNT";
            soAgent = agent;
        }

        QueryBuilder findSOByPageQuery = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" so.BKG_REF_NO,so.SO_NUMBER,SO.JOB_NUMBER,so.BL_NO blNumber,so.POL,so.POD,so.VSL,so.VOY,so.LOADING_AGNT loadingAgentCode" +
                        ",'SHPR.DESCRIPTION1' shipperName,'CNEE.DESCRIPTION1' consigneeName,SO.ETD,'sman.name' sales_by,SO.ETA ETA,SO.BKG_TYPE," +
                        "so.DEST_AGNT,SO.DEST,SO.NO_OF_ORIGINAL,so.ORIGN_WH,so.SO_STATUS,SO.POR,so.COMM_INV_NUMBER,so.BKG_DATE bookingRefDate ," +
                        "so.DELIVERY_DATE DELIVERY_DATE,so.ACT_SHPR,SO.CARRIER_CODE,SO.CARGO_RECEIVE_DATE CARGO_RECEIVE_DATE " +
                        ",'ASHPR.DESCRIPTION1' ASHPRDESC,so.ACT_CNEE,'ACNEE.DESCRIPTION1' ACNEEDESC,so.SHPR shipper," +
                        "so.CNEE consignee,SO.CREATED_BY,SO.CREATION_DATE CREATION_DATE,SO.AMENDED_BY" +
                        ",SO.AMENDED_DATE AMENDED_DATE,SO.CNEE_CONTCT_DTLS,SO.SHPR_CONTCT_DTLS ")
                .FROM("SO_HDR_F", "SO")
                .WHERE("SO." + soAgent, "?");
        if ("BL_CREATE".equalsIgnoreCase(bean.getAction())) {
            if (StringUtils.hasText(bean.getShipper())) {
                findSOByPageQuery.AND("so.shpr", "'" + bean.getShipper() + "'");
            }
            if (StringUtils.hasText(bean.getConsignee())) {
                findSOByPageQuery.AND("so.cnee", "'" + bean.getConsignee() + "'");
            }

            findSOByPageQuery.AND("so.pol", "'" + bean.getPol() + "'")
                    .AND("so.pod", "'" + bean.getPod() + "'");
            if (StringUtils.hasText(bean.getDest())) {
                findSOByPageQuery.AND("so.dest", "'" + bean.getDest() + "'");
            }
            if (StringUtils.hasText(bean.getBkgType())) {
                findSOByPageQuery.AND("so.BKG_TYPE", "'" + bean.getBkgType() + "'");
            }
            findSOByPageQuery.ANDNULL("so.BL_NO");
            findSOByPageQuery.FREEWHERECONDITION("AND EXISTS (SELECT 'X' FROM so_fcl_lcl_f WHERE so_number = so.so_number) ");
        } else if ("JOBSO".equalsIgnoreCase(bean.getAction())) {
            findSOByPageQuery.AND("so.pol", "'" + bean.getPol() + "'");
            findSOByPageQuery.AND("so.pod", "'" + bean.getPod() + "'");
            findSOByPageQuery.AND("so.etd", "'" + bean.getEtd() + "'");
            findSOByPageQuery.AND("so.eta", "'" + bean.getEta() + "'");
            findSOByPageQuery.AND("so.vsl", "'" + bean.getVsl() + "'");
            findSOByPageQuery.AND("so.voy", "'" + bean.getVoy() + "'");

            findSOByPageQuery.AND("(so.JOB_NUMBER", " '"+bean.getJobNumber()+"' OR so.JOB_NUMBER IS NULL) ");
        }
        else if ("CLP".equalsIgnoreCase(bean.getAction())) {
            findSOByPageQuery.AND("so.pol", "'" + bean.getPol() + "'");
            findSOByPageQuery.AND("so.pod", "'" + bean.getPod() + "'");
            findSOByPageQuery.AND("so.ORIGN_WH", "'" + bean.getOriginWareHouse() + "'");
            findSOByPageQuery.AND("so.BKG_TYPE", "'L'");
            findSOByPageQuery.AND("(so.load_plan_no", " '"+bean.getLoadPlanNo()+"' OR so.load_plan_no IS NULL) ");
            bean.setLength(50);
        }else {
            String[] fields = {"so.BKG_REF_NO", "so.SO_NUMBER", "", "", "", "",
                    "", "", "", "", "so.JOB_NUMBER", "so.BL_NO"};

            addFilter(findSOByPageQuery, bean, fields);
        }

        findSOByPageQuery.ORDER_BY(" BKG_REF_NO DESC ")
                .LIMIT(bean.getStart(), bean.getLength());

        return jdbcTemplate.query(findSOByPageQuery.build().toString(),
                new BeanPropertyRowMapper<>(SOBean.class), bean.getLoadingAgent());
    }

    @MeasureTime
    public List<SOBean> searchSOByPage(SOBean bean) {
        String agent = "LOADNG_AGNT";
        String soAgent = "LOADING_AGNT";
        if ("IMPORT".equalsIgnoreCase(bean.getExpImp())) {
            //agent = "DEST_AGNT";
            soAgent = "DEST_AGNT";
        }

        QueryBuilder findSOByPageQuery = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" so.BKG_REF_NO,so.SO_NUMBER,SO.JOB_NUMBER,so.BL_NO blNumber,so.POL,so.POD,so.VSL,so.VOY,so.LOADING_AGNT loadingAgentCode" +
                        ",SHPR.DESCRIPTION1 shipperName,CNEE.DESCRIPTION1 consigneeName,CONVERT(SO.ETD, DATE) ETD,'sman.name' sales_by,CONVERT(SO.ETA,DATE) ETA,SO.BKG_TYPE," +
                        "so.DEST_AGNT,SO.DEST,SO.NO_OF_ORIGINAL,so.ORIGN_WH,so.SO_STATUS,SO.POR,so.COMM_INV_NUMBER,so.BKG_DATE bookingRefDate ," +
                        "so.DELIVERY_DATE DELIVERY_DATE,so.ACT_SHPR,SO.CARRIER_CODE,SO.CARGO_RECEIVE_DATE CARGO_RECEIVE_DATE " +
                        ",ASHPR.DESCRIPTION1 actShipperName,so.ACT_CNEE,ACNEE.DESCRIPTION1 actConsigneeName,so.SHPR shipper," +
                        "so.CNEE consignee,SO.CREATED_BY,SO.CREATION_DATE CREATION_DATE,SO.AMENDED_BY" +
                        ",SO.AMENDED_DATE AMENDED_DATE,SO.CNEE_CONTCT_DTLS,SO.SHPR_CONTCT_DTLS ")
                .FROM("SO_HDR_F", "SO")
                .LEFT_JOIN("PARTNER_ACCOUNT_D", "CNEE",
                        new Condition("SO.CNEE","CNEE.PARTNER_CODE", Query.EQUALS),
                        new Condition("CNEE.STATUS","'A'", Query.EQUALS),
                        new Condition("CNEE."+agent,"?", Query.EQUALS))
                .LEFT_JOIN("PARTNER_ACCOUNT_D", "SHPR",
                        new Condition("SO.SHPR","SHPR.PARTNER_CODE", Query.EQUALS),
                        new Condition("SHPR.STATUS","'A'", Query.EQUALS),
                        new Condition("SHPR."+agent,"?", Query.EQUALS))
                .LEFT_JOIN("PARTNER_ACCOUNT_D", "ACNEE",
                        new Condition("SO.ACT_CNEE","ACNEE.PARTNER_CODE", Query.EQUALS),
                        new Condition("ACNEE.STATUS","'A'", Query.EQUALS),
                        new Condition("ACNEE."+agent,"?", Query.EQUALS))
                .LEFT_JOIN("PARTNER_ACCOUNT_D", "ASHPR",
                        new Condition("SO.ACT_SHPR","ASHPR.PARTNER_CODE", Query.EQUALS),
                        new Condition("ASHPR.STATUS","'A'", Query.EQUALS),
                        new Condition("ASHPR."+agent,"?", Query.EQUALS))
                .WHERE("SO." + soAgent, "?");

        addFilter(findSOByPageQuery, "SO.BKG_REF_NO", bean.getBkgRefNo()+"", true);
        addFilter(findSOByPageQuery, "SO.BL_NO", bean.getBlNumber()+"", true);
        addFilter(findSOByPageQuery, "SO.SO_NUMBER", bean.getSoNumber()+"", true);
        addFilter(findSOByPageQuery, "SO.JOB_NUMBER", bean.getJobNumber()+"", true);
        addFilter(findSOByPageQuery, "SO.VSL", bean.getVsl()+"", true);
        addFilter(findSOByPageQuery, "SO.VOY", bean.getVoy()+"", true);
        addFilter(findSOByPageQuery, "SO.POL", bean.getPol()+"", true);
        addFilter(findSOByPageQuery, "SO.POD", bean.getPod()+"", true);
        addFilter(findSOByPageQuery, "SHPR.DESCRIPTION1", bean.getShipperName()+"", true);
        addFilter(findSOByPageQuery, "CNEE.DESCRIPTION1", bean.getConsigneeName()+"", true);
        addFilter(findSOByPageQuery, "ACNEE.DESCRIPTION1", bean.getActConsignee()+"", true);
        addFilter(findSOByPageQuery, "ASHPR.DESCRIPTION1", bean.getActShipperName()+"", true);
        addFilter(findSOByPageQuery, "SO.COMM_INV_NUMBER", bean.getComInvNumber()+"", true);
        if(StringUtils.hasText(bean.getBookingRefDate()) && StringUtils.hasText(bean.getBookingRefDateTo())) {
            findSOByPageQuery.AND_BETWEEN("SO.BKG_DATE", "'"+bean.getBookingRefDate()+"'", "'"+bean.getBookingRefDateTo()+"'");
        }
        addFilter(findSOByPageQuery, "SO.BKG_TYPE", bean.getBookingType(), false);
        if(StringUtils.hasText(bean.getCb().getShippingBillNumber())){
            findSOByPageQuery.FREEWHERECONDITION("AND EXISTS (SELECT 'X' FROM so_fcl_lcl_f WHERE so_number = so.so_number " +
                    "AND shipping_bill_no = '"+bean.getCb().getShippingBillNumber()+"' ) ");
        }

        findSOByPageQuery.ORDER_BY(" BKG_REF_NO DESC ")
                .LIMIT(bean.getStart(), bean.getLength());


        String selectQuery = findSOByPageQuery.build().toString();
        setRecordCount(selectQuery, bean, true, bean.getLoadingAgent(),
                bean.getLoadingAgent(), bean.getLoadingAgent(), bean.getLoadingAgent(), bean.getLoadingAgent());

        return jdbcTemplate.query(findSOByPageQuery.build().toString(),
                new BeanPropertyRowMapper<>(SOBean.class), bean.getLoadingAgent(),
                bean.getLoadingAgent(), bean.getLoadingAgent(), bean.getLoadingAgent(), bean.getLoadingAgent());
    }

    public List<SOBean> retrieve(SOBean bean) {
        String la = bean.getLoadingAgent();
        String bkgNo = bean.getBkgRefNo() + "";
        if (bean.getIds() != null && bean.getIds().length > 0) {
            bkgNo = "'" + Arrays.stream(bean.getIds())
                    .mapToObj(String::valueOf) // convert each int to a string
                    .collect(Collectors.joining("','")) + "'";
        }
        String query = ("SELECT SO.SO_NUMBER soNumber,SO.NO_OF_ORIGINAL,STR_TO_DATE(SO.DELIVERY_DATE,'%Y-%m-%d') AS DELIVERY_DATE"
                + ",SO.DELIVERY_AT,SO.BKG_REF_NO,STR_TO_DATE(SO.BKG_DATE,'%Y-%m-%d') AS bookingRefDate,SO.JOB_NUMBER"
                + ",SO.BKG_TYPE bookingType,SO.JOB_NUMBER,SO.CARGO_TYPE,SO.NO_OF_ORIGINAL,SO.ACT_SHPR actShipper,SO.ACT_CNEE actConsignee"
                + ",SO.SHPR shipper,SO.SHPR_CONTCT_DTLS shipperContactDetails,SO.CNEE consignee,SO.CNEE_CONTCT_DTLS consigneeContactDetails,SO.COMM_INV_NUMBER comInvNumber,"
                + "STR_TO_DATE(SO.COMM_INV_DT,'%Y-%m-%d') AS comInvDate,SO.SO_NOTIFY,NOTIFY.DESCRIPTION1 soNotifyName"
                + ",SO.SO_NOTIFY_CONTCT_DTLS soNotifyContactDetails,SO.ALSO_NOTIFY alsoNotify,ANOTIFY.DESCRIPTION1 alsoNotifyName,SO.ALSO_NOTIFY_CONTCT_DTLS alsoNotifyContactDetails"
                + ",SO.POR,SO.POR_NAME,SO.POR_IND,SO.POL,SO.POL_NAME,ATPORT.DESCRIPTION1 ATPORTDESC"
                + ",SO.POL_IND,SO.FROM_FREIGHTAGE_CODE fromFreightCode,SO.FROM_FREIGHTAGE_NAME fromFreightName,SO.POD,SO.POD_NAME,SO.POD_IND"
                + ",SO.DEST,SO.DEST_NAME,SO.DEST_IND,SO.UPTO_FREIGHTAGE_CODE upToFreightCode,SO.UPTO_FREIGHTAGE_NAME upToFreightName,SO.PRE_CARRAIGE preCarriage"
                + ",SO.PRE_VOYAGE preVoyage,SO.SS_NUMBER,SO.VSL,SO.VOY,SO.CARRIER_CODE"
                + ",CARR.DESCRIPTION1 carrierName,SO.FREIGHT_TYPE,SO.FREIGHT_PAYABLE_AT,STR_TO_DATE(SO.CFS_DATE,'%Y-%m-%d') AS CFS_DATE"
                + ",SO.CFS_TIME,STR_TO_DATE(SO.CY_DATE,'%Y-%m-%d') AS CY_DATE"
                + ",SO.CY_TIME,SO.CARGO_RECEIVE_DATE,STR_TO_DATE(SO.ETD,'%Y-%m-%d') AS ETD,STR_TO_DATE(SO.ETA,'%Y-%m-%d') AS ETA"
                + ",SO.LOADING_AGNT loadingAgentCode,LAGNT.DESCRIPTION1 loadingAgentName,SO.LOADING_AGNT_CONTCT_DTLS loadingAgentContactDetails,SO.DEST_AGNT destinationAgentCode"
                + ",DAGNT.DESCRIPTION1 destinationAgentName,SO.DEST_AGNT_CONTCT_DTLS destinationAgentContactDetails,SO.ORIGN_WH originWareHouse,OWH.DESCRIPTION1 originWareHouseName"
                + ",SO.ORIGN_WH_CONTCT_DTLS originWareHouseContactDetails,SO.SO_REMARK remarks,BL_NO blNumber,STR_TO_DATE(SO.DATE_OF_ISSUE,'%Y-%m-%d') AS DATE_OF_ISSUE"
                + ",SO.SO_STATUS,ACNEE.DESCRIPTION1 ACNEEDESC,ASHPR.DESCRIPTION1 actShipperName,CNEE.DESCRIPTION1 actConsigneeName"
                + ",SHPR.DESCRIPTION1 shipperName,CNEE.DESCRIPTION1 consigneeName,SO.CREATED_BY,STR_TO_DATE(SO.CREATION_DATE,'%Y-%m-%d') AS CREATION_DATE"
                + ",SO.AMENDED_BY,STR_TO_DATE(so.CARGO_RECEIVE_DATE,'%Y-%m-%d') AS cargoReceivedDate,SO.SHIPMENT_FHAN shipmentFHAN "

                + ",STR_TO_DATE(SO.AMENDED_DATE,'%Y-%m-%d') AS AMENDED_DATE, TERMINAL_CODE, TERMINAL_NAME "
                + " FROM (((((((((((SO_HDR_F SO "
                + "LEFT OUTER JOIN (SELECT ACNEE.PARTNER_CODE,ACNEE.DESCRIPTION1 FROM PARTNER_ACCOUNT_D ACNEE WHERE ACNEE.LOADNG_AGNT = ? AND ACNEE.STATUS = 'A') ACNEE ON (SO.ACT_CNEE = ACNEE.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT NOTIFY.PARTNER_CODE,NOTIFY.DESCRIPTION1 FROM PARTNER_ACCOUNT_D NOTIFY WHERE NOTIFY.LOADNG_AGNT = ? AND NOTIFY.STATUS = 'A') NOTIFY ON (SO.SO_NOTIFY = NOTIFY.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT ANOTIFY.PARTNER_CODE,ANOTIFY.DESCRIPTION1 FROM PARTNER_ACCOUNT_D ANOTIFY WHERE ANOTIFY.LOADNG_AGNT = ? AND ANOTIFY.STATUS = 'A') ANOTIFY ON (SO.ALSO_NOTIFY = ANOTIFY.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT ASHPR.PARTNER_CODE,ASHPR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D ASHPR WHERE ASHPR.LOADNG_AGNT = ? AND ASHPR.STATUS = 'A') ASHPR ON (SO.ACT_SHPR = ASHPR.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT CNEE.PARTNER_CODE,CNEE.DESCRIPTION1 FROM PARTNER_ACCOUNT_D CNEE WHERE CNEE.LOADNG_AGNT = ? AND CNEE.STATUS = 'A') CNEE ON (SO.CNEE = CNEE.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT SHPR.PARTNER_CODE,SHPR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D SHPR WHERE SHPR.LOADNG_AGNT = ? AND SHPR.STATUS = 'A') SHPR ON (SO.SHPR = SHPR.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT LAGNT.PARTNER_CODE,LAGNT.DESCRIPTION1 FROM PARTNER_ACCOUNT_D LAGNT WHERE LAGNT.LOADNG_AGNT = ? AND LAGNT.STATUS = 'A') LAGNT ON (SO.LOADING_AGNT = LAGNT.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT DAGNT.PARTNER_CODE,DAGNT.DESCRIPTION1 FROM PARTNER_ACCOUNT_D DAGNT WHERE DAGNT.LOADNG_AGNT = ? AND DAGNT.STATUS = 'A') DAGNT ON (SO.DEST_AGNT = DAGNT.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT OWH.PARTNER_CODE,OWH.DESCRIPTION1 FROM PARTNER_ACCOUNT_D OWH WHERE OWH.LOADNG_AGNT = ? AND OWH.STATUS = 'A') OWH ON (SO.ORIGN_WH = OWH.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT CARR.PARTNER_CODE,CARR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D CARR WHERE CARR.LOADNG_AGNT = ? AND CARR.STATUS = 'A') CARR ON (SO.CARRIER_CODE = CARR.PARTNER_CODE ))"
                + "LEFT OUTER JOIN PORT_D ATPORT ON (SO.DELIVERY_AT = ATPORT.PORT_CODE)) WHERE SO.BKG_REF_NO IN (" + bkgNo + ") ");

        List<SOBean> soBeanList = jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(SOBean.class),
                la, la, la, la, la, la, la, la, la, la);

        return soBeanList;
    }

    public List<SOBean> retrieveBySoNumber(SOBean bean) {
        String la = bean.getLoadingAgent();
        String bkgNo = bean.getSoNumber() + "";
        if (bean.getIds() != null && bean.getIds().length > 0) {
            bkgNo = "'" + Arrays.stream(bean.getIds())
                    .mapToObj(String::valueOf) // convert each int to a string
                    .collect(Collectors.joining("','")) + "'";
        }
        String query = ("SELECT SO.SO_NUMBER soNumber,SO.NO_OF_ORIGINAL,STR_TO_DATE(SO.DELIVERY_DATE,'%Y-%m-%d') AS DELIVERY_DATE"
                + ",SO.DELIVERY_AT,SO.BKG_REF_NO,STR_TO_DATE(SO.BKG_DATE,'%Y-%m-%d') AS bookingRefDate,SO.JOB_NUMBER"
                + ",SO.BKG_TYPE bookingType,SO.JOB_NUMBER,SO.CARGO_TYPE,SO.NO_OF_ORIGINAL,SO.ACT_SHPR actShipper,SO.ACT_CNEE actConsignee"
                + ",SO.SHPR shipper,SO.SHPR_CONTCT_DTLS shipperContactDetails,SO.CNEE consignee,SO.CNEE_CONTCT_DTLS consigneeContactDetails,SO.COMM_INV_NUMBER comInvNumber,"
                + "STR_TO_DATE(SO.COMM_INV_DT,'%Y-%m-%d') AS comInvDate,SO.SO_NOTIFY,NOTIFY.DESCRIPTION1 soNotifyName"
                + ",SO.SO_NOTIFY_CONTCT_DTLS soNotifyContactDetails,SO.ALSO_NOTIFY alsoNotify,ANOTIFY.DESCRIPTION1 alsoNotifyName,SO.ALSO_NOTIFY_CONTCT_DTLS alsoNotifyContactDetails"
                + ",SO.POR,SO.POR_NAME,SO.POR_IND,SO.POL,SO.POL_NAME,ATPORT.DESCRIPTION1 ATPORTDESC"
                + ",SO.POL_IND,SO.FROM_FREIGHTAGE_CODE fromFreightCode,SO.FROM_FREIGHTAGE_NAME fromFreightName,SO.POD,SO.POD_NAME,SO.POD_IND"
                + ",SO.DEST,SO.DEST_NAME,SO.DEST_IND,SO.UPTO_FREIGHTAGE_CODE upToFreightCode,SO.UPTO_FREIGHTAGE_NAME upToFreightName,SO.PRE_CARRAIGE preCarriage"
                + ",SO.PRE_VOYAGE preVoyage,SO.SS_NUMBER,SO.VSL,SO.VOY,SO.CARRIER_CODE"
                + ",CARR.DESCRIPTION1 carrierName,SO.FREIGHT_TYPE,SO.FREIGHT_PAYABLE_AT,STR_TO_DATE(SO.CFS_DATE,'%Y-%m-%d') AS CFS_DATE"
                + ",SO.CFS_TIME,STR_TO_DATE(SO.CY_DATE,'%Y-%m-%d') AS CY_DATE"
                + ",SO.CY_TIME,SO.CARGO_RECEIVE_DATE,STR_TO_DATE(SO.ETD,'%Y-%m-%d') AS ETD,STR_TO_DATE(SO.ETA,'%Y-%m-%d') AS ETA"
                + ",SO.LOADING_AGNT loadingAgentCode,LAGNT.DESCRIPTION1 loadingAgentName,SO.LOADING_AGNT_CONTCT_DTLS loadingAgentContactDetails,SO.DEST_AGNT destinationAgentCode"
                + ",DAGNT.DESCRIPTION1 destinationAgentName,SO.DEST_AGNT_CONTCT_DTLS destinationAgentContactDetails,SO.ORIGN_WH originWareHouse,OWH.DESCRIPTION1 originWareHouseName"
                + ",SO.ORIGN_WH_CONTCT_DTLS originWareHouseContactDetails,SO.SO_REMARK remarks,BL_NO blNumber,STR_TO_DATE(SO.DATE_OF_ISSUE,'%Y-%m-%d') AS DATE_OF_ISSUE"
                + ",SO.SO_STATUS,ACNEE.DESCRIPTION1 ACNEEDESC,ASHPR.DESCRIPTION1 actShipperName,CNEE.DESCRIPTION1 actConsigneeName"
                + ",SHPR.DESCRIPTION1 shipperName,CNEE.DESCRIPTION1 consigneeName,SO.CREATED_BY,STR_TO_DATE(SO.CREATION_DATE,'%Y-%m-%d') AS CREATION_DATE"
                + ",SO.AMENDED_BY,STR_TO_DATE(so.CARGO_RECEIVE_DATE,'%Y-%m-%d') AS cargoReceivedDate,SO.SHIPMENT_FHAN shipmentFHAN "

                + ",STR_TO_DATE(SO.AMENDED_DATE,'%Y-%m-%d') AS AMENDED_DATE, TERMINAL_CODE, TERMINAL_NAME "
                + " FROM (((((((((((SO_HDR_F SO "
                + "LEFT OUTER JOIN (SELECT ACNEE.PARTNER_CODE,ACNEE.DESCRIPTION1 FROM PARTNER_ACCOUNT_D ACNEE WHERE ACNEE.LOADNG_AGNT = ? AND ACNEE.STATUS = 'A') ACNEE ON (SO.ACT_CNEE = ACNEE.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT NOTIFY.PARTNER_CODE,NOTIFY.DESCRIPTION1 FROM PARTNER_ACCOUNT_D NOTIFY WHERE NOTIFY.LOADNG_AGNT = ? AND NOTIFY.STATUS = 'A') NOTIFY ON (SO.SO_NOTIFY = NOTIFY.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT ANOTIFY.PARTNER_CODE,ANOTIFY.DESCRIPTION1 FROM PARTNER_ACCOUNT_D ANOTIFY WHERE ANOTIFY.LOADNG_AGNT = ? AND ANOTIFY.STATUS = 'A') ANOTIFY ON (SO.ALSO_NOTIFY = ANOTIFY.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT ASHPR.PARTNER_CODE,ASHPR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D ASHPR WHERE ASHPR.LOADNG_AGNT = ? AND ASHPR.STATUS = 'A') ASHPR ON (SO.ACT_SHPR = ASHPR.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT CNEE.PARTNER_CODE,CNEE.DESCRIPTION1 FROM PARTNER_ACCOUNT_D CNEE WHERE CNEE.LOADNG_AGNT = ? AND CNEE.STATUS = 'A') CNEE ON (SO.CNEE = CNEE.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT SHPR.PARTNER_CODE,SHPR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D SHPR WHERE SHPR.LOADNG_AGNT = ? AND SHPR.STATUS = 'A') SHPR ON (SO.SHPR = SHPR.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT LAGNT.PARTNER_CODE,LAGNT.DESCRIPTION1 FROM PARTNER_ACCOUNT_D LAGNT WHERE LAGNT.LOADNG_AGNT = ? AND LAGNT.STATUS = 'A') LAGNT ON (SO.LOADING_AGNT = LAGNT.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT DAGNT.PARTNER_CODE,DAGNT.DESCRIPTION1 FROM PARTNER_ACCOUNT_D DAGNT WHERE DAGNT.LOADNG_AGNT = ? AND DAGNT.STATUS = 'A') DAGNT ON (SO.DEST_AGNT = DAGNT.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT OWH.PARTNER_CODE,OWH.DESCRIPTION1 FROM PARTNER_ACCOUNT_D OWH WHERE OWH.LOADNG_AGNT = ? AND OWH.STATUS = 'A') OWH ON (SO.ORIGN_WH = OWH.PARTNER_CODE ))"
                + "LEFT OUTER JOIN (SELECT CARR.PARTNER_CODE,CARR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D CARR WHERE CARR.LOADNG_AGNT = ? AND CARR.STATUS = 'A') CARR ON (SO.CARRIER_CODE = CARR.PARTNER_CODE ))"
                + "LEFT OUTER JOIN PORT_D ATPORT ON (SO.DELIVERY_AT = ATPORT.PORT_CODE)) WHERE SO.SO_NUMBER IN (" + bkgNo + ") ");

        List<SOBean> soBeanList = jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(SOBean.class),
                la, la, la, la, la, la, la, la, la, la);

        return soBeanList;
    }

    public List<ContainerBean> getContainerDetails(SOBean bean, int lineNo, int type) {
        String clpnoso = "";
        if(type == 6){
            type = 5;
            clpnoso = " OR c.load_plan_no IS NULL";
        }
        String bkgNo = bean.getBkgRefNo() + "";
        if (bean.getIds() != null && bean.getIds().length > 0) {
            bkgNo = "'" + Arrays.stream(bean.getIds())
                    .mapToObj(String::valueOf) // convert each int to a string
                    .collect(Collectors.joining("','")) + "'";
        }
        String param = null;
        String query = ("select distinct f.SO_NUMBER,f.BKG_REF_NO bookingRefNumber,f.BKG_TYPE,f.CONTNR_NO number"
                + ",f.SIZE_CODE size,s.DESCRIPTION,f.BKG_QTY qty,c.LOADING_AGNT LOADINGAGNT,c.shpr shipper, c.cnee consignee"
                + ",f.BKG_UNIT unit,f.BKG_KGS_GROSS_WT weight,f.BKG_NET_WT netWeight,f.BKG_CBM measurement"
                + ",f.ACT_QTY actualQty,f.ACT_UNIT actualUnit,f.ACT_GROSS_WT actualWeight,f.ACT_NET_KGS actualNetWeight,f.ACT_CBM actualMeasurement," +
                "f.SHIPPING_BILL_NO shippingBillNumber,f.LINE_NO lineNumber,c.dest"
                + ",STR_TO_DATE(f.SHIPPING_BILL_DATE,'%Y-%m-%d') AS SHIPPING_BILL_DATE,f.REMARKS containerRemarks,f.STATUS,f.MARK_NO markNumber,f.MARK_DETAILS," +
                " f.CUSTM_SEAL_NO customSealNumber,f.LINE_SEAL_NO lineSealNumber,c.load_plan_no uuid, bl.service_term_code serviceTerm,CONVERT(f.ACT_STUFFING_DATE,DATE) actualStuffingDate"
                + ",f.CREATED_BY,STR_TO_DATE(f.CREATION_DATE,'%Y-%m-%d') AS CREATION_DATE,f.AMENDED_BY,STR_TO_DATE(f.AMENDED_DATE,'%Y-%m-%d') AS AMENDED_DATE "
                + " from so_fcl_lcl_f f " +
                "  INNER JOIN so_hdr_f c on (f.BKG_REF_NO =c.BKG_REF_NO) " +
                "LEFT OUTER JOIN bl_fcl_lcl_f bl ON (c.bl_no = bl.bl_no) "
                + " LEFT OUTER JOIN size_d s ON (s.SIZE_CODE=f.SIZE_CODE) ");

        if (type == 0) {
            query = query + " WHERE f.BKG_REF_NO IN (" + bkgNo + ") ORDER BY f.line_no";
        }
        else if (type == 1) {
            query = query + " WHERE f.BKG_REF_NO IN (" + bkgNo + ") AND f.line_no = "+lineNo;
        }
        else if (type == 5) {
            bean.setLength(50);
            query = query + " WHERE c.pod = '"+ bean.getPod()+"' AND c.pol = '" + bean.getPol() + "' AND f.bkg_type = 'L' " +
                    "AND c.ORIGN_WH = '"+bean.getOriginWareHouse()+"' AND "+
                    "(c.load_plan_no = '"+bean.getLoadPlanNo()+"' "+clpnoso+" ) ";

            if(StringUtils.hasText(bean.getCb().getContainerNo())){
                query += "AND f.contnr_no = '"+bean.getCb().getContainerNo()+"' ";
            }

            query += " ORDER BY c.load_plan_no DESC ";
        }
        else {
            query = query + " WHERE c.JOB_NUMBER='" + bean.getJobNumber() + "' ORDER BY f.line_no";
        }


        return jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(ContainerBean.class));
    }


    public void saveSO(SOBean bean) {
        String query = ("INSERT INTO so_hdr_f (BKG_REF_NO,SO_NUMBER,COMM_INV_NUMBER,DELIVERY_DATE,DELIVERY_AT,BKG_DATE,BKG_TYPE," +
                "CARGO_TYPE,NO_OF_ORIGINAL,ACT_SHPR,ACT_CNEE,SHPR,SHPR_CONTCT_DTLS,CNEE,CNEE_CONTCT_DTLS,COMM_INV_DT,SO_NOTIFY," +
                "SO_NOTIFY_CONTCT_DTLS,ALSO_NOTIFY,ALSO_NOTIFY_CONTCT_DTLS,POR,POR_NAME,POR_IND,POL,POL_NAME,FROM_FREIGHTAGE_CODE,FROM_FREIGHTAGE_NAME," +
                "POD,POD_NAME,POD_IND,DEST,DEST_NAME,UPTO_FREIGHTAGE_CODE,UPTO_FREIGHTAGE_NAME,PRE_CARRAIGE,PRE_VOYAGE,VSL,VOY,CARRIER_CODE,FREIGHT_TYPE," + //40
                "FREIGHT_PAYABLE_AT,CFS_DATE,CFS_TIME,CY_DATE,CY_TIME,CARGO_RECEIVE_DATE,ETD,ETA,LOADING_AGNT,LOADING_AGNT_CONTCT_DTLS,DEST_AGNT,DEST_AGNT_CONTCT_DTLS," + //52
                "ORIGN_WH,ORIGN_WH_CONTCT_DTLS,SO_REMARK,CREATED_BY,CREATION_DATE,SO_STATUS, SHIPMENT_FHAN) " + //59
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE(),?, ?)");

        jdbcTemplate.update(query,
                bean.getBkgRefNo(), bean.getSoNumber(), bean.getComInvNumber(), bean.getDeliveryDate(), bean.getDeliveryAt(), bean.getBookingRefDate(), bean.getBookingType(),
                bean.getCargoType(), bean.getNoOfOriginal(), bean.getActShipper(), bean.getActConsignee(), bean.getShipper(), bean.getShipperContactDetails(), bean.getConsignee(),
                bean.getConsigneeContactDetails(), org.apache.commons.lang3.StringUtils.defaultIfEmpty(bean.getComInvDate(), null), bean.getSoNotify(),
                bean.getSoNotifyContactDetails(), bean.getAlsoNotify(), bean.getAlsoNotifyContactDetails(), bean.getPor(), bean.getPorName(), bean.getPorInd(), bean.getPol(), bean.getPolName(), bean.getFromFreightCode(), bean.getFromFreightName(),
                bean.getPod(), bean.getPodName(), bean.getPodInd(), bean.getDest(), bean.getDestName(), bean.getUpToFreightCode(), bean.getUpToFreightName(), bean.getPreCarriage(), bean.getPreVoyage(), bean.getVsl(), bean.getVoy(), bean.getCarrierCode(), bean.getFreightType(),
                bean.getFreightPayableAt(), org.apache.commons.lang3.StringUtils.defaultIfEmpty(bean.getCfsDate(), null), bean.getCfsTime(), org.apache.commons.lang3.StringUtils.defaultIfEmpty(bean.getCyDate(), null),
                bean.getCyTime(), org.apache.commons.lang3.StringUtils.defaultIfEmpty(bean.getCargoReceivedDate(), null), org.apache.commons.lang3.StringUtils.defaultIfEmpty(bean.getEtd(), null),
                org.apache.commons.lang3.StringUtils.defaultIfEmpty(bean.getEta(), null), bean.getLoadingAgentCode(), bean.getLoadingAgentContactDetails(), bean.getDestinationAgentCode(), bean.getDestinationAgentContactDetails(),
                bean.getOriginWareHouse(), bean.getOriginWareHouseContactDetails(), bean.getRemarks(), bean.getUserId(), bean.getStatus(), bean.getShipmentFHAN());
    }

    public void updateSO(SOBean bean) {
        String query = ("UPDATE so_hdr_f SET DELIVERY_DATE=?,DELIVERY_AT=?,SO_NUMBER=?,BKG_DATE=?,BKG_TYPE=?,CARGO_TYPE=?,NO_OF_ORIGINAL=?,ACT_SHPR=?,ACT_CNEE=?,SHPR=?,SHPR_CONTCT_DTLS=?," +
                "CNEE=?,CNEE_CONTCT_DTLS=?,COMM_INV_NUMBER=?,COMM_INV_DT=?,SO_NOTIFY=?,SO_NOTIFY_CONTCT_DTLS=?,ALSO_NOTIFY=?,ALSO_NOTIFY_CONTCT_DTLS=?,POR=?,POR_IND=?,POL=?,POL_IND=?," +
                "FROM_FREIGHTAGE_CODE=?,FROM_FREIGHTAGE_NAME=?,POD=?,POD_IND=?,DEST=?,DEST_IND=?,UPTO_FREIGHTAGE_CODE=?,UPTO_FREIGHTAGE_NAME=?,PRE_CARRAIGE=?,PRE_VOYAGE=?,VSL=?,VOY=?," +
                "CARRIER_CODE=?,FREIGHT_TYPE=?,FREIGHT_PAYABLE_AT=?,CFS_DATE=?,CFS_TIME=?,CY_DATE=?,CY_TIME=?,CARGO_RECEIVE_DATE=?,ETD=?,ETA=?,LOADING_AGNT=?,LOADING_AGNT_CONTCT_DTLS=?," +
                "DEST_AGNT=?,DEST_AGNT_CONTCT_DTLS=?,ORIGN_WH=?,ORIGN_WH_CONTCT_DTLS=?,SO_REMARK=?,SO_STATUS=?,POR_NAME=?,POL_NAME=?,POD_NAME=?,DEST_NAME=?,AMENDED_BY=?,AMENDED_DATE=SYSDATE(), " +
                "TERMINAL_CODE = ?, TERMINAL_NAME=?, GATEWAY_CUTOFF_DATE = ?, SHIPMENT_FHAN=? WHERE BKG_REF_NO = ? ");

        jdbcTemplate.update(query,
                bean.getDeliveryDate(), bean.getDeliveryAt(), bean.getSoNumber(), bean.getBookingRefDate(), bean.getBookingType(), bean.getCargoType(), bean.getNoOfOriginal(), bean.getActShipper(), bean.getActConsignee(), bean.getShipper(), bean.getShipperContactDetails(),
                bean.getConsignee(), bean.getConsigneeContactDetails(), bean.getComInvNumber(), org.apache.commons.lang3.StringUtils.defaultIfEmpty(bean.getComInvDate(), null), bean.getSoNotify(), bean.getSoNotifyContactDetails(), bean.getAlsoNotify(), bean.getAlsoNotifyContactDetails(), bean.getPor(), bean.getPorInd(), bean.getPol(), bean.getPolInd(),
                bean.getFromFreightCode(), bean.getFromFreightName(), bean.getPod(), bean.getPodInd(), bean.getDest(), bean.getDestInd(), bean.getUpToFreightCode(), bean.getUpToFreightName(), bean.getPreCarriage(), bean.getPreVoyage(), bean.getVsl(), bean.getVoy(),
                bean.getCarrierCode(), bean.getFreightType(), bean.getFreightPayableAt(), org.apache.commons.lang3.StringUtils.defaultIfEmpty(bean.getCfsDate(), null), bean.getCfsTime(),
                org.apache.commons.lang3.StringUtils.defaultIfEmpty(bean.getCyDate(), null), bean.getCyTime(), org.apache.commons.lang3.StringUtils.defaultIfEmpty(bean.getCargoReceivedDate(), null), org.apache.commons.lang3.StringUtils.defaultIfEmpty(bean.getEtd(), null),
                org.apache.commons.lang3.StringUtils.defaultIfEmpty(bean.getEta(), null), bean.getLoadingAgentCode(), bean.getLoadingAgentContactDetails(),
                bean.getDestinationAgentCode(), bean.getDestinationAgentContactDetails(), bean.getOriginWareHouse(), bean.getOriginWareHouseContactDetails(), bean.getRemarks(), bean.getStatus(), bean.getPorName(), bean.getPolName(), bean.getPodName(), bean.getDestName(), bean.getUserId(),
                bean.getTerminalCode(), bean.getTerminalName(), bean.getGatewayCutOffDate(), bean.getShipmentFHAN(), bean.getBkgRefNo());
    }

    public void soPDF(SOBean soBean) {
        SOPDF.writeUCMpdf(soBean.getBkgRefNo(), soBean, jdbcTemplate);
    }

    public void saveLclFcl(ContainerBean cb, String lineNo) {
        updateCB(cb);
        String query = "INSERT INTO so_fcl_lcl_f ( SO_NUMBER,BKG_REF_NO,BKG_TYPE,CONTNR_NO,SIZE_CODE,CUSTM_SEAL_NO,"//
                + "LINE_SEAL_NO,BKG_QTY,BKG_UNIT,BKG_KGS_GROSS_WT,BKG_NET_WT,BKG_CBM,ACT_QTY,ACT_UNIT,"//
                + "ACT_GROSS_WT,ACT_NET_KGS,ACT_CBM,ACT_STUFFING_DATE,SHIPPING_BILL_NO,SHIPPING_BILL_DATE"//
                + ",REMARKS,STATUS,CREATED_BY,CREATION_DATE,AMENDED_BY,AMENDED_DATE,MARK_NO,MARK_DETAILS,LINE_NO) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        jdbcTemplate.update(query,
                cb.getSoNumber(), cb.getBookingRefNumber(), cb.getBookingType(), cb.getNumber().toUpperCase(),
                cb.getSize(), cb.getCustomSealNumber(), cb.getLineSealNumber(), cb.getQty(), cb.getUnit(), cb.getWeight(), cb.getNetWeight(),
                cb.getMeasurement(), defaultNull(cb.getActualQty()), defaultNull(cb.getActualUnit()), defaultNull(cb.getActualWeight()), defaultNull(cb.getActualNetWeight()),
                defaultNull(cb.getActualMeasurement()), null, cb.getShippingBillNumber(), defaultNull(cb.getShippingBillDate()), cb.getContainerRemarks(), "A", cb.getUserId(),
                DateUtil.getSystemDate(), cb.getUserId(), DateUtil.getSystemDate(), cb.getMarkNumber(), cb.getMarkDetails(), lineNo);
    }

    private void updateCB(ContainerBean cb){
        if("F".equalsIgnoreCase(cb.getBookingType())){
            cb.setActualQty(cb.getQty());
            cb.setActualUnit(cb.getUnit());
            cb.setActualMeasurement(cb.getMeasurement());
            cb.setActualWeight(cb.getWeight());
            cb.setActualNetWeight(cb.getNetWeight());
        }
    }

    public void updateLclFcl(ContainerBean cb) {
        updateCB(cb);
        String query = "UPDATE so_fcl_lcl_f SET SO_NUMBER=?,CONTNR_NO=?,SIZE_CODE=?,CUSTM_SEAL_NO=?,"//
                + "LINE_SEAL_NO=?,BKG_QTY=?,BKG_UNIT=?,BKG_KGS_GROSS_WT=?,BKG_NET_WT=?,BKG_CBM=?,ACT_QTY=?,ACT_UNIT=?,"//
                + "ACT_GROSS_WT=?,ACT_NET_KGS=?,ACT_CBM=?,ACT_STUFFING_DATE=?,SHIPPING_BILL_NO=?,SHIPPING_BILL_DATE"//
                + "=?,REMARKS=?,AMENDED_BY=?,AMENDED_DATE=?,MARK_NO=?,MARK_DETAILS=? " +
                "WHERE BKG_REF_NO = ? AND LINE_NO=?";

        jdbcTemplate.update(query,
                cb.getSoNumber(), cb.getNumber().toUpperCase(),
                cb.getSize(), cb.getCustomSealNumber(), cb.getLineSealNumber(), cb.getQty(), cb.getUnit(), cb.getWeight(), cb.getNetWeight(),
                cb.getMeasurement(), defaultNull(cb.getActualQty()), defaultNull(cb.getActualUnit()), defaultNull(cb.getActualWeight()), defaultNull(cb.getActualNetWeight()),
                defaultNull(cb.getActualMeasurement()), defaultNull(cb.getActualStuffingDate()), cb.getShippingBillNumber(), defaultNull(cb.getShippingBillDate()), cb.getContainerRemarks(),
                cb.getUserId(), DateUtil.getSystemDate(), cb.getMarkNumber(), cb.getMarkDetails(), cb.getBookingRefNumber(), cb.getLineNumber());
    }

    public void uploadFile(FileUploadBean fileUploadBean) {

        String query = "INSERT INTO so_file (BKG_REF_NO,SO_NUMBER,LOADING_AGNT,POD,FILE_NAME,FILE_,DESCRIPTION,CREATED_BY,CREATION_DATE) "
                + "VALUES (?,?,?,?,?,?,?,?,SYSDATE())";

        for (FileUploadRowBean row : fileUploadBean.getRowList()) {
            if (StringUtils.hasText(row.getDocument().getOriginalFilename())) {
                jdbcTemplate.update(query, fileUploadBean.getParam1(), fileUploadBean.getParam2(), fileUploadBean.getLoadingAgent(),
                        fileUploadBean.getParam3(), row.getDocument().getOriginalFilename(), null, row.getDescription(), fileUploadBean.getUserId());
            }
        }
    }

    public void retrieveFileUpload(FileUploadBean fileUploadBean) {
        String query = "SELECT BKG_REF_NO,SO_NUMBER,LOADING_AGNT,POD,FILE_NAME,FILE_,DESCRIPTION FROM so_file "
                + "WHERE bkg_ref_no = ? ";

        List<FileUploadRowBean> rowList = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(FileUploadRowBean.class), fileUploadBean.getParam1());
        fileUploadBean.setRowList(rowList);
    }

    public List<ReportBean> retrieve(String soNo, String loadingAgent, String yrStartDate, String seaAir, String expImp) {
        String soAgent = "LOADING_AGNT";
        if ("IMPORT".equalsIgnoreCase(expImp)) {
            //agent = "DEST_AGNT";
            soAgent = "DEST_AGNT";
        }
        String query = "SELECT so_number param1,bl_no param2 FROM so_hdr_f WHERE "+soAgent+"=? AND so_number LIKE ? AND BKG_DATE > DATE_SUB(?, INTERVAL 400 DAY) limit 10";

        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ReportBean.class), loadingAgent, soNo, yrStartDate);
    }

    public void updateSO(BLBean bean) {
        String bkgNo = null;
        if (bean.getIds() != null && bean.getIds().length > 0) {
            bkgNo = "'" + Arrays.stream(bean.getIds())
                    .mapToObj(String::valueOf) // convert each int to a string
                    .collect(Collectors.joining("','")) + "'";
        }
        String query = ("UPDATE so_hdr_f SET BL_NO=?,DATE_OF_ISSUE=?,VSL=?,VOY=?,CARRIER_CODE=?,ETD=?,ETA=? WHERE BKG_REF_NO IN (" + bkgNo + ") ");
        jdbcTemplate.update(query, bean.getBlNumber(),

                defaultNull(bean.getBlIssueDate()), bean.getVsl(), bean.getVoy(), bean.getCarrierCode(), defaultNull(bean.getEtd()),
                defaultNull(bean.getEta()));
    }

    public void updateSO(ContainerBean bean) {
        String soNumber = "";
        String soNumberRest = "";
        int i=1;
        for(ContainerBean cb : bean.getContainerBeanList()){
            if(StringUtils.hasText(cb.getUuid())) {
                soNumber += cb.getSoNumber() + ",";
                String soUpdateQuery = "UPDATE SO_HDR_F SET LOAD_PLAN_NO =?, LOAD_PLAN_SEQ_NO =? WHERE SO_NUMBER IN ("+cb.getSoNumber()+") ";
                jdbcTemplate.update(soUpdateQuery, bean.getNumber(), i++);
            }
            else{
                soNumberRest += cb.getSoNumber()+",";
            }
        }
        soNumber+="-1";
        soNumberRest += "-1";

        String soRestUpdateQuery = "UPDATE SO_HDR_F SET LOAD_PLAN_NO =NULL, LOAD_PLAN_SEQ_NO =999 WHERE SO_NUMBER IN ("+soNumberRest+") ";
        String fclUpdateQuery = ("UPDATE SO_FCl_LCL_F SET CONTNR_NO=?,LINE_SEAL_NO=?,SIZE_CODE=?,CUSTM_SEAL_NO=?,ACT_STUFFING_DATE=? WHERE SO_NUMBER IN  (" + soNumber + ") ");

        jdbcTemplate.update(soRestUpdateQuery);
        jdbcTemplate.update(fclUpdateQuery,bean.getContainerNo(), bean.getLineSealNumber(), bean.getSize(), bean.getCustomSealNumber(), defaultNull(bean.getActualLoadingDate()));
    }

    public void updateSO(JobBean bean) {
        String soNumber = "";
        String soNumberRest = "";

        for(SOBean cb : bean.getSoBeanList()){
            if(StringUtils.hasText(cb.getUuid())) {
                soNumber += cb.getSoNumber() + ",";
            }
            else{
                soNumberRest += cb.getSoNumber()+",";
            }
        }
        soNumber+="-1";
        soNumberRest += "-1";

        String soRestUpdateQuery = "UPDATE SO_HDR_F SET JOB_NUMBER =NULL WHERE SO_NUMBER IN ("+soNumberRest+") ";
        String soUpdateQuery = "UPDATE SO_HDR_F SET JOB_NUMBER =? WHERE SO_NUMBER IN ("+soNumber+") ";
        jdbcTemplate.update(soRestUpdateQuery);
        jdbcTemplate.update(soUpdateQuery, bean.getJobNumber());
    }

    public void updateSOWithJob(JobBean bean) {
        String soUpdateQuery = "UPDATE SO_HDR_F SET VSL=?,VOY=?,CARRIER_CODE=?,ETD=?,ETA=? WHERE JOB_NUMBER = ? ";
        jdbcTemplate.update(soUpdateQuery,bean.getVsl(), bean.getVoy(),bean.getCarrierCode(), bean.getEtd(), bean.getEta(), bean.getJobNumber());
    }

    public void deleteFclLcl(String bkgRefNo, String lineNo) {
        String query = "delete from so_fcl_lcl_f where BKG_REF_NO = ? and LINE_NO = ? ";
        jdbcTemplate.update(query, bkgRefNo, lineNo);
    }

    public void deleteSO(SOBean bean) {
        String soDeleteQuery = "delete from so_hdr_f where BKG_REF_NO = ? ";
        String soFCLDeleteQuery = "delete from so_fcl_lcl_f where BKG_REF_NO = ? ";
        for (String record : bean.getUuids()) {
            jdbcTemplate.update(soDeleteQuery, record);
            jdbcTemplate.update(soFCLDeleteQuery, record);
        }
    }
}
