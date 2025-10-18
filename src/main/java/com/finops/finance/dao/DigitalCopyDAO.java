package com.finops.finance.dao;

import com.finops.dao.AbstractDAO;
import com.finops.report.model.ReportBean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Component
public class DigitalCopyDAO extends AbstractDAO {

    private static final String MODULE = "DigitalCopy";

    public void saveDigitalCopyRecord(ReportBean bean){
        String query = "INSERT INTO digital_copy_d " +
                "VALUES (?,?,?,?,?,SYSDATE(),?,?,?,?,?,?,?,?,?,?,?,?) ";

        jdbcTemplate.update(query, UUID.randomUUID().toString(), StringUtils.trimAllWhitespace(bean.getParam2()),
                StringUtils.trimAllWhitespace(bean.getParam3()),bean.getParam1(),bean.getUserId(),
                bean.getParam9(), bean.getParam10(), bean.getParam8(), bean.getParam5(), bean.getParam4()
                , bean.getParam7(), bean.getParam6(), bean.getParam11(), bean.getParam12(), bean.getParam13(),
                bean.getLoadingAgent(), bean.getParam14());

    }

    public List<ReportBean> viewDigitalCopyRecords(ReportBean bean){
        StringBuilder query = new StringBuilder("SELECT id param1,so_number param2,bl_number param3," +
                "record_type param4, local_path param5, cloud_url param6, shipper_name param7," +
                "consignee_name param8, document_no param13, job_number param14 " +
                "FROM digital_copy_d WHERE BRANCH = ? ");

        String[] fields = {"DOCUMENT_NO","SO_NUMBER", "BL_NUMBER", "record_type", "SHIPPER_NAME", "CONSIGNEE_NAME", "JOB_NUMBER"};
        addFilter(query, bean, fields);
        query.append(" ORDER BY creation_date desc limit 0, 50 ");

        return jdbcTemplate.query(query.toString(), new BeanPropertyRowMapper<>(ReportBean.class),bean.getLoadingAgent());
    }

    public void deleteDigitalCopy(ReportBean bean) {
        String sql = "DELETE FROM digital_copy_d WHERE id=?";
        for (String hdrId : bean.getUuids()) {
            jdbcTemplate.update(sql, hdrId);
        }

    }
}
