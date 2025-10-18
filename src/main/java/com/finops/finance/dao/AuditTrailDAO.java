package com.finops.finance.dao;

import com.finops.admin.model.AuditTrail;
import com.finops.dao.AbstractDAO;
import com.finops.finance.bean.AuditBean;
import com.finops.finance.bean.BillTemplateBean;
import com.finops.util.sql.QueryBuilder;
import com.finops.util.sql.QueryType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AuditTrailDAO extends AbstractDAO {


    public List<AuditBean> fetchAuditData(AuditBean auditBean) {
        QueryBuilder query = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" user_id param1,audit_action param3,audit_date param4,audit_type param5," +
                        "subtype,id param2,acct_year, branch param6, audit_data param7 ")
                .FROM("audit_trail", "")
                .WHERE("1", "1");

        String[] fields = {"user_id", "audit_data", "audit_action"};
        addFilter(query, auditBean, fields);
        addLimit(" audit_date desc ", query, auditBean);

        return jdbcTemplate.query(query.build().toString(),
                new BeanPropertyRowMapper<>(AuditBean.class));
    }

    public void insertAuditLog(AuditTrail auditTrail) {

        String query = "INSERT INTO audit_trail (user_id, audit_action, audit_date, audit_type, subtype, id, acct_year, branch, audit_data) " +
                "VALUES (?, ?, SYSDATE(), ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(query,
                auditTrail.getUserId(),
                auditTrail.getAction(),
                auditTrail.getType(),
                auditTrail.getSubType(),
                UUID.randomUUID().toString(),
                auditTrail.getAcctYear(),
                auditTrail.getBranch(),
                auditTrail.getData());
    }
}
