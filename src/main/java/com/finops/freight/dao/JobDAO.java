package com.finops.freight.dao;

import com.finops.aop.MeasureTime;
import com.finops.dao.AbstractDAO;
import com.finops.freight.bean.BLBean;
import com.finops.freight.bean.JobBean;
import com.finops.report.legacy.BLPDFDraft;
import com.finops.report.legacy.JOBPDF;
import com.finops.util.sql.QueryBuilder;
import com.finops.util.sql.QueryType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class JobDAO extends AbstractDAO {

    @MeasureTime
    public List<JobBean> findJobByPage(JobBean bean){
        String agent = "LOADING_AGNT";
        if ("IMPORT".equalsIgnoreCase(bean.getExpImp())) {
            agent = "DEST_AGNT";
        }

        QueryBuilder findJobByPageQuery = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" JOB_NUMBER, JOB_SEQ_NO,STR_TO_DATE(JOB_DATE,'%Y-%m-%d') JOB_DATE" +
                        ",POR,POL,POD, VSL, VOY,DEST, CARRIER_CODE" +
                                ", STR_TO_DATE(ETD,'%Y-%m-%d') ETD, STR_TO_DATE(ETA,'%Y-%m-%d') ETA, " +
                                "STR_TO_DATE(SOB_DATE,'%Y-%m-%d') SOB_DATE,LOADING_AGNT,DEST_AGNT,CURRENCY_CODE" +
                                ",CURR_CONV_FACTOR,REMARK,JOB_STATUS,CREATED_BY" +
                                ", STR_TO_DATE(CREATION_DATE,'%Y-%m-%d')CREATION_DATE" +
                                ", AMENDED_BY" +
                                ", STR_TO_DATE(AMENDED_DATE,'%Y-%m-%d')AMENDED_DATE")
                .FROM("job_hdr_f", "JOBH")
                .WHERE(agent, "?");

        addFilter(findJobByPageQuery,"JOBH.job_number", bean.getJobNumber(), true);
        addFilter(findJobByPageQuery,"JOBH.job_date",bean.getJobDate(), true);
        addFilter(findJobByPageQuery,"JOBH.pol",bean.getPol(), true);
        addFilter(findJobByPageQuery,"JOBH.pod",bean.getPod(), true);
        addFilter(findJobByPageQuery,"JOBH.etd",bean.getEtd(), true);
        addFilter(findJobByPageQuery,"JOBH.eta",bean.getEta(), true);
        addFilter(findJobByPageQuery,"JOBH.vsl",bean.getVsl(), true);
        addFilter(findJobByPageQuery,"JOBH.voy",bean.getVoy(), true);
        findJobByPageQuery.ORDER_BY(" JOB_SEQ_NO DESC ");
        findJobByPageQuery.LIMIT(bean.getStart(), bean.getLength());

        return jdbcTemplate.query(findJobByPageQuery.build().toString(),
                new BeanPropertyRowMapper<>(JobBean.class),
                bean.getLoadingAgent());
    }

    public JobBean retrieve(JobBean jobBean) {
        String query = "SELECT  job.JOB_NUMBER ,job.JOB_SEQ_NO jobAutoSequence,STR_TO_DATE(job.JOB_DATE,'%Y-%m-%d') JOB_DATE,STR_TO_DATE(job.SOB_DATE,'%Y-%m-%d') SOB_DATE," +
                "job.CURRENCY_CODE,job.CURR_CONV_FACTOR,job.POR,POR.DESCRIPTION1 porName,job.POL,POL.DESCRIPTION1 polName,job.POD,POD.DESCRIPTION1 podName,j" +
                "ob.DEST,DEST.DESCRIPTION1 destName,job.vsl,job.voy,job.CARRIER_CODE,CARR.DESCRIPTION1 carrierName,STR_TO_DATE(job.ETD,'%Y-%m-%d') ETD," +
                "STR_TO_DATE(job.ETA,'%Y-%m-%d') ETA,job.LOADING_AGNT loadingAgentCode,LAGNT.DESCRIPTION1 loadingAgentName," +
                "job.DEST_AGNT destinationAgentCode,DAGNT.DESCRIPTION1 destinationAgentName,job.REMARK remarks," +
                "job.JOB_STATUS,job.CREATED_BY,STR_TO_DATE(job.CREATION_DATE,'%Y-%m-%d') CREATION_DATE,job.AMENDED_BY,STR_TO_DATE(job.AMENDED_DATE,'%Y-%m-%d') AMENDED_DATE " +
                "FROM JOB_HDR_F job " +
                "LEFT OUTER JOIN PORT_D POR ON (job.POR = POR.PORT_CODE) " +
                "LEFT OUTER JOIN PORT_D POL ON (job.POL = POL.PORT_CODE ) " +
                "LEFT OUTER JOIN PORT_D POD  ON (job.POD = POD.PORT_CODE) " +
                "LEFT OUTER JOIN PORT_D DEST ON (job.DEST = DEST.PORT_CODE) " +
                "LEFT OUTER JOIN PARTNER_ACCOUNT_D LAGNT ON (LAGNT.LOADNG_AGNT = ? AND LAGNT.STATUS = 'A' AND job.LOADING_AGNT = LAGNT.PARTNER_CODE) " +
                "LEFT OUTER JOIN PARTNER_ACCOUNT_D DAGNT ON (DAGNT.LOADNG_AGNT = ? AND DAGNT.STATUS = 'A' AND job.DEST_AGNT = DAGNT.PARTNER_CODE ) " +
                "LEFT OUTER JOIN PARTNER_ACCOUNT_D CARR ON (CARR.LOADNG_AGNT = ? AND CARR.STATUS = 'A' AND job.CARRIER_CODE = CARR.PARTNER_CODE) " +
                "WHERE job.JOB_NUMBER = ?";

        return jdbcTemplate.queryForObject(query,
                new BeanPropertyRowMapper<>(JobBean.class),
                jobBean.getLoadingAgent(), jobBean.getLoadingAgent(),
                jobBean.getLoadingAgent(), jobBean.getJobNumber());
    }

    public void shippingAdvice(JobBean bean) {
        JOBPDF.writeUCMpdf(bean.getJobNumber(), bean, bean.getLegacyReportPath(),jdbcTemplate);
    }

    public void save(JobBean bean) {
        String query = "INSERT INTO job_hdr_f(JOB_NUMBER,JOB_SEQ_NO,JOB_DATE,POR,POL,POD,DEST,VSL,VOY,CARRIER_CODE," +
                "ETD,ETA,SOB_DATE,LOADING_AGNT,DEST_AGNT,CURRENCY_CODE,CURR_CONV_FACTOR,REMARK,JOB_STATUS," +
                "CREATED_BY,CREATION_DATE,AMENDED_BY,AMENDED_DATE) " +
                "VALUES (?,?,?,?,?,?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,SYSDATE(),?,SYSDATE())";
        jdbcTemplate.update(query,
                bean.getJobNumber(),bean.getJobAutoSequence(), bean.getJobDate(), bean.getPor(), bean.getPol(), bean.getPod(), bean.getDest(),
                bean.getVsl(), bean.getVoy(), bean.getCarrierCode(), bean.getEtd(), bean.getEta(), defaultNull(bean.getSobDate()),
                bean.getLoadingAgentCode(), bean.getDestinationAgentCode(), bean.getCurrencyCode(), bean.getCurrencyCode(),
                bean.getRemarks(), "A", bean.getUserId(), bean.getUserId());

    }

    public void delete(JobBean bean) {
        String query = "DELETE FROM job_hdr_f WHERE job_number = ?";
        jdbcTemplate.update(query,
                bean.getJobNumber());

    }

public void deleteJob(JobBean jobBean) {
        String updateSOQuery = "UPDATE SO_HDR_F SET JOB_NUMBER=NULL WHERE JOB_NUMBER = ? ";
        String updateBLQuery = "UPDATE BL_HDR_F SET JOB_NUMBER=NULL WHERE JOB_NUMBER = ? ";
        String deleteJobQuery = "DELETE FROM job_hdr_f WHERE job_number = ? ";
        for (String record : jobBean.getUuids()) {
            jdbcTemplate.update(updateSOQuery, record);
            jdbcTemplate.update(updateBLQuery, record);
            jdbcTemplate.update(deleteJobQuery, record);
        }
    }
}
