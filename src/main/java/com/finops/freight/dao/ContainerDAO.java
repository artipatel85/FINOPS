package com.finops.freight.dao;

import com.finops.aop.MeasureTime;
import com.finops.dao.AbstractDAO;
import com.finops.freight.bean.BLBean;
import com.finops.freight.bean.ContainerBean;
import com.finops.report.legacy.CLPPDF;
import com.finops.util.DateUtil;
import com.finops.util.sql.Condition;
import com.finops.util.sql.Query;
import com.finops.util.sql.QueryBuilder;
import com.finops.util.sql.QueryType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContainerDAO extends AbstractDAO {

    @MeasureTime
    public List<ContainerBean> findCLPByPage(ContainerBean bean, boolean isRetrieve){
        int limit = bean.getLength();
        QueryBuilder findBLByPageQuery = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" DISTINCT LP.LOAD_PLAN_NO number, LP.VSL,LP.LOADING_AGNT loadingAgentCode, LP.VOY, LP.CARRIER_CODE, " +
                        "STR_TO_DATE(LOAD_PLAN_DT,'%Y-%m-%d') loadPlanDate , LP.POL, STR_TO_DATE(ACT_LOADING_DT,'%Y-%m-%d') actualLoadingDate, " +
                        "LP.POD, LP.WAREHSE warehouse, LP.CONTNR_NO containerNo, LP.SIZE_CODE size, LP.LINE_SEAL_NUM lineSealNumber, " +
                        "LP.CUSTM_SEAL_NO customSealNumber, LP.CREATED_BY,CONVERT(LP.CFS_DATE,DATE) ETD, CONVERT(LP.CY_DATE,DATE) ETA, " +
                        "LP.REMARKS containerRemarks, LP.load_method, service_term_code serviceTerm, " +
                        "STR_TO_DATE(LP.CREATION_DATE,'%Y-%m-%d') CREATION_DATE, LP.AMENDED_BY, STR_TO_DATE(LP.AMENDED_DATE,'%Y-%m-%d') AMENDED_DATE")
                .FROM("load_plan_hdr_f", "lp")
                .LEFT_JOIN("SO_HDR_F", "so", new Condition("so.LOAD_PLAN_NO","lp.LOAD_PLAN_NO", Query.EQUALS))
                .WHERE("1", "1");

        if(isRetrieve == true){
            findBLByPageQuery.AND("LP.LOAD_PLAN_NO", bean.getNumber() );
            limit = 10;
        }
        String[] fields = {"lp.load_plan_no", "LP.loadPlanDate", "warehse", "LP.carrier_code", "LP.vsl", "LP.voy", "LP.pol", "LP.pod", "party","","so.so_number"};
        addFilter(findBLByPageQuery, bean, fields);
        findBLByPageQuery.ORDER_BY(" LP.LOAD_PLAN_NO DESC ").LIMIT(bean.getStart(), limit);

        return jdbcTemplate.query(findBLByPageQuery.build().toString(),
        new BeanPropertyRowMapper<>(ContainerBean.class));
    }

    public void clpPDFReport(ContainerBean containerBean) {
    CLPPDF.writeUCMpdf(containerBean.getNumber(), containerBean, containerBean.getLegacyReportPath(), jdbcTemplate);
    }

    public void saveCLP(ContainerBean cb) {
        String query = "INSERT INTO load_plan_hdr_f( LOAD_PLAN_NO, LOAD_PLAN_DT,POL,POD,CARRIER_CODE, WAREHSE, VSL, VOY, " +
                "CONTNR_NO,SIZE_CODE,SERVICE_TERM_CODE,CFS_DATE,CY_DATE,ACT_LOADING_DT,LINE_SEAL_NUM,CUSTM_SEAL_NO,LOAD_METHOD," +
                "CREATED_BY,CREATION_DATE,AMENDED_BY,AMENDED_DATE,REMARKS,LOADING_AGNT,HQ_SUB) " +
                "VALUES ( ?,?,?,?,?,?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        jdbcTemplate.update(query, cb.getNumber(),defaultNull(cb.getLoadPlanDate()),cb.getPol(),cb.getPod(),cb.getCarrierCode(),
                cb.getWarehouse(),cb.getVsl(), cb.getVoy(), cb.getContainerNo(),
                cb.getSize(), cb.getServiceTerm(),defaultNull(cb.getEtd()), defaultNull(cb.getEta()),
                defaultNull(cb.getActualLoadingDate()), cb.getLineSealNumber(),
                cb.getCustomSealNumber(), cb.getLoadMethod(), cb.getUserId(), DateUtil.getSystemDate(), cb.getUserId(), DateUtil.getSystemDate(),
                cb.getContainerRemarks(),cb.getLoadingAgent(), "N");
    }

    public void updateCLP(ContainerBean cb) {
        String query = "UPDATE LOAD_PLAN_HDR_F SET CARRIER_CODE=?,VSL=?, VOY=?,CONTNR_NO=?"
                + ",SIZE_CODE=?,CFS_DATE=?,CY_DATE=?,ACT_LOADING_DT=?,SERVICE_TERM_CODE=?,LINE_SEAL_NUM=?"
                + ",CUSTM_SEAL_NO=?,LOAD_METHOD=?,REMARKS=?,HQ_SUB=?,AMENDED_BY=?,AMENDED_DATE=? "
                + " WHERE LOAD_PLAN_NO = ?";

        jdbcTemplate.update(query, cb.getCarrierCode(), cb.getVsl(), cb.getVoy(), cb.getContainerNo(),
                cb.getSize(), defaultNull(cb.getEtd()), defaultNull(cb.getEta()),
                defaultNull(cb.getActualLoadingDate()), cb.getServiceTerm(), cb.getLineSealNumber(),
                cb.getCustomSealNumber(), cb.getLoadMethod(), cb.getContainerRemarks(), "N", cb.getUserId(), DateUtil.getSystemDate(),
                cb.getNumber());
    }
}
