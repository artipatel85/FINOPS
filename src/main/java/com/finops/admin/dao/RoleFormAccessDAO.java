package com.finops.admin.dao;

import com.finops.admin.model.FormBean;
import com.finops.dao.AbstractDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RoleFormAccessDAO extends AbstractDAO {

    public List<FormBean> fetchUserPrivilegeMap(String roleName, boolean forLogin) {
        String formAccessQuery = ("select u.form_id,u._select 'select',u._create 'create',u._update 'update',u._delete 'delete'," +
                "u._print print,u._upload upload, u._approve approve"
                + ",u._download download,f.form_name name,f.parent_id,f.url "
                + "FROM role_priviledge_d u,forms_d f "
                + "WHERE u.role_name=? AND u.form_id=f.form_id AND f.status='A' ");

        if(forLogin){
            formAccessQuery += "AND u._select='Y' ";
        }

        formAccessQuery += " ORDER BY u.form_id ";

        return jdbcTemplate.query(
                formAccessQuery,
                new BeanPropertyRowMapper<>(FormBean.class),
                roleName);
    }

    public Map<Integer, FormBean> fetchUserPrivilegeMap(List<FormBean> formBeans) {
        return formBeans.stream().collect(Collectors.toMap(FormBean ::getFormId, formBean -> formBean, (x, y) -> y, LinkedHashMap::new));
    }
}
