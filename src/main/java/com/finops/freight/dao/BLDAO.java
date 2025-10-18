package com.finops.freight.dao;

import com.finops.aop.MeasureTime;
import com.finops.dao.AbstractDAO;
import com.finops.finance.bean.InvoiceBean;
import com.finops.freight.bean.BLBean;
import com.finops.freight.bean.ContainerBean;
import com.finops.freight.bean.JobBean;
import com.finops.freight.bean.SOBean;
import com.finops.partner.model.PartnerBean;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BLDAO extends AbstractDAO {

    /**
     * BL EXPORT and BL IMPORT use the same BL_HDR_F table.
     *
     * @param bean
     * @return
     */
    @MeasureTime
    public List<BLBean> findBLByPage(BLBean bean, Map<String, PartnerBean> partnerBeanMap) {
        String agent = "LOADNG_AGNT";
        String blAgent = "LOADING_AGNT";
        if ("IMPORT".equalsIgnoreCase(bean.getExpImp())) {
            agent = "DEST_AGNT";
            blAgent = agent;
        }

        QueryBuilder findBLByPageQuery = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" BLH.BL_NO blNumber,BLH.BL_AUTO_SEQ_NO ,BLH.JOB_NUMBER ,BLH.SHPR shipper," +
                        "SHPR.DESCRIPTION1 shipperName ,BLH.CNEE consignee,CNEE.DESCRIPTION1 consigneeName ," +
                        "BLH.LOADING_AGNT ,BLH.DEST_AGNT  ,BLH.POL ,POL.DESCRIPTION1 POLDESCRIPTION ,BLH.POD ,POD.DESCRIPTION1 PODDESCRIPTION ," +
                        "BLH.VSL ,BLH.VOY ,CONVERT(BLH.ETD,DATE) ETD ,CONVERT(BLH.ETA,DATE) ETA ,BLH.CARGO_RECEIVE_DATE  ,BLH.CREATED_BY  ," +
                        "BLH.CREATION_DATE  ,BLH.AMENDED_BY  ,BLH.AMENDED_DATE  ," +
                        "ASHPR.DESCRIPTION1 actShipperName,ACNEE.DESCRIPTION1 actConsigneeName ")
                .FROM("bl_hdr_f", "BLH")
                .INNER_JOIN("so_hdr_f", "soh", new Condition("BLH.BKG_REF_NO","SOH.BKG_REF_NO", Query.EQUALS))
                .INNER_JOIN("port_d", "pol", new Condition("POL.PORT_CODE", "BLH.POL", Query.EQUALS))
                .INNER_JOIN("port_d", "pod", new Condition("POD.PORT_CODE", "BLH.POD", Query.EQUALS))
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
                .WHERE("BLH." + blAgent, "?");

        //String[] fields = {"blh.BL_NO", "blh.job_number", "", "", "blh.vsl", "blh.voy", "blh.pol", "blh.pod",""};

        //addFilter(findBLByPageQuery, bean, fields);

        addFilter(findBLByPageQuery, "BLH.BL_NO", bean.getBlNumber() + "", true);
        addFilter(findBLByPageQuery, "soh.so_number", bean.getSoNumber() + "", false);
        addFilter(findBLByPageQuery, "BLH.JOB_NUMBER", bean.getJobNumber() + "", true);
        addFilter(findBLByPageQuery, "BLH.VSL", bean.getVsl() + "", true);
        addFilter(findBLByPageQuery, "BLH.VOY", bean.getVoy() + "", true);
        addFilter(findBLByPageQuery, "BLH.POL", bean.getPol() + "", true);
        addFilter(findBLByPageQuery, "BLH.POD", bean.getPod() + "", true);
        addFilter(findBLByPageQuery, "SHPR.DESCRIPTION1", bean.getShipperName() + "", true);
        addFilter(findBLByPageQuery, "CNEE.DESCRIPTION1", bean.getConsigneeName() + "", true);
        addFilter(findBLByPageQuery, "ASHPR.DESCRIPTION1", bean.getActShipperName()+"", true);
        addFilter(findBLByPageQuery, "ACNEE.DESCRIPTION1", bean.getActConsigneeName()+"", true);
        addFilter(findBLByPageQuery, "LA.DESCRIPTION1", bean.getLoadingAgentName()+"", true);
        addFilter(findBLByPageQuery, "DA.DESCRIPTION1", bean.getDestinationAgentName()+"", true);
        addFilter(findBLByPageQuery, "BLH.M_BL_NUMBER", bean.getMBlNumber()+"", true);
        if(org.springframework.util.StringUtils.hasText(bean.getCb().getShippingBillNumber())){
            findBLByPageQuery.FREEWHERECONDITION("AND EXISTS (SELECT 'X' FROM bl_fcl_lcl_f WHERE bl_no = blh.bl_no " +
                    "AND shipping_bill_no = '"+bean.getCb().getShippingBillNumber()+"' ) ");
        }
        if(org.springframework.util.StringUtils.hasText(bean.getCb().getNumber())){
            findBLByPageQuery.FREEWHERECONDITION("AND EXISTS (SELECT 'X' FROM bl_fcl_lcl_f WHERE bl_no = blh.bl_no " +
                    "AND contnr_no = '"+bean.getCb().getNumber()+"' ) ");
        }

        findBLByPageQuery.ORDER_BY(" BL_AUTO_SEQ_NO DESC ")
                .LIMIT(bean.getStart(), bean.getLength());

        return jdbcTemplate.query(findBLByPageQuery.build().toString(),
                new BeanPropertyRowMapper<>(BLBean.class), bean.getLoadingAgent(),
                bean.getLoadingAgent(), bean.getLoadingAgent(), bean.getLoadingAgent(), bean.getLoadingAgent(),
                bean.getLoadingAgent(), bean.getLoadingAgent());
    }

    public BLBean generateBLNo(BLBean blBean) {
        String query = "SELECT CONCAT('" + blBean.getLoadingAgent() + "',(SUBSTRING(STR_TO_DATE(SYSDATE(),'%Y-%m-%d'),3,2)),(SUBSTRING(STR_TO_DATE(SYSDATE(),'%Y-%m-%d'),6,2)) ,(MAX(BL_AUTO_SEQ_NO) +1 )) blNumber ,\n" +
                "(MAX(BL_AUTO_SEQ_NO) +1) blAutoSequence from bl_hdr_f ";

        return jdbcTemplate.queryForObject(query,
                new BeanPropertyRowMapper<>(BLBean.class));

    }

    @MeasureTime
    public void save(BLBean bean) {
        String query = "INSERT INTO bl_hdr_f(BL_NO,BL_AUTO_SEQ_NO,M_BL_NUMBER,BL_ISSUE_DATE,BKG_REF_NO,BKG_TYPE," +
                "JOB_NUMBER,CARGO_TYPE,NO_OF_ORIGINAL,ACT_SHPR,ACT_CNEE,SHPR,SHPR_CONTCT_DTLS,CNEE,CNEE_CONTCT_DTLS," +
                "BL_NOTIFY,BL_NOTIFY_CONTCT_DTLS,ALSO_NOTIFY,ALSO_NOTIFY_CONTCT_DTLS,POR,POR_NAME,POR_IND,POL,POL_NAME," +
                "POL_IND,FROM_FREIGHTAGE_CODE,FROM_FREIGHTAGE_NAME,POD,POD_NAME,POD_IND,DEST,DEST_NAME,DEST_IND,UPTO_FREIGHTAGE_CODE," +
                "UPTO_FREIGHTAGE_NAME,PRE_CARRAIGE,PRE_VOYAGE,VSL,VOY,CARRIER_CODE,FREIGHT_TYPE,FREIGHT_PAYABLE_AT,CARGO_RECEIVE_DATE," +
                "ETD,ETA,POI,POI_NAME,LOADING_AGNT,LOADING_AGNT_CONTCT_DTLS,DEST_AGNT,DEST_AGNT_CONTCT_DTLS,BL_REMARK,QTY_IN_WORDS," +
                "CREATED_BY,CREATION_DATE,BL_STATUS,BL_RELASE_DATE,SIGNATURE_FILE,igm_no,igm_date,item_no,to_the_manager,free_days," +
                "icd_factory,surveyors,empty_return_location,do_number,mbl_date,destcrd_date,terminal_code,terminal_name " +
                ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                "?,?,?,?,sysdate(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        jdbcTemplate.update(query,
                bean.getBlNumber(), bean.getBlAutoSequence(), bean.getMBlNumber(), StringUtils.defaultIfEmpty(bean.getBlIssueDate(), null), bean.getBkgRefNo(), bean.getBookingType(),
                bean.getJobNumber(), bean.getCargoType(), bean.getNoOfOriginal(), bean.getActShipper(), bean.getActConsignee(), bean.getShipper(),
                bean.getShipperContactDetails(), bean.getConsignee(), bean.getConsigneeContactDetails(),
                bean.getSoNotify(), bean.getSoNotifyContactDetails(), bean.getAlsoNotify(), bean.getAlsoNotifyContactDetails(), bean.getPor(),
                bean.getPorName(), bean.getPorInd(), bean.getPol(), bean.getPolName(),
                bean.getPolInd(), bean.getFromFreightCode(), bean.getFromFreightName(), bean.getPod(), bean.getPodName(), bean.getPodInd(),
                bean.getDest(), bean.getDestName(), bean.getDestInd(), bean.getUpToFreightCode(),
                bean.getUpToFreightName(), bean.getPreCarriage(), bean.getPreVoyage(), bean.getVsl(), bean.getVoy(), bean.getCarrierCode(),
                bean.getFreightType(), bean.getFreightPayableAt(), StringUtils.defaultIfEmpty(bean.getCargoReceivedDate(), null),
                StringUtils.defaultIfEmpty(bean.getEtd(), null), StringUtils.defaultIfEmpty(bean.getEta(), null), bean.getPoi(),
                bean.getPoiName(), bean.getLoadingAgentCode(), bean.getLoadingAgentContactDetails(), bean.getDestinationAgentCode(),
                bean.getDestinationAgentContactDetails(), bean.getRemarks(), bean.getTotalInWords(),
                bean.getUserId(), "A", bean.getBlReleaseDate(), bean.getSignature(), bean.getIgmNo(), defaultNull(bean.getIgmDate()), bean.getItemNumber(),
                bean.getTheManager(), bean.getFreeDays(), bean.getIcdFactory(), bean.getSurveyors(), bean.getEmptyReturnLocation(),
                bean.getDoNumber(), StringUtils.defaultIfEmpty(bean.getMblDate(), null), StringUtils.defaultIfEmpty(bean.getDescribedDate(), null)
                , bean.getTerminalCode(), bean.getTerminalName());
    }

    public BLBean retrieve(BLBean bean) {
        String query = "SELECT  BL.BL_NO blNumber,bl.M_BL_NUMBER mblNumber,BL.NO_OF_ORIGINAL noOfOriginal,BL.BKG_REF_NO bkgRefNo,STR_TO_DATE(BL.BL_ISSUE_DATE,'%Y-%m-%d') BL_ISSUE_DATE"
                + ",BL.JOB_NUMBER,BL.CARGO_TYPE,BL.NO_OF_ORIGINAL,BL.ACT_SHPR actShipper,BL.ACT_CNEE actConsignee, STR_TO_DATE(BL.BL_RELASE_DATE,'%Y-%m-%d') BL_RELEASE_DATE" +
                ",BL.BKG_TYPE bookingType,BL.SHPR shipper,BL.SHPR_CONTCT_DTLS shipperContactDetails,BL.CNEE consignee,BL.CNEE_CONTCT_DTLS consigneeContactDetails," +
                "BL.BL_NOTIFY soNotify,NOTIFY.DESCRIPTION1 soNotifyName," +
                "BL.BL_NOTIFY_CONTCT_DTLS soNotifyContactDetails,BL.ALSO_NOTIFY ,ANOTIFY.DESCRIPTION1 alsoNotifyName,BL.ALSO_NOTIFY_CONTCT_DTLS alsoNotifyContactDetails,BL.POR,BL.POR_NAME"
                + ",BL.POL,BL.POL_NAME,BL.FROM_FREIGHTAGE_CODE fromFreightCode,BL.FROM_FREIGHTAGE_NAME fromFreightName,BL.POD,BL.POD_NAME"
                + ",BL.DEST,BL.DEST_NAME,BL.UPTO_FREIGHTAGE_CODE upToFreightCode,BL.UPTO_FREIGHTAGE_NAME upToFreightName"
                + ",BL.PRE_CARRAIGE preCarriage,BL.PRE_VOYAGE preVoyage,BL.VSL,BL.VOY,BL.CARRIER_CODE,CARR.DESCRIPTION1 carrierName,BL.FREIGHT_TYPE"
                + ",BL.FREIGHT_PAYABLE_AT,STR_TO_DATE(BL.CARGO_RECEIVE_DATE,'%Y-%m-%d') CARGO_RECEIVED_DATE,STR_TO_DATE(BL.ETD,'%Y-%m-%d') ETD,STR_TO_DATE(BL.ETA,'%Y-%m-%d') ETA," +
                "BL.POI,BL.POI_NAME,BL.LOADING_AGNT loadingAgentCode, BL.IS_APPROVED isApproved,igm_no,STR_TO_DATE(igm_date,'%Y-%m-%d') igmDate,item_no itemNumber,to_the_manager theManager," +
                "free_days,icd_factory,surveyors,empty_return_location,do_number,STR_TO_DATE(mbl_date,'%Y-%m-%d') mblDate,STR_TO_DATE(destcrd_date,'%Y-%m-%d') describedDate,terminal_code,terminal_name"
                + ",LAGNT.DESCRIPTION1 loadingAgentName,BL.LOADING_AGNT_CONTCT_DTLS loadingAgentContactDetails"
                + ",BL.DEST_AGNT destinationAgentCode,DAGNT.DESCRIPTION1 destinationAgentName,BL.DEST_AGNT_CONTCT_DTLS destinationAgentContactDetails," +
                "BL.BL_REMARK remarks,BL.QTY_IN_WORDS totalInWords, BL.SIGNATURE_FILE signature"
                + ",ACNEE.DESCRIPTION1 actConsigneeName,ASHPR.DESCRIPTION1 actShipperName,CNEE.DESCRIPTION1 consigneeName,bl.created_by, bl.amended_by "
                + ",SHPR.DESCRIPTION1 shipperName,bl.creation_date, bl.amended_date " +
                " FROM BL_HDR_F BL " +
                " LEFT OUTER JOIN PARTNER_ACCOUNT_D NOTIFY ON (BL.BL_NOTIFY = NOTIFY.PARTNER_CODE AND NOTIFY.LOADNG_AGNT = '" + bean.getLoadingAgent() + "')" +
                " LEFT OUTER JOIN PARTNER_ACCOUNT_D ACNEE ON (BL.ACT_CNEE = ACNEE.PARTNER_CODE AND ACNEE.LOADNG_AGNT = '" + bean.getLoadingAgent() + "')" +
                " LEFT OUTER JOIN PARTNER_ACCOUNT_D ANOTIFY ON (BL.ALSO_NOTIFY = ANOTIFY.PARTNER_CODE AND ANOTIFY.LOADNG_AGNT = '" + bean.getLoadingAgent() + "')" +
                " LEFT OUTER JOIN PARTNER_ACCOUNT_D ASHPR ON (BL.ACT_SHPR = ASHPR.PARTNER_CODE AND ASHPR.LOADNG_AGNT = '" + bean.getLoadingAgent() + "')" +
                " LEFT OUTER JOIN PARTNER_ACCOUNT_D CNEE ON (BL.CNEE = CNEE.PARTNER_CODE AND CNEE.LOADNG_AGNT = '" + bean.getLoadingAgent() + "')" +
                " LEFT OUTER JOIN PARTNER_ACCOUNT_D SHPR ON (BL.SHPR = SHPR.PARTNER_CODE AND SHPR.LOADNG_AGNT = '" + bean.getLoadingAgent() + "')" +
                " LEFT OUTER JOIN PARTNER_ACCOUNT_D LAGNT ON (BL.LOADING_AGNT = LAGNT.PARTNER_CODE AND LAGNT.LOADNG_AGNT = '" + bean.getLoadingAgent() + "')" +
                " LEFT OUTER JOIN PARTNER_ACCOUNT_D DAGNT ON (BL.DEST_AGNT = DAGNT.PARTNER_CODE AND DAGNT.LOADNG_AGNT = '" + bean.getLoadingAgent() + "')" +
                " LEFT OUTER JOIN PARTNER_ACCOUNT_D CARR ON (BL.CARRIER_CODE = CARR.PARTNER_CODE AND CARR.LOADNG_AGNT = '" + bean.getLoadingAgent() + "')" +
                " WHERE BL.BL_NO = ? ";

        BLBean blBean = jdbcTemplate.queryForObject(query,
                new BeanPropertyRowMapper<>(BLBean.class),
                bean.getBlNumber());

        blBean.setReportParams(getColumnHeaderData(blBean));

        return blBean;
    }

    private Map<String, String> getColumnHeaderData(BLBean bean) {
        String query1 = "SELECT BL_NO,sum(ACT_QTY) actualQty,ACT_UNIT actualUnit From bl_fcl_lcl_f WHERE BL_NO=? Group By BL_NO,ACT_UNIT ";

        String query2 = "SELECT BL_NO,sum(ACT_GROSS_WT) actualWeight,sum(ACT_NET_KGS)ACTUAL_KGS,sum(ACT_CBM) actualMeasurement From bl_fcl_lcl_f "
                + "WHERE BL_NO=? Group By BL_NO";

        String query3 = "SELECT sfl.MARK_NO markNumber,sfl.MARK_DETAILS FROM bl_fcl_lcl_f sfl WHERE sfl.BL_NO = ?";

        StringBuilder packageDetails = new StringBuilder();

        List<ContainerBean> cbList = jdbcTemplate.query(query1, new BeanPropertyRowMapper<>(ContainerBean.class), bean.getBlNumber());

        cbList.forEach(cb -> packageDetails.append(cb.getActualQty()).append("\n").append(cb.getActualUnit()).append("\n"));

        cbList = jdbcTemplate.query(query2, new BeanPropertyRowMapper<>(ContainerBean.class), bean.getBlNumber());
        Map<String, String> map = new HashMap<>();
        if (!cbList.isEmpty()) {
            map.put("actualGross", cbList.get(0).getActualWeight());
            map.put("actualCBM", cbList.get(0).getActualMeasurement());
        }

        cbList = jdbcTemplate.query(query3, new BeanPropertyRowMapper<>(ContainerBean.class), bean.getBlNumber());
        if (!cbList.isEmpty()) {
            map.put("markNo", cbList.get(0).getMarkNumber());
            map.put("markDetails", cbList.get(0).getMarkDetails());
        }
        map.put("packageDetails", packageDetails.toString());
        map.put("saidToContain", "SAID TO CONTAIN");
        if ("F".equalsIgnoreCase(bean.getBookingType())) {
            map.put("saidToContain", "SAID TO CONTAIN\nSHIPPERS LOAD, WEIGH STOW AND\nCOUNT");
        }

        return map;
    }

    @MeasureTime
    public void update(BLBean bean) {
        String updateQuery = "UPDATE bl_hdr_f SET M_BL_NUMBER=?, BL_ISSUE_DATE=?, BKG_REF_NO=?, JOB_NUMBER=?,CARGO_TYPE=?,NO_OF_ORIGINAL=?,ACT_SHPR=?," +
                "ACT_CNEE=?,SHPR=?,SHPR_CONTCT_DTLS=?,CNEE=?,CNEE_CONTCT_DTLS=?,BL_NOTIFY=?," +
                "BL_NOTIFY_CONTCT_DTLS=?,ALSO_NOTIFY=?,ALSO_NOTIFY_CONTCT_DTLS=?,POR=?,POR_NAME=?,POR_IND=?," +
                "POL=?,POL_NAME=?,POL_IND=?,FROM_FREIGHTAGE_CODE=?,FROM_FREIGHTAGE_NAME=?,POD=?,POD_NAME=?," +
                "POD_IND=?,DEST=?,DEST_NAME=?, DEST_IND=?,UPTO_FREIGHTAGE_CODE=?,UPTO_FREIGHTAGE_NAME=?,PRE_CARRAIGE=?,PRE_VOYAGE=?,"
                + "VSL=?,VOY=?,CARRIER_CODE=?,FREIGHT_TYPE=?,FREIGHT_PAYABLE_AT=?,CARGO_RECEIVE_DATE=?," +
                "ETD=?,ETA=?,POI=?,POI_NAME=?," +
                "LOADING_AGNT=?,LOADING_AGNT_CONTCT_DTLS=?,DEST_AGNT=?,DEST_AGNT_CONTCT_DTLS=?,BL_REMARK=?," +
                "QTY_IN_WORDS=?,AMENDED_BY=?,AMENDED_DATE=sysdate(), SIGNATURE_FILE=?,BL_RELASE_DATE=?,igm_no=?," +
                "igm_date=?,item_no=?,to_the_manager=?,free_days=?,icd_factory=?,surveyors=?,empty_return_location=?," +
                "do_number=?,mbl_date=?,destcrd_date=?,terminal_code=?,terminal_name=? " +
                "WHERE BL_NO=? ";

        jdbcTemplate.update(updateQuery,
                bean.getMBlNumber().toUpperCase(), StringUtils.defaultIfEmpty(bean.getBlIssueDate(), null), bean.getBkgRefNo(), bean.getJobNumber(), bean.getCargoType(),
                bean.getNoOfOriginal(), bean.getActShipper(),
                bean.getActConsignee(), bean.getShipper(), bean.getShipperContactDetails(), bean.getConsignee(), bean.getConsigneeContactDetails(), bean.getSoNotify(),
                bean.getSoNotifyContactDetails(), bean.getAlsoNotify(), bean.getAlsoNotifyContactDetails(), bean.getPor(), bean.getPorName(), bean.getPorInd(),
                bean.getPol(), bean.getPolName(), bean.getPolInd(), bean.getFromFreightCode(), bean.getFromFreightName(), bean.getPod(), bean.getPodName(),
                bean.getPodInd(), bean.getDest(), bean.getDestName(), bean.getDestInd(), bean.getUpToFreightCode(), bean.getUpToFreightName(), bean.getPreCarriage(), bean.getPreVoyage(),
                bean.getVsl(), bean.getVoy(), bean.getCarrierCode(), bean.getFreightType(), bean.getFreightPayableAt(), StringUtils.defaultIfEmpty(bean.getCargoReceivedDate(), null),
                StringUtils.defaultIfEmpty(bean.getEtd(), null), StringUtils.defaultIfEmpty(bean.getEta(), null), bean.getPoi(), bean.getPoiName(),
                bean.getLoadingAgentCode(), bean.getLoadingAgentContactDetails(), bean.getDestinationAgentCode(), bean.getDestinationAgentContactDetails(), bean.getRemarks(),
                bean.getTotalInWords(), bean.getUserId(), bean.getSignature(), bean.getBlReleaseDate(), bean.getIgmNo(), defaultNull(bean.getIgmDate()), bean.getItemNumber(), bean.getTheManager(), bean.getFreeDays(),
                bean.getIcdFactory(), bean.getSurveyors(), bean.getEmptyReturnLocation(), bean.getDoNumber(), StringUtils.defaultIfEmpty(bean.getMblDate(), null),
                StringUtils.defaultIfEmpty(bean.getDescribedDate(), null), bean.getTerminalCode(), bean.getTerminalName(), bean.getBlNumber());
    }

    public List<ContainerBean> getContainerDetails(BLBean bean, int lineNo) {
        String query = ("select f.bl_no blNumber,f.BKG_REF_NO bookingRefNumber,f.BKG_TYPE,f.CONTNR_NO number"
                + ",f.SIZE_CODE size,s.DESCRIPTION,f.BKG_QTY qty,c.LOADING_AGNT loadingAgentCode"
                + ",f.BKG_UNIT unit,f.BKG_KGS_GROSS_WT weight,f.BKG_NET_WT netWeight,f.BKG_CBM measurement"
                + ",f.ACT_QTY actualQty,f.ACT_UNIT actualUnit,f.ACT_GROSS_WT actualWeight,f.ACT_NET_KGS actualNetWeight,f.ACT_CBM actualMeasurement,f.SHIPPING_BILL_NO,f.BL_LINE_NO lineNumber"
                + ",STR_TO_DATE(f.SHIPPING_BILL_DATE,'%Y-%m-%d') AS SHIPPING_BILL_DATE,f.PRE_ALERT_REMARKS remarks,f.STATUS,f.MARK_NO markNumber,f.MARK_DETAILS," +
                " f.CUSTM_SEAL_NO customSealNumber,f.LINE_SEAL_NO lineSealNumber, f.so_number, f.service_term_code serviceTerm "
                + ",f.CREATED_BY,STR_TO_DATE(f.CREATION_DATE,'%Y-%m-%d') AS CREATION_DATE,f.AMENDED_BY,STR_TO_DATE(f.AMENDED_DATE,'%Y-%m-%d') AS AMENDED_DATE "
                + " from (bl_fcl_lcl_f f LEFT OUTER JOIN bl_hdr_f c on (f.BL_NO =c.BL_NO))"
                + " LEFT OUTER JOIN size_d s ON (s.SIZE_CODE=f.SIZE_CODE)"
                + " WHERE f.BL_NO=? ");

        if (lineNo > 0) {
            query += " AND f.bkg_ref_no = " + bean.getBkgRefNo() + " AND f.bl_line_no = " + lineNo;
        }
        query += " ORDER BY f.bl_line_no";

        return jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(ContainerBean.class),
                bean.getBlNumber());
    }

    public void blPDF(BLBean blBean) {
        BLPDFOriginal.writeUCMpdf(blBean.getBlNumber(), blBean, jdbcTemplate);
    }

    public BLBean getBlForBilling(InvoiceBean bean) {
        String party = "bl.shpr";
        String theCC = "destcc";
        if ("IMPORT".equalsIgnoreCase(bean.getExpImp())) {
            party = "bl.cnee";
            theCC = "loadingcc";
        }

        String query = "SELECT " +
                "COALESCE(cntnr.grosswt,0) weight,COALESCE(cntnr.qty,0) qty,COALESCE(cntnr.cbm,0) actualCbm,cntnr.fclbl,cntnr.container containerList,fcl.shipping_bill_no," +
                "bl.bl_no,convert(bl.bl_issue_date,DATE) bl_issue_date,bl.pod,bl.pod_name," +
                "bl.pol,bl.pol_name,bl.carrier_code,bl.job_number,DATE_FORMAT(etd,120) etd,bl.shpr shipper,bl.cnee consignee,shpr.description1 shipperName," +
                "cnee.description1 consigneeName,COALESCE(party.code_combination_id,0) partyAcctCode,sal.name salesBy,party.credit_period,bl.dest_agnt destinationAgentCode, " +
                "dest.description1 destinationAgentName, bl.loading_agnt loadingAgentCode, loading.description1 loadingAgentName,shpr.address1 shipperContactDetails, " +
                "cnee.address1 consigneeContactDetails,dest.address1 destinationAgentContactDetails, loading.address1 loadingAgentContactDetails,party.gstin_no," +
                "COALESCE("+theCC+".code_combination_id,0) destCCId,bl.M_BL_NUMBER,party.state_code,party.is_sez," +
                "bl.shpr billTo,  shpr.description1 billToName, shpr.address1 billToAddress,so.invNo " +
                "FROM BL_HDR_F bl " +
                "LEFT OUTER JOIN (SELECT pa.partner_account_code,pa.partner_code,cc.code_combination_id,pa.credit_period,pa.state_code," +
                "pa.is_sez,pa.gstin_no FROM partner_account_d pa, gl_code_combination_d cc " +
                "WHERE cc.code_combination_id = pa.partner_acct_code AND pa.loadng_agnt = ? AND pa.status = 'A') party ON (party.partner_code = " + party + " ) " +
                "LEFT OUTER JOIN (SELECT SUM(act_gross_wt) grosswt,SUM(act_cbm) cbm,SUM(ACT_QTY) qty,bl_no fclbl, " +
                "(GROUP_CONCAT(concat(CONTNR_NO, (CASE WHEN size_code IS NULL THEN '' ELSE Concat(' / ',size_code) END)) SEPARATOR ',')) container " +
                "FROM bl_fcl_lcl_f WHERE BL_NO=?   GROUP BY bl_no) cntnr ON (cntnr.fclbl = bl.bl_no) " +
                "LEFT OUTER JOIN (SELECT bl_no,(GROUP_CONCAT(shipping_bill_no SEPARATOR ',')) shipping_bill_no FROM bl_fcl_lcl_f  WHERE BL_NO=?   " +
                "GROUP BY bl_no) fcl ON (fcl.bl_no = bl.bl_no) " +
                "LEFT OUTER JOIN (SELECT bl_no,(GROUP_CONCAT(t2.comm_inv_number SEPARATOR ',')) invNo FROM so_hdr_f t2 WHERE BL_NO=? " +
                "GROUP BY bl_no) so ON (so.bl_no = bl.bl_no) " +
                "LEFT OUTER JOIN partner_account_d shpr ON (shpr.partner_code = bl.shpr AND shpr.loadng_agnt = ? AND shpr.status = 'A') " +
                "LEFT OUTER JOIN partner_account_d cnee ON (cnee.partner_code = bl.cnee AND cnee.loadng_agnt = ? AND cnee.status = 'A') " +
                "LEFT OUTER JOIN partner_account_d dest ON (dest.partner_code = bl.dest_agnt AND dest.loadng_agnt = ? AND dest.status = 'A') " +
                "LEFT OUTER JOIN partner_account_d loading ON (loading.partner_code = bl.loading_agnt AND loading.loadng_agnt = ? AND loading.status = 'A') " +
                "LEFT OUTER JOIN gl_code_combination_d destcc ON (destcc.code_combination_id = dest.partner_acct_code) " +
                "LEFT OUTER JOIN gl_code_combination_d loadingcc ON (loadingcc.code_combination_id = loading.partner_acct_code) " +
                "LEFT OUTER JOIN salesman_f sal ON (sal.code = shpr.salesman_code) WHERE bl.bl_no = ? ";

        return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(BLBean.class), bean.getLoadingAgent(), bean.getBlNo(), bean.getBlNo(), bean.getBlNo(),
                bean.getLoadingAgent(), bean.getLoadingAgent(), bean.getLoadingAgent(), bean.getLoadingAgent(), bean.getBlNo());
    }

    public void blPDFDraft(BLBean blBean) {
        BLPDFDraft.writeUCMpdf(blBean.getBlNumber(), blBean, jdbcTemplate);
    }

    public void approve(BLBean blBean) {
        jdbcTemplate.update("UPDATE bl_hdr_f SET is_approved='Y' WHERE bl_no = ?", blBean.getBlNumber());
    }

    public void saveContainers(BLBean bean, ContainerBean cb, int counter) {
        String query = "INSERT INTO bl_fcl_lcl_f"
                + "(BL_NO,SO_NUMBER,BL_LINE_NO,SO_LINE_NO,BKG_REF_NO,BKG_TYPE,CONTNR_NO,SIZE_CODE,CUSTM_SEAL_NO,LINE_SEAL_NO,BKG_QTY,BKG_UNIT," +
                "BKG_KGS_GROSS_WT,BKG_NET_WT,BKG_CBM,ACT_QTY,ACT_UNIT,ACT_GROSS_WT,ACT_NET_KGS,ACT_CBM,ACT_STUFFING_DATE,SHIPPING_BILL_NO," +
                "SHIPPING_BILL_DATE,PRE_ALERT_REMARKS,MARK_NO,MARK_DETAILS  ,STATUS,CREATED_BY,CREATION_DATE ) VALUES " +
                "(?, ?, " + counter + ",?,?,?,?,?,?," +
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE()) ";

        jdbcTemplate.update(query, bean.getBlNumber(), cb.getSoNumber(), cb.getLineNumber(), cb.getBookingRefNumber(), cb.getBookingType(), cb.getNumber(), cb.getSize(),
                cb.getCustomSealNumber(), cb.getLineSealNumber(), cb.getQty(), cb.getUnit(), cb.getWeight(), cb.getNetWeight(), cb.getMeasurement(), cb.getActualQty(), cb.getActualUnit(),
                cb.getActualWeight(), cb.getActualNetWeight(), cb.getActualMeasurement(), cb.getActualStuffingDate(), cb.getShippingBillNumber(), cb.getShippingBillDate(), cb.getContainerRemarks(),
                cb.getMarkNumber(), cb.getMarkDetails(), "A", bean.getUserId());
    }

    public void updateContainer(ContainerBean cb) {
        String query = "UPDATE bl_fcl_lcl_f SET PRE_ALERT_REMARKS=?,AMENDED_BY = ?,AMENDED_DATE = ?,MARK_NO = ?,MARK_DETAILS = ?,SERVICE_TERM_CODE=?,BKG_CBM=? WHERE BL_NO=? AND BL_LINE_NO=? ";
        jdbcTemplate.update(query, cb.getContainerRemarks(), cb.getUserId(), DateUtil.getSystemDate(), cb.getMarkNumber(), cb.getMarkDetails(),
                cb.getServiceTerm(), cb.getMeasurement(), cb.getBlNumber(), cb.getLineNumber());
    }

    public void updateJobNumber(JobBean bean) {
        String soNumber = "";
        String soNumberRest = "";

        for (SOBean cb : bean.getSoBeanList()) {
            if (org.springframework.util.StringUtils.hasText(cb.getUuid())) {
                soNumber += cb.getSoNumber() + ",";
            } else {
                soNumberRest += cb.getSoNumber() + ",";
            }
        }
        soNumber += "-1";
        soNumberRest += "-1";

        String blRestUpdateQuery = "UPDATE bl_hdr_f,so_hdr_f  SET bl_hdr_f.JOB_NUMBER=NULL  " +
                "WHERE so_hdr_f.SO_NUMBER IN (" + soNumberRest + ") and bl_hdr_f.bl_no=so_hdr_f.bl_no  and so_hdr_f.bl_no is not null ";
        String blUpdateQuery = "UPDATE bl_hdr_f,so_hdr_f  SET bl_hdr_f.JOB_NUMBER=?  " +
                "WHERE so_hdr_f.SO_NUMBER IN (" + soNumber + ") and bl_hdr_f.bl_no=so_hdr_f.bl_no  and so_hdr_f.bl_no is not null ";
        jdbcTemplate.update(blRestUpdateQuery);
        jdbcTemplate.update(blUpdateQuery, bean.getJobNumber());
    }

    public void updateBlWithJob(JobBean bean) {
        String blUpdateQuery = "UPDATE BL_HDR_F SET VSL=?,VOY=?,CARRIER_CODE=?,ETD=?,ETA=? WHERE JOB_NUMBER = ? ";
        jdbcTemplate.update(blUpdateQuery, bean.getVsl(), bean.getVoy(), bean.getCarrierCode(), bean.getEtd(), bean.getEta(), bean.getJobNumber());
    }

    public void deletBL(BLBean bean) {
        String soUpdateQuery = "UPDATE SO_HDR_F SET BL_NO=NULL WHERE BL_NO = ? ";
        String blDeleteQuery = "delete from bl_hdr_f where BL_NO = ? ";
        String blDtlDeleteQuery = "delete from bl_fcl_lcl_f where BL_NO = ? ";
        String blDocCollectionDeleteQuery = "delete from bl_doc_collection_d where BL_NO = ? ";
        for (String record : bean.getUuids()) {
            jdbcTemplate.update(soUpdateQuery, record);
            jdbcTemplate.update(blDtlDeleteQuery, record);
            jdbcTemplate.update(blDeleteQuery, record);
            jdbcTemplate.update(blDocCollectionDeleteQuery, record);
        }
    }
}
